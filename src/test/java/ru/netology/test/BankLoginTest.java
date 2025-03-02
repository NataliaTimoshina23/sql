package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.SQLHelper.cleanDatabase;
import static ru.netology.data.SQLHelper.cleanAuthCodes;


public class BankLoginTest {
    LoginPage loginPage;

    @AfterEach
    void tearDown() {
        cleanAuthCodes();
    }
    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @Test
    void shouldSuccessfulLogin() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.VerificationCode();
        verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldErrorNotificationIfLogin() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();

        String expectedErrorMessage = "Ошибка\n" + "Ошибка! Неверно указан логин или пароль";
        String actualErrorMessage = loginPage.getErrorNotificationText();
        assertEquals(expectedErrorMessage, actualErrorMessage, "Сообщение об ошибке не совпадает");
    }

    @Test
    void shouldGetErrorNotificationIfLoginWithExistUser() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        //verificationPage.verifyErrorNotificationVisibility();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotificationVisibility();

        String expectedErrorMessage = "Ошибка\n" + "Ошибка! Неверно указан код! Попробуйте ещё раз.";
        String actualErrorMessage = verificationPage.getErrorNotificationText();
        assertEquals(expectedErrorMessage, actualErrorMessage, "Сообщение об ошибке не совпадает");

    }
}
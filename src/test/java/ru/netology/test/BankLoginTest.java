package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.SQLHelper.cleanDatabase;
import static ru.netology.data.SQLHelper.cleanAuthCodes;

public class BankLoginTest {
    LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

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
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.VerificationCode();
        verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldErrorNotificationIfLogin() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();

        String expectedErrorMessage = "Ошибка\nОшибка! Неверно указан логин или пароль";
        loginPage.verifyErrorNotificationVisibility(expectedErrorMessage);
    }

    @Test
    void shouldGetErrorNotificationIfLoginWithExistUser() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        String expectedErrorMessage = null;
        verificationPage.verifyErrorNotificationVisibility(expectedErrorMessage);

        expectedErrorMessage = "Ошибка\nОшибка! Неверно указан код! Попробуйте ещё раз.";
        verificationPage.verifyErrorNotificationVisibility(expectedErrorMessage);
    }

    @Test
    void shouldShowErrorNotificationWithCorrectMessage() {
        var authInfo = DataHelper.getInvalidAuthInfo();
        loginPage.invalidLogin(authInfo);
        String expectedErrorMessage = "Ошибка\nОшибка! Неверно указан логин или пароль";
        loginPage.verifyErrorNotificationVisibility(expectedErrorMessage);
    }
}
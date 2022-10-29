package ru.netology.test.UI;
import static com.codeborne.selenide.Selenide.$;

import ru.netology.data.SQL;
import ru.netology.page.Main;
import ru.netology.page.PaymentCard;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;

import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.selenide.AllureSelenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

public class UICardTest {

    private SelenideElement errorSpecifiedPeriodCard = $(Selectors.withText("Неверно указан срок действия карты"));
    private SelenideElement errorPeriodCard = $(Selectors.withText("Истёк срок действия карты"));
    private SelenideElement errorEmptyFieldOwner = $(Selectors.withText("Поле обязательно для заполнения"));
    private SelenideElement errorFormat = $(Selectors.withText("Неверный формат"));
    private SelenideElement messageSuccess = $(Selectors.withText("Успешно"));
    private SelenideElement messageApprove = $(Selectors.withText("Операция одобрена Банком."));
    private SelenideElement messageError = $(Selectors.withText("Ошибка"));
    private SelenideElement messageDecline = $(Selectors.withText("Ошибка! Банк отказал в проведении операции."));

    @BeforeAll
    static void setup() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDown() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void openSetup() {
        open("http://localhost:8080");
    }

    long numberFromPayment() {
        SQL sql = new SQL();
        return sql.getNumberOfPayment();
    }

    String statusAfterServer() {
        SQL sql = new SQL();
        return sql.getStatusPayment();
    }

    public void positiveMessage() {
        messageSuccess.shouldBe(Condition.visible, Duration.ofSeconds(15));
        messageApprove.shouldBe(Condition.visible);
    }

    public void notPositiveMessage() {
        messageSuccess.shouldNotBe(Condition.visible, Duration.ofSeconds(15));
        messageApprove.shouldNotBe(Condition.visible);
    }

    public void denialMessage() {
        messageError.shouldBe(Condition.visible, Duration.ofSeconds(15));
        messageDecline.shouldBe(Condition.visible);
    }

    public void notDenialMessage() {
        messageError.shouldNotBe(Condition.visible, Duration.ofSeconds(15));
        messageDecline.shouldNotBe(Condition.visible);
    }

    public void checkNumberPayment(long initialNumberPayment, int x) {
        long finalNumberPayment = numberFromPayment();
        assertEquals(initialNumberPayment + x, finalNumberPayment);
    }

    PaymentCard choicePaymentCard() {
        Main page = new Main();
        return page.clickButtonPay();
    }

    @Test
    @DisplayName("Successful Payment")
    public void shouldBeSuccessfulPayment() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.approvedNumberCard();
        positiveMessage();
        String statusAfterServer = statusAfterServer();
        checkNumberPayment(initialNumberPayment, 1);
        assertEquals("APPROVED", statusAfterServer);
    }

    @Test
    @DisplayName("Latin Letters In Card Number Field")
    public void shouldErrorIfLatinLettersInCardNumberField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.latinNumber();
        errorFormat.shouldBe(Condition.visible);
        notDenialMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("seventeenDigitsNumberField")
    public void shouldErrorIfCyrillicLettersInCardNumberField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.seventeenDigitsNumber();
        errorFormat.shouldBe(Condition.visible);
        notDenialMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }
    @Test
    @DisplayName("randomNumber")
    public void shouldErrorIfCyrillicLettersInCardNumber() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.randomNumber();
        errorFormat.shouldBe(Condition.visible);
        notDenialMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("zeroNumber")
    public void shouldErrorIfZeroNumber() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.zeroNumber();
        errorFormat.shouldBe(Condition.visible);
        notDenialMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("oneDigitNumber")
    public void shouldErrorIfOneDigitNumber() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.oneDigitNumber();
        errorFormat.shouldBe(Condition.visible);
        notDenialMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("fifteenDigitsNumber")
    public void shouldErrorIfFifteenDigitsNumber() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.fifteenDigitsNumber();
        errorFormat.shouldBe(Condition.visible);
        notDenialMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Symbols In Card Number Field")
    public void shouldErrorIfSymbolsInCardNumberField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.symbolsNumber();
        errorFormat.shouldBe(Condition.visible);
        notDenialMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Empty Card Number Field")
    public void shouldErrorIfCardNumberFieldIsEmpty() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.emptyNumber();
        errorFormat.shouldBe(Condition.visible);
        notDenialMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Bank Rejection If Number Of Declined Card")
    public void shouldBeBankRejectionIfNumberOfDeclinedCard() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.declinedNumberCard();
        String statusAfterSendingDataToServer = statusAfterServer();
        denialMessage();
        checkNumberPayment(initialNumberPayment, 1);
        assertEquals("DECLINED", statusAfterSendingDataToServer);
    }

    @Test
    @DisplayName("Invalid Month Format")
    public void shouldErrorIfInvalidMonthFormat() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.oneDigitsNumberMonth();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Not Existed Month 13")
    public void shouldErrorIfNotExistedMonth13() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.thirteenthMonth();
        errorSpecifiedPeriodCard.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Not Existed Month 0")
    public void shouldErrorIfNotExistedMonth0() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.zeroMonth();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("oneDigitYear")
    public void shouldErrorIfOneDigitYear() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.oneDigitYear();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Latin Letters In Month Field")
    public void shouldErrorIfLatinLettersInMonthField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.latinLettersMonth();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }


    @Test
    @DisplayName("Symbols In Month Field")
    public void shouldErrorIfSymbolsInMonthField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.symbolsMonth();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Year More Than 5")
    public void shouldErrorIfYearMoreThan5() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.yearMoreThanFive();
        errorSpecifiedPeriodCard.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Past Year")
    public void shouldErrorIfPastYear() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.pastYear();
        errorPeriodCard.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Empty Year Field")
    public void shouldErrorIfEmptyYearField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.emptyYear();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Latin Letters In Year Field")
    public void shouldErrorIfLatinLettersInYearField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.latinLettersYear();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Cyrillic !!!! Year FieldZeroYear")
    public void shouldErrorIfCyrillicLettersInYearField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.zeroYear();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("NumberOwner")
    public void shouldErrorIfNumberOwner() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.numberOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Symbols In Year Field")
    public void shouldErrorIfSymbolsInYearField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.symbolsYear();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Cyrillic Letters In Owner Field")
    public void shouldErrorIfCyrillicLettersInOwnerField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.cyrillicLettersOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("symbolsOwner")
    public void shouldErrorIfsymbolsOwner() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.symbolsOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Symbols In Owner!!!!!!!!!!!!! Field")
    public void shouldErrorIfSymbolsInOwnerField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.quantitySymbolsOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Empty Owner Field")
    public void shouldErrorIfEmptyOwnerField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.emptyOwner();
        errorEmptyFieldOwner.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("One Word Owner")
    public void shouldErrorIfFiguresInOwnerField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.OneWordOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Empty CVC Field")
    public void shouldErrorIfEmptyCVCField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.emptyCVC();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("1 Figure In CVC Field")
    public void shouldErrorIf1FiguresInCVCField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.oneDigitCVC();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("2 Figures In CVC Field")
    public void shouldErrorIf2FiguresInCVCField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.twoDigitsCVC();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("fourDigitsCVC")
    public void shouldErrorIfСyrillicLettersInCVCField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.fourDigitsCVC();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Latin Letters In CVC Field")
    public void shouldErrorIfLatinLettersInCVCField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.latinLettersCVC();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }

    @Test
    @DisplayName("Symbols In CVC Field")
    public void shouldErrorIfSymbolsInCVCField() {
        long initialNumberPayment = numberFromPayment();
        var card = choicePaymentCard();
        card.symbolsCVC();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        checkNumberPayment(initialNumberPayment, 0);
    }
}
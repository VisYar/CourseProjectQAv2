package ru.netology.test.UI;

import static com.codeborne.selenide.Selenide.$;

import ru.netology.data.SQL;
import ru.netology.page.Main;
import ru.netology.page.PaymentCard;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.selenide.AllureSelenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;

import java.time.Duration;

public class UICardTest {

    public UICardTest test = new UICardTest();

    private SelenideElement errorSpecifiedPeriodCard = $(Selectors.withText("Неверно указан срок действия карты"));
    private SelenideElement errorPeriodCard = $(Selectors.withText("Истёк срок действия карты"));
    private SelenideElement errorEmptyFieldOwner = $(Selectors.withText("Поле обязательно для заполнения"));
    private SelenideElement errorEmptyFieldNumber = $(Selectors.withText("Поле обязательно для заполнения"));
    private SelenideElement errorEmptyFieldMonth = $(Selectors.withText("Поле обязательно для заполнения"));
    private SelenideElement errorEmptyFieldYear = $(Selectors.withText("Поле обязательно для заполнения"));
    private SelenideElement errorEmptyFieldCVC = $(Selectors.withText("Поле обязательно для заполнения"));
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

    @AfterEach
    void clear() {
        SQL.clear();
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

    PaymentCard choicePaymentCard() {
        Main page = new Main();
        return page.clickButtonPay();
    }

    public void payApprovedStatus() {
        assertEquals("APPROVED", SQL.getStatusPayment());
    }

    public void payDeclinedStatus() {
        assertEquals("DECLINED", SQL.getStatusPayment());
    }

    public void payAcceptCount() {
        assertEquals(1, SQL.getPaymentCount());
    }

    public void payDenialCount() {
        assertEquals(0, SQL.getPaymentCount());
    }

    public void orderAcceptCount() {
        assertEquals(1, SQL.getOrderCount());
    }

    public void orderDenialCount() {
        assertEquals(0, SQL.getOrderCount());
    }

    @Test
    @DisplayName("Payment approved card")
    public void shouldSuccessfulPaymentApprovedCard() {
        var card = choicePaymentCard();
        card.approvedNumberCard();
        positiveMessage();
        Assertions.assertAll(
                test::payApprovedStatus,
                test::payAcceptCount,
                test::orderAcceptCount
        );
    }

    @Test
    @DisplayName("Payment declined card")
    public void shouldUnsuccessfulPaymentDeclinedCard() {
        var card = choicePaymentCard();
        card.declinedNumberCard();
        denialMessage();
        Assertions.assertAll(
                test::payDeclinedStatus,
                test::payAcceptCount,
                test::orderAcceptCount);
    }

    @Test
    @DisplayName("Empty card number")
    public void shouldErrorEmptyNumber() {
        var card = choicePaymentCard();
        card.emptyNumber();
        errorEmptyFieldNumber.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Random card number")
    public void shouldErrorRandomNumber() {
        var card = choicePaymentCard();
        card.randomNumber();
        denialMessage();
        Assertions.assertAll(
                test::payDeclinedStatus,
                test::payAcceptCount,
                test::orderAcceptCount);
    }

    @Test
    @DisplayName("Zero card number")
    public void shouldErrorZeroNumber() {
        var card = choicePaymentCard();
        card.zeroNumber();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("One digit card number")
    public void shouldErrorOneDigitNumber() {
        var card = choicePaymentCard();
        card.oneDigitNumber();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Fifteen digits card number")
    public void shouldErrorFifteenDigitsNumber() {
        var card = choicePaymentCard();
        card.fifteenDigitsNumber();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Empty month")
    public void shouldErrorEmptyMonth() {
        var card = choicePaymentCard();
        card.emptyMonth();
        errorEmptyFieldMonth.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("One digits number month")
    public void shouldErrorIfInvalidMonthFormat() {
        var card = choicePaymentCard();
        card.oneDigitsNumberMonth();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Thirteenth month")
    public void shouldErrorIfNotExistedMonth13() {
        var card = choicePaymentCard();
        card.thirteenthMonth();
        errorSpecifiedPeriodCard.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Zero month")
    public void shouldErrorZeroMonth() {
        var card = choicePaymentCard();
        card.zeroMonth();
        errorSpecifiedPeriodCard.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Empty year field")
    public void shouldErrorEmptyYear() {
        var card = choicePaymentCard();
        card.emptyYear();
        errorEmptyFieldYear.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("One digit year")
    public void shouldErrorOneDigitYear() {
        var card = choicePaymentCard();
        card.oneDigitYear();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Year more than five")
    public void shouldErrorIfYearMoreThan5() {
        var card = choicePaymentCard();
        card.yearMoreThanFive();
        errorSpecifiedPeriodCard.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Past year")
    public void shouldErrorPastYear() {
        var card = choicePaymentCard();
        card.pastYear();
        errorPeriodCard.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Zero year")
    public void shouldErrorZeroYear() {
        var card = choicePaymentCard();
        card.zeroYear();
        errorPeriodCard.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Empty owner")
    public void shouldErrorIfEmptyOwnerField() {
        var card = choicePaymentCard();
        card.emptyOwner();
        errorEmptyFieldOwner.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Cyrillic letters owner")
    public void shouldErrorIfCyrillicLettersInOwnerField() {
        var card = choicePaymentCard();
        card.cyrillicLettersOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Symbols owner")
    public void shouldErrorIfsymbolsOwner() {
        var card = choicePaymentCard();
        card.symbolsOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Number owner")
    public void shouldErrorNumberOwner() {
        var card = choicePaymentCard();
        card.numberOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Quantity symbols owner")
    public void shouldErrorSymbolsOwner() {
        var card = choicePaymentCard();
        card.quantitySymbolsOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("One word owner")
    public void shouldErrorOneWordOwner() {
        var card = choicePaymentCard();
        card.OneWordOwner();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Empty CVC")
    public void shouldErrorIfEmptyCVCField() {
        var card = choicePaymentCard();
        card.emptyCVC();
        errorEmptyFieldCVC.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("One digit CVC")
    public void shouldErrorOneDigitCVC() {
        var card = choicePaymentCard();
        card.oneDigitCVC();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }

    @Test
    @DisplayName("Two digit CVC")
    public void shouldErrorTwoDigitsCVC() {
        var card = choicePaymentCard();
        card.twoDigitsCVC();
        errorFormat.shouldBe(Condition.visible);
        notPositiveMessage();
        notDenialMessage();
        Assertions.assertAll(
                test::payDenialCount,
                test::orderDenialCount);
    }
}
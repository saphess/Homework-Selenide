package ru.netology.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SendFormTest {
    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    @BeforeAll
    static void setUp() {
        Configuration.browser = "chrome";
    }

    @BeforeEach
    void open() {
        Selenide.open("http://localhost:9999");
    }

    @Test
    void shouldSendFormWithDateOf5Days() {
        String date = generateDate(5, "dd.MM.yyyy");

        $("[data-test-id='city'] .input__control").setValue("Тула");
        $("[data-test-id='date'] .input__control").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME),
                Keys.BACK_SPACE);
        $("[data-test-id='date'] .input__control").setValue(date);
        $("[data-test-id='name'] .input__control").setValue("Илья Нефедов");
        $("[data-test-id='phone'] .input__control").setValue("+79505005050");
        $("[data-test-id='agreement']").click();

        $$("button").findBy(Condition.text("Забронировать")).click();

        $("[data-test-id='notification']").shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Встреча успешно забронирована на " + date),
                        Condition.text("Успешно!"));
    }

    @Test
    void shouldSendFormViaPopUpWindow() {
        int days = 7; // Количество дней вперед
        String date = generateDate(days, "dd.MM.yyyy");

        $("[data-test-id='city'] .input__control").setValue("Ту");
        $(".menu").$(Selectors.byText("Тула")).click();

        $("[data-test-id='date'] .input__control").click();
        String day = String.valueOf(LocalDate.now().plusDays(days).getDayOfMonth());
        if (LocalDate.now().plusDays(days).getMonth() != LocalDate.now().getMonth()) {
            $("[data-step='1']").click();
        }
        $$(".calendar__day").findBy(Condition.text(day)).click();

        $("[data-test-id='name'] .input__control").setValue("Ольга Петросян");
        $("[data-test-id='phone'] .input__control").setValue("+79505005050");
        $("[data-test-id='agreement']").click();

        $$("button").findBy(Condition.text("Забронировать")).click();

        $("[data-test-id='notification']").shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Встреча успешно забронирована на " + date),
                        Condition.text("Успешно!"));
    }
}

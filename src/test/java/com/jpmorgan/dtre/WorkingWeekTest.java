package com.jpmorgan.dtre;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for the WorkingWeek
 * @author Peter D Bell, 4rd May 2017
 */
public class WorkingWeekTest {

    /**
     * Check what happens for special case currencies
     */
    @Test
    public void checkCurrencySpecialCases() {
        assertThat(WorkingWeek.getAdjustedCurrencies(), not(empty()));

        Arrays.stream(ISO4217.Currency.values())
                .filter(c -> WorkingWeek.getAdjustedCurrencies().contains(c))
                .forEach(c -> {
                    assertThat(WorkingWeek.isAdjustedCurrency(c), is(true));
                    assertThat(WorkingWeek.getFirstDayOfWeek(c), is(DayOfWeek.SUNDAY));
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-01")), is(false)); // Monday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-02")), is(false)); // Tuesday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-03")), is(false)); // Wednesday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-04")), is(false)); // Thursday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-05")), is(true)); // Friday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-06")), is(true)); // Saturday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-07")), is(false)); // Sunday
                    // check adjustment
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-01")), is(LocalDate.parse("2017-05-01")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-02")), is(LocalDate.parse("2017-05-02")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-03")), is(LocalDate.parse("2017-05-03")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-04")), is(LocalDate.parse("2017-05-04")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-05")), is(LocalDate.parse("2017-05-07")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-06")), is(LocalDate.parse("2017-05-07")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-07")), is(LocalDate.parse("2017-05-07")));
                });
    }

    /**
     * Check what happens for regular currencies
     */
    @Test
    public void checkCurrencyDefaultCases() {
        assertThat(WorkingWeek.getAdjustedCurrencies(), not(empty()));

        Arrays.stream(ISO4217.Currency.values())
                .filter(c -> !(WorkingWeek.getAdjustedCurrencies().contains(c)))
                .forEach(c -> {
                    assertThat(WorkingWeek.isAdjustedCurrency(c), is(false));
                    assertThat(WorkingWeek.getFirstDayOfWeek(c), is(DayOfWeek.MONDAY));
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-01")), is(false)); // Monday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-02")), is(false)); // Tuesday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-03")), is(false)); // Wednesday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-04")), is(false)); // Thursday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-05")), is(false)); // Friday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-06")), is(true)); // Saturday
                    assertThat(WorkingWeek.isWeekendByCurrency(c, LocalDate.parse("2017-05-07")), is(true)); // Sunday
                    // check adjustment
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-01")), is(LocalDate.parse("2017-05-01")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-02")), is(LocalDate.parse("2017-05-02")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-03")), is(LocalDate.parse("2017-05-03")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-04")), is(LocalDate.parse("2017-05-04")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-05")), is(LocalDate.parse("2017-05-05")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-06")), is(LocalDate.parse("2017-05-08")));
                    assertThat(WorkingWeek.adjustForWeekendByCurrency(c, LocalDate.parse("2017-05-07")), is(LocalDate.parse("2017-05-08")));
                });
    }
}
package com.jpmorgan.dtre;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for ISO4217 (Currency) class and enum definition
 * @author Peter D Bell, 4rd May 2017
 */
public class ISO4217Test {

    /**
     * Check any currency with a numeric code less than zero has an accurate string representation of that code
     * with leading zeroes
     */
    @Test
    public void numericCodesLessThan100() {
        Arrays.stream(ISO4217.Currency.values())
                .filter(c -> c.numericCode < 100)
                .forEach(c -> {
                    assertThat(c.numericCode, lessThan(100));
                    assertThat(c.getNumericCode3Digits(), startsWith("0"));
                    if (c.numericCode < 10) {
                        assertThat(c.getNumericCode3Digits(), startsWith("00"));
                    }
                });
    }

    /**
     * Check US currencies are the main USD and the special USN (next day)
     */
    @Test
    public void theUSDollar() {
        final Set<ISO4217.Currency> us = Arrays.stream(ISO4217.Currency.values())
                .filter(c -> c.name().startsWith("US"))
                .collect(Collectors.toSet());
        assertThat(us, hasItems(ISO4217.Currency.USD, ISO4217.Currency.USN));
    }

    /**
     * Our logic depends on AED, let's be sure this is still a valid currency
     */
    @Test
    public void currencyAED() {
        // two tests in one: valueOf will throw IllegalArgumentException if not value and fail the test
        assertThat(ISO4217.Currency.valueOf("AED").numericCode, is(784));
    }

    /**
     * Our logic depends on SAR, let's be sure this is still a valid currency
     */
    @Test
    public void currencySAR() {
        // two tests in one: valueOf will throw IllegalArgumentException if not value and fail the test
        assertThat(ISO4217.Currency.valueOf("SAR").numericCode, is(682));
    }

    /**
     * One of 3 ways of checking an exception occurred: (we only need one, 4 used here to demonstrate)
     * 1. expected annotation
     */
    @Test(expected = IllegalArgumentException.class)
    public void invalidCurrency_ExpectedAnnotation() {
        ISO4217.Currency.valueOf("PDB");
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * One of 3 ways of checking an exception occurred:
     * 2. expectedexception rule
     */
    @Test
    public void invalidCurrency_ExpectedExceptionRule() {
        thrown.expect(IllegalArgumentException.class);
        ISO4217.Currency.valueOf("123");
    }

    /**
     * One of 3 ways of checking an exception occurred:
     * 3. junit 3 catch with no error
     */
    @Test
    public void invalidCurrency_Junit3TryCatch() {
        try {
            ISO4217.Currency.valueOf("#']");
            fail("Invalid currency did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ignore = success
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCurrencyTooShort0() {
        ISO4217.Currency.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCurrencyTooShort1() {
        ISO4217.Currency.valueOf("X");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCurrencyTooShort2() {
        ISO4217.Currency.valueOf("AB");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCurrencyTooLong() {
        ISO4217.Currency.valueOf("FOUR");
    }

    /**
     * Show that the sample data currency name 'SGP' supplied is not valid
     */
    @Test(expected = IllegalArgumentException.class)
    public void invalidCurrencySampleData() {
        ISO4217.Currency.valueOf("SGP");
    }
}
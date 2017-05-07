/**
 * TODO: Company copyright/licence notice
 */
package com.jpmorgan.dtre;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for Daily Trade Reporting Engine
 *
 * @author Peter D Bell, 4rd May 2017
 */
public class DailyTradesTest {

    private final NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);

    @Test
    public void initalization() {
        final DailyTrades dt = new DailyTrades();
        assertThat(dt.dataRowsLoaded(), greaterThan(0));
    }

    @Test
    public void aggregateSumPerDay_NoAggregate() {
        final DailyTrades dt = new DailyTrades();
        final Set<ImmutableInstruction> orders = new HashSet<>();

        final ImmutableInstruction foo = new ImmutableInstruction(
                DataSource.ENTITIES.foo,
                ImmutableInstruction.BUYSELL.B,
                new BigDecimal("0.50"),
                ISO4217.Currency.SGD,
                LocalDate.parse("2016-01-01"),
                LocalDate.parse("2016-01-02"),
                200,
                DataSource.ENTITIES.foo.getLastValueUSD()
        );

        final ImmutableInstruction bar = new ImmutableInstruction(
                DataSource.ENTITIES.bar,
                ImmutableInstruction.BUYSELL.S,
                new BigDecimal("0.22"),
                ISO4217.Currency.AED,
                LocalDate.parse("2016-01-05"),
                LocalDate.parse("2016-01-07"),
                450,
                DataSource.ENTITIES.bar.getLastValueUSD()
        );

        orders.add(foo);
        orders.add(bar);

        final Map<LocalDate, BigDecimal> agg = dt.aggregateSumPerDay(orders);
        assertThat(agg.size(), equalTo(2));
        assertThat(nf.format(agg.get(LocalDate.parse("2016-01-04"))), equalTo(nf.format(foo.getAmountOfTradeUSD())));
        assertThat(nf.format(agg.get(LocalDate.parse("2016-01-07"))), equalTo(nf.format(bar.getAmountOfTradeUSD())));
        assertThat(nf.format(agg.get(LocalDate.parse("2016-01-04"))), equalTo(nf.format(new BigDecimal("10025.00"))));
        assertThat(nf.format(agg.get(LocalDate.parse("2016-01-07"))), equalTo(nf.format(new BigDecimal("14899.50"))));
    }

    @Test
    public void aggregateSumPerDay_SimpleUnadjusted() {
        final DailyTrades dt = new DailyTrades();
        final Set<ImmutableInstruction> orders = new HashSet<>();

        final ImmutableInstruction foo = new ImmutableInstruction(
                DataSource.ENTITIES.foo,
                ImmutableInstruction.BUYSELL.B,
                new BigDecimal("0.50"),
                ISO4217.Currency.SGD,
                LocalDate.parse("2016-01-01"),
                LocalDate.parse("2016-01-04"),
                200,
                DataSource.ENTITIES.foo.getLastValueUSD()
        );

        final ImmutableInstruction bar = new ImmutableInstruction(
                DataSource.ENTITIES.bar,
                ImmutableInstruction.BUYSELL.S,
                new BigDecimal("0.22"),
                ISO4217.Currency.GBP,
                LocalDate.parse("2016-01-02"),
                LocalDate.parse("2016-01-04"),
                450,
                DataSource.ENTITIES.bar.getLastValueUSD()
        );

        orders.add(foo);
        orders.add(bar);

        final Map<LocalDate, BigDecimal> agg = dt.aggregateSumPerDay(orders);
        assertThat(agg.size(), equalTo(1));
        assertThat(nf.format(agg.get(LocalDate.parse("2016-01-04"))), equalTo(nf.format(foo.getAmountOfTradeUSD().add(bar.getAmountOfTradeUSD()))));
        assertThat(nf.format(agg.get(LocalDate.parse("2016-01-04"))), equalTo(nf.format(new BigDecimal("24924.50"))));
    }

    @Test
    public void aggregateSumPerDay_OneAdjustedSettlementDateCombines() {
        final DailyTrades dt = new DailyTrades();
        final Set<ImmutableInstruction> orders = new HashSet<>();

        final ImmutableInstruction foo = new ImmutableInstruction(
                DataSource.ENTITIES.foo,
                ImmutableInstruction.BUYSELL.B,
                new BigDecimal("0.50"),
                ISO4217.Currency.SGD,
                LocalDate.parse("2016-01-01"),
                LocalDate.parse("2016-01-03"),
                200,
                DataSource.ENTITIES.foo.getLastValueUSD()
        );

        final ImmutableInstruction bar = new ImmutableInstruction(
                DataSource.ENTITIES.bar,
                ImmutableInstruction.BUYSELL.S,
                new BigDecimal("0.22"),
                ISO4217.Currency.AED,
                LocalDate.parse("2016-01-02"),
                LocalDate.parse("2016-01-04"),
                450,
                DataSource.ENTITIES.bar.getLastValueUSD()
        );

        orders.add(foo);
        orders.add(bar);

        final Map<LocalDate, BigDecimal> agg = dt.aggregateSumPerDay(orders);
        assertThat(agg.size(), equalTo(1));
        assertThat(nf.format(agg.get(LocalDate.parse("2016-01-04"))), equalTo(nf.format(foo.getAmountOfTradeUSD().add(bar.getAmountOfTradeUSD()))));
        assertThat(nf.format(agg.get(LocalDate.parse("2016-01-04"))), equalTo(nf.format(new BigDecimal("24924.50"))));
    }
}
package com.jpmorgan.dtre;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jpmorgan.dtre.ImmutableInstruction.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.time.temporal.TemporalAdjusters.*;
/**
 * @author Peter D Bell, 5rd May 2017
 */
public class ImmutableInstructionTest {

    @Test
    public void buySellRandom() {
        assertThat(Arrays.asList(BUYSELL.values()), hasItem(BUYSELL.randomBuySell()));
    }

    @Test
    public void calculatedUSDAmountOfTrade() {
        final DataSource ds = DataSource.getInstance();
        assertThat(ds.getRows(), not(empty()));

        final DecimalFormat df = new DecimalFormat("#.##");
        ds.getRows()
                .stream()
                .forEach(r -> {
                    // redo the calculation we expect
                    final BigDecimal usd = r.getPricePerUnit().multiply(new BigDecimal(r.getUnits())).multiply(r.getAgreedFx());
                    assertThat(r.getAmountOfTradeUSD(), equalTo(usd));
                    // show the difference is BigDecimal, double and long (in cents)
                    System.out.println("["+r.getPricePerUnit()+" * "+r.getUnits()+" * "+r.getAgreedFx()+
                            "] = "+r.getAmountOfTradeUSD().setScale(2, RoundingMode.HALF_EVEN)+ " ("+r.getAmountOfTradeUSD()+") "+
                            " => "+df.format(r.getAmountOfTradeUSDDoubleValue())+" ("+r.getAmountOfTradeUSDDoubleValue()+") "+
                            " => "+df.format(r.getAmountOfTradeUSDCentsLongValue()/100.0d)+" ("+r.getAmountOfTradeUSDCentsLongValue()+") ");
                });
    }

    @Test
    public void settlementDate() {
        final DataSource ds = DataSource.getInstance();

        ds.getRows()
                .stream()
                .forEach(r -> {
                    System.out.print((WorkingWeek.isAdjustedCurrency(r.getCurrency()) ? "* " : "")
                            + r.getCurrency().name()+" => "+r.getOriginalUnadjustedSettlementDate()+" ("+r.getOriginalUnadjustedSettlementDate().getDayOfWeek()+") => ");

                    if (WorkingWeek.isAdjustedCurrency(r.getCurrency())) {

                        if (WorkingWeek.isWeekendByCurrency(r.getCurrency(), r.getOriginalUnadjustedSettlementDate())) {

                            System.out.println("ROLLED "+r.getOriginalUnadjustedSettlementDate().with(nextOrSame(WorkingWeek.getFirstDayOfWeek(r.getCurrency()))));

                            assertNotSame(r.getSettlementDate(), r.getOriginalUnadjustedSettlementDate());
                            assertNotEquals(r.getSettlementDate(), r.getOriginalUnadjustedSettlementDate());
                            assertThat(r.getSettlementDate(), is(r.getOriginalUnadjustedSettlementDate().with(nextOrSame(WorkingWeek.getFirstDayOfWeek(r.getCurrency())))));
                        } else {

                            System.out.println("NO-OP " + r.getSettlementDate() + " " + r.getOriginalUnadjustedSettlementDate());

                            assertSame(r.getSettlementDate(), r.getOriginalUnadjustedSettlementDate());
                            assertEquals(r.getSettlementDate(), r.getOriginalUnadjustedSettlementDate());
                            assertThat(r.getSettlementDate(), is(r.getOriginalUnadjustedSettlementDate()));
                        }
                    } else {
                        if (WorkingWeek.isWeekendByCurrency(r.getCurrency(), r.getOriginalUnadjustedSettlementDate())) {

                            System.out.println("ROLLED " +r.getOriginalUnadjustedSettlementDate().with(nextOrSame(DayOfWeek.MONDAY)));

                            assertNotSame(r.getSettlementDate(), r.getOriginalUnadjustedSettlementDate());
                            assertNotEquals(r.getSettlementDate(), r.getOriginalUnadjustedSettlementDate());
                            assertThat(r.getSettlementDate(), is(r.getOriginalUnadjustedSettlementDate().with(nextOrSame(DayOfWeek.MONDAY))));
                        } else {

                            System.out.println("NO-OP "+r.getSettlementDate()+" "+r.getOriginalUnadjustedSettlementDate());

                            assertSame(r.getSettlementDate(), r.getOriginalUnadjustedSettlementDate());
                            assertEquals(r.getSettlementDate(), r.getOriginalUnadjustedSettlementDate());
                            assertThat(r.getSettlementDate(), is(r.getOriginalUnadjustedSettlementDate()));
                        }
                    }
                });
    }
}
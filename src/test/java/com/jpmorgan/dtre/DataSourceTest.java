package com.jpmorgan.dtre;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


/**
 * @author Peter D Bell, 5th May 2017
 */
public class DataSourceTest {

    @Test
    public void getInstance() {
        final DataSource ds = DataSource.getInstance();
        assertSame(ds, DataSource.getInstance());
    }

    @Test
    public void getRows() {
        final DataSource ds = DataSource.getInstance();
        assertThat(ds.getRows(), not(empty()));
        ds.getRows()
                .stream()
                .forEach(r -> {
            System.out.println(r.toString()); // information only during debugging, remove
        });
    }

    @Test
    public void buySellRandom() {
        assertThat(Arrays.asList(DataSource.ENTITIES.values()), hasItem(DataSource.ENTITIES.randomEntity()));
    }

    @Test
    public void generatedSettlementDateOnOrAfterInstructionDate() {
        final DataSource ds = DataSource.getInstance();
        assertThat(ds.getRows(), not(empty()));
        ds.getRows()
                .stream()
                .forEach(r -> {
            assertTrue(r.getSettlementDate().isAfter(r.getInstructionDate()) || r.getSettlementDate().isEqual(r.getInstructionDate()));
        });
    }

    @Test
    public void fields() {
        final DataSource ds = DataSource.getInstance();
        assertThat(ds.getRows(), not(empty()));
        ds.getRows()
                .stream()
                .forEach(r -> {
            assertThat(Arrays.asList(DataSource.ENTITIES.values()), hasItem(r.getEntity()));
            assertThat(Arrays.asList(ImmutableInstruction.BUYSELL.values()), hasItem(r.getBuySell()));

            // question over whether compareTo or signum is 'better' or more readable here
            assertThat(r.getAgreedFx().compareTo(BigDecimal.ZERO), greaterThan(0)); // same as signum below
            //assertThat(r.getAgreedFx().signum(), greaterThan(0)); // same as compareTo above

            assertThat(Arrays.asList(ISO4217.Currency.values()), hasItem(r.getCurrency())); // costly as .values() creates new array each time

            assertThat(r.getInstructionDate(), not(nullValue()));
            assertTrue(r.getSettlementDate().isAfter(r.getInstructionDate()) || r.getSettlementDate().isEqual(r.getInstructionDate()));

            assertThat(r.getUnits(), greaterThan(0));

            assertThat(r.getPricePerUnit().compareTo(BigDecimal.ZERO), greaterThan(0));
        });
    }
}
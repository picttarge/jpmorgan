package com.jpmorgan.dtre;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Random;

import static com.jpmorgan.dtre.DataSource.*;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

/**
 * @author Peter D Bell, 5rd May 2017
 */
public final class ImmutableInstruction {

    enum BUYSELL {
        B("Buy"),
        S("Sell");

        // As we'll be asking for .values() a lot and this creates an array each time, cache this
        private final static BUYSELL[] VALUES = values();
        private final static Random random = new Random();

        public final String description;
        BUYSELL(String description) {
            this.description = description;
        }

        public static BUYSELL randomBuySell() {
            return VALUES[(random.nextInt(VALUES.length))];
        }
    }

    private final ENTITIES entity;
    private final BUYSELL buySell;
    private final BigDecimal agreedFx;
    private final ISO4217.Currency currency;
    private final LocalDate instructionDate;
    private final LocalDate settlementDate;
    private final LocalDate originalUnadjustedSettlementDate;
    private final int units; // TODO: assumption only (2^31)-1 units would be max bought/sold in any one instruction
    private final BigDecimal pricePerUnit;

    private final BigDecimal amountOfTradeUSD;
    private final double amountOfTradeUSDDoubleValue;
    private final long amountOfTradeUSDCentsLongValue;

    public ImmutableInstruction(ENTITIES entity, BUYSELL buySell, BigDecimal agreedFx, ISO4217.Currency currency,
                                LocalDate instructionDate, LocalDate settlementDate, int units, BigDecimal pricePerUnit) {
        this.entity = entity;
        this.buySell = buySell;
        this.agreedFx = agreedFx;
        this.currency = currency;
        this.instructionDate = instructionDate;

        this.originalUnadjustedSettlementDate = settlementDate; // retain for audit

        // Adjust the settlement date here, as the record is created
        // so that we don't forget to do it (at some other point)
        // or end up doing it more than once.
        // Also note we don't do this: WeekFields.of(new Locale("ar", "AE")).getFirstDayOfWeek()
        // as Java's ar_AE says the first day of the week is SATURDAY.
        // So, we override Locale-specific preferences, per the requirements:
        this.settlementDate = WorkingWeek.adjustForWeekendByCurrency(currency, settlementDate);

        this.units = units;
        this.pricePerUnit = pricePerUnit;
        this.amountOfTradeUSD = pricePerUnit.multiply(new BigDecimal(units)).multiply(agreedFx);
        this.amountOfTradeUSDDoubleValue = amountOfTradeUSD.doubleValue(); // possible precision loss
        this.amountOfTradeUSDCentsLongValue = amountOfTradeUSD.multiply(BigDecimal.valueOf(100)).longValue(); // possible truncation
    }

    public ENTITIES getEntity() {
        return entity;
    }

    public BUYSELL getBuySell() {
        return buySell;
    }

    public BigDecimal getAgreedFx() {
        return agreedFx;
    }

    public ISO4217.Currency getCurrency() {
        return currency;
    }

    public LocalDate getInstructionDate() {
        return instructionDate;
    }

    /**
     * Gets the adjusted, actual date the settlement took place,
     * having taking into account any currency-dependent
     * first-day-of-the-week adjustments.
     * @return LocalDate Adjusted, actual date of the settlement
     */
    public LocalDate getSettlementDate() {
        return settlementDate;
    }


    /**
     * Gets the original, unadjusted settlement date with a long method name
     * that should make any future programmers think twice about using it.
     * We want to allow an audit on the original to take place, but we don't
     * want any confusion over what's "The Right Way" to ask for the accurate
     * settlement date
     * @return LocalDate original settlement date (before adjustment) for audit/testing
     */
    public LocalDate getOriginalUnadjustedSettlementDate() {
        return originalUnadjustedSettlementDate;
    }

    public int getUnits() {
        return units;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    /**
     * The accurate representation in BigDecimal form of:
     *   USD amount of a trade = Price per unit * Units * Agreed Fx
     * @return BigDecimal USD amount of a trade
     */
    public BigDecimal getAmountOfTradeUSD() {
        return amountOfTradeUSD;
    }

    /**
     * The representation in double form of:
     *   USD amount of a trade = Price per unit * Units * Agreed Fx
     * @return double amount of a trade in USD
     */
    public double getAmountOfTradeUSDDoubleValue() {
        return amountOfTradeUSDDoubleValue;
    }

    /**
     * The representation in long form of:
     *   USD cents amount of a trade = 100 * (Price per unit * Units * Agreed Fx)
     * @return long amount of a trade in USD cents
     */
    public long getAmountOfTradeUSDCentsLongValue() {
        return amountOfTradeUSDCentsLongValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableInstruction that = (ImmutableInstruction) o;

        if (units != that.units) return false;
        if (entity != that.entity) return false;
        if (buySell != that.buySell) return false;
        if (!agreedFx.equals(that.agreedFx)) return false;
        if (currency != that.currency) return false;
        if (!instructionDate.equals(that.instructionDate)) return false;
        if (!originalUnadjustedSettlementDate.equals(that.originalUnadjustedSettlementDate)) return false;
        if (!settlementDate.equals(that.settlementDate)) return false;
        return pricePerUnit.equals(that.pricePerUnit);
    }

    @Override
    public int hashCode() {
        int result = entity.hashCode();
        result = 31 * result + buySell.hashCode();
        result = 31 * result + agreedFx.hashCode();
        result = 31 * result + currency.hashCode();
        result = 31 * result + instructionDate.hashCode();
        result = 31 * result + originalUnadjustedSettlementDate.hashCode();
        result = 31 * result + settlementDate.hashCode();
        result = 31 * result + units;
        result = 31 * result + pricePerUnit.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ImmutableInstruction{" +
                "entity='" + entity + '\'' +
                ", buySell=" + buySell +
                ", agreedFx=" + agreedFx +
                ", currency=" + currency +
                ", instructionDate=" + instructionDate +
                ", settlementDate=" + originalUnadjustedSettlementDate +
                ", actualSettlementDate=" + settlementDate +
                ", units=" + units +
                ", pricePerUnit=" + pricePerUnit +
                '}';
    }
}

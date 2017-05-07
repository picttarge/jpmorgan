package com.jpmorgan.dtre;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import static java.time.temporal.TemporalAdjusters.nextOrSame;

/**
 * A work week starts Monday and ends Friday, unless the currency of the trade is AED or SAR, where
 * the work week starts Sunday and ends Thursday. No other holidays to be taken into account.
 *
 * @author Peter D Bell, 5th May 2017
 */
public final class WorkingWeek {

    /**
     * Weekend (Holiday) definition for Monday to Friday, inclusive
     */
    private final static Set<DayOfWeek> WEEKEND_FOR_MON_FRI = new HashSet<>();

    /**
     * Weekend (Holiday) definition for Sunday to Thursday, inclusive
     */
    private final static Set<DayOfWeek> WEEKEND_FOR_SUN_THU = new HashSet<>();

    private final static Map<ISO4217.Currency, DayOfWeek> ADJUSTMENTBUREAU = new HashMap<>();

    static {
        // what are the weekends in a Mon-Fri working week?
        WEEKEND_FOR_MON_FRI.add(DayOfWeek.SATURDAY);
        WEEKEND_FOR_MON_FRI.add(DayOfWeek.SUNDAY);

        // what are the weekends in a Sun-Thu working week?
        WEEKEND_FOR_SUN_THU.add(DayOfWeek.FRIDAY);
        WEEKEND_FOR_SUN_THU.add(DayOfWeek.SATURDAY);

        // adjustments needed for the following currencies
        ADJUSTMENTBUREAU.put(ISO4217.Currency.AED, DayOfWeek.SUNDAY);
        ADJUSTMENTBUREAU.put(ISO4217.Currency.SAR, DayOfWeek.SUNDAY);
    }

    /**
     * All we really need to do is determine if, for the supplied currency, the supplied date falls on a weekend
     * @param currency Currency to check against - it matters when the host country does business
     * @param date LocalDate to check the day of the week
     * @return boolean true if this is a weekend, false if it's the working business week, for this currency
     */
    public static LocalDate adjustForWeekendByCurrency(ISO4217.Currency currency, LocalDate date) {

        if (isWeekendByCurrency(currency, date)) {
            return ADJUSTMENTBUREAU.containsKey(currency)
                    ? date.with(nextOrSame(ADJUSTMENTBUREAU.get(currency))) // adjust according to currency
                    : date.with(nextOrSame(DayOfWeek.MONDAY)); // or default
        } else {
            return date; // no change
        }
    }

    static boolean isWeekendByCurrency(ISO4217.Currency currency, LocalDate date) {

        if (ADJUSTMENTBUREAU.containsKey(currency)) {
            // TODO: consider future proofing where adjustment currency maps to a Pair<DayOfWeek,Set<DayOfWeek>> (start to weekends)
            return WEEKEND_FOR_SUN_THU.contains(date.getDayOfWeek());
        } else {
            return WEEKEND_FOR_MON_FRI.contains(date.getDayOfWeek());
        }
    }

    static boolean isAdjustedCurrency(ISO4217.Currency currency) {
        return ADJUSTMENTBUREAU.containsKey(currency);
    }

    static DayOfWeek getFirstDayOfWeek(ISO4217.Currency currency) {
        return ADJUSTMENTBUREAU.get(currency);
    }
}
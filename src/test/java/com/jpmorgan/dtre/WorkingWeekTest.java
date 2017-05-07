package com.jpmorgan.dtre;

import org.junit.Test;

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
     * Ensure that settlement dates get adjusted onto the next business day
     */
    @Test
    public void adjustedSettlementDates() {

    }

}
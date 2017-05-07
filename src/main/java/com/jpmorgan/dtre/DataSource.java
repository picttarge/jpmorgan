package com.jpmorgan.dtre;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Invented sample data source (replace with DB or otherwise)
 * @author Peter D Bell, 4rd May 2017
 */
public class DataSource {
    private final static DataSource data = new DataSource();

    private final List<ImmutableInstruction> rows;

    enum ENTITIES { // this is a sample of first 20 from NASDAQ
        PIH	 ("1347 Property Insurance Holdings, Inc.", new BigDecimal("7.1")),
        TURN ("180 Degree Capital Corp.",               new BigDecimal("1.57")),
        FLWS ("1-800 FLOWERS.COM, Inc.",                new BigDecimal("10.15")),
        FCCY ("1st Constitution Bancorp (NJ)",          new BigDecimal("17.5")),
        SRCE ("1st Source Corporation",                 new BigDecimal("48.71")),
        VNET ("21Vianet Group, Inc.",                   new BigDecimal("5.27")),
        TWOU ("2U, Inc.",                               new BigDecimal("44.38")),
        JOBS ("51job, Inc.",                            new BigDecimal("41.15")),
        CAFD ("8point3 Energy Partners LP",             new BigDecimal("12.5")),
        EGHT ("8x8 Inc",                                new BigDecimal("13.2")),
        AVHI ("A V Homes, Inc.",                        new BigDecimal("17.15")),
        SHLM ("A. Schulman, Inc.",                      new BigDecimal("30.95")),
        AAON ("AAON, Inc.",                             new BigDecimal("37.25")),
        ABAX ("ABAXIS, Inc.",                           new BigDecimal("47.4")),
        ABEO ("Abeona Therapeutics Inc.",               new BigDecimal("5.65")),
        ABEOW ("Abeona Therapeutics Inc.",              new BigDecimal("2.77")),
        ABIL ("Ability Inc.",                           new BigDecimal("0.7679")),
        ABMD ("ABIOMED, Inc.",                          new BigDecimal("130.81")),
        AXAS ("Abraxas Petroleum Corporation",          new BigDecimal("1.7")),
        ACIU ("AC Immune SA",                           new BigDecimal("9.1")),
        foo  ("foo (sample data)",                      new BigDecimal("100.25")),
        bar  ("bar (sample data)",                      new BigDecimal("150.5"));

        // As we'll be asking for .values() a lot and this creates an array each time, cache this
        private final static ENTITIES[] VALUES = values();
        private final static Random random = new Random();

        private final String entityName;
        private final BigDecimal lastValueUSD;
        ENTITIES (String entityName, BigDecimal lastValueUSD) {
            this.entityName = entityName;
            this.lastValueUSD  = lastValueUSD;
        }

        public String getEntityName() {
            return entityName;
        }

        public BigDecimal getLastValueUSD() {
            return lastValueUSD;
        }

        public static ENTITIES randomEntity() {
            return VALUES[(random.nextInt(VALUES.length))];
        }
    }

    private DataSource() {
        System.out.println("Generating DataSource...");
        rows = new ArrayList<>();

        // sample data says "SGP" but that's Singapore's 3-letter ISO country code, not currency code.
        // ISO4217 exists to standardise the currency code, make up (where possible) from:
        // - two letters of the ISO 3166-1 alpha-2 country codes (e.g. 'SG')
        // - usually the initial of the currency itself (e.g. Dollar -> 'D')
        // Singapore Dollar = 'SGD' and the sample data is supplying an unrecognised currency.

        System.out.println("Adding sample data...");
        rows.add(new ImmutableInstruction(
                ENTITIES.foo,
                ImmutableInstruction.BUYSELL.B,
                new BigDecimal("0.50"),
                ISO4217.Currency.SGD,
                LocalDate.parse("2016-01-01"),
                LocalDate.parse("2016-01-02"),
                200,
                ENTITIES.foo.getLastValueUSD()
        ));

        rows.add(new ImmutableInstruction(
                ENTITIES.bar,
                ImmutableInstruction.BUYSELL.S,
                new BigDecimal("0.22"),
                ISO4217.Currency.AED,
                LocalDate.parse("2016-01-05"),
                LocalDate.parse("2016-01-07"),
                450,
                ENTITIES.bar.getLastValueUSD()
        ));

        final int numRows = 100;
        System.out.println("Adding "+numRows+" rows of generated data...");
        generateRandomSampleData(numRows);
    }

    /**
     * Obtains the one and only instance of the DataSource object
     * @return DataSource
     */
    public static DataSource getInstance() {
        return data;
    }

    private void generateRandomSampleData(int numRows) {
        final Random random = new Random();

        for (int i=0; i < numRows; i++) {
            final ENTITIES entity = ENTITIES.randomEntity();
            final LocalDate instructionDate = getRandomInstructionDate(random);
            rows.add(new ImmutableInstruction(
                    entity,
                    ImmutableInstruction.BUYSELL.randomBuySell(),
                    // TODO: agreedFX could in reality be between almost zero and have no upper bound (this is sample data)
                    new BigDecimal(random.nextDouble()).setScale(5, RoundingMode.HALF_EVEN),
                    ISO4217.Currency.randomCurrency(),
                    instructionDate,
                    getRandomSettlementDateAfter(random, instructionDate),
                    // TODO: sample data limited to 1 mil units per instruction (upper bound not specified in requirements)
                    random.nextInt(1_000_000),
                    // TODO: sample data price per unit is the last traded price for that entity, adjusted up or down slightly (at random)
                    entity.getLastValueUSD().add(BigDecimal.valueOf((random.nextDouble() - 0.5) * entity.getLastValueUSD().doubleValue()))
            ));
        }
    }

    /**
     * For sample data, gets a local date within 2 weeks of now, 1 week either side
     * @param random Supplied Random object reference
     * @return LocalDate that falls within 1 week ago to 1 week from now
     */
    private LocalDate getRandomInstructionDate(Random random) {
        if (random.nextBoolean()) { //
            return LocalDate.now().plusDays(random.nextInt(7));
        } else { // or down
            return LocalDate.now().minusDays(random.nextInt(7));
        }
    }

    /**
     * For sample data, gets a local date after the supplied date, up to 1 week
     *
     * @param random Supplied Random object reference
     * @param instructionDate Date this new date must be after
     * @return LocalDate that falls within 1 week from the supplied instruction date
     */
    private LocalDate getRandomSettlementDateAfter(Random random, LocalDate instructionDate) {
        return instructionDate.plusDays(random.nextInt(7));
    }

    /**
     * Returns all the rows this data source has to offer
     * @return List<ImmutableInstruction> all instructions available
     */
    public List<ImmutableInstruction> getRows() {
        return rows;
    }

    /**
     * Returns count of all rows available for processing
     * @return Number of rows of data
     */
    public int getRowsCount() {
        return rows.size();
    }
}

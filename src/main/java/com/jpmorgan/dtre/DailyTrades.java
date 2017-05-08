/**
 * TODO: Company copyright/licence notice
 */
package com.jpmorgan.dtre;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Daily Trade Reporting Engine to process instructions sent by various clients
 * to JP Morgan to execute in the international market.
 *
 * Guidelines:
 * - No database or UI is required.
 * - Assume the code will only ever be executed in a single threaded environment.
 * - Minimise the number of external jar dependencies the code has.
 * - All data to be in memory.
 * - Output format to be plain text, printed out to the console.
 *
 * Sample data:
 * Entity | Buy/Sell |  AgreedFx | Currency | InstructionDate | C | Units |  Price per unit
 *  foo   |    B     |    0.50   |   SGP    |   01 Jan 2016   |   02 Jan 2016  |  200  |     100.25
 *  bar   |    S     |    0.22   |   AED    |   05 Jan 2016   |   07 Jan 2016  |  450  |     150.5
 *
 * Logic:
 * - A work week starts Monday and ends Friday, unless the currency of the trade is AED or SAR, where
 *   the work week starts Sunday and ends Thursday. No other holidays to be taken into account.
 * - A trade can only be settled on a working day.
 * - If an instructed settlement date falls on a weekend, then the settlement date should be changed to
 *   the next working day.
 * - USD amount of a trade = Price per unit * Units * Agreed Fx
 *
 * Reporting requirements:
 * - Amount in USD settled incoming everyday
 * - Amount in USD settled outgoing everyday
 * - Ranking of entities based on incoming and outgoing amount. Eg: If entity foo instructs the highest
 *   amount for a buy instruction, then foo is rank 1 for outgoing
 *
 * Glossary:
 * - Instruction: An instruction to buy or sell
 * - Entity: A financial entity whose shares are to be bought or sold
 * - Instruction Date: Date on which the instruction was sent to JP Morgan by various clients
 * - Settlement Date: The date on which the client wished for the instruction to be settled with respect
 *                    to Instruction Date
 * - Buy/Sell flag:
 *     B – Buy – outgoing
 *     S – Sell – incoming
 * - Agreed Fx is the foreign exchange rate with respect to USD that was agreed
 * - Units: Number of shares to be bought or sold
 *
 * Author notes:
 *   - Use BigDecimal to represent currency, never float or double types
 *   - Reporting engine indicates this is to process the instruction data (perhaps from a DB ultimately)
 *     and possibly offline as a batch job, rather than service user requests
 *   -
 * @author Peter D Bell, 5rd May 2017
 */
public class DailyTrades {

    private final DataSource ds;
    /** Number format for US, for presentation layer */
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);

    public DailyTrades() {
        ds = DataSource.getInstance();
    }

    public static void main (String[] a) {
        System.out.println("Daily Trade Reporting Engine");

        final DailyTrades dt = new DailyTrades();
        dt.reports();

        System.exit(0);
    }

    public void reports() {
        incomingSettledUSDDaily();
        outgoingSettledUSDDaily();
        rank();
    }

    /**
     * Incoming = Sell orders
     */
    void incomingSettledUSDDaily() {
        final Set<ImmutableInstruction> sellOrders = ds.getRows()
                .stream()
                .filter(r -> r.getBuySell() == ImmutableInstruction.BUYSELL.S)
                // it would be nice to use mapToDouble (or ideally a new mapToBigDecimal) at this point, but we can't
                // so we drop the java 8 lambda aggregation and process individual BigDecimal objects, otherwise
                // we risk losing either precision or number of digits
                // so just collect, don't map and aggregate (yet)
                .collect(Collectors.toSet());

        System.out.println("=== Amount in USD settled incoming (Sell) every day ===");
        aggregateSumPerDay(sellOrders);
    }

    /**
     * Outgoing = Buy orders
     */
    void outgoingSettledUSDDaily() {
        final Set<ImmutableInstruction> buyOrders = ds.getRows()
                .stream()
                .filter(r -> r.getBuySell() == ImmutableInstruction.BUYSELL.B)
                // it would be nice to use mapToDouble (or ideally a new mapToBigDecimal) at this point, but we can't
                // so we drop the java 8 lambda aggregation and process individual BigDecimal objects, otherwise
                // we risk losing either precision or number of digits
                // so just collect, don't map and aggregate (yet)
                .collect(Collectors.toSet());

        System.out.println("=== Amount in USD settled outgoing (Buy) every day ===");
        aggregateSumPerDay(buyOrders);
    }

    void rank() {
        final Set<ImmutableInstruction> sellOrders = ds.getRows()
                .stream()
                .filter(r -> r.getBuySell() == ImmutableInstruction.BUYSELL.S)
                // it would be nice to use mapToDouble (or ideally a new mapToBigDecimal) at this point, but we can't
                // so we drop the java 8 lambda aggregation and process individual BigDecimal objects, otherwise
                // we risk losing either precision or number of digits
                // so just collect, don't map and aggregate (yet)
                .collect(Collectors.toSet());
        System.out.println("=== Rank Incoming (Sell) ===");
        aggregateSumByEntity(sellOrders);

        final Set<ImmutableInstruction> buyOrders = ds.getRows()
                .stream()
                .filter(r -> r.getBuySell() == ImmutableInstruction.BUYSELL.B)
                // it would be nice to use mapToDouble (or ideally a new mapToBigDecimal) at this point, but we can't
                // so we drop the java 8 lambda aggregation and process individual BigDecimal objects, otherwise
                // we risk losing either precision or number of digits
                // so just collect, don't map and aggregate (yet)
                .collect(Collectors.toSet());
        System.out.println("=== Rank Outgoing (Buy) ===");
        aggregateSumByEntity(buyOrders);

    }

    int dataRowsLoaded() {
        return ds.getRowsCount();
    }

    /**
     * Prints out a list of aggregate 'AmountOfTradeUSD' by date, sorted by date.
     * Note: Operates on the entire list of sample data (no defined date range)
     *       and will not print a date with $0.00, if no data exist
     * @param orders Pre-filtered Set of ImmutableInstruction objects to take into account
     * @return Map<LocalDate, BigDecimal> (TreeMap) in natural date order of date-to-sum-USD-trade
     */
    Map<LocalDate, BigDecimal> aggregateSumPerDay(Set<ImmutableInstruction> orders) {
        final Map<LocalDate, BigDecimal> dateToSumPerDay = new TreeMap<>(); // ordering on keys
        for (final ImmutableInstruction in : orders) {
            BigDecimal existingAmount = dateToSumPerDay.get(in.getSettlementDate());
            if (existingAmount == null) {
                existingAmount = BigDecimal.ZERO;
            }
            dateToSumPerDay.put(in.getSettlementDate(), existingAmount.add(in.getAmountOfTradeUSD()));
        }

        for (final Map.Entry<LocalDate, BigDecimal> e : dateToSumPerDay.entrySet()) {
            System.out.println(e.getKey()+" => "+nf.format(e.getValue()));
        }
        return dateToSumPerDay;
    }

    Map<DataSource.ENTITIES, BigDecimal> aggregateSumByEntity(Set<ImmutableInstruction> orders) {
        final Map<DataSource.ENTITIES, BigDecimal> entityToSumPerDay = new HashMap<>();
        for (final ImmutableInstruction in : orders) {
            BigDecimal existingAmount = entityToSumPerDay.get(in.getEntity());
            if (existingAmount == null) {
                existingAmount = BigDecimal.ZERO;
            }
            entityToSumPerDay.put(in.getEntity(), existingAmount.add(in.getAmountOfTradeUSD()));
        }


        // Java 8: sort this, by value, descending (.reversed())
        final AtomicInteger rank = new AtomicInteger(1);
        entityToSumPerDay.entrySet().stream()
                .sorted(Map.Entry.<DataSource.ENTITIES, BigDecimal>comparingByValue().reversed())
                .forEach(e -> System.out.println(rank.getAndAdd(1) + ". " + e.getKey() + " => " + nf.format(e.getValue())));

        // Java 7: sort this, by value, descending
//        final Map<DataSource.ENTITIES, BigDecimal> sortedMap = sortByValueDesc(entityToSumPerDay);
//        int c = 1;
//        for (final Map.Entry<DataSource.ENTITIES, BigDecimal> e : sortedMap.entrySet()) {
//            System.out.println((c++)+". "+e.getKey()+" => "+nf.format(e.getValue()));
//        }

        return entityToSumPerDay;
    }

//    Java 7: shown here for information in the assessment only, wouldn't leave so-called "zombie code" lying around production
//    /**
//     * Sort by value (descending) specifically for a map of DataSource.ENTITIES to BigDecimal
//     * Note: Operates on the entire list of sample data (no defined date range)
//     *       and will not print an entity with $0.00 sum total USD trade, if no data exist
//     * @param unsortedMap Map<DataSource.ENTITIES, BigDecimal>
//     * @return Map sorted by value, mapping ENTITIES to sum total USD trade
//     */
//    private Map<DataSource.ENTITIES, BigDecimal> sortByValueDesc (Map<DataSource.ENTITIES, BigDecimal> unsortedMap) {
//        // prepare the treemap with the appropriate value.compareTo(value) comparator:
//        final Map<DataSource.ENTITIES, BigDecimal> sortedMap = new TreeMap<>(new ValueComparator(unsortedMap));
//        // fill it will all the unsorted entries
//        sortedMap.putAll(unsortedMap);
//        return sortedMap;
//    }
//
//    /**
//     * Comparator for use with TreeMap which will sort by value, rather than key (by default)
//     */
//    class ValueComparator<K, V extends Comparable<V>> implements Comparator<V>{
//
//        final Map<K, V> map;
//
//        public ValueComparator(Map<K, V> map){
//            this.map = map;
//        }
//
//        @Override
//        public int compare(V o1, V o2) {
//            return -map.get(o1).compareTo(map.get(o2));//descending order
//        }
//    }

}

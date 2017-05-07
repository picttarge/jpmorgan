package com.jpmorgan.dtre;

import java.util.Random;

/**
 * ISO 4217 currencies
 * Source: https://www.iso.org/iso-4217-currency-codes.html (published Jan 1st, 2017)
 * @author Peter D Bell, 4rd May 2017
 */
public class ISO4217 {
    enum Currency {
        // Alphabetic Code (Numeric Code,Minor unit,"Currency")
        AED (784,2,"UAE Dirham"),
        AFN (971,2,"Afghani"),
        ALL (  8,2,"Lek"),
        AMD ( 51,2,"Armenian Dram"),
        ANG (532,2,"Netherlands Antillean Guilder"),
        AOA (973,2,"Kwanza"),
        ARS ( 32,2,"Argentine Peso"),
        AUD ( 36,2,"Australian Dollar"),
        AWG (533,2,"Aruban Florin"),
        AZN (944,2,"Azerbaijanian Manat"),
        BAM (977,2,"Convertible Mark"),
        BBD ( 52,2,"Barbados Dollar"),
        BDT ( 50,2,"Taka"),
        BGN (975,2,"Bulgarian Lev"),
        BHD ( 48,3,"Bahraini Dinar"),
        BIF (108,0,"Burundi Franc"),
        BMD ( 60,2,"Bermudian Dollar"),
        BND ( 96,2,"Brunei Dollar"),
        BOB ( 68,2,"Boliviano"),
        BOV (984,2,"Mvdol"),
        BRL (986,2,"Brazilian Real"),
        BSD ( 44,2,"Bahamian Dollar"),
        BTN ( 64,2,"Ngultrum"),
        BWP ( 72,2,"Pula"),
        BYN (933,2,"Belarusian Ruble"),
        BZD ( 84,2,"Belize Dollar"),
        CAD (124,2,"Canadian Dollar"),
        CDF (976,2,"Congolese Franc"),
        CHE (947,2,"WIR Euro"),
        CHF (756,2,"Swiss Franc"),
        CHW (948,2,"WIR Franc"),
        CLF (990,4,"Unidad de Fomento"),
        CLP (152,0,"Chilean Peso"),
        CNY (156,2,"Yuan Renminbi"),
        COP (170,2,"Colombian Peso"),
        COU (970,2,"Unidad de Valor Real"),
        CRC (188,2,"Costa Rican Colon"),
        CUC (931,2,"Peso Convertible"),
        CUP (192,2,"Cuban Peso"),
        CVE (132,2,"Cabo Verde Escudo"),
        CZK (203,2,"Czech Koruna"),
        DJF (262,0,"Djibouti Franc"),
        DKK (208,2,"Danish Krone"),
        DOP (214,2,"Dominican Peso"),
        DZD ( 12,2,"Algerian Dinar"),
        EGP (818,2,"Egyptian Pound"),
        ERN (232,2,"Nakfa"),
        ETB (230,2,"Ethiopian Birr"),
        EUR (978,2,"Euro"),
        FJD (242,2,"Fiji Dollar"),
        FKP (238,2,"Falkland Islands Pound"),
        GBP (826,2,"Pound Sterling"),
        GEL (981,2,"Lari"),
        GHS (936,2,"Ghana Cedi"),
        GIP (292,2,"Gibraltar Pound"),
        GMD (270,2,"Dalasi"),
        GNF (324,0,"Guinea Franc"),
        GTQ (320,2,"Quetzal"),
        GYD (328,2,"Guyana Dollar"),
        HKD (344,2,"Hong Kong Dollar"),
        HNL (340,2,"Lempira"),
        HRK (191,2,"Kuna"),
        HTG (332,2,"Gourde"),
        HUF (348,2,"Forint"),
        IDR (360,2,"Rupiah"),
        ILS (376,2,"New Israeli Sheqel"),
        INR (356,2,"Indian Rupee"),
        IQD (368,3,"Iraqi Dinar"),
        IRR (364,2,"Iranian Rial"),
        ISK (352,0,"Iceland Krona"),
        JMD (388,2,"Jamaican Dollar"),
        JOD (400,3,"Jordanian Dinar"),
        JPY (392,0,"Yen"),
        KES (404,2,"Kenyan Shilling"),
        KGS (417,2,"Som"),
        KHR (116,2,"Riel"),
        KMF (174,0,"Comoro Franc"),
        KPW (408,2,"North Korean Won"),
        KRW (410,0,"Won"),
        KWD (414,3,"Kuwaiti Dinar"),
        KYD (136,2,"Cayman Islands Dollar"),
        KZT (398,2,"Tenge"),
        LAK (418,2,"Kip"),
        LBP (422,2,"Lebanese Pound"),
        LKR (144,2,"Sri Lanka Rupee"),
        LRD (430,2,"Liberian Dollar"),
        LSL (426,2,"Loti"),
        LYD (434,3,"Libyan Dinar"),
        MAD (504,2,"Moroccan Dirham"),
        MDL (498,2,"Moldovan Leu"),
        MGA (969,2,"Malagasy Ariary"),
        MKD (807,2,"Denar"),
        MMK (104,2,"Kyat"),
        MNT (496,2,"Tugrik"),
        MOP (446,2,"Pataca"),
        MRO (478,2,"Ouguiya"),
        MUR (480,2,"Mauritius Rupee"),
        MVR (462,2,"Rufiyaa"),
        MWK (454,2,"Malawi Kwacha"),
        MXN (484,2,"Mexican Peso"),
        MXV (979,2,"Mexican Unidad de Inversion (UDI)"),
        MYR (458,2,"Malaysian Ringgit"),
        MZN (943,2,"Mozambique Metical"),
        NAD (516,2,"Namibia Dollar"),
        NGN (566,2,"Naira"),
        NIO (558,2,"Cordoba Oro"),
        NOK (578,2,"Norwegian Krone"),
        NPR (524,2,"Nepalese Rupee"),
        NZD (554,2,"New Zealand Dollar"),
        OMR (512,3,"Rial Omani"),
        PAB (590,2,"Balboa"),
        PEN (604,2,"Sol"),
        PGK (598,2,"Kina"),
        PHP (608,2,"Philippine Peso"),
        PKR (586,2,"Pakistan Rupee"),
        PLN (985,2,"Zloty"),
        PYG (600,0,"Guarani"),
        QAR (634,2,"Qatari Rial"),
        RON (946,2,"Romanian Leu"),
        RSD (941,2,"Serbian Dinar"),
        RUB (643,2,"Russian Ruble"),
        RWF (646,0,"Rwanda Franc"),
        SAR (682,2,"Saudi Riyal"),
        SBD ( 90,2,"Solomon Islands Dollar"),
        SCR (690,2,"Seychelles Rupee"),
        SDG (938,2,"Sudanese Pound"),
        SEK (752,2,"Swedish Krona"),
        // sample data says "SGP" but that's Singapore's 3-letter ISO country code, not currency code.
        // ISO4217 exists to standardise the currency code, make up (where possible) from:
        // - two letters of the ISO 3166-1 alpha-2 country codes (e.g. 'SG')
        // - usually the initial of the currency itself (e.g. Dollar -> 'D')
        // Singapore Dollar = 'SGD' and the sample data is supplying an unrecognised currency.
        SGD (702,2,"Singapore Dollar"),
        SHP (654,2,"Saint Helena Pound"),
        SLL (694,2,"Leone"),
        SOS (706,2,"Somali Shilling"),
        SRD (968,2,"Surinam Dollar"),
        SSP (728,2,"South Sudanese Pound"),
        STD (678,2,"Dobra"),
        SVC (222,2,"El Salvador Colon"),
        SYP (760,2,"Syrian Pound"),
        SZL (748,2,"Lilangeni"),
        THB (764,2,"Baht"),
        TJS (972,2,"Somoni"),
        TMT (934,2,"Turkmenistan New Manat"),
        TND (788,3,"Tunisian Dinar"),
        TRY (949,2,"Turkish Lira"),
        TTD (780,2,"Trinidad and Tobago Dollar"),
        TWD (901,2,"New Taiwan Dollar"),
        TZS (834,2,"Tanzanian Shilling"),
        UAH (980,2,"Hryvnia"),
        UGX (800,0,"Uganda Shilling"),
        USD (840,2,"US Dollar"),
        USN (997,2,"US Dollar (Next day)"),
        UYI (940,0,"Uruguay Peso en Unidades Indexadas (URUIURUI)"),
        UYU (858,2,"Peso Uruguayo"),
        UZS (860,2,"Uzbekistan Sum"),
        VND (704,0,"Dong"),
        VUV (548,0,"Vatu"),
        WST (882,2,"Tala"),
        XAF (950,0,"CFA Franc BEAC"),
        XAG (961,null,"Silver"),
        XAU (959,null,"Gold"),
        XBA (955,null,"Bond Markets Unit European Composite Unit (EURCO)"),
        XBB (956,null,"Bond Markets Unit European Monetary Unit (E.M.U.-6)"),
        XBC (957,null,"Bond Markets Unit European Unit of Account 9 (E.U.A.-9)"),
        XBD (958,null,"Bond Markets Unit European Unit of Account 17 (E.U.A.-17)"),
        XCD (951,2,"East Caribbean Dollar"),
        XDR (960,null,"SDR (Special Drawing Right)"),
        XOF (952,0,"CFA Franc BCEAO"),
        XPD (964,null,"Palladium"),
        XPF (953,0,"CFP Franc"),
        XPT (962,null,"Platinum"),
        XSU (994,null,"Sucre"),
        XTS (963,null,"Codes specifically reserved for testing purposes"),
        XUA (965,null,"ADB Unit of Account"),
        XXX (999,null,"The codes assigned for transactions where no currency is involved"),
        YER (886,2,"Yemeni Rial"),
        ZAR (710,2,"Rand"),
        ZMW (967,2,"Zambian Kwacha"),
        ZWL (932,2,"Zimbabwe Dollar");

        // As we'll be asking for .values() a lot and this creates an array each time, cache this
        private final static Currency[] VALUES = values();
        private final static Random random = new Random();

        final int numericCode;
        final Integer minorUnit; // support null
        final String currency;
        // Alphabetic Code (Numeric Code,Minor unit,"Currency")
        Currency (int numericCode, Integer minorUnit, String currency) {
            this.numericCode = numericCode;
            this.minorUnit   = minorUnit;
            this.currency = currency;
        }

        public String getNumericCode3Digits() {
            return String.format("%03d", numericCode);
        }

        public static Currency randomCurrency() {
            return VALUES[(random.nextInt(VALUES.length))];
        }

        public String toString() {
            return name()+","+numericCode+","+getNumericCode3Digits()+","+minorUnit+","+currency;
        }
    }
}

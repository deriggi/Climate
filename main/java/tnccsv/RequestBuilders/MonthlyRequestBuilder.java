/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv.RequestBuilders;


import ascii.AsciiDataLoader;
import ascii.CacheDataStoreAsciiAction;
import dao.country.CountryDao;
import dao.deriveddata.DerivedDataDao;
import dao.derivedmapdata.DerivedMapDataDao;
import data.domain.DatePoint;
import domain.Country;
import tnccsv.DataFileHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import domain.DerivativeStats;
import tnccsv.PageFetcher;
import tnccsv.TNCCsvProcessor;
import tnccsv.TNCDateFromFileNameExtractor;
import tnccsv.TNCRegexBufferedReaderProcessor;

/**
 *
 * @author wb385924
 */
public class MonthlyRequestBuilder {

    public enum order {

        ASC, DESC
    }
    private static final String A1B_MONTHLY_MID_CEN = "http://184.72.144.73/afrMENA_countries_monthly_a1b_midCen/aaronAfrica_ALL_COUNTRY_a1b_monthly_midCen/";
    private static final String A1B_MONTHLY_END_CEN = "http://174.129.115.227/afrMENA_countries_monthly_a1b_endCen/aaronAfrica_ALL_COUNTRY_a1b_monthly_endCen/";
    private static final String A2B1_MONTHLY_END_CEN = "http://67.202.62.210/afrMENA_countries_monthly_a2b1_endCen/afrMENA_countries_a2b1_monthly_endCen/";
    private static final String A2B1_MONTHLY_MID_CEN = "http://204.236.240.64/afrMENA_countries_monthly_a2b1_midCen/afrMENA_countries_a2b1_monthly_midCen/";
    private static final String FORWARD_SLASH = "/";
    private static final String DOT = ".";
    private static final String _ = "_";
    private static final Logger log = Logger.getLogger(MonthlyRequestBuilder.class.getName());

    public static String getURL(DerivativeStats.time_period timePeriod, DerivativeStats.gcm gcm, DerivativeStats.scenario scenario, DerivativeStats.climatestat climatestat, String iso3, int run, DerivativeStats.temporal_aggregation temporalAggregation) {
        // validate stat request
        if (!DerivativeStats.getInstance().hasRun(gcm, scenario, run)) {
            log.log(Level.WARNING, "configuration request was denied because we dont have this run  gcm: {0} scenario: {1} run:{2}", new Object[]{gcm, scenario, run});
            return null;
        }

        StringBuilder sb = new StringBuilder();
        // yearly -- climatewizardcustom.org/WorldBank/afrMENA_countries_annual_midCen/ AGO / cd18 / cccma_cgcm3_1.1_a2 /
        // yearly -- http://184.72.144.73/ afrMENA_countries_monthly_a1b_midCen /aaronAfrica_ALL_COUNTRY_a1b_monthly_midCen/ AGO /cd18 / cccma_cgcm3_1.1_a1b /


        if ((timePeriod.compareTo(DerivativeStats.time_period.mid_century) == 0) && (scenario.compareTo(DerivativeStats.scenario.a1b) == 0)) {
            sb.append(A1B_MONTHLY_MID_CEN);
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.end_century) == 0) && (scenario.compareTo(DerivativeStats.scenario.a1b) == 0)) {
            sb.append(A1B_MONTHLY_END_CEN);
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.mid_century) == 0) && (scenario.compareTo(DerivativeStats.scenario.a2) == 0)) {
            sb.append(A2B1_MONTHLY_MID_CEN);
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.end_century) == 0) && (scenario.compareTo(DerivativeStats.scenario.a2) == 0)) {
            sb.append(A2B1_MONTHLY_END_CEN);
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.mid_century) == 0) && (scenario.compareTo(DerivativeStats.scenario.b1) == 0)) {
            sb.append(A2B1_MONTHLY_MID_CEN);
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.end_century) == 0) && (scenario.compareTo(DerivativeStats.scenario.b1) == 0)) {
            sb.append(A2B1_MONTHLY_END_CEN);
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.baseline) == 0) && (scenario.compareTo(DerivativeStats.scenario.b1) == 0)) {
            sb.append(A2B1_MONTHLY_END_CEN);
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.baseline) == 0) && (scenario.compareTo(DerivativeStats.scenario.a2) == 0)) {
            sb.append(A2B1_MONTHLY_END_CEN);
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.baseline) == 0) && (scenario.compareTo(DerivativeStats.scenario.a1b) == 0)) {
            sb.append(A1B_MONTHLY_END_CEN);
        }

        if (sb.length() == 0) {
            log.warning("could not consruct a base url from parameters");
            return null;
        }

        sb.append(iso3);
        sb.append(FORWARD_SLASH);

        sb.append(climatestat);
        sb.append(FORWARD_SLASH);

        sb.append(gcm);
        sb.append(DOT);
        sb.append(run);
        sb.append(_);
        sb.append(scenario);

        return sb.toString();

    }

    public void downloadMonthlyMapData(ArrayList<String> ignoreList, order order) {
        DerivativeStats.getInstance();
        DerivativeStats.time_period endOfCentury = DerivativeStats.time_period.mid_century;
        DerivativeStats.temporal_aggregation monthly = DerivativeStats.temporal_aggregation.monthly;
        DerivativeStats.stat_type mean = DerivativeStats.stat_type.mean;
        String fileExtension = DerivativeStats.getInstance().getFileExtension(DerivativeStats.file_type.map);
        DerivativeStats.file_name fileName = DerivativeStats.map_file_name.map_mean_AR4;
        int run = 1;

        DerivativeStats.climatestat[] tempstats = DerivativeStats.tempstat.values();
        DerivativeStats.climatestat[] prstats = DerivativeStats.precipstat.values();
        ArrayList<DerivativeStats.climatestat> stats = new ArrayList<DerivativeStats.climatestat>();
        stats.addAll(Arrays.asList(tempstats));
        stats.addAll(Arrays.asList(prstats));
        List<DerivativeStats.gcm> gcms = Arrays.asList(DerivativeStats.gcm.values());

        CountryDao countryDao = CountryDao.get();
        HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countryDao.getCountriesFromRegionName("Africa_MENA"));

        Set<String> unOrderedIsos = countryMap.keySet();
        TreeSet<String> isos = new TreeSet<String>();
        isos.addAll(unOrderedIsos);
        Iterator<String> codeIterator;
        if (order.name().equals(order.ASC.name())) {
            codeIterator = isos.iterator();
        } else {
            codeIterator = isos.descendingIterator();
        }

        while (codeIterator.hasNext()) {
            String iso3Code = codeIterator.next();
            if (ignoreList.contains(iso3Code)) {
                continue;
            }


            for (DerivativeStats.scenario scenario : DerivativeStats.scenario.values()) {

                for (DerivativeStats.gcm gcm : gcms) {

                    for (DerivativeStats.climatestat stat : stats) {
                        if (stat.getId() <= 0) {
                            log.log(Level.WARNING, "got -1 for temp for {0}", stat);
                            continue;
                        }
                        if(!stat.isMonthly()){
                            continue;
                        }

                        String url = MonthlyRequestBuilder.getURL(endOfCentury, gcm, scenario, stat, iso3Code, run, monthly);
                        log.log(Level.FINE, "trying  {0}", url);
                        TNCRegexBufferedReaderProcessor processor = new TNCRegexBufferedReaderProcessor();
                        PageFetcher.readPage(url, processor);
                        tnccsv.DataFileHandler dfh = processor.getDfh();
                        log.log(Level.FINE, "number of links are {0}", dfh.getSize());
                        log.log(Level.FINE, "number of files {0} of type {1}", new Object[]{dfh.getFiles(fileExtension).size(), fileExtension});
                        ArrayList<String> yearlyFiles = dfh.getFiles(fileExtension, fileName);

                        for (String s : yearlyFiles) {
                            TNCDateFromFileNameExtractor dateGetter = new TNCDateFromFileNameExtractor();
                            dateGetter.extratDateProperties(s);
                            if (dateGetter.isProperlySet()) {

                                // this is the specific action to be taken
                                CacheDataStoreAsciiAction cacheData = new CacheDataStoreAsciiAction();
                                AsciiDataLoader loader = new AsciiDataLoader(cacheData);
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, dateGetter.getStartYear());
                                if (dateGetter.isIsMonthly()) {
                                    c.set(Calendar.MONTH, dateGetter.getMonth() - 1);
                                } else {
                                    if (monthly.name().equals(DerivativeStats.temporal_aggregation.monthly.name())) {
                                        log.severe("tyring to save monthly data but found yearly data");
                                    }
                                    c.set(Calendar.MONTH, 0);
                                }
                                c.set(Calendar.DATE, 1);
                                c.set(Calendar.HOUR, 0);
                                c.set(Calendar.MINUTE, 0);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.MILLISECOND, 0);

                                loader.parseAsciiFile(c.getTime(), PageFetcher.getInputStream(s), null);
                                Set<DatePoint> datePoints = cacheData.getDatePoints();
                                Iterator<DatePoint> datePointIterator = datePoints.iterator();
                                DerivedMapDataDao mapDataDao = DerivedMapDataDao.get();

                                while (datePointIterator.hasNext()) {
                                    DatePoint datePoint = datePointIterator.next();
                                    int cellId = mapDataDao.savePoint(datePoint.getLat(), datePoint.getLon());
                                    log.log(Level.FINE, "map data datepoint is {0} from file {1}", new Object[]{datePoint.toString(), s});

                                    mapDataDao.saveSpatialData(datePoint.getData(), countryMap.get(iso3Code).getId(), endOfCentury, mean, stat, monthly, scenario, gcm, run, dateGetter.getMonth(), dateGetter.getStartYear(), cellId);
                                }
                                log.log(Level.FINE, "{0} {1}", new Object[]{s, cacheData.toString()});
                                // end of specific action

                            }
                        }
                    }
                }
            }
        }
    }

    private void downloadMonthlyData(ArrayList<String> ignoreList) {
        DerivativeStats.getInstance();
        DerivativeStats.time_period endOfCentury = DerivativeStats.time_period.mid_century;
        DerivativeStats.temporal_aggregation monthly = DerivativeStats.temporal_aggregation.monthly;
        DerivativeStats.stat_type mean = DerivativeStats.stat_type.mean;
        String fileExtension = DerivativeStats.getInstance().getFileExtension(DerivativeStats.file_type.map);
        DerivativeStats.file_name fileName = DerivativeStats.map_file_name.map_mean_AR4;
        int run = 1;

        DerivativeStats.climatestat[] tempstats = DerivativeStats.tempstat.values();
        DerivativeStats.climatestat[] prstats = DerivativeStats.precipstat.values();
        ArrayList<DerivativeStats.climatestat> stats = new ArrayList<DerivativeStats.climatestat>();
        stats.addAll(Arrays.asList(tempstats));
        stats.addAll(Arrays.asList(prstats));
        List<DerivativeStats.gcm> gcms = Arrays.asList(DerivativeStats.gcm.values());

        CountryDao countryDao = CountryDao.get();
        HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countryDao.getCountriesFromRegionName("Africa_MENA"));
        Set<String> isos = countryMap.keySet();

        for (String iso3Code : isos) {
            if (ignoreList.contains(iso3Code)) {
                continue;
            }

            for (DerivativeStats.scenario scenario : DerivativeStats.scenario.values()) {

                for (DerivativeStats.gcm gcm : gcms) {

                    for (DerivativeStats.climatestat stat : stats) {
                        if (stat.getId() <= 0) {
                            log.log(Level.WARNING, "got -1 for temp for {0}", stat);
                            continue;
                        }

                        String url = MonthlyRequestBuilder.getURL(endOfCentury, gcm, scenario, stat, iso3Code, run, monthly);
                        log.log(Level.INFO, "trying  {0}", url);
                        TNCRegexBufferedReaderProcessor processor = new TNCRegexBufferedReaderProcessor();
                        PageFetcher.readPage(url, processor);
                        tnccsv.DataFileHandler dfh = processor.getDfh();
                        log.log(Level.FINE, "number of links are {0}", dfh.getSize());
                        log.log(Level.FINE, "number of files {0} of type {1}", new Object[]{dfh.getFiles(fileExtension).size(), fileExtension});
                        ArrayList<String> yearlyFiles = dfh.getFiles(fileExtension, fileName);

                        for (String s : yearlyFiles) {
                            TNCDateFromFileNameExtractor dateGetter = new TNCDateFromFileNameExtractor();
                            dateGetter.extratDateProperties(s);
                            if (dateGetter.isProperlySet()) {
                                // this is the specific action to be taken
//                                CacheDataStoreAsciiAction cacheData = new CacheDataStoreAsciiAction();
//                                AsciiDataLoader loader = new AsciiDataLoader(cacheData);
//                                Calendar c = Calendar.getInstance();
//                                c.set(Calendar.YEAR, dateGetter.getStartYear());
//                                if (dateGetter.isIsMonthly()) {
//                                    c.set(Calendar.MONTH, dateGetter.getMonth()-1);
//                                } else {
//                                    if(monthly.name().equals(DerivativeStats.temporal_aggregation.monthly.name())){
//                                        log.severe("tyring to save monthly data but found yearly data");
//                                    }
//                                    c.set(Calendar.MONTH, 0);
//                                }
//                                c.set(Calendar.DATE, 1);
//                                c.set(Calendar.HOUR, 0);
//                                c.set(Calendar.MINUTE, 0);
//                                c.set(Calendar.SECOND, 0);
//                                c.set(Calendar.MILLISECOND, 0);
//
//                                loader.parseAsciiFile(c.getTime(), PageFetcher.getInputStream(s), null);
//                                Set<DatePoint> datePoints = cacheData.getDatePoints();
//                                Iterator<DatePoint> datePointIterator = datePoints.iterator();
//                                DerivedMapDataDao mapDataDao = DerivedMapDataDao.get();
//
//                                while (datePointIterator.hasNext()) {
//                                    DatePoint datePoint = datePointIterator.next();
//                                    int cellId = mapDataDao.savePoint(datePoint.getLat(), datePoint.getLon());
//                                    log.log(Level.FINE,"map data datepoint is {0} from file {1}",new Object[]{datePoint.toString(),s});
//
//                                    mapDataDao.saveSpatialData(datePoint.getData(),countryMap.get(iso3Code).getId(), endOfCentury,  mean, stat,  monthly, scenario, gcm, run, dateGetter.getMonth(), dateGetter.getStartYear(), cellId);
//                                }
//                                log.log(Level.FINE, "{0} {1}", new Object[]{s, cacheData.toString()});
                                // end of specific action
                            }
                        }
                    }
                }
            }
        }
    }

//    public static void main(String[] args) {
//
//        new MonthlyRequestBuilder().oldMain();
//    }
    public void downloadMonthlyCSV(ArrayList<String> ignoreList, order order) {
        DerivativeStats ds = DerivativeStats.getInstance();
        DerivativeStats.time_period timePeriod = DerivativeStats.time_period.baseline;

        DerivativeStats.temporal_aggregation monthly = DerivativeStats.temporal_aggregation.monthly;
        DerivativeStats.stat_type mean = DerivativeStats.stat_type.mean;
        DerivativeStats.climatestat[] tempstats = DerivativeStats.tempstat.values();
        DerivativeStats.climatestat[] prstats = DerivativeStats.precipstat.values();
        ArrayList<DerivativeStats.climatestat> stats = new ArrayList<DerivativeStats.climatestat>();
        stats.addAll(Arrays.asList(tempstats));
        stats.addAll(Arrays.asList(prstats));
        List<DerivativeStats.gcm> gcms = Arrays.asList(DerivativeStats.gcm.values());
//     
        int run = DerivativeStats.RUN;
        CountryDao countryDao = CountryDao.get();
        HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countryDao.getCountriesFromRegionName("Africa_MENA"));
        Set<String> unOrderedIsos = countryMap.keySet();
        TreeSet<String> isos = new TreeSet<String>();
        isos.addAll(unOrderedIsos);
        Iterator<String> codeIterator;
        boolean ascending = false;
        if (order.name().equals(order.ASC.name())) {
            codeIterator = isos.iterator();
            ascending = true;
        } else {
            codeIterator = isos.descendingIterator();
        }

        while (codeIterator.hasNext()) {
            String iso3Code = codeIterator.next();
            if (ignoreList.contains(iso3Code)) {
                continue;
            }
//            if(ascending && iso3Code.compareToIgnoreCase(startwWith) < 0){
//                continue;
//            }else if(!ascending && iso3Code.compareToIgnoreCase(startwWith) > 0){
//                continue;
//            }

            for (DerivativeStats.scenario scenario : DerivativeStats.scenario.values()) {

                for (DerivativeStats.gcm gcm : gcms) {

                    for (DerivativeStats.climatestat stat : stats) {
                        if (stat.getId() <= 0) {
                            log.log(Level.WARNING, "got -1 for temp for {0}", stat.toString());
                            continue;
                        }
                        if(!stat.isMonthly()){
                            log.log(Level.INFO, "ignoring stat non monthly {0} ", stat.toString());
                            continue;
                        }

                        String url = MonthlyRequestBuilder.getURL(timePeriod, gcm, scenario, stat, iso3Code, run, monthly);
                        log.log(Level.INFO, "trying  {0}", url);
                        TNCRegexBufferedReaderProcessor processor = new TNCRegexBufferedReaderProcessor();
                        PageFetcher.readPage(url, processor);
                        DataFileHandler dfh = processor.getDfh();
                        log.log(Level.FINE, "number of links are {0}", dfh.getSize());
                        log.log(Level.FINE, "number of csv files {0}", dfh.getFiles(DataFileHandler.CSV_FILE).size());
                        ArrayList<String> yearlyFiles = dfh.getFiles(DataFileHandler.CSV_FILE, DerivativeStats.table_file_name.table_yearly_baseline_AR4);

                        for (String s : yearlyFiles) {
                            TNCCsvProcessor csvProcessor = new TNCCsvProcessor();
                            PageFetcher.readPage(s, csvProcessor);
                            log.log(Level.FINE, "year data map size is {0}", csvProcessor.getYearDataMap());
                            Map<Integer, Double> dataMap = csvProcessor.getYearDataMap();

                            Set<Integer> yearKeys = dataMap.keySet();
                            DerivedDataDao dataDao = DerivedDataDao.get();

                            for (int i : yearKeys) {
                                log.log(Level.FINE, "{0} - {1}", new Object[]{i, dataMap.get(i)});
                                Calendar dataCalendar = Calendar.getInstance();
                                TNCDateFromFileNameExtractor dateGetter = new TNCDateFromFileNameExtractor();
                                dateGetter.extratDateProperties(s);


                                if (dateGetter.isProperlySet() && dateGetter.isIsMonthly()) {
                                    dataCalendar.set(Calendar.MONTH, dateGetter.getMonth() - 1);
                                    dataCalendar.set(Calendar.YEAR, i);
                                    dataCalendar.set(Calendar.DATE, 1);
                                    dataCalendar.set(Calendar.HOUR, 0);
                                    dataCalendar.set(Calendar.MINUTE, 0);
                                    dataCalendar.set(Calendar.SECOND, 0);
                                    dataCalendar.set(Calendar.MILLISECOND, 0);
                                    dataDao.saveDerivedStat(gcm, stat, scenario, mean, monthly, dataMap.get(i), run, countryMap.get(iso3Code).getId(), dataCalendar.getTime());


                                } else {
                                    log.log(Level.WARNING, "not saving data because could not get date from file name properly set: {0}", dateGetter.isProperlySet());

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

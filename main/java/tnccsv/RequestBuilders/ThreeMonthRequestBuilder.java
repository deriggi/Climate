/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv.RequestBuilders;

import dao.country.CountryDao;
import dao.deriveddata.DerivedDataDao;
import domain.Country;
import tnccsv.DataFileHandler;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import domain.DerivativeStats;
import tnccsv.PageFetcher;
import tnccsv.TNCCsvProcessor;
import tnccsv.TNCDateFromFileNameExtractor;
import tnccsv.TNCRegexBufferedReaderProcessor;
import util.ClimateDateUtils;

/**
 *
 * @author wb385924
 */
public class ThreeMonthRequestBuilder {

    // @TODO initialize the enums with these base urls
    private static String BASE_URL = "http://174.129.115.227/afrMENA_countries_monthly_a1b_endCen/aaronAfrica_ALL_COUNTRY_a1b_monthly_endCen/";
    
    private static final String FORWARD_SLASH = "/";
    private static final String DOT = ".";
    private static final String _ = "_";
    private static Logger log = Logger.getLogger(ThreeMonthRequestBuilder.class.getName());

    public static String getURL(DerivativeStats.time_period timePeriod, DerivativeStats.gcm gcm, DerivativeStats.scenario scenario, DerivativeStats.climatestat climatestat, String iso3, int run, DerivativeStats.temporal_aggregation temporalAggregation) {
        // validate stat request
        if (!DerivativeStats.getInstance().hasRun(gcm, scenario, run)) {
            log.log(Level.WARNING, "configuration request was denied because we dont have this run  gcm: {0} scenario: {1} run:{2}", new Object[]{gcm, scenario, run});
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);

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

    public static void main(String[] args) {
        DerivativeStats ds = DerivativeStats.getInstance();
        DerivativeStats.time_period endOfCentury = DerivativeStats.time_period.mid_century;
//        DerivativeStats.gcm cnrm = DerivativeStats.gcm.cnrm_cm3;
//        DerivativeStats.scenario a1b = DerivativeStats.scenario.b1;
//        DerivativeStats.climatestat cdd = DerivativeStats.precipstat.cdd;
        DerivativeStats.temporal_aggregation monthly = DerivativeStats.temporal_aggregation.monthly;
        DerivativeStats.stat_type mean = DerivativeStats.stat_type.mean;
        DerivativeStats.climatestat[] tempstats = DerivativeStats.tempstat.values();
        DerivativeStats.climatestat[] prstats = DerivativeStats.precipstat.values();
        ArrayList<DerivativeStats.climatestat> stats = new ArrayList<DerivativeStats.climatestat>();
        stats.addAll(Arrays.asList(tempstats));
        stats.addAll(Arrays.asList(prstats));
        List<DerivativeStats.gcm> gcms = Arrays.asList(DerivativeStats.gcm.values());

        Iterator<DerivativeStats.climatestat> statsIterator = stats.iterator();

        // remove monthly stats
        while(statsIterator.hasNext()){
            DerivativeStats.climatestat cstat = statsIterator.next();
            if  (
                    cstat.getId() == DerivativeStats.precipstat.cdd.getId() ||
                    cstat.getId() == DerivativeStats.precipstat.cdd5.getId() ||
                    cstat.getId() == DerivativeStats.tempstat.tx90.getId() ||
                    cstat.getId() == DerivativeStats.tempstat.hwdi.getId()

                    ){

                statsIterator.remove();
            }
        }


        int run = 1;
        CountryDao countryDao = CountryDao.get();
        HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countryDao.getCountriesFromRegionName("Africa_MENA"));

        ArrayList<String> isos = new ArrayList<String>();
        isos.add("AGO");
        isos.add("EGY");
        isos.add("MOZ");


        for (String iso3Code : isos) {

            for (DerivativeStats.scenario scenario : DerivativeStats.scenario.values()) {

                for (DerivativeStats.gcm gcm : gcms) {

                  for (DerivativeStats.climatestat stat : stats) {
                        if (stat.getId() <= 0) {
                            log.log(Level.WARNING, "got -1 for temp for {0}", stat);
                            continue;
                        }

                        String url = ThreeMonthRequestBuilder.getURL(endOfCentury, gcm, scenario, stat, iso3Code, run, monthly);
                        log.log(Level.INFO, "trying  {0}", url);
                        TNCRegexBufferedReaderProcessor processor = new TNCRegexBufferedReaderProcessor();
                        PageFetcher.readPage(url, processor);
                        DataFileHandler dfh = processor.getDfh();
                        log.log(Level.FINE, "number of links are {0}", dfh.getSize());
                        log.log(Level.FINE, "number of csv files {0}", dfh.getFiles(DataFileHandler.CSV_FILE).size());
                        ArrayList<String> yearlyFiles = dfh.getFiles(DataFileHandler.CSV_FILE, DerivativeStats.table_file_name.table_yearly_AR4);

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
                                if(dateGetter.isProperlySet() && dateGetter.isIsMonthly()){
                                    dataCalendar.set(Calendar.MONTH,dateGetter.getMonth()-1);
                                    dataCalendar.set(Calendar.YEAR, i);
                                    dataCalendar.set(Calendar.DATE, 1);
                                    dataDao.saveDerivedStat(gcm, stat, scenario, mean, monthly, dataMap.get(i), run, countryMap.get(iso3Code).getId(), dataCalendar.getTime());
                                }else{
                                    log.warning("not saving data because could not get date from file name properly set: " + dateGetter.isProperlySet());
                                }
//                                
                            }
                        }
                    }
                }
            }

        }

    }
}

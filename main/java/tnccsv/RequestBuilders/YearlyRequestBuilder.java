/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv.RequestBuilders;

import dao.country.CountryDao;
import dao.deriveddata.DerivedDataDao;
import domain.Country;
import tnccsv.DataFileHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import domain.DerivativeStats;
import tnccsv.PageFetcher;
import tnccsv.TNCCsvProcessor;
import tnccsv.TNCRegexBufferedReaderProcessor;
import util.ClimateDateUtils;

/**
 *
 * @author wb385924
 */
public class YearlyRequestBuilder {

    // @TODO initialize the enums with these base urls
    private static String AFRICA_MENA_ANNUAL_MID_CENTURY = "http://climatewizardcustom.org/WorldBank/afrMENA_countries_annual_midCen/";
    private static String AFRICA_MENA_ANNUAL_END_CENTURY = "http://climatewizardcustom.org/afrMENA_countries_annual_endCen/afrMENA_countries_annual_endCen/";

//    http://climatewizardcustom.org/WorldBank/afrMENA_countries_annual_midCen/AGO/cd18/cccma_cgcm3_1.1_a2/
//    private static String AFRICA_MENA_MONTHLY_MID_CENTURY = "http://184.72.144.73/afrMENA_countries_monthly_a1b_midCen/aaronAfrica_ALL_COUNTRY_a1b_monthly_midCen/";


        private static final String FORWARD_SLASH = "/";
    private static final String DOT = ".";
    private static final String _ = "_";
    private static final Logger log = Logger.getLogger(YearlyRequestBuilder.class.getName());

    public static String getURL(DerivativeStats.time_period timePeriod, DerivativeStats.gcm gcm, DerivativeStats.scenario scenario, DerivativeStats.climatestat climatestat, String iso3, int run, DerivativeStats.temporal_aggregation temporalAggregation) {
        // validate stat request
        if (!DerivativeStats.getInstance().hasRun(gcm, scenario, run)) {
            log.log(Level.WARNING, "configuration request was denied because we dont have this run  gcm: {0} scenario: {1} run:{2}", new Object[]{gcm, scenario, run});
            return null;
        }

        StringBuilder sb = new StringBuilder();
        // yearly -- climatewizardcustom.org/WorldBank/afrMENA_countries_annual_midCen/ AGO / cd18 / cccma_cgcm3_1.1_a2 /
        // yearly -- http://184.72.144.73/ afrMENA_countries_monthly_a1b_midCen /aaronAfrica_ALL_COUNTRY_a1b_monthly_midCen/ AGO /cd18 / cccma_cgcm3_1.1_a1b /


        if (timePeriod.compareTo(DerivativeStats.time_period.mid_century) == 0) {
            sb.append(AFRICA_MENA_ANNUAL_MID_CENTURY);
        } else if (timePeriod.compareTo(DerivativeStats.time_period.end_century) == 0) {
            sb.append(AFRICA_MENA_ANNUAL_END_CENTURY);
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

    public static void main(String[] args) {
        DerivativeStats ds = DerivativeStats.getInstance();
        DerivativeStats.time_period endOfCentury = DerivativeStats.time_period.mid_century;
//        DerivativeStats.gcm cnrm = DerivativeStats.gcm.cnrm_cm3;
//        DerivativeStats.scenario a1b = DerivativeStats.scenario.b1;
//        DerivativeStats.climatestat cdd = DerivativeStats.precipstat.cdd;
        DerivativeStats.temporal_aggregation yearly = DerivativeStats.temporal_aggregation.yearly;
        DerivativeStats.stat_type mean = DerivativeStats.stat_type.mean;
        DerivativeStats.climatestat[] tempstats = DerivativeStats.tempstat.values();
        DerivativeStats.climatestat[] prstats = DerivativeStats.precipstat.values();
        ArrayList<DerivativeStats.climatestat> stats = new ArrayList<DerivativeStats.climatestat>();
        stats.addAll(Arrays.asList(tempstats));
        stats.addAll(Arrays.asList(prstats));
        List<DerivativeStats.gcm> gcms = Arrays.asList(DerivativeStats.gcm.values());
//        gcms.remove(DerivativeStats.gcm.cccma_cgcm3_1);
//        gcms.remove(DerivativeStats.gcm.cnrm_cm3);

        int run = 1;
        CountryDao countryDao = CountryDao.get();
        HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countryDao.getCountriesFromRegionName("Africa_MENA"));
        Set<String> isos = countryMap.keySet();


        for (String iso3Code : isos) {

            for (DerivativeStats.scenario scenario : DerivativeStats.scenario.values()) {

                for (DerivativeStats.gcm gcm : gcms) {
                    if(gcm.name().equals(DerivativeStats.gcm.cccma_cgcm3_1.name()) || gcm.name().equals(DerivativeStats.gcm.cnrm_cm3.name())){
                        log.log(Level.WARNING, "skipping ", gcm.name());
                            continue;
                    }


                    for (DerivativeStats.climatestat stat : stats) {
                        if (stat.getId() <= 0) {
                            log.log(Level.WARNING, "got -1 for temp for {0}", stat);
                            continue;
                        }

                        String url = YearlyRequestBuilder.getURL(endOfCentury, gcm, scenario, stat, iso3Code, run, yearly);
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
                                dataDao.saveDerivedStat(gcm, stat, scenario, mean, yearly, dataMap.get(i), run, countryMap.get(iso3Code).getId(), ClimateDateUtils.getFirstDateOfYear(i));
                            }

                        }
                    }
                }
            }

        }

    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv.RequestBuilders;

import dao.country.CountryDao;
import domain.Country;
import domain.DerivativeStats;
import export.util.FileExportHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import tnccsv.PageFetcher;
import tnccsv.TNCRegexBufferedReaderProcessor;

/**
 *
 * @author wb385924
 */
public class SimpleAnnualEnsembleRequestBuilder {

    private static final String MID_CEN = "http://www.climatewizardcustom.org/WorldBank/afrMENA_countries_annual_midCen";
    private static final String END_CEN = "http://www.climatewizardcustom.org/WorldBank/afrMENA_countries_annual_endCen";
    private static final String outputDirectory = "C:\\Users\\wb385924\\ensembleAnnualAsc\\";
    private static final Logger log = Logger.getLogger(SimpleAnnualEnsembleRequestBuilder.class.getName());
    private static final String FORWARD_SLASH = "/";
    private static final String DOT = ".";
    private static final String ENSEMBLE = "ensemble";
    private static final String _ = "_";

    public static void main(String[] args) {
        SimpleAnnualEnsembleRequestBuilder.getData();
    }

    public static void init() {
       
    }

    public static String getBase(DerivativeStats.time_period timePeriod, DerivativeStats.scenario scenario) {
        if ((timePeriod.compareTo(DerivativeStats.time_period.mid_century) == 0)) {
            return MID_CEN;
        } else if ((timePeriod.compareTo(DerivativeStats.time_period.end_century) == 0)) {
            return END_CEN;
        }

        return MID_CEN;

    }

    public static String getURL(DerivativeStats.time_period timePeriod, DerivativeStats.scenario scenario, DerivativeStats.climatestat climatestat, String iso3, DerivativeStats.temporal_aggregation temporalAggregation) {
        StringBuilder sb = new StringBuilder();

        String baseURL = getBase(timePeriod, scenario);
        sb.append(baseURL);
        sb.append(FORWARD_SLASH);
        sb.append(iso3);
        sb.append(FORWARD_SLASH);

        sb.append(climatestat);
        sb.append(FORWARD_SLASH);

        sb.append(ENSEMBLE);
        sb.append(_);
        sb.append(scenario);
        
        return sb.toString();

    }

    public static void getData() {
        DerivativeStats.getInstance();
        DerivativeStats.time_period timePeriod = DerivativeStats.time_period.mid_century;
        DerivativeStats.temporal_aggregation monthly = DerivativeStats.temporal_aggregation.monthly;
        DerivativeStats.stat_type mean = DerivativeStats.stat_type.mean;
        String fileExtension = DerivativeStats.getInstance().getFileExtension(DerivativeStats.file_type.map);
        DerivativeStats.file_name fileName = DerivativeStats.map_file_name.map_mean_baseline_ensemble_0_;

        DerivativeStats.climatestat[] tempstats = DerivativeStats.tempstat.values();
        DerivativeStats.climatestat[] prstats = DerivativeStats.precipstat.values();
        ArrayList<DerivativeStats.climatestat> stats = new ArrayList<DerivativeStats.climatestat>();
        stats.addAll(Arrays.asList(tempstats));
        stats.addAll(Arrays.asList(prstats));

        CountryDao countryDao = CountryDao.get();
        HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countryDao.getCountriesFromRegionName("Africa_MENA"));

        Set<String> unOrderedIsos = countryMap.keySet();
        TreeSet<String> isos = new TreeSet<String>();
        isos.addAll(unOrderedIsos);
        Iterator<String> codeIterator = isos.iterator();

        while (codeIterator.hasNext()) {
            String iso3Code = codeIterator.next();
            for (DerivativeStats.scenario scenario : DerivativeStats.scenario.values()) {


                for (DerivativeStats.climatestat stat : stats) {
                    if (stat.getId() <= 0) {
                        log.log(Level.WARNING, "got -1 for temp for {0}", stat);
                        continue;
                    }
                    if (!stat.isMonthly()) {
                        continue;
                    }

                    String url = SimpleAnnualEnsembleRequestBuilder.getURL(timePeriod, scenario, stat, iso3Code, monthly);

                    log.log(Level.FINE, "trying  {0}", url);
                    TNCRegexBufferedReaderProcessor processor = new TNCRegexBufferedReaderProcessor();
                    PageFetcher.readPage(url, processor);
                    tnccsv.DataFileHandler dfh = processor.getDfh();
                    log.log(Level.FINE, "number of links are {0}", dfh.getSize());
                    log.log(Level.FINE, "number of files {0} of type {1}", new Object[]{dfh.getFiles(fileExtension).size(), fileExtension});
                    ArrayList<String> yearlyFiles = dfh.getFiles(fileExtension, fileName);
                    InputStream is = null;

                    for (String s : yearlyFiles) {
                        try {

                            is = PageFetcher.getInputStream(s);
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String line = null;

                            while ((line = br.readLine()) != null) {
                                File f = new File(outputDirectory + iso3Code + "\\");
                                f.mkdir();

                                FileExportHelper.appendToFile(outputDirectory + iso3Code + "\\" + s.substring(s.lastIndexOf("/") + 1), line);
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(YearlyRequestBuilder.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                if (is != null) {
                                    is.close();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(SimpleAnnualEnsembleRequestBuilder.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }

                }
            }
        }
    }
}

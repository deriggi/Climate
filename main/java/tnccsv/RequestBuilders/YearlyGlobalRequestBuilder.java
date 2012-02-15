/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv.RequestBuilders;

import dao.country.CountryDao;
import domain.Country;
import tnccsv.DataFileHandler;
import export.util.FileExportHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import domain.DerivativeStats;
import tnccsv.PageFetcher;
import tnccsv.TNCRegexBufferedReaderProcessor;

/**
 *
 * @author wb385924
 */
public class YearlyGlobalRequestBuilder {

    // @TODO initialize the enums with these base urls
    private static String GLOBAL_ANNUAL = "http://184.72.162.13/global_annual/";
    private static final String FORWARD_SLASH = "/";
    private static final String DOT = ".";
    private static final String _ = "_";
    private static final Logger log = Logger.getLogger(YearlyGlobalRequestBuilder.class.getName());

    public static String getURL(DerivativeStats.time_period timePeriod, DerivativeStats.scenario scenario, DerivativeStats.climatestat climatestat, int run, DerivativeStats.temporal_aggregation temporalAggregation) {


        StringBuilder sb = new StringBuilder();
        sb.append(GLOBAL_ANNUAL);

        if (    (scenario.name().equals(DerivativeStats.scenario.a2.name())) || (scenario.name().equals(DerivativeStats.scenario.b1.name()) ) )   {
            sb.append("global_annual_a2b1");
        }else if((scenario.name().equals(DerivativeStats.scenario.a1b.name()))){
            sb.append("global_annual_a1b");
        }

        sb.append(_);
        
        if (timePeriod.compareTo(DerivativeStats.time_period.mid_century) == 0) {
            sb.append("midCen/");
        }else if (timePeriod.compareTo(DerivativeStats.time_period.end_century) == 0) {
            sb.append("endCen/");
        }
        else if (timePeriod.compareTo(DerivativeStats.time_period.baseline) == 0) {
            sb.append("midCen/");
        }

        sb.append("GlobalLand/");

        sb.append(climatestat);
        sb.append(FORWARD_SLASH);

        sb.append("ensemble");
        sb.append(_);
        sb.append(scenario);
        sb.append(FORWARD_SLASH);

        return sb.toString();

    }

    public static void main(String[] args) {

        DerivativeStats.time_period endOfCentury = DerivativeStats.time_period.end_century;
//        DerivativeStats.scenario scenario = DerivativeStats.scenario.b1;
        DerivativeStats.temporal_aggregation yearly = DerivativeStats.temporal_aggregation.yearly;
        DerivativeStats.stat_type mean = DerivativeStats.stat_type.mean;
        DerivativeStats.climatestat[] tempstats = DerivativeStats.tempstat.values();
        DerivativeStats.climatestat[] prstats = DerivativeStats.precipstat.values();
        ArrayList<DerivativeStats.climatestat> stats = new ArrayList<DerivativeStats.climatestat>();
        stats.addAll(Arrays.asList(tempstats));
        stats.addAll(Arrays.asList(prstats));
        List<DerivativeStats.gcm> gcms = Arrays.asList(DerivativeStats.gcm.values());

        ArrayList<DerivativeStats.map_file_name> fileNames = new ArrayList<DerivativeStats.map_file_name>();
//        fileNames.add(DerivativeStats.map_file_name.map_mean_ensemble_100);
        fileNames.add(DerivativeStats.map_file_name.map_mean_ensemble_50_);
        

        int run = 1;
        DerivativeStats.getInstance();

        for (DerivativeStats.climatestat stat : stats) {
        
            if (stat.getId() <= 0) {
                log.log(Level.WARNING, "got -1 for temp for {0}", stat);
//                continue;
            }
            for (DerivativeStats.map_file_name mapFileName : fileNames) {

                for (DerivativeStats.scenario scenario : DerivativeStats.scenario.values()) {

                    String url = YearlyGlobalRequestBuilder.getURL(endOfCentury, scenario, stat, run, yearly);
                    log.log(Level.INFO, "trying  {0}", url);
                    TNCRegexBufferedReaderProcessor processor = new TNCRegexBufferedReaderProcessor();
                    PageFetcher.readPage(url, processor);
                    DataFileHandler dfh = processor.getDfh();
                    log.log(Level.FINE, "number of links are {0}", dfh.getSize());
                    log.log(Level.FINE, "number of csv files {0}", dfh.getFiles(DataFileHandler.ASCII_FILE).size());
                    ArrayList<String> yearlyFiles = dfh.getFiles(DataFileHandler.ASCII_FILE, mapFileName);

                    for (String s : yearlyFiles) {
                        try {

                            InputStream is = PageFetcher.getInputStream(s);
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                File f = new File("S:\\GLOBAL\\ClimatePortal_2\\RENDERING\\JohnnyDAutoDownload\\" + stat + "\\");
                                f.mkdir();

                                FileExportHelper.appendToFile("S:\\GLOBAL\\ClimatePortal_2\\RENDERING\\JohnnyDAutoDownload\\" + stat + "\\" + s.substring(s.lastIndexOf("/") + 1), line);
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(YearlyRequestBuilder.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }
            }
        }

    }
}

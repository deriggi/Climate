/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.ensemble;

import shapefileloader.gcm.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
@Deprecated
public class P_EnsembleNameParser {

    private static final Logger log = Logger.getLogger(P_EnsembleNameParser.class.getName());
    static String testPath = "C:\\Users\\wb385924\\Documents\\NetBeansProjects\\GeoSDN\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1960-1999.shp.csv";

    protected String getStatType(String path) {

        if (path.indexOf("_clim") != -1) {

            return "mean";
        } else if (path.indexOf("_anom") != -1) {
            return "anom";
        }
        return null;
    }

    public P_EnsembleConfig parsePathName(String path) {
        try {
            String name = path.substring(path.lastIndexOf("\\") + 1);
            String[] parts = name.split("\\.");
            // if parts length is is 6 then it should be an ensemble

            String var = parts[1];
            String scenario = parts[2];
            
            if (scenario.startsWith("sres")) {
                scenario = scenario.substring(4);
            }
            int fYear = Integer.parseInt(parts[3].split("\\-")[0]);
            int tYear = Integer.parseInt(parts[3].split("\\-")[1]);

            String percentileString = parts[4];
            if(percentileString.equalsIgnoreCase("median")){
                percentileString = "50";
            }
            int percentile = Integer.parseInt(percentileString);


            P_GcmStatsProperties ds = P_GcmStatsProperties.getInstance();
            
            
            log.log(Level.FINE, "var is {0}", ds.getClimateStat(var));
            log.log(Level.FINE, "scenario is {0}", ds.getScenario(scenario));
            log.log(Level.FINE, "fyear is {0}", fYear);
            log.log(Level.FINE, "tyear is {0}", tYear);
            return new P_EnsembleConfig(ds.getStatType(getStatType(path)), ds.getScenario(scenario), ds.getClimateStat(var), fYear, tYear, percentile);
        } catch (Exception e) {
            return new P_EnsembleConfig();
        }



    }

    public void readDirectory(String dir) {
        P_GcmStatsProperties.getInstance();
        File[] files = new File(dir).listFiles();
        for (File f : files) {
            log.log(Level.FINE, "trying to parse {0}{1}", new Object[]{f.getParent(), f.getName()});

            log.info(parsePathName(f.getParent() + f.getName()).toString());
        }
    }

    public static void main(String[] args) {
        new P_EnsembleNameParser().parsePathName(testPath);
//        new OldMonthlyNameParser().readDirectory("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\");

    }
}

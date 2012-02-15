/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.GML.Version;
import shapefileloader.ensemble.P_EnsembleConfig;

/**
 *
 * @author wb385924
 */
public class P_NameParser {

    private static final Logger log = Logger.getLogger(P_NameParser.class.getName());
//    static String testPath = "C:\\Users\\wb385924\\Documents\\NetBeansProjects\\GeoSDN\\pcmdi_long_clim.pr.20c3m.1920-1939.10.shp";
    static String testPath = "C:\\Users\\wb385924\\statsMonthlyCsv\\_.anom.sresa2.futureA.tmax.days10th.10.json.shp.csv";
    static String testDir = "C:\\Users\\wb385924\\statsMonthlyCsv\\";
    private final String shp = ".shp";
    private final String SHP = ".SHP";
    private final String control = "control";
    private final String futureA = "futurea";
    private final String futureB = "futureb";

    protected String getStatType(String path) {

        if (path.indexOf("_clim") != -1 || path.indexOf("_.clim.") != -1) {

            return "mean";
        } else if (path.indexOf("_anom") != -1 || path.indexOf("_.anom.") != -1) {
            return "anom";
        }
        return null;
    }

    public P_Config parsePathName(String path) {
        String name = path.substring(path.lastIndexOf("\\") + 1);
        P_Config config = null;
        // rip off extension
        if (name.contains(shp)) {
            name = name.substring(0, name.lastIndexOf(shp));
        } else if (name.contains(SHP)) {
            name = name.substring(0, name.lastIndexOf(SHP));
        }
        String[] parts = name.split("\\.");
        // if parts length is is 6 then it should be an ensemble
        log.log(Level.INFO, "just split {0} has length {1}", new Object[]{name, parts.length});
        String lowerPath = path.toLowerCase();

        if (lowerPath.contains(futureA) || lowerPath.contains(futureB) || lowerPath.contains(control)) {
            config = parseStatisticPathName(path);
        } else if (parts.length == 4) {
            config = parseGcmPathName(name);
        } else if (parts.length == 5) {
            config = parseEnsemblePathName(name);
        } else {
            log.warning("name parser did not find a match, returning null");
            return config; // null
        }
        return config;
    }

    //pcmdi_long_anom.mri_cgcm2_3_2a.pr_sresa2.2020-2039.shp
    private P_GcmConfig parseGcmPathName(String path) {
        try {
            log.log(Level.INFO, "treating {0} as gcm path ", path);
            String name = path.substring(path.lastIndexOf("\\") + 1);
            String[] parts = name.split("\\.");
            // if parts length is is 6 then it should be an ensemble

            String gcm = parts[1];
            String var = parts[2].split("\\_")[0];
            String scenario = parts[2].split("\\_")[1];
            if (scenario.startsWith("sres")) {
                scenario = scenario.substring(4);
            }
            int fYear = Integer.parseInt(parts[3].split("\\-")[0]);
            int tYear = Integer.parseInt(parts[3].split("\\-")[1]);

            P_GcmStatsProperties ds = P_GcmStatsProperties.getInstance();

            log.log(Level.INFO, "gcm is {0}", ds.getGcm(gcm));
            log.log(Level.INFO, "var is {0}", ds.getClimateStat(var));
            log.log(Level.INFO, "type is {0}", ds.getStatType(getStatType(path)));
            log.log(Level.INFO, "scenario is {0}", ds.getScenario(scenario));
            log.log(Level.INFO, "fyear is {0}", fYear);
            log.log(Level.INFO, "tyear is {0}", tYear);
            return new P_GcmConfig(ds.getStatType(getStatType(path)), ds.getGcm(gcm), ds.getScenario(scenario), ds.getClimateStat(var), fYear, tYear);
        } catch (Exception e) {
            e.printStackTrace();
            return new P_GcmConfig();
        }
    }

    private P_EnsembleConfig parseEnsemblePathName(String path) {
        try {
            log.log(Level.INFO, "treating {0} as ensemble path ", path);
            String name = path.substring(path.lastIndexOf("\\") + 1);
            String[] parts = name.split("\\.");
            // if parts length is is 6 then it should be an ensemble
            // _.sresa2.pcmdi_long_anom.pr.sresa2.2020-2039.10.shp
            String var = parts[1];
            String scenario = parts[2];

            if (scenario.startsWith("sres")) {
                scenario = scenario.substring(4);
            }
            int fYear = Integer.parseInt(parts[3].split("\\-")[0]);
            int tYear = Integer.parseInt(parts[3].split("\\-")[1]);

            String percentileString = parts[4];
            if (percentileString.equalsIgnoreCase("median")) {
                percentileString = "50";
            }
            int percentile = Integer.parseInt(percentileString);


            P_GcmStatsProperties ds = P_GcmStatsProperties.getInstance();


            log.log(Level.INFO, "var is {0}", ds.getClimateStat(var));
            log.log(Level.INFO, "scenario is {0}", ds.getScenario(scenario));
            log.log(Level.INFO, "fyear is {0}", fYear);
            log.log(Level.INFO, "tyear is {0}", tYear);
            log.log(Level.INFO, "percentile is {0}", percentile);
            return new P_EnsembleConfig(ds.getStatType(getStatType(path)), ds.getScenario(scenario), ds.getClimateStat(var), fYear, tYear, percentile);
        } catch (Exception e) {
            return new P_EnsembleConfig();
        }
    }

    private int[] getYearRangeForStatsConfig(String path) {

        int[] range = new int[2];
        if (path == null) {
            return range;
        }
        String lowerPath = path.toLowerCase();

        if (lowerPath.contains(futureA)) {
            range[0] = 2046;
            range[1] = 2065;
        } else if (lowerPath.contains(futureB)) {
            range[0] = 2081;
            range[1] = 2100;
        } else if (lowerPath.contains(control)) {
            range[0] = 1961;
            range[1] = 2000;
        }

        return range;
    }

    private P_EnsembleConfig parseStatisticPathName(String path) {
        try {
            log.log(Level.INFO, "treating {0} as statistic path ", path);
            String name = path.substring(path.lastIndexOf("\\") + 1);
            int range[] = getYearRangeForStatsConfig(path);
            int fYear = range[0];
            int tYear = range[1];
            String[] parts = name.split("\\.");

            String var = parts[4] + "_" + parts[5];
            String scenario = parts[2];

            if (scenario.startsWith("sres")) {
                scenario = scenario.substring(4);
            }

            String percentileString = parts[6];
            if (percentileString.equalsIgnoreCase("median")) {
                percentileString = "50";
            }
            int percentile = Integer.parseInt(percentileString);
            P_GcmStatsProperties ds = P_GcmStatsProperties.getInstance();

            log.log(Level.INFO, "var is {0}", ds.getClimateStat(var));
            log.log(Level.INFO, "scenario is {0}", ds.getScenario(scenario));
            log.log(Level.INFO, "fyear is {0}", fYear);
            log.log(Level.INFO, "tyear is {0}", tYear);
            log.log(Level.INFO, "percentile is {0}", percentile);
            return new P_EnsembleConfig(ds.getStatType(getStatType(path)), ds.getScenario(scenario), ds.getClimateStat(var), fYear, tYear, percentile);
        } catch (Exception e) {
            return new P_EnsembleConfig();
        }

    }

    public void readDirectory(String dir) {
        P_GcmStatsProperties.getInstance();
        File[] files = new File(dir).listFiles();
        String  currentfile = null;
        try {
            for (File f : files) {
                currentfile = f.getAbsolutePath();
                log.log(Level.FINE, "trying to parse {0}{1}", new Object[]{f.getParent(), f.getName()});

                log.info(parsePathName(f.getParent() + f.getName()).toString());

            }
        } catch (Exception e) {
            
            System.out.println(e.getMessage() + " while parsing " + currentfile);
            
        }
    }

    public static void main(String[] args) {
        new P_NameParser().readDirectory(testDir);
//        new OldMonthlyNameParser().readDirectory("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\");

    }

    
}

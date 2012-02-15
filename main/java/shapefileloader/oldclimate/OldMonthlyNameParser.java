/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.oldclimate;

import shapefileloader.gcm.P_GcmStatsProperties;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class OldMonthlyNameParser {
    private static final Logger log = Logger.getLogger(OldMonthlyNameParser.class.getName());

    static String testPath = "C:\\Users\\wb385924\\monthlyclim\\pcmdi_long_clim.mpi_echam5.pr_20c3m.1920-1939.shp.csv";

    private String getStatType(String path){
       
        if(path.indexOf("_clim.") != -1){
            
            return "mean";
        }else if(path.indexOf("_anom.") != -1){
            return "anom";
        }
        return null;
    }
    public OldMonthlyCellularConfig parsePathName(String path){
        String name = path.substring(path.lastIndexOf("\\")+1);
        String[] parts = name.split("\\.");
        String gcm = parts[1];
        String var = parts[2].split("\\_")[0];
        String scenario = parts[2].split("\\_")[1];
        if(scenario.startsWith("sres")){
            scenario = scenario.substring(4);
        }
        P_GcmStatsProperties ds = P_GcmStatsProperties.getInstance();
        


        int fYear = Integer.parseInt(parts[3].split("\\-")[0]);
        int tYear = Integer.parseInt(parts[3].split("\\-")[1]);

        log.log(Level.INFO, "gcm is {0}",ds.getGcm(gcm));

        log.log(Level.INFO, "var is {0}",ds.getClimateStat(var));
        log.log(Level.INFO, "scenario is {0}", ds.getScenario(scenario));
        log.log(Level.INFO, "fyear is {0}",fYear);
        log.log(Level.INFO, "tyear is {0}",tYear);

       return new OldMonthlyCellularConfig(ds.getStatType(getStatType(path)), ds.getGcm(gcm), ds.getScenario(scenario), ds.getClimateStat(var), fYear, tYear);

        
    }

    public void readDirectory(String dir){
        P_GcmStatsProperties.getInstance();
        File[] files = new File(dir).listFiles();
        for(File f: files){
            log.log(Level.FINE, "trying to parse {0}{1}", new Object[]{f.getParent(), f.getName()});
            
            log.info(parsePathName(f.getParent() + f.getName()).toString());
        }
    }


    public static void main(String[] args){
          new OldMonthlyNameParser().parsePathName(testPath);
//        new OldMonthlyNameParser().readDirectory("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\");
        
    }
}

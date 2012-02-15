/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.gcm.areaidstrategy.AreaIdGetterStrategyFactory;

/**
 * read from a list of monthly gcm csv files and insert into db
 * @author wb385924
 */
public class GcmNormalizer {

//    private enum AREA {basin, region, country;}

    public static void main(String[] args) {
        GcmNormalizer n = new GcmNormalizer();
//        n.loadMonthlyConfigsAtRoot("C:\\Users\\wb385924\\statsbasin\\monthly\\");
//        n.loadAnnualFilesAtRoot("C:\\Users\\wb385924\\countryb1avg\\");
        n.loadAnnualFilesAtRoot("C:\\Users\\wb385924\\Dropbox\\clusteroutput\\region 10\\region\\stats\\annual\\");

    }
    private final static Logger log = Logger.getLogger(GcmNormalizer.class.getName());
    private P_NameParser nameParser = new P_NameParser();
    private String NULL = "null";
    public GcmNormalizer() {
    }

    public void loadMonthlyFilesAtRoot(String rootPath) {
        // read a file
        File rootDir = new File(rootPath);
        String[] files = rootDir.list();

        for (String s : files) {
            File csvFile = new File(rootPath + s);
            if (!csvFile.isDirectory()) {
                P_Config config = nameParser.parsePathName(rootPath + s);
                if (config.isCompleteIgnoringAreaValueMonth()) {

                    loadFile(csvFile, config, false);
                } else {
                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
                }
            }
        }
        // get id or insert and get id for config
        // insert data
    }

    public void loadAnnualFilesAtRoot(String rootPath) {
        // read a file
        File rootDir = new File(rootPath);
        String[] files = rootDir.list();

        for (String s : files) {
            File csvFile = new File(rootPath + s);
            if (!csvFile.isDirectory()) {
                P_Config config = nameParser.parsePathName(rootPath + s);
                config.setMonth(-1);
                if (config.isCompleteIgnoringAreaValueMonth()) {
                    loadFile(csvFile, config, true);
                } else {
                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
                }
            }
        }
        // get id or insert and get id for config
        // insert data
    }

    public void loadMonthlyConfigsAtRoot(String rootPath) {
        // read a file
        File rootDir = new File(rootPath);
        String[] files = rootDir.list();

        for (String s : files) {
            File csvFile = new File(rootPath + s);
            if (!csvFile.isDirectory()) {
                log.log(Level.INFO, "{0}{1}", new Object[]{rootPath, s});
                P_Config config = nameParser.parsePathName(rootPath + s);
                if (config.isCompleteIgnoringAreaValueMonth()) {
                    saveConfigForAllMonths(config,rootPath, s);

                } else {
                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
                }
            }
        }
        // get id or insert and get id for config
        // insert data
    }

    public void loadAnnualConfigsAtRoot(String rootPath) {
        // read a file
        File rootDir = new File(rootPath);
        String[] files = rootDir.list();

        for (String s : files) {
            File csvFile = new File(rootPath + s);
            if (!csvFile.isDirectory()) {
                log.log(Level.INFO, "{0}{1}", new Object[]{rootPath, s});
                P_Config config = nameParser.parsePathName(rootPath + s);
                config.setMonth(-1);
                if (config.isCompleteIgnoringAreaValueMonth()) {
                    saveConfigForAnnual(config,rootPath, s);

                } else {
                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
                }
            }
        }
        // get id or insert and get id for config
        // insert data
    }

    private void saveAreaConfig(P_Config config,int configId, int areaId, double data) {
        P_ConfigAreaDao dao = config.getConfigAreaDao();

        dao.insertAreaValue(configId, areaId, data);

    }

    private void saveConfigForAllMonths(P_Config config,String rootPath, String fileName) {
        int normalcount = 0;

        P_ConfigDao dao = config.getConfigDao();
        if (new File(rootPath + fileName).isDirectory()) {
            return;
        }
        
        if (!config.isCompleteIgnoringAreaValueMonth()) {
            log.log(Level.WARNING, "not clomplete for {0}", fileName);
        } else {
            for (int i = 0; i < 12; i++) {
                config.setMonth(i + 1);
                dao.insertConfig(config);
            }
            normalcount++;
        }
    }

    private void saveConfigForAnnual(P_Config config,String rootPath, String fileName) {
        int normalcount = 0;

        P_ConfigDao dao = config.getConfigDao();
        if (new File(rootPath + fileName).isDirectory()) {
            return;
        }
//        P_Config config = nameParser.parsePathName(rootPath + fileName);
        if (!config.isCompleteIgnoringAreaValueMonth()) {
            log.log(Level.WARNING, "not clomplete for {0}", fileName);
        } else {

            config.setMonth(-1);
            dao.insertConfig(config);
            normalcount++;
        }
    }

    public void testGetConfig(String rootPath, String fileName) {
        int normalcount = 0;
        int id = -1;

        if (new File(rootPath + fileName).isDirectory()) {
            return;
        }
        P_Config config = nameParser.parsePathName(rootPath + fileName);
        P_ConfigDao dao = config.getConfigDao();
        if (!config.isCompleteIgnoringAreaValueMonth()) {
            log.log(Level.WARNING, "not clomplete for {0}", fileName);
        } else {
            for (int i = 0; i < 12; i++) {
                config.setMonth(i + 1);
                id = dao.getConfigId(config);
                if (id != -1) {
                    normalcount++;
                } else {
                    log.log(Level.WARNING, "config not found when looking for {0}", fileName);
                }

            }
            log.log(Level.FINE, "{0} normal configs", normalcount);

        }

    }

    private void loadFile(File file, P_Config config, boolean isAnnual) {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (isAnnual) {
                    processLineAsAnnual(line, config);
                } else {
                    processLineAsMonthly(line, config);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(GcmNormalizer.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(GcmNormalizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    

    private void processLineAsMonthly(String csvLine, P_Config config) {
        P_ConfigDao configDao = config.getConfigDao();

        String[] parts = csvLine.split(",");
        if (parts.length != 13) {
            log.log(Level.WARNING, "a potentially not valid line of monthly data {0}", csvLine);
            return;
        }

//        int countryId = countryMap.get(parts[0]).getId();
        int areaId = AreaIdGetterStrategyFactory.getIdStrategy(parts[0]).getAreaId(parts[0]);

        for (int i = 1; i < 13; i++) {
            if(parts[i] == null || parts[i].equalsIgnoreCase(NULL)){
                continue;
            }
            double value = Double.parseDouble(parts[i]);
            config.setMonth(i);
            int configId = configDao.getConfigId(config);

            // config id, area are valid?
            if (configId != -1 && areaId != -1 && config.isIsAnnual() == false) {

                saveAreaConfig(config,configId, areaId, value);
            }

        }
    }

    private void processLineAsAnnual(String csvLine, P_Config config) {
        P_ConfigDao configDao = config.getConfigDao();
        if (!config.isIsAnnual()) {
            log.warning("config is set as monthly but trying to save as annual!");
        }
        String[] parts = csvLine.split(",");
        if(parts.length > 2){
            log.log(Level.WARNING, "a potentially not valid line of annual data {0}", csvLine);
            return;
        }
//        int countryId = countryMap.get(parts[0]).getId();
        int areaId = AreaIdGetterStrategyFactory.getIdStrategy(parts[0]).getAreaId(parts[0]);

        if(parts[1] == null || parts[1].equalsIgnoreCase(NULL)){
            log.log(Level.WARNING,"found null data val: {0}",csvLine);
            return;
        }
        double value = Double.parseDouble(parts[1]);

        int configId = configDao.getConfigId(config);

        // config id, area are valid?
        if (configId != -1 && areaId != -1) {

            saveAreaConfig(config,configId, areaId, value);
        }
    }
}

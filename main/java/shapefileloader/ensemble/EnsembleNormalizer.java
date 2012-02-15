/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.ensemble;

/**
 * read from a list of monthly gcm csv files and insert into db
 * @author wb385924
 */
public class EnsembleNormalizer {

//    public static void main(String[] args) {
//        EnsembleNormalizer n = new EnsembleNormalizer();
//        n.loadAnnualConfigsAtRoot("C:\\Users\\wb385924\\shape2csvAnnual\\");
//        n.loadAnnualFilesAtRoot("C:\\Users\\wb385924\\shape2csvAnnual\\");
//    }
//
//    private final static Logger log = Logger.getLogger(EnsembleNormalizer.class.getName());
//    private HashMap<String, Country> countryMap = null;
//    private P_GcmConfigDao configDao;
//    private P_GcmNameParser nameParser = new P_GcmNameParser();
//
//
//
//    public EnsembleNormalizer() {
//        CountryDao dao = CountryDao.get();
//        countryMap = dao.getCountriesAsMap(dao.getCountries());
//        configDao = P_GcmConfigDao.get();
//
//    }
//
//    public void loadMonthlyFilesAtRoot(String rootPath) {
//        // read a file
//        File rootDir = new File(rootPath);
//        String[] files = rootDir.list();
//
//        for (String s : files) {
//            File csvFile = new File(rootPath + s);
//            if (!csvFile.isDirectory()) {
//                P_Config config = nameParser.parsePathName(rootPath + s);
//                if (config.isCompleteIgnoringAreaValueMonth()) {
//
//                    loadFile(csvFile, config, false);
//                } else {
//                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
//                }
//            }
//        }
//        // get id or insert and get id for config
//        // insert data
//    }
//
//    public void loadAnnualFilesAtRoot(String rootPath) {
//        // read a file
//        File rootDir = new File(rootPath);
//        String[] files = rootDir.list();
//
//        for (String s : files) {
//            File csvFile = new File(rootPath + s);
//            if (!csvFile.isDirectory()) {
//                P_GcmConfig config = nameParser.parsePathName(rootPath + s);
//                config.setMonth(-1);
//                if (config.isCompleteIgnoringAreaValueMonth()) {
//                    loadFile(csvFile, config, true);
//                } else {
//                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
//                }
//            }
//        }
//        // get id or insert and get id for config
//        // insert data
//    }
//
//    public void loadConfigsAtRoot(String rootPath) {
//        // read a file
//        File rootDir = new File(rootPath);
//        String[] files = rootDir.list();
//
//        for (String s : files) {
//            File csvFile = new File(rootPath + s);
//            if (!csvFile.isDirectory()) {
//                log.log(Level.INFO, "{0}{1}", new Object[]{rootPath, s});
//                P_GcmConfig config = nameParser.parsePathName(rootPath + s);
//                if (config.isCompleteIgnoringAreaValueMonth()) {
//                    saveConfigForAllMonths(rootPath, s);
//
//                } else {
//                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
//                }
//            }
//        }
//        // get id or insert and get id for config
//        // insert data
//    }
//
//    public void loadAnnualConfigsAtRoot(String rootPath) {
//        // read a file
//        File rootDir = new File(rootPath);
//        String[] files = rootDir.list();
//
//        for (String s : files) {
//            File csvFile = new File(rootPath + s);
//            if (!csvFile.isDirectory()) {
//                log.log(Level.INFO, "{0}{1}", new Object[]{rootPath, s});
//                P_GcmConfig config = nameParser.parsePathName(rootPath + s);
//                config.setMonth(-1);
//                if (config.isCompleteIgnoringAreaValueMonth()) {
//                    saveConfigForAnnual(rootPath, s);
//
//                } else {
//                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
//                }
//            }
//        }
//        // get id or insert and get id for config
//        // insert data
//    }
//
//    private void saveAreaConfig(int configId, int areaId, double data) {
//        P_GcmConfigAreaDao dao = P_GcmConfigAreaDao.get();
//        dao.insertGcmArea(configId, areaId, data);
//
//    }
//
//    private void saveConfigForAllMonths(String rootPath, String fileName) {
//        int normalcount = 0;
//
//        P_GcmConfigDao dao = P_GcmConfigDao.get();
//        if (new File(rootPath + fileName).isDirectory()) {
//            return;
//        }
//        P_GcmConfig config = nameParser.parsePathName(rootPath + fileName);
//        if (!config.isCompleteIgnoringAreaValueMonth()) {
//            log.log(Level.WARNING, "not clomplete for {0}", fileName);
//        } else {
//            for (int i = 0; i < 12; i++) {
//                config.setMonth(i + 1);
//                dao.insertConfig(config);
//            }
//            normalcount++;
//        }
//    }
//
//    private void saveConfigForAnnual(String rootPath, String fileName) {
//        int normalcount = 0;
//
//        P_GcmConfigDao dao = P_GcmConfigDao.get();
//        if (new File(rootPath + fileName).isDirectory()) {
//            return;
//        }
//        P_Config config = nameParser.parsePathName(rootPath + fileName);
//        if (!config.isCompleteIgnoringAreaValueMonth()) {
//            log.log(Level.WARNING, "not clomplete for {0}", fileName);
//        } else {
//
//            config.setMonth(-1);
//            dao.insertConfig(config);
//            normalcount++;
//        }
//    }
//
//    public void testGetConfig(String rootPath, String fileName) {
//        int normalcount = 0;
//        int id = -1;
//
//        P_GcmConfigDao dao = P_GcmConfigDao.get();
//        if (new File(rootPath + fileName).isDirectory()) {
//            return;
//        }
//        P_Config config = nameParser.parsePathName(rootPath + fileName);
//        if (!config.isCompleteIgnoringAreaValueMonth()) {
//            log.log(Level.WARNING, "not clomplete for {0}", fileName);
//        } else {
//            for (int i = 0; i < 12; i++) {
//                config.setMonth(i + 1);
//                id = dao.getConfigId(config);
//                if (id != -1) {
//                    normalcount++;
//                } else {
//                    log.log(Level.WARNING, "config not found when looking for {0}", fileName);
//                }
//
//            }
//            log.log(Level.FINE, "{0} normal configs", normalcount);
//
//        }
//
//    }
//
//    private void loadFile(File file, P_Config config, boolean isAnnual) {
//
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                if (isAnnual) {
//                    processLineAsAnnual(line, config);
//                } else {
//                    processLineAsMonthly(line, config);
//                }
//            }
//
//        } catch (IOException ex) {
//            Logger.getLogger(EnsembleNormalizer.class.getName()).log(Level.SEVERE, null, ex);
//
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(EnsembleNormalizer.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//
//        }
//    }
//
//    private void processLineAsMonthly(String csvLine, P_GcmConfig config) {
//
//        String[] parts = csvLine.split(",");
//        if (parts.length != 13) {
//            log.log(Level.WARNING, "a potentially not valid line of data {0}", csvLine);
//            return;
//        }
//
//        int countryId = countryMap.get(parts[0]).getId();
//
//        for (int i = 1; i < 13; i++) {
//            double value = Double.parseDouble(parts[i]);
//            config.setMonth(i);
//            int configId = configDao.getConfigId(config);
//
//            // config id, area are valid?
//            if (configId != -1 && countryId != -1 && config.isIsAnnual() == false) {
//
//                saveAreaConfig(configId, countryId, value);
//            }
//
//        }
//    }
//
//    private void processLineAsAnnual(String csvLine, P_GcmConfig config) {
//        if(!config.isIsAnnual()){
//            log.warning("config is set as monthly but trying to save as annual!");
//        }
//        String[] parts = csvLine.split(",");
//        int countryId = countryMap.get(parts[0]).getId();
//        double value = Double.parseDouble(parts[1]);
//
//        int configId = configDao.getConfigId(config);
//
//        // config id, area are valid?
//        if (configId != -1 && countryId != -1) {
//
//            saveAreaConfig(configId, countryId, value);
//        }
//
//
//
//
//    }
}

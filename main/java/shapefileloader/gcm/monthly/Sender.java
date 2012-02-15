/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm.monthly;

import shapefileloader.gcm.P_GcmConfigDao;
import shapefileloader.gcm.P_GcmConfigAreaDao;
import shapefileloader.gcm.P_GcmConfig;
import com.thoughtworks.xstream.XStream;
import dao.country.CountryDao;
import domain.Country;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.gcm.P_Config;
import shapefileloader.gcm.P_ConfigDao;
import shapefileloader.gcm.P_NameParser;

/**
 * read from a list of monthly gcm csv files and insert into db
 * @author wb385924
 */
public class Sender {

    public static void main(String[] args) {
        P_Config config = new Sender().testGetConfig("C:\\Users\\wb385924\\ensembleAnnualCsv\\", "pcmdi_long_anom.pr.sresa2.2020-2039.10.shp");
        String xml = new XStream().toXML(config);
        System.out.println(xml);
    }
    private final static Logger log = Logger.getLogger(Sender.class.getName());
    private HashMap<String, Country> countryMap = null;
    private P_GcmConfigDao configDao;
    private P_NameParser nameParser = new P_NameParser();
    private List<P_GcmConfig> configsToSend = new ArrayList<P_GcmConfig>();

    ;

    public Sender() {
        CountryDao dao = CountryDao.get();
        countryMap = dao.getCountriesAsMap(dao.getCountries());
        configDao = P_GcmConfigDao.get();

    }

    public void read(String rootPath) {
        // read a file
        File rootDir = new File(rootPath);
        String[] files = rootDir.list();

        for (String s : files) {
            File csvFile = new File(rootPath + s);
            if (!csvFile.isDirectory()) {
                P_Config config = nameParser.parsePathName(rootPath + s);
                if (config.isCompleteIgnoringAreaValueMonth()) {

                    readFile(csvFile, config);
                } else {
                    log.log(Level.WARNING, "could not resolve config for file {0}", s);
                }
            }
        }
        // get id or insert and get id for config
        // insert data
    }

    public void saveAreaConfig(int configId, int areaId, double data) {
        P_GcmConfigAreaDao dao = P_GcmConfigAreaDao.get();
        dao.insertAreaValue(configId, areaId, data);

    }

    public void saveConfigForAllMonths(String rootPath, String s) {
        int normalcount = 0;

        P_GcmConfigDao dao = P_GcmConfigDao.get();
        if (new File(rootPath + s).isDirectory()) {
            return;
        }
        P_Config config = nameParser.parsePathName(rootPath + s);
        if (!config.isCompleteIgnoringAreaValueMonth()) {
            log.log(Level.WARNING, "not clomplete for {0}", s);
        } else {
            for (int i = 0; i < 12; i++) {
                config.setMonth(i + 1);
                dao.insertConfig((P_GcmConfig) config);
            }
            normalcount++;
        }
    }

    public P_Config testGetConfig(String rootPath, String fileName) {
        int normalcount = 0;
        int id = -1;

        
        if (new File(rootPath + fileName).isDirectory()) {
            return null;
        }
        P_Config config = nameParser.parsePathName(rootPath + fileName);
        P_ConfigDao dao = config.getConfigDao();
        if (!config.isCompleteIgnoringAreaValueMonth()) {
            log.log(Level.WARNING, "not clomplete for {0}", fileName);
        } else {
            for (int i = 0; i < 12; i++) {
                config.setMonth(i + 1);
                id = dao.getConfigId( config);
                if (id != -1) {
                    normalcount++;
                } else {
                    log.log(Level.WARNING, "config not found when looking for {0}", fileName);
                }

            }
            log.log(Level.FINE, "{0} normal configs", normalcount);

        }
        return config;

    }

    private void readFile(File file, P_Config config) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while ((line = br.readLine()) != null) {
                parseLine(line, config);
            }

        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    private void parseLine(String csvLine, P_Config config) {

        String[] parts = csvLine.split(",");
        if (parts.length != 13) {
            log.log(Level.WARNING, "a potentially not valid line of data {0}", csvLine);
            return;
        }

        int countryId = countryMap.get(parts[0]).getId();

        for (int i = 1; i < 13; i++) {
            double value = Double.parseDouble(parts[i]);

            int configId = configDao.getConfigId((P_GcmConfig) config);

            // config id, area are valid?
            if (configId != -1 && countryId != -1) {
                config.setAreaId(countryId);
                config.setValue(value);
                config.setMonth(i);
                configsToSend.add((P_GcmConfig) config);
                if (configsToSend.size() == 150) {
                    try {
                        sendData(configsToSend);
                        Thread.sleep(2500);
                    } catch (Exception ex) {
                        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }


    }

    public void sendData(List<P_GcmConfig> configs) throws Exception {
//        URL url = new URL("http://64.95.129.89:8080/climateweb/testpost");
//        URLConnection connection = url.openConnection();
//        connection.setDoOutput(true);
//        OutputStreamWriter out = new OutputStreamWriter(
//                connection.getOutputStream());
//        out.write("string=" + xml);
//        out.close();
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(
//                connection.getInputStream()));
//        String decodedString;
//        while ((decodedString = in.readLine()) != null) {
//            log.log(Level.INFO, "server response: {0}", decodedString);
//        }
//        in.close();
    }
}

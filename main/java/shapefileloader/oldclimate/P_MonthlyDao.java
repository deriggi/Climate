/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.oldclimate;

import shapefileloader.gcm.P_GcmStatsProperties;
import shapefileloader.gcm.P_NameParser;
import shapefileloader.gcm.P_GcmConfig;
import com.thoughtworks.xstream.XStream;
import dao.country.CountryDao;
import database.DBUtils;
import domain.Country;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.gcm.P_Config;

/**
 *
 * @author wb385924
 */
public class P_MonthlyDao {

    private static P_MonthlyDao dao = null;
    private final String AVG = "avg";
    private final String insert = "insert into p_monthly (p_monthly_o_stat_type_id, p_monthly_o_var_id, p_monthly_from_year, p_monthly_to_year, p_monthly_month, p_monthly_value, p_monthly_area_id, p_monthly_gcm_id, p_monthly_scenario_id) values(?,?,?,?,?,?,?,?,?)";
    private final String select_contained_points = "select o_cell_id from o_cell where st_intersects((select boundary_shape from boundary where boundary_area_id = ?),o_cell_geom)";
    private static final Logger log = Logger.getLogger(P_MonthlyDao.class.getName());

    public static P_MonthlyDao get() {
        if (dao == null) {
            dao = new P_MonthlyDao();
        }

        return dao;
    }

    public void load(){
         P_GcmStatsProperties.getInstance();
        long t0 = new Date().getTime();
        String root = "C:\\Users\\wb385924\\monthlyclim\\";
        File rootDir = new File(root);
        File[] files = rootDir.listFiles();
        for (File csvFile : files) {
            if (!csvFile.isDirectory()) {
                new P_MonthlyDao().parseAndLoadCSV(csvFile.getAbsolutePath());
            }
        }
//        new P_MonthlyDao().parseAndLoadCSV("C:\\Users\\wb385924\\Documents\\NetBeansProjects\\GeoSDN\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1920-1939.shp.csv");
//        new P_MonthlyDao().parseAndLoadCSV("C:\\Users\\wb385924\\Documents\\NetBeansProjects\\GeoSDN\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1940-1959.shp.csv");
//        new P_MonthlyDao().parseAndLoadCSV("C:\\Users\\wb385924\\Documents\\NetBeansProjects\\GeoSDN\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1960-1979.shp.csv");
//        new P_MonthlyDao().parseAndLoadCSV("C:\\Users\\wb385924\\Documents\\NetBeansProjects\\GeoSDN\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1960-1999.shp.csv");
        P_NameParser nameParser = new P_NameParser();
        P_Config config = nameParser.parsePathName("C:\\Users\\wb385924\\Documents\\NetBeansProjects\\GeoSDN\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1960-1999.shp.csv");
        TreeMap<Integer, Double> map = new TreeMap<Integer, Double>();
        map = P_MonthlyDao.get().getOldMonthlyData(712, config);

        long t1 = new Date().getTime();

        log.log(Level.INFO, "it took {0}", (t1 - t0) / 1000.0f);
        log.log(Level.INFO, "map size {0} ", map.size());
        Set<Integer> keys = map.keySet();
        for (Integer i : keys) {
            log.info(Double.toString(map.get(i)));
        }
    }

    public static void main(String[] args) {
       P_MonthlyDao.get().load();

    }

    public void parseAndLoadCSV(String filePath) {
        BufferedReader br = null;
        try {
            CountryDao countryDao = CountryDao.get();
            HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countryDao.getCountries());
            File f = new File(filePath);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                P_NameParser nameParser = new P_NameParser();
                P_Config config = nameParser.parsePathName(f.getAbsolutePath());
                Country c = countryMap.get(parts[0]);
                if (c == null) {
                    log.log(Level.WARNING, "do not have country for!{0}", parts[0]);
                }
                if (parts.length != 13) {
                    log.log(Level.WARNING, "csv line is not of correct size!{0}", parts.length);
                    continue;
                }
                config.setAreaId(c.getId());

                for (int i = 0; i < 12; i++) {
                    double dataVal = Double.parseDouble(parts[i + 1]);
                    config.setMonth(i + 1);
                    config.setValue(dataVal);
                    saveMonthlyData(config);

                }

            }



        } catch (FileNotFoundException ex) {
            Logger.getLogger(P_MonthlyDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(P_MonthlyDao.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(P_MonthlyDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private final String selectAll = "select * from p_monthly inner join country on p_monthly_area_id = country_id where p_monthly_gcm_id in (13,5)";

    public void sendAllTheConfigs() {
        Connection c = null;
        PreparedStatement ps = null;


        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(selectAll);

            ResultSet rs = ps.executeQuery();

            P_GcmStatsProperties ds = P_GcmStatsProperties.getInstance();
            List<P_GcmConfig> configs = new ArrayList<P_GcmConfig>();

            while (rs.next()) {
                P_GcmStatsProperties.stat_type stype = ds.getStatTypeById(rs.getInt("p_monthly_o_stat_type_id"));
                P_GcmStatsProperties.climatestat cstat = ds.getClimateStatById(rs.getInt("p_monthly_o_var_id"));
                int fyear = rs.getInt("p_monthly_from_year");
                int tyear = rs.getInt("p_monthly_to_year");
                int month = rs.getInt("p_monthly_month");
                double val = rs.getDouble("p_monthly_value");
                int areaId = rs.getInt("p_monthly_area_id");
                String iso3 = rs.getString("country_iso_3");
                P_GcmStatsProperties.gcm gcm =ds.getGCMById(rs.getInt("p_monthly_gcm_id"));
                P_GcmStatsProperties.scenario sc = ds.getScenarioById(rs.getInt("p_monthly_scenario_id"));
                P_GcmConfig config = new P_GcmConfig(stype, gcm, sc, cstat, fyear, tyear);
                config.setAreaId(areaId);
                config.setMonth(month);
                config.setIso3(iso3);
                config.setValue(val);
                if(config.isComplete()){
                    
                    configs.add(config);
                    if(configs.size() == 150){
                        log.info("sending configs!");
                        try {
                            sendData(configs);
                            configs.clear();
                            Thread.sleep(2000);
                            
                        } catch (Exception ex) {
                            Logger.getLogger(P_MonthlyDao.class.getName()).log(Level.SEVERE, null, ex);
                            configs.clear();
                        }
                    }
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(P_MonthlyDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }

    }

    public void sendData(List<P_GcmConfig> configs) throws Exception {
        String xml = new XStream().toXML(configs);
        URL url = new URL("http://64.95.129.89:8080/climateweb/testpost");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());
        out.write("string=" + xml);
        out.close();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                connection.getInputStream()));
        String decodedString;
        while ((decodedString = in.readLine()) != null) {
            log.log(Level.INFO, "server response: {0}", decodedString);
        }
        in.close();
    }

private final String selectExact =
            "select p_monthly_month, p_monthly_value from p_monthly where p_monthly_o_stat_type_id = ? AND "
            + "p_monthly_o_var_id = ? AND p_monthly_from_year = ? and p_monthly_to_year = ? and p_monthly_area_id = ? "
            + "and p_monthly_gcm_id = ? and p_monthly_scenario_id = ? and p_monthly_month = ?";
    public boolean doesDataExist(int areaId, P_Config config) {

        Connection c = null;
        PreparedStatement ps = null;


        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(selectExact);
            ps.setInt(1, config.getStatType().getId());
            ps.setInt(2, config.getStat().getId());
            ps.setInt(3, config.getfYear());
            ps.setInt(4, config.gettYear());

            ps.setInt(5, areaId);
            ps.setInt(6, config.getGcm().getGcmId());
            ps.setInt(7, config.getScenario().getId());
            ps.setInt(8, config.getMonth());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return true;

            }
        } catch (SQLException ex) {
            Logger.getLogger(P_MonthlyDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        return false;

    }

    public void saveMonthlyData(P_Config config) {
        if (config == null) {
            return;
        }
        if (doesDataExist(config.getAreaId(), config)) {
            log.info("skipping p_monthly data because we already have it");
            return;
        }

        Connection c = null;
        PreparedStatement ps = null;
        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(insert);
            ps.setInt(1, config.getStatType().getId());
            ps.setInt(2, config.getStat().getId());
            ps.setInt(3, config.getfYear());
            ps.setInt(4, config.gettYear());
            ps.setInt(5, config.getMonth());
            ps.setDouble(6, config.getValue());
            ps.setLong(7, config.getAreaId());
            ps.setInt(8, config.getGcm().getGcmId());
            ps.setInt(9, config.getScenario().getId());
            ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(P_MonthlyDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }

    }
    private final String select =
            "select p_monthly_month, p_monthly_value from p_monthly where p_monthly_o_stat_type_id = ? AND "
            + "p_monthly_o_var_id = ? AND p_monthly_from_year = ? and p_monthly_to_year = ? and p_monthly_area_id = ? "
            + "and p_monthly_gcm_id = ? and p_monthly_scenario_id = ?";

    public TreeMap<Integer, Double> getOldMonthlyData(int areaId, P_Config config) {

        Connection c = null;
        PreparedStatement ps = null;

        TreeMap<Integer, Double> monthVals = new TreeMap<Integer, Double>();
        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(select);
            ps.setInt(1, config.getStatType().getId());
            ps.setInt(2, config.getStat().getId());
            ps.setInt(3, config.getfYear());
            ps.setInt(4, config.gettYear());

            ps.setInt(5, areaId);
            ps.setInt(6, config.getGcm().getGcmId());
            ps.setInt(7, config.getScenario().getId());
            ResultSet rs = ps.executeQuery();


            while (rs.next()) {

                monthVals.put(rs.getInt("p_monthly_month"), rs.getDouble("p_monthly_value"));
//                log.info("avg is " + d);

            }
        } catch (SQLException ex) {
            Logger.getLogger(P_MonthlyDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        return monthVals;

    }
}

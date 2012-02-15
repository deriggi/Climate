/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.ensemble;

import database.DBUtils;
import domain.web.AnnualEnsembleDatum;
import domain.web.EnsembleDatum;
import domain.web.MonthlyEnsembleDatum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.gcm.P_Config;
import shapefileloader.gcm.P_ConfigAreaDao;
import shapefileloader.gcm.P_GcmStatsProperties;

/**
 *
 * @author wb385924
 */
public class P_EnsembleConfigAreaDao implements P_ConfigAreaDao{

    private static P_EnsembleConfigAreaDao dao = null;

    private static final Logger log = Logger.getLogger(P_EnsembleConfigAreaDao.class.getName());
    
    private P_EnsembleConfigAreaDao(){
        
    }
    public static P_EnsembleConfigAreaDao get(){
        if(dao == null){
            dao = new P_EnsembleConfigAreaDao();
        }
        return dao;
    }
   
    private String INSERT_ENSEMBLE_CONFIG_AREA = "insert into p_ensemble_config_area (p_ensemble_config_area_config_id, p_ensemble_config_area_area_id, p_ensemble_config_area_value) values (?, ?, ?)";

    public void insertAreaValue(int configId, int areaId, double value) {

        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = DBUtils.getConnection();
            ps = c.prepareStatement(INSERT_ENSEMBLE_CONFIG_AREA);
            ps.setInt(1, configId);
            ps.setInt(2, areaId);
            ps.setDouble(3, value);
            int executeUpdate = ps.executeUpdate();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DBUtils.close(c, ps, null);
        }
    }
    private String GET_ENSEMBLE_CONFIG_AREA = "select p_ensemble_config_month, p_ensemble_config_area_value from  p_ensemble_config inner join p_ensemble_config_area on p_ensemble_config_id = p_ensemble_config_area_config_id and "
            + "  p_ensemble_config_area_area_id = ? and p_ensemble_config_o_stat_type_id = ? and  p_ensemble_config_o_var_id = ?  and p_ensemble_config_from_year = ?  and p_ensemble_config_to_year = ? and  "
            + "  p_ensemble_config_scenario_id = ? and p_ensemble_config_percentile = ? ";

    private final String monthlyConstraint = "and p_ensemble_config_month != -1";
    private final String annualConstraint = "and p_ensemble_config_month = -1";

    private final String p_ensemble_config_month = "p_ensemble_config_month";
    private final String p_ensemble_config_area_value = "p_ensemble_config_area_value";
    private final String p_ensemble_config_from_year = "p_ensemble_config_from_year";
    private final String p_ensemble_config_to_year = "p_ensemble_config_to_year";
    private final String p_ensemble_config_scenario_id = "p_ensemble_config_scenario_id";
    private final String p_ensemble_config_percentile = "p_ensemble_config_percentile";

    private String constructMonthlyOrAnnualQueryString(String basePath, boolean isAnnual){
        StringBuilder sb = new StringBuilder();
        sb.append(basePath);
        if(isAnnual){
            sb.append(annualConstraint);
        }else{
            sb.append(monthlyConstraint);
        }
        return sb.toString();
    }


    public HashMap<Integer, Double> getAreaDataForTime(P_Config config, boolean isAnnual) {
        P_EnsembleConfig ensembleConfig = (P_EnsembleConfig)config;
        long t0 = new Date().getTime();
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        HashMap<Integer, Double> monthVals = new HashMap<Integer, Double>();
        String queryString = constructMonthlyOrAnnualQueryString(GET_ENSEMBLE_CONFIG_AREA, isAnnual);
        try {
            log.log(Level.INFO, "getting  data for ensemble {0} {1} ", new Object[]{ensembleConfig.toCompactString(), config.getAreaId() } );
            ps = c.prepareStatement(queryString);
            ps.setInt(1, ensembleConfig.getAreaId());
            ps.setInt(2, ensembleConfig.getStatType().getId());
            ps.setInt(3, ensembleConfig.getStat().getId());
            ps.setInt(4, ensembleConfig.getfYear());
            ps.setInt(5, ensembleConfig.gettYear());
            ps.setInt(6, ensembleConfig.getScenario().getId());
            ps.setInt(7, ensembleConfig.getPercentile());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                monthVals.put(rs.getInt(p_ensemble_config_month), rs.getDouble(p_ensemble_config_area_value));
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_EnsembleConfigAreaDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        long t1 = new Date().getTime();

        log.log( Level.INFO, "{0} seconds for query ", (t1 - t0) / 1000.0);
        return monthVals;
    }


     private String GET_SINGLE_SCENARIO_ALL_PERCENTILE = "select p_ensemble_config_scenario_id, p_ensemble_config_percentile, p_ensemble_config_month, p_ensemble_config_area_value from  p_ensemble_config inner join p_ensemble_config_area on p_ensemble_config_id = p_ensemble_config_area_config_id and "
            + "  p_ensemble_config_scenario_id = ? and p_ensemble_config_area_area_id = ? and p_ensemble_config_o_stat_type_id = ? and  p_ensemble_config_o_var_id = ?  and p_ensemble_config_from_year = ?  and p_ensemble_config_to_year = ? ";


     public List<EnsembleDatum> getSingleScenarioAllPercentile(P_Config config, boolean isAnnual) {
        P_EnsembleConfig ensembleConfig = (P_EnsembleConfig)config;
        long t0 = new Date().getTime();
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        HashMap<String, EnsembleDatum> ensembleDatums = new HashMap<String, EnsembleDatum>();
        String queryString = constructMonthlyOrAnnualQueryString(GET_SINGLE_SCENARIO_ALL_PERCENTILE, isAnnual);
        ResultSet rs = null;
        try {
            log.log(Level.INFO, "getting  data for ensemble {0} {1} ", new Object[]{ensembleConfig.toCompactString(), config.getAreaId() } );
            ps = c.prepareStatement(queryString);
            ps.setInt(1, ensembleConfig.getScenario().getId());
            ps.setInt(2, ensembleConfig.getAreaId());
            ps.setInt(3, ensembleConfig.getStatType().getId());
            ps.setInt(4, ensembleConfig.getStat().getId());
            ps.setInt(5, ensembleConfig.getfYear());
            ps.setInt(6, ensembleConfig.gettYear());
            rs = ps.executeQuery();
            P_GcmStatsProperties props = P_GcmStatsProperties.getInstance();
            while (rs.next()) {
                P_GcmStatsProperties.scenario scenario = props.getScenarioById(rs.getInt(p_ensemble_config_scenario_id));
                int percentile = rs.getInt(p_ensemble_config_percentile);
                String key = hashScenarioPercentile(scenario, percentile);
                if(!ensembleDatums.containsKey(key)){
                    ensembleDatums.put(key, createEnsembleDatum(isAnnual));
                }

                int month  = rs.getInt(p_ensemble_config_month);
                if(!isAnnual){
                    month -= 1;
                }
                ensembleDatums.get(key).addVal(month, rs.getDouble(p_ensemble_config_area_value));
                ensembleDatums.get(key).setPercentile(percentile);

                if(config.getfYear() > 2000){
                    ensembleDatums.get(key).setScenario(scenario.toString());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_EnsembleConfigAreaDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, rs);
        }
        long t1 = new Date().getTime();

        log.log( Level.INFO, "{0} seconds for query ", (t1 - t0) / 1000.0);

        return new ArrayList<EnsembleDatum>(ensembleDatums.values());

    }



     private String GET_SINGLE_PERCENTILE_ALL_SCENARIO = "select p_ensemble_config_scenario_id, p_ensemble_config_percentile, p_ensemble_config_month, p_ensemble_config_area_value from  p_ensemble_config inner join p_ensemble_config_area on p_ensemble_config_id = p_ensemble_config_area_config_id and "
            + "  p_ensemble_config_percentile = ? and p_ensemble_config_area_area_id = ? and p_ensemble_config_o_stat_type_id = ? and  p_ensemble_config_o_var_id = ?  and p_ensemble_config_from_year = ?  and p_ensemble_config_to_year = ? ";


     public List<EnsembleDatum> getEnsembleData(P_Config config, boolean isAnnual) {
        P_EnsembleConfig ensembleConfig = (P_EnsembleConfig)config;
        long t0 = new Date().getTime();
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        HashMap<String, EnsembleDatum> ensembleDatums = new HashMap<String, EnsembleDatum>();
        String query =  P_EnsembleConfigAreaRequestBuilder.request(ensembleConfig);
        String queryString = constructMonthlyOrAnnualQueryString(query, isAnnual);
        ResultSet rs = null;
        try {
            log.log(Level.INFO, "getting  data for ensemble {0} {1} ", new Object[]{ensembleConfig.toCompactString(), config.getAreaId() } );
            ps = c.prepareStatement(queryString);
            rs = ps.executeQuery();
            P_GcmStatsProperties props = P_GcmStatsProperties.getInstance();
            while (rs.next()) {
                P_GcmStatsProperties.scenario scenario = props.getScenarioById(rs.getInt(p_ensemble_config_scenario_id));
                int percentile = rs.getInt(p_ensemble_config_percentile);
                int fromYear = rs.getInt(p_ensemble_config_from_year);
                int toYear = rs.getInt(p_ensemble_config_to_year);
                String key = hashEnsemble(scenario, percentile, fromYear, toYear);
                if(!ensembleDatums.containsKey(key)){
                    ensembleDatums.put(key, createEnsembleDatum(isAnnual));
                }

                int month  = rs.getInt(p_ensemble_config_month);
                if(!isAnnual){
                    month -= 1;
                }
                ensembleDatums.get(key).addVal(month, rs.getDouble(p_ensemble_config_area_value));
                ensembleDatums.get(key).setPercentile(percentile);
                ensembleDatums.get(key).setFromYear(fromYear);
                ensembleDatums.get(key).setToYear(toYear);

                if(fromYear > 2000){
                    ensembleDatums.get(key).setScenario(scenario.toString());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_EnsembleConfigAreaDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, rs);
        }
        long t1 = new Date().getTime();

        log.log( Level.INFO, "{0} seconds for query ", (t1 - t0) / 1000.0);

        return new ArrayList<EnsembleDatum>(ensembleDatums.values());

    }

//  public List<EnsembleDatum> getSinglePercentileAllScenario(P_Config config, boolean isAnnual) {
//        P_EnsembleConfig ensembleConfig = (P_EnsembleConfig)config;
//        long t0 = new Date().getTime();
//        Connection c = DBUtils.getConnection();
//        PreparedStatement ps = null;
//        HashMap<String, EnsembleDatum> ensembleDatums = new HashMap<String, EnsembleDatum>();
//        String queryString = constructMonthlyOrAnnualQueryString(GET_SINGLE_PERCENTILE_ALL_SCENARIO, isAnnual);
//        ResultSet rs = null;
//        try {
//            log.log(Level.INFO, "getting  data for ensemble {0} {1} ", new Object[]{ensembleConfig.toCompactString(), config.getAreaId() } );
//            ps = c.prepareStatement(queryString);
//            ps.setInt(1, ensembleConfig.getPercentile());
//            ps.setInt(2, ensembleConfig.getAreaId());
//            ps.setInt(3, ensembleConfig.getStatType().getId());
//            ps.setInt(4, ensembleConfig.getStat().getId());
//            ps.setInt(5, ensembleConfig.getfYear());
//            ps.setInt(6, ensembleConfig.gettYear());
//            rs = ps.executeQuery();
//            P_GcmStatsProperties props = P_GcmStatsProperties.getInstance();
//            while (rs.next()) {
//                P_GcmStatsProperties.scenario scenario = props.getScenarioById(rs.getInt(p_ensemble_config_scenario_id));
//                int percentile = rs.getInt(p_ensemble_config_percentile);
//                String key = hashScenarioPercentile(scenario, percentile);
//                if(!ensembleDatums.containsKey(key)){
//                    ensembleDatums.put(key, createEnsembleDatum(isAnnual));
//                }
//
//                int month  = rs.getInt(p_ensemble_config_month);
//                if(!isAnnual){
//                    month -= 1;
//                }
//                ensembleDatums.get(key).addVal(month, rs.getDouble(p_ensemble_config_area_value));
//                ensembleDatums.get(key).setPercentile(percentile);
//
//                if(config.getfYear() > 2000){
//                    ensembleDatums.get(key).setScenario(scenario.toString());
//                }
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(P_EnsembleConfigAreaDao.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            DBUtils.close(c, ps, rs);
//        }
//        long t1 = new Date().getTime();
//
//        log.log( Level.INFO, "{0} seconds for query ", (t1 - t0) / 1000.0);
//
//        return new ArrayList<EnsembleDatum>(ensembleDatums.values());
//
//    }

    private String GET_All_SCENARIO_ENSEMBLE_CONFIG_AREA = "select p_ensemble_config_scenario_id, p_ensemble_config_percentile, p_ensemble_config_month, p_ensemble_config_area_value from  p_ensemble_config inner join p_ensemble_config_area on p_ensemble_config_id = p_ensemble_config_area_config_id and "
            + "  p_ensemble_config_area_area_id = ? and p_ensemble_config_o_stat_type_id = ? and  p_ensemble_config_o_var_id = ?  and p_ensemble_config_from_year = ?  and p_ensemble_config_to_year = ? ";


     public List<EnsembleDatum> getAllScenarioAreaDataForTime(P_Config config, boolean isAnnual) {
        P_EnsembleConfig ensembleConfig = (P_EnsembleConfig)config;
        long t0 = new Date().getTime();
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        HashMap<String, EnsembleDatum> ensembleDatums = new HashMap<String, EnsembleDatum>();
        String queryString = constructMonthlyOrAnnualQueryString(GET_All_SCENARIO_ENSEMBLE_CONFIG_AREA, isAnnual);
        ResultSet rs = null;
        try {
            log.log(Level.FINE, "getting  data for ensemble {0} {1} ", new Object[]{ensembleConfig.toCompactString(), config.getAreaId() } );
            ps = c.prepareStatement(queryString);
            ps.setInt(1, ensembleConfig.getAreaId());
            ps.setInt(2, ensembleConfig.getStatType().getId());
            ps.setInt(3, ensembleConfig.getStat().getId());
            ps.setInt(4, ensembleConfig.getfYear());
            ps.setInt(5, ensembleConfig.gettYear());
            rs = ps.executeQuery();
            P_GcmStatsProperties props = P_GcmStatsProperties.getInstance();
            while (rs.next()) {
                P_GcmStatsProperties.scenario scenario = props.getScenarioById(rs.getInt(p_ensemble_config_scenario_id));
                int percentile = rs.getInt(p_ensemble_config_percentile);
                String key = hashScenarioPercentile(scenario, percentile);
                if(!ensembleDatums.containsKey(key)){
                    ensembleDatums.put(key, createEnsembleDatum(isAnnual));
                }

                int month  = rs.getInt(p_ensemble_config_month);
                if(!isAnnual){
                    month -= 1;
                }
                ensembleDatums.get(key).addVal(month, rs.getDouble(p_ensemble_config_area_value));
                ensembleDatums.get(key).setPercentile(percentile);

                if(config.getfYear() > 2000){
                    ensembleDatums.get(key).setScenario(scenario.toString());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_EnsembleConfigAreaDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, rs);
        }
        long t1 = new Date().getTime();

        log.log( Level.INFO, "{0} seconds for query ", (t1 - t0) / 1000.0);

        return new ArrayList<EnsembleDatum>(ensembleDatums.values());
        
    }

    private String hashEnsemble(P_GcmStatsProperties.scenario scenario, int percentile, int fromYear, int toYear){
        StringBuilder sb = new StringBuilder();
        if(scenario != null){
            sb.append(scenario.toString());
            sb.append(percentile);
        }
        sb.append(fromYear);
        sb.append(toYear);

        return sb.toString();
    }
    private String hashScenarioPercentile(P_GcmStatsProperties.scenario scenario, int percentile){
        StringBuilder sb = new StringBuilder();
        if(scenario != null){
            sb.append(scenario.toString());
            sb.append(percentile);
        }

        return sb.toString();
    }

    private EnsembleDatum createEnsembleDatum(boolean isAnnual){
        if(isAnnual){
            return new AnnualEnsembleDatum();
        }
        return new MonthlyEnsembleDatum();
    }




















    
    private String GET_ENSEMBLE_CONFIG_AREA_START_YEAR_RANGE = "select p_ensemble_config_from_year,p_ensemble_config_month, p_ensemble_config_area_value from  p_ensemble_config inner join p_ensemble_config_area on p_ensemble_config_id = p_ensemble_config_area_config_id and "
            + "  p_ensemble_config_area_area_id = ? and p_ensemble_config_o_stat_type_id = ? and  p_ensemble_config_o_var_id = ?  and p_ensemble_config_from_year >= ?  and p_ensemble_config_from_year <=? and  "
            + " and p_ensemble_config_scenario_id = ? ";

     public TreeMap<Integer,HashMap<Integer, Double>> getAreaDataForStartYearRange(P_Config config, int fromStartYear, int toStartYear, boolean isAnnual) {
        long t0 = new Date().getTime();
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        TreeMap<Integer,HashMap<Integer, Double>> monthVals = new TreeMap<Integer,HashMap<Integer, Double>>();
        String queryString = constructMonthlyOrAnnualQueryString(GET_ENSEMBLE_CONFIG_AREA_START_YEAR_RANGE, isAnnual);
        try {
            ps = c.prepareStatement(queryString);
            ps.setInt(1, config.getAreaId());
            ps.setInt(2, config.getStatType().getId());
            ps.setInt(3, config.getStat().getId());
            ps.setInt(4, fromStartYear);
            ps.setInt(5, toStartYear);
            ps.setInt(6, config.getScenario().getId());
//            ps.setInt(8, config.getMonth());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int fyear = rs.getInt(p_ensemble_config_from_year);
                if(!monthVals.containsKey(fyear)){
                    monthVals.put(fyear, new HashMap<Integer, Double>());
                }
                monthVals.get(fyear).put(rs.getInt(p_ensemble_config_month), rs.getDouble(p_ensemble_config_area_value));
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_EnsembleConfigAreaDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        long t1 = new Date().getTime();

        log.log( Level.INFO, "{0} seconds for query ", (t1 - t0) / 1000.0);
        return monthVals;
    }
}

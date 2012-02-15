/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.metadata;

import database.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import shapefileloader.ensemble.P_EnsembleConfig;
import shapefileloader.gcm.P_Config;
import shapefileloader.gcm.P_GcmConfig;
import shapefileloader.gcm.P_GcmConfigAreaMetadataRequestBuilder;
import shapefileloader.gcm.P_GcmStatsProperties;

/**
 *
 * @author wb385924
 */
public class P_AreaMetadataDao {

    private static final String GET_P_GCM_CONFIG_AREA_OPTIONS =
            "select o_var_name, gcm_code, scenario_code, p_gcm_config_from_year, p_gcm_config_to_year from p_gcm_config "
            + " inner join o_stat_type on p_gcm_config_o_stat_type_id = o_stat_type_id "
            + " inner join o_var on p_gcm_config_o_var_id = o_var_id "
            + " inner join gcm on p_gcm_config_gcm_id = gcm_id "
            + " inner join scenario on p_gcm_config_scenario_id = scenario_id "
            + " inner join p_gcm_config_area on p_gcm_config_id = p_gcm_config_area_gcm_config_id "
            + " where p_gcm_config_area_area_id = ? and o_stat_type_name = ?"
            + " group by gcm_code, scenario_code, o_stat_type_name, o_var_name, p_gcm_config_from_year, p_gcm_config_to_year order by gcm_code, scenario_code, o_var_name, p_gcm_config_from_year";

     private static final String GET_P_GCM_AGGREGATED_CONFIG_AREA_OPTIONS =
            "select o_var_name, p_gcm_config_from_year, p_gcm_config_to_year from p_gcm_config "
            + " inner join o_stat_type on p_gcm_config_o_stat_type_id = o_stat_type_id "
            + " inner join o_var on p_gcm_config_o_var_id = o_var_id "
            + " inner join gcm on p_gcm_config_gcm_id = gcm_id "
            + " inner join p_gcm_config_area on p_gcm_config_id = p_gcm_config_area_gcm_config_id "
            + " on p_gcm_config_area_area_id = ? and o_stat_type_name = ? and p_gcm_config_month = 1 "
            + " group by  o_stat_type_name, o_var_name, p_gcm_config_from_year, p_gcm_config_to_year order by o_var_name, p_gcm_config_from_year";

    private static final String GET_P_ENSEMBLE_CONFIG_AREA_OPTIONS =
            "select o_var_name,  scenario_code, p_ensemble_config_from_year, p_ensemble_config_to_year,  p_ensemble_config_percentile from p_ensemble_config "
            + " inner join o_stat_type on p_ensemble_config_o_stat_type_id = o_stat_type_id "
            + " inner join o_var on p_ensemble_config_o_var_id = o_var_id "
            + " inner join scenario on p_ensemble_config_scenario_id = scenario_id "
            + " inner join p_ensemble_config_area on p_ensemble_config_id = p_ensemble_config_area_config_id "
            + " where p_ensemble_config_area_area_id = ? and o_stat_type_name = ?"
            + " group by  scenario_code, o_stat_type_name, p_ensemble_config_percentile, o_var_name, p_ensemble_config_from_year, p_ensemble_config_to_year order by  scenario_code, o_var_name, p_ensemble_config_from_year";


    private static final String o_var_name = "o_var_name";
    private static final String gcm_code = "gcm_code";
    private static final String scenario_code = "scenario_code";
    private static final String p_gcm_config_from_year = "p_gcm_config_from_year";
    private static final String p_gcm_config_to_year = "p_gcm_config_to_year";
    private static final String p_gcm_config_o_var_id = "p_gcm_config_o_var_id";
    private static final String p_ensemble_config_from_year = "p_ensemble_config_from_year";
    private static final String p_ensemble_config_to_year = "p_ensemble_config_to_year";
    private static final String p_ensemble_config_percentile = "p_ensemble_config_percentile";


    private static P_AreaMetadataDao dao= null;
    private P_AreaMetadataDao(){

    }
    
    public static P_AreaMetadataDao get(){
        if(dao == null){
            dao = new P_AreaMetadataDao();
        }
        return dao;
    }
    
    
    public ArrayList<P_GcmConfig> getAreaGcmConfigOptions(int areaId, String statTypeName) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<P_GcmConfig> configs = new ArrayList<P_GcmConfig>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(GET_P_GCM_CONFIG_AREA_OPTIONS);
            ps.setInt(1, areaId);
            ps.setString(2, statTypeName.toLowerCase());
            rs = ps.executeQuery();
            P_GcmStatsProperties props = P_GcmStatsProperties.getInstance();

            while (rs.next()) {

                P_GcmConfig config = new P_GcmConfig();
                String varName = rs.getString(o_var_name);
                String gcmName = rs.getString(gcm_code);
                String scenarioCode = rs.getString(scenario_code);

                config.setStat(props.getClimateStat(varName));
                config.setGcm(props.getGcm(gcmName));
                config.setScenario(props.getScenario(scenarioCode));

                config.setfYear(rs.getInt(p_gcm_config_from_year));
                config.settYear(rs.getInt(p_gcm_config_to_year));
                configs.add(config);

            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DBUtils.close(c, ps, null);
        }

        return configs;

    }

     public ArrayList<P_GcmConfig> getOptions(P_Config config, boolean isAnnual) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<P_GcmConfig> configs = new ArrayList<P_GcmConfig>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(P_GcmConfigAreaMetadataRequestBuilder.request((P_GcmConfig)config, isAnnual));
            rs = ps.executeQuery();
            P_GcmStatsProperties props = P_GcmStatsProperties.getInstance();

            while (rs.next()) {

                P_GcmConfig resultConfig = new P_GcmConfig();
                resultConfig.setStat(props.getClimateStatById(rs.getInt(p_gcm_config_o_var_id)));
                resultConfig.setfYear(rs.getInt(p_gcm_config_from_year));
                resultConfig.settYear(rs.getInt(p_gcm_config_to_year));
                configs.add(resultConfig);

            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DBUtils.close(c, ps, null);
        }

        return configs;

    }

    /**
     * No scenario or gcm
     *
     * @param areaId
     * @param statTypeName
     * @return
     */
    public ArrayList<P_GcmConfig> getL3ConfigOptions(int areaId, String statTypeName) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<P_GcmConfig> configs = new ArrayList<P_GcmConfig>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(GET_P_GCM_AGGREGATED_CONFIG_AREA_OPTIONS);
            ps.setInt(1, areaId);
            ps.setString(2, statTypeName.toLowerCase());
            rs = ps.executeQuery();
            P_GcmStatsProperties props = P_GcmStatsProperties.getInstance();

            while (rs.next()) {

                P_GcmConfig config = new P_GcmConfig();
                String varName = rs.getString(o_var_name);

                config.setStat(props.getClimateStat(varName));

                config.setfYear(rs.getInt(p_gcm_config_from_year));
                config.settYear(rs.getInt(p_gcm_config_to_year));
                configs.add(config);

            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DBUtils.close(c, ps, null);
        }

        return configs;

    }

    public ArrayList<P_EnsembleConfig> getAreaEnsembleConfigOptions(int areaId, String statTypeName) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<P_EnsembleConfig> configs = new ArrayList<P_EnsembleConfig>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(GET_P_ENSEMBLE_CONFIG_AREA_OPTIONS);
            ps.setInt(1, areaId);
            ps.setString(2, statTypeName.toLowerCase());
            rs = ps.executeQuery();
            P_GcmStatsProperties props = P_GcmStatsProperties.getInstance();

            while (rs.next()) {

                P_EnsembleConfig config = new P_EnsembleConfig();
                String varName = rs.getString(o_var_name);
                String scenarioCode = rs.getString(scenario_code);

                config.setStat(props.getClimateStat(varName));
                config.setScenario(props.getScenario(scenarioCode));

                config.setfYear(rs.getInt(p_ensemble_config_from_year));
                config.settYear(rs.getInt(p_ensemble_config_to_year));

                config.setPercentile(rs.getInt(p_ensemble_config_percentile));
                configs.add(config);

            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DBUtils.close(c, ps, null);
        }

        return configs;

    }
}

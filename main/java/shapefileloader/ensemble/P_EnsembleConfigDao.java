/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.ensemble;

import database.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.gcm.P_Config;
import shapefileloader.gcm.P_ConfigDao;

/**
 *
 * @author wb385924
 */
public class P_EnsembleConfigDao implements P_ConfigDao {

    private String INSERT_ENSEMBLE_CONFIG = "insert into p_ensemble_config (p_ensemble_config_o_stat_type_id, p_ensemble_config_o_var_id, p_ensemble_config_from_year, p_ensemble_config_to_year, p_ensemble_config_scenario_id, p_ensemble_config_month, p_ensemble_config_percentile) values(?,?,?,?,?,?,?) returning p_ensemble_config_id";
    private String p_ensemble_config_id = "p_ensemble_config_id";
    private static P_EnsembleConfigDao dao = null;

    private P_EnsembleConfigDao() {
    }

    public static P_EnsembleConfigDao get() {
        if (dao == null) {
            dao = new P_EnsembleConfigDao();
        }
        return dao;
    }

    public int insertConfig(P_Config config) {
        P_EnsembleConfig ensembleConfig = (P_EnsembleConfig) config;
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        int id = -1;
        try {

            ps = c.prepareStatement(INSERT_ENSEMBLE_CONFIG);
            ps.setInt(1, ensembleConfig.getStatType().getId());
            ps.setInt(2, ensembleConfig.getStat().getId());
            ps.setInt(3, ensembleConfig.getfYear());
            ps.setInt(4, ensembleConfig.gettYear());
            ps.setInt(5, ensembleConfig.getScenario().getId());
            ps.setInt(6, ensembleConfig.getMonth());
            ps.setDouble(7, ensembleConfig.getPercentile());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(p_ensemble_config_id);
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_EnsembleConfigDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }

        return id;
    }
    private String GET_ENSEMBLE_CONFIG = "select p_ensemble_config_id from  p_ensemble_config where p_ensemble_config_o_stat_type_id = ? and  p_ensemble_config_o_var_id = ?  and p_ensemble_config_from_year = ?  and p_ensemble_config_to_year = ?  and p_ensemble_config_scenario_id = ? and  p_ensemble_config_month = ? and p_ensemble_config_percentile = ?";

    public int getConfigId(P_Config config) {
        P_EnsembleConfig ensembleConfig = (P_EnsembleConfig) config;
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        int id = -1;
        try {

            ps = c.prepareStatement(GET_ENSEMBLE_CONFIG);
            ps.setInt(1, ensembleConfig.getStatType().getId());
            ps.setInt(2, ensembleConfig.getStat().getId());
            ps.setInt(3, ensembleConfig.getfYear());
            ps.setInt(4, ensembleConfig.gettYear());
            ps.setInt(5, ensembleConfig.getScenario().getId());
            ps.setInt(6, ensembleConfig.getMonth());
            ps.setInt(7, ensembleConfig.getPercentile());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(p_ensemble_config_id);
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_EnsembleConfigDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c);
        }

        return id;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm;

import shapefileloader.gcm.P_GcmConfig;
import database.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class P_GcmConfigDao implements P_ConfigDao {

    private String INSERT_GCM_CONFIG = "insert into p_gcm_config (p_gcm_config_o_stat_type_id, p_gcm_config_o_var_id, p_gcm_config_from_year, p_gcm_config_to_year, p_gcm_config_gcm_id, p_gcm_config_scenario_id, p_gcm_config_month) values(?,?,?,?,?,?,?) returning p_gcm_config_id";
    private String p_gcm_config_id = "p_gcm_config_id";
    private static P_GcmConfigDao dao = null;

    private P_GcmConfigDao() {
    }

    public static P_GcmConfigDao get() {
        if (dao == null) {
            dao = new P_GcmConfigDao();
        }
        return dao;
    }

    public int insertConfig(P_Config config) {
        P_Config gcmConfig = (P_Config) config;
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        int id = -1;
        try {

            ps = c.prepareStatement(INSERT_GCM_CONFIG);
            ps.setInt(1, gcmConfig.getStatType().getId());
            ps.setInt(2, gcmConfig.getStat().getId());
            ps.setInt(3, gcmConfig.getfYear());
            ps.setInt(4, gcmConfig.gettYear());
            ps.setInt(5, gcmConfig.getGcm().getGcmId());
            ps.setInt(6, gcmConfig.getScenario().getId());
            ps.setInt(7, gcmConfig.getMonth());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("p_gcm_config_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_GcmConfigDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }

        return id;
    }
    private String GET_GCM_CONFIG = "select p_gcm_config_id from  p_gcm_config where p_gcm_config_o_stat_type_id = ? and  p_gcm_config_o_var_id = ?  and p_gcm_config_from_year = ?  and p_gcm_config_to_year = ?  and  p_gcm_config_gcm_id = ? and p_gcm_config_scenario_id = ? and  p_gcm_config_month = ?";

    public int getConfigId(P_Config config) {
        P_Config gcmConfig = (P_Config) config;
       
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        int id = -1;
        try {

            ps = c.prepareStatement(GET_GCM_CONFIG);
            ps.setInt(1, gcmConfig.getStatType().getId());
            ps.setInt(2, gcmConfig.getStat().getId());
            ps.setInt(3, gcmConfig.getfYear());
            ps.setInt(4, gcmConfig.gettYear());
            ps.setInt(5, gcmConfig.getGcm().getGcmId());
            ps.setInt(6, gcmConfig.getScenario().getId());
            ps.setInt(7, gcmConfig.getMonth());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(p_gcm_config_id);
            }
        } catch (SQLException ex) {
            Logger.getLogger(P_GcmConfigDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                c.close();
                DBUtils.close(c, ps, null);
            } catch (SQLException ex) {
                Logger.getLogger(P_GcmConfigDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return id;
    }

    public P_GcmConfigDao getInstance() {
        return get();
    }
}

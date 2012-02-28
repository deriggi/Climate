/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author wb385924
 */
public class DBUtils {

    private static final Logger log = Logger.getLogger(DBUtils.class.getName());
//    public static final String propfile = "C:\\Users\\Johnny\\db.properties";
    public static final String propfile = "C:\\db.properties";
    private static BasicDataSource cpds = null;
    private static String url;
    private static String username;
    private static String password;

    ;
  
    private DBUtils() {
    }

    static {
        loadProperties();
        init();

    }

    private static void loadProperties() {
        FileInputStream fis = null;
        Properties properties = null;
        try {

            properties = new Properties();
            fis = new FileInputStream(propfile);
            properties.load(fis);

            url = (String) properties.get("url");
            username = (String) properties.get("username");
            password = (String) properties.get("password");


        } catch (IOException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void init() {
        if (cpds != null) {
            return;
        }
        cpds = new BasicDataSource();
        cpds.setDriverClassName("org.postgresql.Driver"); //loads the jdbc driver
        cpds.setUrl(url);
        cpds.setUsername(username);
        cpds.setPassword(password);
        cpds.setMinIdle(10);
        cpds.setInitialSize(10);

        cpds.setMaxActive(10);
        // the settings below are optional -- c3p0 can work with defaults
        log.info(cpds.getUrl());
        log.log(Level.INFO, "max active {0}", cpds.getMaxActive());
        log.log(Level.INFO, "initial size {0}", cpds.getInitialSize());
    }

    public static Connection getConnection() {
        try {
//            if(cpds.getNumActive() > 10){
            log.log(Level.FINE, "active connections {0}", cpds.getNumActive());
            log.log(Level.FINE, "idle connections {0}", cpds.getNumIdle());
//            log.log(Level.INFO, "idle connections {0}", cpds.getNumActive());
//            }
            return cpds.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void closeAll() {
        if (cpds != null) {
            try {
                cpds.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void close(PreparedStatement ps) {
        try {

            if (ps != null) {
                ps.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void close(PreparedStatement ps, ResultSet rs) {
        try {

            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void close(Connection c, PreparedStatement ps, ResultSet rs) {
        try {
            if (c != null) {
                c.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void close(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        try {
            Connection c;
            PreparedStatement ps = null;
            ResultSet rs = null;
            for (int i = 0; i < 38; i++) {
                c = DBUtils.getConnection();
                ps = c.prepareStatement("select count(*) from p_gcm_config");
                rs = ps.executeQuery();
                if(rs.next()){
                    System.out.println(rs.getInt(1));
                }
                ps.close();
                rs.close();
                c.close();
            }


            cpds.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}

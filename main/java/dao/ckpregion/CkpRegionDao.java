/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.ckpregion;

import dao.GeoDao;
import database.DBUtils;
import domain.CkpRegion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class CkpRegionDao {

    private static CkpRegionDao regionDao = null;
    private final static String CODE = "code";
    private final static String CKP_REGION = "ckp_region";
    private CkpRegionDao() {
    }
    private final String GET_REGIONS = "SELECT CKP_REGION_ID, CKP_REGION_CODE FROM CKP_REGION";
    private final String CKP_REGION_ID = "ckp_region_id";
    private final String CKP_REGION_CODE = "ckp_region_code";

    private final Logger log= Logger.getLogger(CkpRegionDao.class.getName());

    public static CkpRegionDao get() {
        if (regionDao == null) {
            regionDao = new CkpRegionDao();
        }
        return regionDao;
    }

    public int getRegionId(String code) {
        
        Connection connection = DBUtils.getConnection();
        int id = GeoDao.getEntityId(connection, CKP_REGION, CODE, code);
        DBUtils.close(connection);

        return id;
    }

    public Set<CkpRegion> getCkpRegions(){
        Connection c = null;
        PreparedStatement ps = null;
        c = DBUtils.getConnection();
        Set<CkpRegion> ckpRegions = new HashSet<CkpRegion>();
        try{
            long t0 = new Date().getTime();
            log.info("getting regions");
            ps = c.prepareStatement(GET_REGIONS);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String code = rs.getString(CKP_REGION_CODE);
                if(code != null){
                    ckpRegions.add(new CkpRegion(rs.getInt(CKP_REGION_ID), code.toUpperCase()));
                }
            }
            long t1 = new Date().getTime();
            log.log(  Level.INFO, "{0} seconds to get the ckp regions ", (t1 - t0) / 1000.0);
            
        } catch(SQLException sqle){
            sqle.printStackTrace();
        } finally{
            DBUtils.close(c, ps, null);
        }
        return ckpRegions;
    }
}

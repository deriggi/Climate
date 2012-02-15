/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.droughtaffected;

import dao.GeoDao;
import dao.RowMapper;
import database.DBUtils;
import domain.DroughtAffected;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class DroughtAffectedDao {

    private GeoDao<DroughtAffected> geoDao = null;
    private RowMapper mapper = null;
    private static DroughtAffectedDao dao = null;

    public static DroughtAffectedDao get(){
        if(dao == null){
            dao = new DroughtAffectedDao();
            dao.geoDao = new GeoDao<DroughtAffected>();
            dao.mapper = new DroughtAffectedRowMapper();
        }
        
        return dao;
    }

    public List<DroughtAffected> getDroughtAffectedData(int countryId){
        Connection c = DBUtils.getConnection();
        List<DroughtAffected> droughtAffecteds = geoDao.getChildEntitiesByParentId(c, "country", "drought_affected", mapper, countryId);
        DBUtils.close(c);
        
        return droughtAffecteds;
    }


    private DroughtAffectedDao(){}


}

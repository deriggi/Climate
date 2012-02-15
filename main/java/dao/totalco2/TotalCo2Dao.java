/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.totalco2;

import dao.GeoDao;
import dao.RowMapper;
import database.DBUtils;
import domain.TotalCo2;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class TotalCo2Dao {

    private GeoDao<TotalCo2> geoDao = null;
    private RowMapper mapper = null;
    private static TotalCo2Dao dao = null;
   // private String GET_TOTAL_SUM =

    public static TotalCo2Dao get(){
        if(dao == null){
            dao = new TotalCo2Dao();
            dao.geoDao = new GeoDao<TotalCo2>();
            dao.mapper = new TotalCo2RowMapper();
        }
        
        return dao;
    }

    public List<TotalCo2> getTotalCo2Data(int countryId){
        Connection c = DBUtils.getConnection();
        List<TotalCo2> droughtAffecteds = geoDao.getChildEntitiesByParentId(c, "country", "total_co2", mapper, countryId);
        DBUtils.close(c);
        
        return droughtAffecteds;
    }


    private TotalCo2Dao(){}


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.percapitaco2;

import dao.GeoDao;
import dao.RowMapper;
import database.DBUtils;
import domain.PerCaptiaCo2;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class PerCapitaCo2Dao {

    private GeoDao<PerCaptiaCo2> geoDao = null;
    private RowMapper mapper = null;
    private static PerCapitaCo2Dao dao = null;

    public static PerCapitaCo2Dao get(){
        if(dao == null){
            dao = new PerCapitaCo2Dao();
            dao.geoDao = new GeoDao<PerCaptiaCo2>();
            dao.mapper = new TotalCo2RowMapper();
        }
        
        return dao;
    }

    public List<PerCaptiaCo2> getTotalCo2Data(int countryId){
        
        Connection c = DBUtils.getConnection();
        List<PerCaptiaCo2> perCapitasCo2 = geoDao.getChildEntitiesByParentId(c, "country", "per_capita_co2", mapper, countryId);
        DBUtils.close(c);
        
        return perCapitasCo2;
    }


    private PerCapitaCo2Dao(){}


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.carbonintensitywri;

import dao.GeoDao;
import dao.RowMapper;
import database.DBUtils;
import domain.CarbonIntensityWri;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class CarbonIntensityWriDao {

    private GeoDao<CarbonIntensityWri> geoDao = null;
    private RowMapper mapper = null;
    private static CarbonIntensityWriDao dao = null;

    public static CarbonIntensityWriDao get(){
        if(dao == null){
            dao = new CarbonIntensityWriDao();
            dao.geoDao = new GeoDao<CarbonIntensityWri>();
            dao.mapper = new CarbonIntensityWriRowMapper();
        }
        return dao;
    }

    public List<CarbonIntensityWri> getCarbonIntensityData(int countryId){
        Connection c = DBUtils.getConnection();

        List<CarbonIntensityWri> carbonIntensities = geoDao.getChildEntitiesByParentId(c, "country", "carbon_intensity_wri", mapper, countryId);
        DBUtils.close(c);
        
        return carbonIntensities;
    }


    private CarbonIntensityWriDao(){}


}

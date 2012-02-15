/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.country;

import dao.GeoDao;
import database.DBUtils;
import java.sql.Connection;

/**
 * @author wb385924
 */
public class RegionDao {

    private static RegionDao regionDao = null;

    private RegionDao() {
    }

    public static RegionDao get() {
        if (regionDao == null) {
            regionDao = new RegionDao();
        }
        return regionDao;
    }

    public int getRegionId(String name) {
        Connection connection = DBUtils.getConnection();
        int id = GeoDao.getEntityId(connection, "region", "name", name);
        DBUtils.close(connection);

        return id;
    }
}

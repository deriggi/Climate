/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.drainage;

import dao.GeoDao;
import dao.RowMapper;
import database.DBUtils;
import domain.Drainage;
import domain.Drainage;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class DrainageDao {

    private GeoDao<Drainage> geoDao = new GeoDao<Drainage>();
    private RowMapper rowMapper = new DrainageRowMapper();
    private final String DRAINAGE = "drainage";
    private static DrainageDao dao = null;

    private DrainageDao() {
    }

    public static DrainageDao get() {
        if (dao == null) {
            dao = new DrainageDao();
        }
        return dao;
    }

    public List<Drainage> getDrainages() {
        Connection c = DBUtils.getConnection();
        List<Drainage> dainages = null;
        dainages = geoDao.getEntities(c, DRAINAGE, rowMapper);
        DBUtils.close(c);

        return dainages;
    }
}

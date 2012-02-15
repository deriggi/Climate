/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ascii;

import dao.GeoDao;
import database.DBUtils;
import java.sql.Connection;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wb385924
 */
public class DatabaseAsciiAction implements AsciiAction {

    private final static Logger log = Logger.getLogger(DatabaseAsciiAction.class.getName());

    public void handleNonNullData(double y, double x, Date date, double data) {
        log.log(Level.FINE, "x: {0} y: {1}", new Object[]{y, x});
//        log.log(Level.FINE, "row: {0} col: {1}", new Object[]{lineNumber - STANDARD_HEADER_SIZE, colNumber - 1});
        TreeMap<String, Object> extraData = new TreeMap<String, Object>();
        extraData.put("data", data);
        extraData.put("date", date);
        Connection c = DBUtils.getConnection();
        try {

            extraData.put("point_id", getOrAddPoint(c, x, y));

            if (!GeoDao.doesEntityExist(c, "cru_pr", extraData)) {
                GeoDao.storeEntityData(c, "cru_pr", extraData);
            }

        } finally {
            DBUtils.close(c);
        }
    }

    private int getOrAddPoint(Connection c, double longitude, double latitude) {
        int id = GeoDao.getGeometryBoundsId(c, "POINT(" + longitude + " " + latitude + ")", "cru_pr_point", "geom");
        if (id == -1) {
            id = GeoDao.storeGeometry(c, "cru_pr_point", "geom", "POINT(" + longitude + " " + latitude + ")");
        }
        return id;

    }
}

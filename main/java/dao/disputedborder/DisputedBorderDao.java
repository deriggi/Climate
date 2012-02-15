/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.disputedborder;

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
public class DisputedBorderDao {

    private final String GET_KML = "select st_askml(st_union(disputed_border_shape)) from disputed_border where st_intersects(disputed_border_shape,(select boundary_shape from boundary where boundary_area_id = ?))";
    private final String GET_ALL_KML = "select st_askml(st_union(disputed_border_shape)) from disputed_border";
    private static final Logger log = Logger.getLogger(DisputedBorderDao.class.getName());
    private static DisputedBorderDao dao = null;

    private DisputedBorderDao() {
    }

    public static DisputedBorderDao get() {
        if (dao == null) {
            dao = new DisputedBorderDao();
        }
        return dao;
    }

    public String getDisputedBorderKMLForCountry(int countryId) {
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        String kml = "<kml></kml>";
        try {
            log.log(Level.FINE, "testing for {0}", countryId);
            ps = c.prepareStatement(GET_KML);
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                kml = rs.getString(1);
            }
            log.fine(kml);
        } catch (SQLException sqle) {
            log.severe(sqle.getMessage());
        } finally {
            DBUtils.close(c, ps, null);
            return kml;
        }

    }
    public String getDisputedBorder() {
        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        String kml = "<kml></kml>";
        try {
            ps = c.prepareStatement(GET_ALL_KML);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                kml = rs.getString(1);
            }
            log.fine(kml);
        } catch (SQLException sqle) {
            log.severe(sqle.getMessage());
        } finally {
            DBUtils.close(c, ps, null);
            return kml;
        }

    }

    public static void main(String[] args){
        String kml = DisputedBorderDao.get().getDisputedBorderKMLForCountry(465);
        System.out.println(kml);
    }
}

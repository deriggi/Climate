/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.basin;

import dao.GeoDao;
import dao.RowMapper;
import database.DBUtils;
import domain.Basin;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class BasinDao {

    private GeoDao<Basin> geoDao = new GeoDao<Basin>();
    private RowMapper rowMapper = new BasinRowMapper();
    private RowMapper spatialBasinRowMapper = new SpatialBasinRowMapper();
    private final String BASIN = "basin";
    private final String ID = "id";
    private final String CODE = "CODE";
    private static BasinDao dao = null;

    private String GET_SIMPLE_SVG_BASINS = "select basin_name, st_assvg(  st_scale(st_simplify(basin_geom,0.09),10.0,10.0) ,1 ) from "
            + "boundary inner join basin on boundary_area_id = ? AND  "
            + "ST_Intersects(basin_geom,boundary_shape)";
    //+ "(basin_geom && country_boundary_shape)";
//    private String GET_SIMPLE_SVG_BASINS_FROM_RELATION = "select basin_name, st_assvg(  st_scale(st_simplify(basin_geom,0.09),10.0,10.0) ,1 ) from "
//            + "basin where basin_country_id = ? ";
//    private String GET_SIMPLE_SVG_BASIN = "select basin_name, st_assvg(  st_scale(st_simplify(basin_geom,0.09),10.0,10.0) ,1 ) from "
//            + "basin where basin_will_id = ? ";
//    private String GET_BASIN_IDS_FOR_COUNTRY = "select basin_id from basin inner join country on basin_country_id = country_id and country_iso_3 = ?";

    private BasinDao() {
    }

    public static BasinDao get() {
        if (dao == null) {
            dao = new BasinDao();
        }
        return dao;
    }

    public List<Basin> getBasins() {
        Connection c = DBUtils.getConnection();
        List<Basin> basins = null;
        basins = geoDao.getEntities(c, BASIN, rowMapper);
        DBUtils.close(c);

        return basins;
    }

    public int getBasinIdFromCode(int wbhuc){
        Connection c = DBUtils.getConnection();
        int id = GeoDao.getEntityId(c, BASIN, CODE, wbhuc);
        DBUtils.close(c);
        return id;
    }

//    private boolean isValid(String iso3){
//        if(iso3 == null){
//            return false;
//        }
//        if (iso3.length() != 3){
//            return false;
//        }
//        return true;
//    }
//    public List<Integer> getBasins(String iso3) {
//        if(!isValid(iso3)){
//            return new ArrayList<Integer>(0);
//        }
//
//        DBUtils db = DBUtils.get();
//        Connection c = db.getConnection();
//        List<Integer> basinIds = new ArrayList<Integer>();
//        PreparedStatement ps = null;
//        try {
//            ps = c.prepareStatement(GET_BASIN_IDS_FOR_COUNTRY);
//            ps.setString(1, iso3);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next()){
//                basinIds.add(rs.getInt("basin_id"));
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(BasinDao.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            db.close(c, ps, null);
//        }
//        return basinIds;
//    }

//    public List<Basin> getBasinsContainingPoint(double latitude, double longitude) {
//        DBUtils db = DBUtils.get();
//        Connection c = db.getConnection();
//        List<Basin> basins = new ArrayList<Basin>();
//        List<String> joins = new ArrayList<String>();
//        List<String> mappedJoins = new ArrayList<String>();
//        joins.add("country");
//        mappedJoins.add("drainage");
//        basins.addAll(geoDao.getEntitiesContainingPoint(c, latitude, longitude, spatialBasinRowMapper, BASIN, joins, mappedJoins));
//
//        db.close(c);
//
//        return basins;
//    }
//
//    public List<ShapeSvg> getSVGBasinsForCountry(int countryId) {
//        PreparedStatement ps = null;
//        DBUtils db = DBUtils.get();
//        Connection c = null;
//        List<ShapeSvg> basinSvgs = new ArrayList<ShapeSvg>();
//        try {
//
//            c = db.getConnection();
//            ps = c.prepareStatement(GET_SIMPLE_SVG_BASINS);
//            ps.setInt(1, countryId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                basinSvgs.add(new ShapeSvg(rs.getString("st_assvg"), rs.getString("basin_name")));
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(BasinDao.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            db.close(c);
//        }
//        return basinSvgs;
//
//    }
//
//    public List<ShapeSvg> getSVGBasin(int willId) {
//        PreparedStatement ps = null;
//        DBUtils db = DBUtils.get();
//        Connection c = null;
//        List<ShapeSvg> basinSvgs = new ArrayList<ShapeSvg>();
//        try {
//
//            c = db.getConnection();
//            ps = c.prepareStatement(GET_SIMPLE_SVG_BASIN);
//            ps.setInt(1, willId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                basinSvgs.add(new ShapeSvg(rs.getString("st_assvg"), rs.getString("basin_name")));
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(BasinDao.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            db.close(c);
//        }
//        return basinSvgs;
//
//    }
}

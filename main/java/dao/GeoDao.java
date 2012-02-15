
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import database.DBUtils;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class GeoDao<E> {

    private static final Logger log = Logger.getLogger(GeoDao.class.getName());

    public static void main(String args[]) {
        Connection c = null;
        GeoDao.storeJoinTableRecord(c, "basin", 1, "drainage", 2);
    }

    public static void storeJoinTableWithData(Connection c, String parent, int parentId, String child, int childId, String dataField, Object data) {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        if (c == null) {
            log.log(Level.SEVERE, "could not store map of  {0}  {1} because connection was null", new Object[]{parent, child});
            return;

        }
        try {

            sb.append("insert into ");


            sb.append(parent);
            sb.append("_");
            sb.append(child);
            sb.append("(");
            sb.append(parent);
            sb.append("_");
            sb.append("id,");

            sb.append(child);
            sb.append("_");
            sb.append("id,");

            sb.append(dataField);

            sb.append(") values(?,?,?)");
            String query = sb.toString();
            log.fine(query);

            ps = c.prepareStatement(query);
            ps.setInt(1, parentId);
            ps.setInt(2, childId);
            ps.setObject(3, data);

            ps.executeUpdate();

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.severe("query " + sb.toString());

        } finally {
            DBUtils.close(c, ps, null);
        }
    }

    public static void storeJoinTableRecord(Connection c, String parent, int parentId, String child, int childId) {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        if (c == null) {
            log.log(Level.SEVERE, "could not store map of  {0}  {1} because connection was null", new Object[]{parent, child});
            return;

        }
        try {

            sb.append("insert into ");


            sb.append(parent);
            sb.append("_");
            sb.append(child);
            sb.append("(");
            sb.append(parent);
            sb.append("_");
            sb.append("id,");

            sb.append(child);
            sb.append("_");
            sb.append("id");

            sb.append(") values(?,?)");
            String query = sb.toString();
            log.fine(query);

            ps = c.prepareStatement(query);
            ps.setInt(1, parentId);
            ps.setInt(2, childId);

            ps.executeUpdate();

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.severe("query " + sb.toString());

        } finally {
            DBUtils.close(ps, null);
        }
    }

    public static int getIdOfGeometriesNearPoint(Connection c, double latitude, double longitude, String entity, String shapeProperty, String idProperty, double degreeDistance) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        sb.append("select ");
        sb.append(entity);
        sb.append("_");
        sb.append(idProperty);
        sb.append(" from ");
        sb.append(entity);
        sb.append(" where ");
        sb.append("ST_Distance(");
        sb.append(entity);
        sb.append("_");
        sb.append(shapeProperty);
        sb.append(",");
        sb.append("ST_PointFromText('POINT(");
        sb.append(longitude);
        sb.append(" ");
        sb.append(latitude);
        sb.append(")', 4326)) < ");
        sb.append(degreeDistance);
        String query = sb.toString();
        log.info(query);
        long t0 = new Date().getTime();
        try {
            ps = c.prepareStatement(query);
//            ps.setDouble(1, longitude);
//            ps.setDouble(2, latitude);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, String.format("error getting id of {0} near point", entity), ex);
            //ex.printStackTrace();
        } finally {
            long t1 = new Date().getTime();
            float f = (t1 - t0) / 1000.0f;
            log.log(Level.INFO, "finding nearest geom took {0} seconds", f);

            DBUtils.close(ps, rs);
            //DBUtils.close(c);
        }


        return -1;
    }

    public static int getIdOfRegionContainingPoint(Connection c, double latitude, double longitude, String entity, String shapeProperty, String idProperty) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        sb.append("select ");
        sb.append(entity);
        sb.append("_");
        sb.append(idProperty);
        sb.append(" from ");
        sb.append(entity);
        sb.append(" where ");
        sb.append("ST_Contains(");
        sb.append(entity);
        sb.append("_");
        sb.append(shapeProperty);
        sb.append(",");
        sb.append("ST_PointFromText('POINT(");
        sb.append(longitude);
        sb.append(" ");
        sb.append(latitude);
        sb.append(")', 4326))");

        String query = sb.toString();
        log.fine(query);

        try {
            ps = c.prepareStatement(query);
//            ps.setDouble(1, longitude);
//            ps.setDouble(2, latitude);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, String.format("error getting id of {0} containing point", entity), ex);
            //ex.printStackTrace();
        } finally {
            DBUtils.close(ps, rs);
            //DBUtils.close(c);
        }


        return -1;
    }

    /**
     * Basic properties returned are name, id, and centroid
     * @param c
     * @param latitude
     * @param longitude
     * @param entity
     * @param shapeProperty
     * @param idProperty
     * @return
     */
    public static HashMap<String, String> getPropertiesOfRegionContainingPoint(Connection c, double latitude, double longitude, String entity, String shapeProperty, String idProperty) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        sb.append("select ");
        sb.append(entity);
        sb.append("_");
        sb.append(idProperty);
        sb.append(",");

        sb.append("st_astext(ST_Centroid(");
        sb.append(entity);
        sb.append("_");
        sb.append(shapeProperty);
        sb.append(")) ");
        sb.append(" from ");
        sb.append(entity);
        sb.append(" where ");
        sb.append("ST_Contains(");
        sb.append(entity);
        sb.append("_");
        sb.append(shapeProperty);
        sb.append(",");
        sb.append("ST_PointFromText('POINT(");
        sb.append(longitude);
        sb.append(" ");
        sb.append(latitude);
        sb.append(")', 4326))");

        String query = sb.toString();
        log.info(query);
        HashMap<String, String> idNameMap = new HashMap<String, String>(1);

        try {
            ps = c.prepareStatement(query);

            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                //String name = rs.getString(2);
                String point = rs.getString(2);
                idNameMap.put("id", Integer.toString(id));
                //idNameMap.put("name", name);
                idNameMap.put("centroid", point);

                return idNameMap;
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, String.format("error getting id of {0} containing point", entity), ex);
            //ex.printStackTrace();
        } finally {
            DBUtils.close(ps, rs);
            //DBUtils.close(c);
        }


        return idNameMap;
    }

    /**
     * Temporarily abandoned, but this will be useful
     * @param c
     * @param latitude
     * @param longitude
     * @param entity
     * @param shapeProperty
     * @param idProperty
     * @return
     */
    public static HashMap<String, String> getPropertiesOfRegionWithinBox(Connection c, double latitude, double longitude, String entity, String shapeProperty, String idProperty) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        sb.append("select ");
        sb.append(entity);
        sb.append("_");
        sb.append(idProperty);
        sb.append(",");

        sb.append("st_astext(ST_Centroid(");
        sb.append(entity);
        sb.append("_");
        sb.append(shapeProperty);
        sb.append(")) ");
        sb.append(" from ");
        sb.append(entity);
        sb.append(" where ");
        sb.append("ST_Within(");



        sb.append(")', 4326))");

        String query = sb.toString();
        log.info(query);
        HashMap<String, String> idNameMap = new HashMap<String, String>(1);

        try {
            ps = c.prepareStatement(query);

            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                //String name = rs.getString(2);
                String point = rs.getString(2);
                idNameMap.put("id", Integer.toString(id));
                //idNameMap.put("name", name);
                idNameMap.put("centroid", point);

                return idNameMap;
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, String.format("error getting id of {0} containing point", entity), ex);
            //ex.printStackTrace();
        } finally {
            DBUtils.close(ps, rs);
            //DBUtils.close(c);
        }


        return idNameMap;
    }

    public List<E> getEntitiesContainingPoint(Connection c, double latitude, double longitude, RowMapper<E> rowMapper, String entityType, List<String> innerJoins, List<String> mappedJoins) {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        if (c == null) {
            log.log(Level.SEVERE, "could not get entities containing point {0} {1} because connection was null", new Object[]{latitude, longitude});
            return new ArrayList<E>(0);
        }

        sb.append("select ");
        sb.append(entityType);
        sb.append(".");
        sb.append(entityType);
        sb.append("_name,");

        sb.append(entityType);
        sb.append(".");
        sb.append(entityType);
        sb.append("_id,");

        int i = 0;
        for (String t : innerJoins) {
            sb.append(t);
            sb.append(".*");
            if (i++ < innerJoins.size() - 1) {
                sb.append(",");
            }
        }

        if (mappedJoins != null) {
            i = 0;
            sb.append(",");
            for (String t : mappedJoins) {
                sb.append(t);
                sb.append(".*");
                if (i++ < innerJoins.size() - 1) {
                    sb.append(",");
                }
            }
        }

        sb.append(" from ");
        sb.append(entityType);

        if (innerJoins != null) {
            for (String s : innerJoins) {
                sb.append(" inner join ");
                sb.append(s);
                sb.append(" on ");
                sb.append(s);
                sb.append(".");
                sb.append(s);
                sb.append("_id = ");
                sb.append(entityType);
                sb.append(".");
                sb.append(entityType);
                sb.append("_");
                sb.append(s);
                sb.append("_id");
            }
        }

        if (mappedJoins != null) {
            for (String m : mappedJoins) {
                sb.append(" left join ");

                sb.append(entityType);
                sb.append("_");
                sb.append(m);

                sb.append(" on ");
                sb.append(entityType);
                sb.append("_");
                sb.append(m);
                sb.append(".");
                sb.append(entityType);
                sb.append("_id = ");


                sb.append(entityType);
                sb.append(".");
                sb.append(entityType);
                sb.append("_id");

                // ==== could this be an inner join?
                sb.append(" left join ");
                sb.append(m);
                sb.append(" on ");

                sb.append(entityType);
                sb.append("_");
                sb.append(m);
                sb.append(".");
                sb.append(m);
                sb.append("_id = ");

                sb.append(m);
                sb.append(".");
                sb.append(m);
                sb.append("_id");
            }
        }


        sb.append(" where ");
        sb.append("ST_Contains(");
        sb.append(entityType);
        sb.append("_geom,");
        sb.append("ST_PointFromText('POINT(");
        sb.append(longitude);
        sb.append(" ");
        sb.append(latitude);
        sb.append(")', 4326))");


        String query = sb.toString();
        log.info(query);

        try {
            ps = c.prepareStatement(query);
//            ps.setDouble(1, longitude);
//            ps.setDouble(2, latitude);
            rs = ps.executeQuery();
            return rowMapper.results(rs);
        } catch (SQLException ex) {
            log.log(Level.SEVERE, String.format("error getting {0} containing point", entityType), ex);
            //ex.printStackTrace();
        } finally {
            DBUtils.close(ps, rs);
//            DBUtils.close(c);
        }
        return new ArrayList<E>(0);
    }

    public static int storeSimplifiedGeometryChild(Connection c, String entity, String property, int parentId, String parent, String polygon) {

        ResultSet rs = null;
        PreparedStatement ps = null;
        int lastInsertId = -1;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");

            sb.append(entity);
            sb.append("(");

            sb.append(entity);
            sb.append("_");
            sb.append(parent);
            sb.append("_");
            sb.append("id,");



            sb.append(entity);
            sb.append("_");
            sb.append(property);

            sb.append(")");
            sb.append(" values(");
            sb.append(parentId);
            sb.append(",");
            sb.append("ST_Simplify((ST_GeomFromEWKT(\'SRID=4326;");
            sb.append(polygon);
            sb.append("\')),0.09)) returning ");
            sb.append(entity);
            sb.append("_id");

            log.info(sb.toString());
            ps = c.prepareStatement(sb.toString());
//            ps.setString(1, polygon);
            rs = ps.executeQuery();
            if (rs.next()) {
                StringBuffer returnIdParam = new StringBuffer();
                returnIdParam.append(entity);
                returnIdParam.append("_id");
                lastInsertId = rs.getInt(returnIdParam.toString());

            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return lastInsertId;
    }

    public static int updateSimplifiedGeometryChild(Connection c, String entity, String property, int parentId, String parent, String polygon) {

        ResultSet rs = null;
        PreparedStatement ps = null;
        int lastInsertId = -1;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("update ");

            sb.append(entity);
            sb.append(" set ");
            sb.append(entity);
            sb.append("_");
            sb.append(property);

            sb.append(" = ");

            sb.append("ST_Simplify((ST_GeomFromEWKT(\'SRID=4326;");
            sb.append(polygon);
            sb.append("\')),0.09)");
            sb.append(" where ");

            sb.append(entity);
            sb.append("_");
            sb.append(parent);
            sb.append("_id");
            sb.append(" = ");
            sb.append(parentId);


            sb.append(" returning ");
            sb.append(entity);
            sb.append("_id");

            log.info(sb.toString());
            ps = c.prepareStatement(sb.toString());
//            ps.setString(1, polygon);
            rs = ps.executeQuery();
            if (rs.next()) {
                StringBuffer returnIdParam = new StringBuffer();
                returnIdParam.append(entity);
                returnIdParam.append("_id");
                lastInsertId = rs.getInt(returnIdParam.toString());

            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return lastInsertId;
    }

    /*
     *      sb.append(" where ");
    fieldCount = 0;
    for (String s : whereNames) {
    sb.append(entityType);
    sb.append("_");
    sb.append(s);
    sb.append(" = ?");
    if (fieldCount++ < whereMap.size() - 1) {
    sb.append(" AND ");
    }
    }
     */
    public static int storeGeometryChild(Connection c, String entity, String property, int parentId, String parent, String polygon) {

        ResultSet rs = null;
        PreparedStatement ps = null;
        int lastInsertId = -1;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");

            sb.append(entity);
            sb.append("(");

            sb.append(entity);
            sb.append("_");
            sb.append(parent);
            sb.append("_");
            sb.append("id,");



            sb.append(entity);
            sb.append("_");
            sb.append(property);

            sb.append(")");
            sb.append(" values(");
            sb.append(parentId);
            sb.append(",");
            sb.append("ST_GeomFromEWKT(\'SRID=4326;");
            sb.append(polygon);
            sb.append("\')) returning ");
            sb.append(entity);
            sb.append("_id");

            log.info(sb.toString());
            ps = c.prepareStatement(sb.toString());
//            ps.setString(1, polygon);
            rs = ps.executeQuery();
            if (rs.next()) {
                StringBuffer returnIdParam = new StringBuffer();
                returnIdParam.append(entity);
                returnIdParam.append("_id");
                lastInsertId = rs.getInt(returnIdParam.toString());

            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return lastInsertId;
    }

    public static boolean doesGeometryExist(Connection c, String polygon, String entity, String property) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int geometryCount = -1;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select count(*) from ");

            sb.append(entity);
            sb.append(" where ");


            sb.append(" st_equals(");
            sb.append(entity);
            sb.append("_");
            sb.append(property);
            sb.append(",");
            sb.append("ST_GeomFromEWKT(\'SRID=4326;");
            sb.append(polygon);
            sb.append("\')) ");


            log.info(sb.toString());
            ps = c.prepareStatement(sb.toString());
            rs = ps.executeQuery();

            if (rs.next()) {

                geometryCount = rs.getInt(1);
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return geometryCount > 0;
    }

    public static int getGeometryBoundsId(Connection c, String polygon, String entity, String property) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int geometryid = -1;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            sb.append(entity);
            sb.append("_id ");
            sb.append(" from ");

            sb.append(entity);
            sb.append(" where st_equals(");
            sb.append(entity);
            sb.append("_");
            sb.append(property);

            sb.append(" ,");


            sb.append("ST_GeomFromEWKT(\'SRID=4326;");
            sb.append(polygon);
            sb.append("\')) ");


            log.fine(sb.toString());
            ps = c.prepareStatement(sb.toString());
            rs = ps.executeQuery();

            if (rs.next()) {

                geometryid = rs.getInt(1);
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return geometryid;
    }

    public static boolean doesGeometryWithEqualBoundsExist(Connection c, String polygon, String entity, String property) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int geometryCount = -1;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select count(*) from ");

            sb.append(entity);
            sb.append(" where ");
            sb.append(entity);
            sb.append("_");
            sb.append(property);

            sb.append(" = ");


            sb.append("ST_GeomFromEWKT(\'SRID=4326;");
            sb.append(polygon);
            sb.append("\') ");


            log.info(sb.toString());
            ps = c.prepareStatement(sb.toString());
            rs = ps.executeQuery();

            if (rs.next()) {

                geometryCount = rs.getInt(1);
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return geometryCount > 0;
    }

    public static String getTextAsSVG(Connection c, String geomText) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String svg = null;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select st_assvg(st_geomfromtext('");
            sb.append(geomText);
            sb.append("'))");

            log.info(sb.toString());
            ps = c.prepareStatement(sb.toString());

            rs = ps.executeQuery();

            if (rs.next()) {

                svg = rs.getString(1);
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return svg;
    }

    public static String getGeometryAsKML(Connection c, String entity, String geomProperty, String whereProp, Object whereValue) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String kml = null;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select st_asKML(");
            sb.append(entity);
            sb.append("_");
            sb.append(geomProperty);

            sb.append(")");

            sb.append(" from ");

            sb.append(entity);
            sb.append(" where ");
            sb.append(entity);
            sb.append("_");
            sb.append(whereProp);

            sb.append(" = ? ");

            log.info(sb.toString());
            ps = c.prepareStatement(sb.toString());
            ps.setObject(1, whereValue);

            rs = ps.executeQuery();

            if (rs.next()) {

                kml = rs.getString(1);
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return kml;
    }


     public static String getGeometryBboxAsKML(Connection c, String entity, String geomProperty, String whereProp, Object whereValue) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String kml = null;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select st_asKML(st_setsrid(st_extent(");
            sb.append(entity);
            sb.append("_");
            sb.append(geomProperty);

            sb.append("),4326))");

            sb.append(" from ");

            sb.append(entity);
            sb.append(" where ");
            sb.append(entity);
            sb.append("_");
            sb.append(whereProp);

            sb.append(" = ? ");

            log.info(sb.toString());
            ps = c.prepareStatement(sb.toString());
            ps.setObject(1, whereValue);

            rs = ps.executeQuery();

            if (rs.next()) {

                kml = rs.getString(1);
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return kml;
    }


    public static String getGeometryAsText(Connection c, String entity, String geomProperty, String whereProp, Object whereValue) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String wkt = null;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select st_asText(");
            sb.append(entity);
            sb.append("_");
            sb.append(geomProperty);

            sb.append(")");

            sb.append(" from ");

            sb.append(entity);
            sb.append(" where ");
            sb.append(entity);
            sb.append("_");
            sb.append(whereProp);

            sb.append(" = ? ");

            log.fine(sb.toString());
            ps = c.prepareStatement(sb.toString());
            ps.setObject(1, whereValue);

            rs = ps.executeQuery();

            if (rs.next()) {

                wkt = rs.getString(1);
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return wkt;
    }

    /**
     * collecting one to many entities from parent id.  example i.e. study -> basin_study
     *
     */
    public List<E> getChildEntitiesByParentId(Connection c, String parentType, String childType, RowMapper<E> rowMapper, int parentId) {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        if (c == null) {
            log.log(Level.SEVERE, "could not get entities of type{0} because connection was null", childType);
            return new ArrayList<E>(0);
        }
        try {
            sb.append("select * from ");
            sb.append(childType);
            sb.append(" inner join ");
            sb.append(parentType);
            sb.append(" on ");
            sb.append(childType);
            sb.append(".");
            sb.append(childType);
            sb.append("_");
            sb.append(parentType);
            sb.append("_id = ");
            sb.append(parentType);
            sb.append(".");
            sb.append(parentType);
            sb.append("_id");

            sb.append(" and ");
            sb.append(parentType);
            sb.append(".");
            sb.append(parentType);
            sb.append("_id");
            sb.append(" = ");
            sb.append(parentId);

            String query = sb.toString();
            log.info(query);

            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            return rowMapper.results(rs);

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.log(Level.SEVERE, "query {0}", sb.toString());
            DBUtils.close(c);
        } finally {
            DBUtils.close(ps, rs);
        }
        return new ArrayList<E>(0);
    }

    /**
     * collecting many to many entities from parent id.  example i.e. study -> basin_study
     *
     */
    public List<E> getJoinedChildEntitiesByParentId(Connection c, String parentType, String childType, RowMapper<E> rowMapper, int parentId) {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        if (c == null) {
            log.log(Level.SEVERE, "could not get entities of type{0} because connection was null", childType);
            return new ArrayList<E>(0);

        }
        try {

            sb.append("select * from ");
            sb.append(childType);
            sb.append(" inner join ");
            sb.append(parentType);
            sb.append("_");
            sb.append(childType);
            sb.append(" on ");
            sb.append(childType);
            sb.append(".");
            sb.append(childType);
            sb.append("_id = ");
            sb.append(parentType);
            sb.append("_");
            sb.append(childType);
            sb.append(".");
            sb.append(childType);
            sb.append("_id");

            sb.append(" and ");
            sb.append(parentType);
            sb.append("_");
            sb.append(childType);
            sb.append(".");
            sb.append(parentType);
            sb.append("_id");
            sb.append(" = ");
            sb.append(parentId);

            String query = sb.toString();
            log.info(query);

            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            return rowMapper.results(rs);

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.log(Level.SEVERE, "query {0}", sb.toString());
            DBUtils.close(c);

        } finally {
            DBUtils.close(ps, rs);
        }
        return new ArrayList<E>(0);
    }

    public static int storeGeometry(Connection c, String entity, String property, String polygon) {

        ResultSet rs = null;
        PreparedStatement ps = null;
        int lastInsertId = -1;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");

            sb.append(entity);
            sb.append("(");
            sb.append(entity);
            sb.append("_");
            sb.append(property);

            sb.append(")");
            sb.append(" values(");

            sb.append("ST_GeomFromEWKT(\'SRID=4326;");
            sb.append(polygon);
            sb.append("\')) returning ");
            sb.append(entity);
            sb.append("_id");

            log.fine(sb.toString());
            ps = c.prepareStatement(sb.toString());
            rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder returnIdParam = new StringBuilder();
                returnIdParam.append(entity);
                returnIdParam.append("_id");
                lastInsertId = rs.getInt(returnIdParam.toString());
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return lastInsertId;
    }

    /**
     * data can be empty, but the geom must not be null
     * @param c
     * @param entity
     * @param property
     * @param polygon
     * @param data
     * @return
     */
    public static int storeGeometryAndInfo(Connection c, String entity, String property, String polygon, TreeMap<String, Object> data) {

        ResultSet rs = null;
        PreparedStatement ps = null;
        int lastInsertId = -1;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");

            sb.append(entity);
            sb.append("(");





            Set<String> fieldNames = data.keySet();

            for (String s : fieldNames) {
                sb.append(entity);
                sb.append("_");
                sb.append(s);

                sb.append(",");

            }
            sb.append(entity);
            sb.append("_");
            sb.append(property);
            sb.append(")");
            sb.append(" values(");


            for (String s : fieldNames) {
                sb.append("?");

                sb.append(",");

            }


            sb.append("ST_GeomFromEWKT(\'SRID=4326;");
            sb.append(polygon);
            sb.append("\')) returning ");
            sb.append(entity);
            sb.append("_id");

            log.fine(sb.toString());
            ps = c.prepareStatement(sb.toString());

            int fieldCount = 0;
            for (String s : fieldNames) {
                Object o = data.get(s);
                if (o instanceof Date) {
                    ps.setDate(++fieldCount, new java.sql.Date(((Date) o).getTime()));
                } else {
                    ps.setObject(++fieldCount, o);
                }

            }

            rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder returnIdParam = new StringBuilder();
                returnIdParam.append(entity);
                returnIdParam.append("_id");
                lastInsertId = rs.getInt(returnIdParam.toString());
            }

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }

        return lastInsertId;
    }

    public List<E> getEntitiesBySingleParameter(Connection c, String entityType, RowMapper<E> rowMapper, String parameterName, String value) {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        if (c == null) {
            log.log(Level.SEVERE, "could not get entities of type{0} because connection was null", entityType);
            return new ArrayList<E>(0);

        }
        try {

            sb.append("select * from ");
            sb.append(entityType);
            sb.append(" where ");
            sb.append(parameterName);
            sb.append(" = ");
            sb.append(value);

            String query = sb.toString();
            log.info(query);

            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            return rowMapper.results(rs);

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.log(Level.SEVERE, "query {0}", sb.toString());

        } finally {
            DBUtils.close(ps, rs);
        }
        return new ArrayList<E>(0);
    }

    public List<E> getEntities(Connection c, String entityType, RowMapper<E> rowMapper) {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        if (c == null) {
            log.log(Level.SEVERE, "could not get entities of type{0} because connection was null", entityType);
            return new ArrayList<E>(0);

        }
        try {

            sb.append("select * from ");
            sb.append(entityType);

            String query = sb.toString();
            log.info(query);

            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            return rowMapper.results(rs);

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.log(Level.SEVERE, "query {0}", sb.toString());

        } finally {
            DBUtils.close(ps, rs);
        }
        return new ArrayList<E>(0);
    }

    public int getNumEntities(Connection c, String entityType) {
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;
        int count = -1;
        try {

            sb.append("select count(*) from " + entityType);


            String query = sb.toString();
            log.info(query);

            ps = c.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.log(Level.SEVERE, "query {0}", new Object[]{sb.toString()});

        } finally {
            DBUtils.close(ps, rs);
        }
        return count;
    }

    public static void storeEntitySinglePropertyString(Connection c, String entityType, String propertyName, String data) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        try {

            sb.append("insert into ");


            sb.append(entityType);
            sb.append("(");
            sb.append(entityType);
            sb.append("_");
            sb.append(propertyName);
            sb.append(") values(?)");
            String query = sb.toString();
            log.fine(query);

            ps = c.prepareStatement(query);
            ps.setString(1, data);

            ps.executeUpdate();

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.severe("query " + sb.toString());

        } finally {
            DBUtils.close(ps);
        }
    }

    public static void storeEntitySinglePropertyById(Connection c, String entityType, String propertyName, Object data, int id) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        try {

            sb.append("update ");


            sb.append(entityType);
            sb.append(" set ");
            sb.append(entityType);
            sb.append("_");
            sb.append(propertyName);
            sb.append(" = ? where ");
            sb.append(entityType);
            sb.append("_id = ?");
            String query = sb.toString();
            log.info(query);

            ps = c.prepareStatement(query);
            ps.setObject(1, data);
            ps.setObject(2, id);


            ps.executeUpdate();

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.severe("query " + sb.toString());

        } finally {
            DBUtils.close(ps);
        }
    }

    public static void storeEntityDatePropertyById(Connection c, String entityType, String propertyName, Date data, int id) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        try {

            sb.append("update ");


            sb.append(entityType);
            sb.append(" set ");
            sb.append(entityType);
            sb.append("_");
            sb.append(propertyName);
            sb.append(" = ? where ");
            sb.append(entityType);
            sb.append("_id = ?");
            String query = sb.toString();
            log.info(query);

            ps = c.prepareStatement(query);
            ps.setDate(1, new java.sql.Date(data.getTime()));
            ps.setInt(2, id);

            ps.executeUpdate();

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.severe("query " + sb.toString());

        } finally {
            DBUtils.close(ps);
        }
    }

    public static int storeEntityData(Connection c, String entityType, TreeMap<String, ?> data) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        Set<String> fieldNames = data.keySet();
        int fieldCount = 0;
        int insertedId = -1;
        ResultSet rs = null;
        try {

            sb.append("insert into ");
            sb.append(entityType);

            sb.append("(");
            for (String s : fieldNames) {
                sb.append(entityType);
                sb.append("_");
                sb.append(s);
                if (fieldCount++ < data.size() - 1) {
                    sb.append(",");
                }
            }

            sb.append(")values(");
            fieldCount = 0;
            for (String s : fieldNames) {
                sb.append("?");
                if (fieldCount++ < data.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(")returning ");
            sb.append(entityType);
            sb.append("_id");

            String query = sb.toString();
            log.fine(query);
            ps = c.prepareStatement(query);
            fieldCount = 0;
            for (String s : fieldNames) {
                Object o = data.get(s);
                if (o instanceof Date) {
                    ps.setDate(++fieldCount, new java.sql.Date(((Date) o).getTime()));
                } else {
                    ps.setObject(++fieldCount, o);
                }
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                insertedId = rs.getInt(entityType + '_' + "id");
            }

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.log(Level.SEVERE, "query {0}", sb.toString());

        } finally {
            DBUtils.close(ps, rs);
        }
        return insertedId;
    }

    public static boolean doesEntityExist(Connection c, String entityType, TreeMap<String, ?> data) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        Set<String> fieldNames = data.keySet();
        int fieldCount = 0;

        ResultSet rs = null;
        try {

            sb.append("select * from ");
            sb.append(entityType);

            sb.append(" where ");
            for (String s : fieldNames) {
                sb.append(entityType);
                sb.append("_");
                sb.append(s);
                sb.append(" = ? ");
                if (fieldCount++ < data.size() - 1) {
                    sb.append(" AND ");
                }
            }



            String query = sb.toString();
            log.fine(query);
            ps = c.prepareStatement(query);
            fieldCount = 0;
            for (String s : fieldNames) {
                Object o = data.get(s);
                if (o instanceof Date) {
                    ps.setDate(++fieldCount, new java.sql.Date(((Date) o).getTime()));
                } else {
                    ps.setObject(++fieldCount, o);
                }
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.log(Level.SEVERE, "query {0}", sb.toString());

        } finally {
            DBUtils.close(ps, rs);
        }
        return false;
    }

    public static int updateEntityData(Connection c, String entityType, TreeMap<String, ?> data, TreeMap<String, ?> whereMap) {

        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();
        Set<String> fieldNames = data.keySet();
        Set<String> whereNames = whereMap.keySet();
        int fieldCount = 0;
        int insertedId = -1;
        ResultSet rs = null;
        try {

            sb.append("update  ");
            sb.append(entityType);

            sb.append(" set ");
            for (String s : fieldNames) {
                sb.append(entityType);
                sb.append("_");
                sb.append(s);
                sb.append(" = ");
                sb.append("?");
                if (fieldCount++ < data.size() - 1) {
                    sb.append(",");
                }
            }

            sb.append(" where ");
            fieldCount = 0;
            for (String s : whereNames) {
                sb.append(entityType);
                sb.append("_");
                sb.append(s);
                sb.append(" = ?");
                if (fieldCount++ < whereMap.size() - 1) {
                    sb.append(" AND ");
                }
            }


            String query = sb.toString();
            log.info(query);
            ps = c.prepareStatement(query);
            fieldCount = 0;

            for (String s : fieldNames) {
                ps.setObject(++fieldCount, data.get(s));
                log.info("just set " + fieldCount + " to " + data.get(s));
            }

            //int wherecount = 0;
            for (String s : whereNames) {
                ps.setObject(++fieldCount, whereMap.get(s));
                log.info("just set " + fieldCount + " to " + whereMap.get(s));
            }
            ps.executeUpdate();


        } catch (SQLException ex) {

            log.severe(ex.getMessage());
            log.severe("query " + sb.toString());

        } finally {
            DBUtils.close(ps, rs);
        }
        return insertedId;
    }

    public static HashMap<String, Number> getMaxMinAvg(Connection c, String entityName, String propName, List<String> groupByProperties, TreeMap<String, Object> optionalFilterMap) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        HashMap<String, Number> map = new HashMap<String, Number>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select max(");
            sb.append(entityName);
            sb.append("_");
            sb.append(propName);
            sb.append("),");

            sb.append("min(");
            sb.append(entityName);
            sb.append("_");
            sb.append(propName);
            sb.append("), ");

            sb.append("avg(");
            sb.append(entityName);
            sb.append("_");
            sb.append(propName);
            sb.append(") ");

            sb.append(" from ");
            sb.append(entityName);

            if (optionalFilterMap != null) {
                sb.append(" where ");
                Set<String> keys = optionalFilterMap.keySet();
                int filterCount = 0;
                for (String s : keys) {
                    sb.append(entityName);
                    sb.append("_");
                    sb.append(s);
                    sb.append("=? ");
                    if (filterCount++ < optionalFilterMap.size() - 1) {
                        sb.append("AND ");
                    }
                }
            }

            sb.append(" group by ");
            int groupByCount = 0;
            for (String s : groupByProperties) {
                sb.append(entityName);
                sb.append("_");
                sb.append(s);
                if ((groupByCount++) < (groupByProperties.size() - 1)) {
                    sb.append(", ");
                }
            }


            ps = c.prepareStatement(sb.toString());
            if (optionalFilterMap != null) {
                int filterIndex = 0;
                Set<String> keys = optionalFilterMap.keySet();
                for (String s : keys) {
                    ps.setObject(++filterIndex, optionalFilterMap.get(s));

                }
            }
            log.info(sb.toString());

            rs = ps.executeQuery();
            if (rs.next()) {

                map.put("avg", rs.getDouble("avg"));
                map.put("max", rs.getDouble("max"));
                map.put("min", rs.getDouble("min"));
            }

            log.info(sb.toString());
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps,rs);
        }
        return map;
    }

    public static int getEntityId(Connection c, String entityName, String propName, Object propValue) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int countryId = -1;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            sb.append(entityName);
            sb.append("_id from ");
            sb.append(entityName);
            sb.append(" where ");
            sb.append(entityName);
            sb.append("_");
            sb.append(propName);
            sb.append("=?");

            ps = c.prepareStatement(sb.toString());
            ps.setObject(1, propValue);
            rs = ps.executeQuery();
            if (rs.next()) {
                countryId = rs.getInt(entityName + "_id");
            }
            log.fine(sb.toString());
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps, rs);
        }
        return countryId;
    }
}

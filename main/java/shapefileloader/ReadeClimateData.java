/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader;

import dao.GeoDao;
import database.DBUtils;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.filter.FilterFactory2;


/**
 *
 * @author wb385924
 */
public class ReadeClimateData {

    FeatureIterator fi = null;
    Logger log = Logger.getLogger(ReadeClimateData.class.getName());

    private Style createPointStyle() {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
        Graphic gr = styleFactory.createDefaultGraphic();
        Mark mark = styleFactory.getCircleMark();

        mark.setStroke(styleFactory.createStroke(
                filterFactory.literal(Color.BLUE), filterFactory.literal(1)));

        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));

        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(5));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
//        rule.setFilter(filterFactory.less(Expression.NIL, Expression.NIL)Expression.NIL, Expression.NIL))

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    public int storePolygon(Connection c, String name, int countryId, int willId, String polygon) {

        ResultSet rs = null;
        PreparedStatement ps = null;
        int lastInsertId = -1;
        try {

            ps = c.prepareStatement("insert into basin (basin_name,basin_geom, basin_country_id, basin_will_id) values(?,ST_GeomFromEWKT(?),?,?) returning basin_id");
            ps.setString(1, name);
            ps.setString(2, polygon);
            ps.setInt(3, countryId);
            ps.setInt(4, willId);

            rs = ps.executeQuery();
            if (rs.next()) {
                lastInsertId = rs.getInt("basin_id");
            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } finally {
            DBUtils.close(ps);
        }

        return lastInsertId;
    }

    private boolean hasSomething(String s) {
        if (s == null || s.trim().length() == 0) {
            return false;

        }
        return true;
    }

    private void storeCountry(Connection c, String name) {
        if (!hasSomething(name)) {
            return;
        }

        name = name.trim().toLowerCase();
        GeoDao.storeEntitySinglePropertyString(c, Tables.COUNTRY, Tables.NAME, name);
    }

    private int storeStudy(Connection c, String code) {
        if (!hasSomething(code)) {
            return -1;
        }
        TreeMap<String, String> studyMap = new TreeMap<String, String>();
        studyMap.put("code", code);

        return GeoDao.storeEntityData(c, Tables.STUDY, studyMap);

    }

    private void storeDrainage(Connection c, String name) {
        if (!hasSomething(name)) {
            return;
        }

        name = name.trim().toLowerCase();
        GeoDao.storeEntitySinglePropertyString(c, Tables.DRAINAGE, Tables.NAME, name);
    }

    public int getCountryId(Connection c, String name) {
        if (!hasSomething(name)) {
            return -1;
        }
        name = name.trim();
        String entityType = Tables.COUNTRY;
        int countryId = GeoDao.getEntityId(c, entityType,"name", name);
        if (countryId == -1) {
            //storeCountry(c, name);
        }
        countryId = GeoDao.getEntityId(c, entityType, "name", name);

        return countryId;
    }

    public int getStudyId(Connection c, String code) {
        if (!hasSomething(code)) {
            return -1;
        }

        code = code.trim();
        String entityType = Tables.STUDY;
        int studyId = GeoDao.getEntityId(c, entityType, "code", code );
        if (studyId == -1) {
            studyId = storeStudy(c, code);
        }
        studyId = GeoDao.getEntityId(c, entityType,"code", code);

        return studyId;
    }

    public int getDrainageId(Connection c, String name) {
        if (!hasSomething(name)) {
            return -1;
        }

        name = name.trim().toLowerCase();

        int drainageId = GeoDao.getEntityId(c, Tables.DRAINAGE,"name", name);
        if (drainageId == -1) {
            storeDrainage(c, name);
        }
        drainageId = GeoDao.getEntityId(c, Tables.DRAINAGE, "name", name);
        return drainageId;
    }

    public void storeMappedDrainage(Connection c, int basinId, int drainageId) {
        if (basinId == -1 || drainageId == -1) {
            log.info("drainage id or basin id is -1 so not inserting to drainage map table");
        }
        GeoDao.storeJoinTableRecord(c, Tables.BASIN, basinId, Tables.DRAINAGE, drainageId);
    }

    public void storeStudyValue(Connection c, int basinId, int studyId, int data) {
        if (basinId == -1 || studyId == -1) {
            log.info("drainage id or basin id is -1 so not inserting to study value");
        }
        GeoDao.storeJoinTableWithData(c, Tables.BASIN, basinId, Tables.STUDY, studyId, Tables.DATA, data);
    }

    public void storeStudyValues(Connection con, Collection<Property> props, int basinId) {
        if (basinId == -1) {
            return;
        }

        Iterator<Property> pi = props.iterator();
        while (pi.hasNext()) {
            Property p = pi.next();
            String propName = p.getName().toString();
            if (StudyCodeMatcher.isStudyCode(propName)) {
                String studyCode = propName;
                int studyValue = Integer.parseInt(p.getValue().toString());
                int studyId = getStudyId(con, studyCode);
                storeStudyValue(con, basinId, studyId, studyValue);
            }
        }
    }

    public String extractProperty(String property) {
        if (property == null) {
            return null;
        }
        return property.substring(property.indexOf(">=") + 2);
    }

     public int getCountry(String countryName) {



        Connection con = null;
        try {
            
            con = getConnection();
            return getCountryId(con, countryName);
        } catch (SQLException ex) {

            ex.printStackTrace();
        
        } finally {
            try {
                fi.close();
                con.close();
            } catch (SQLException ex) {
                log.severe(ex.getMessage());
            }
        }
        return -1;
    }

    public void readData(String path) {



        Connection con = null;
        try {
            con = getConnection();
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            fi = fc.features();
            int count = 0;


            while (fi.hasNext()) {
                Feature f = fi.next();

//                GeometryAttribute ga = f.getDefaultGeometryProperty();
//                System.out.println("Type " + ga.getType());
//                System.out.println("Descriptor " + ga.getDescriptor());
//                System.out.println("Area:" + ((Geometry) f.getDefaultGeometryProperty().getValue()));
//


                String polygon = f.getDefaultGeometryProperty().getValue().toString();
                log.log(Level.FINE, "Geom Prop: {0}", polygon);

                String name = f.getProperty("Long_Name").getValue().toString();
                //name = extractProperty(name);
                log.log(Level.FINE, "Name:{0}", name);


                // country
                String country = f.getProperty("CNTRY_NAME").getValue().toString();
                int countryId = getCountryId(con, country);








                // will_id
                int willId = -1;
                try {
                    willId = Integer.parseInt(f.getProperty("Will_ID").getValue().toString());



                    // validate
                    if (willId != -1 && countryId != -1) {
                        log.fine("all params are valid");
                        int basinId = storePolygon(con, name, countryId, willId, "SRID=4326;" + polygon);
                        if (basinId != -1) {
                            log.log(Level.FINE, "last basin insert id: {0}", basinId);

                            // drainage
                            String drainage = f.getProperty("DRAINAGE").getValue().toString();
                            int drainageId = getDrainageId(con, drainage);
                            if (drainageId != -1) {
                                storeMappedDrainage(con, basinId, drainageId);
                            }

                            // study values
                            Collection<Property> props = f.getProperties();
                            storeStudyValues(con, props, basinId);
                            
                        } else {
                            log.log(Level.WARNING, "last basin insert not successful {0}", basinId);
                        }

                    } else {
                        log.warning("invalid params for core basin table");
                        log.warning(willId + " " + country + " " + countryId);
                    }
                } catch (NumberFormatException nfe) {
                    log.severe(nfe.getMessage());
                    nfe.printStackTrace();
                }





//                String geom = f.getProperty("the_geom").toString();
//                int indexOfMP = geom.indexOf("MULTIPOLYGON");
//                int indexOFBracket = geom.lastIndexOf("]");
//
//                geom = geom.substring(indexOfMP,indexOFBracket);
                //System.out.println("geom: " + geom);





                count++;
            }
            System.out.println("total features is " + count);

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {
            try {
                fi.close();
                con.close();
            } catch (SQLException ex) {
                log.severe(ex.getMessage());
            }
        }
    }



    public void readSquareData(String path) {



        Connection con = null;
        try {
            con = getConnection();
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            fi = fc.features();
            int count = 0;


            while (fi.hasNext()) {
                Feature f = fi.next();

//                GeometryAttribute ga = f.getDefaultGeometryProperty();
//                System.out.println("Type " + ga.getType());
//                System.out.println("Descriptor " + ga.getDescriptor());
//                System.out.println("Area:" + ((Geometry) f.getDefaultGeometryProperty().getValue()));
//


                String polygon = f.getDefaultGeometryProperty().getValue().toString();
                log.log(Level.INFO, "Geom Prop: {0}", polygon);












//                String geom = f.getProperty("the_geom").toString();
//                int indexOfMP = geom.indexOf("MULTIPOLYGON");
//                int indexOFBracket = geom.lastIndexOf("]");
//
//                geom = geom.substring(indexOfMP,indexOFBracket);
                //System.out.println("geom: " + geom);





                count++;
            }
            System.out.println("total features is " + count);

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {
            try {
                fi.close();
                con.close();
            } catch (SQLException ex) {
                log.severe(ex.getMessage());
            }
        }
    }



    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", "wb385924");
        //connectionProps.put("password", this.password);

        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatial_climate");



        System.out.println("Connected to database");
        return conn;
    }

    public static void main(String[] args) {
        new ReadeClimateData().readData("C:\\climate data\\warter basin 2050 2059\\WaterBasinData_2050_2059_a2.shp");
    }
}

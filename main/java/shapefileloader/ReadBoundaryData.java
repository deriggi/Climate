/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader;

import dao.GeoDao;
import database.DBUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import sdnis.wb.util.ShapeFileUtils;
import sdnis.wb.util.ShapeWrapper;

/**
 *
 * @author wb385924
 */
public class ReadBoundaryData {

    private Logger log = Logger.getLogger(RainDataReader.class.getName());

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", "wb385924");
        //connectionProps.put("password", this.password);

        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatial_climate");



        System.out.println("Connected to database");
        return conn;
    }

    public void readBoundaryData(String path) {


        FeatureIterator fi = null;
        Connection con = null;
        try {
            con = getConnection();
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            List<String> propNames = new ArrayList<String>();
            propNames.add("ISO_CODES");


            ShapeFileUtils shapeUtil = new ShapeFileUtils( null, propNames);
            fi = fc.features();
            int count = 0;


            while (fi.hasNext()) {
                Feature f = fi.next();
                ShapeWrapper wrapper = shapeUtil.extractFeatureProperties(f);

                String isoCode = wrapper.getPropertyMap().get("ISO_CODES");
                if (isoCode.equals("VNM")) {


                    int id = GeoDao.getEntityId(con, "country", "iso_3", isoCode);
                    log.log(Level.INFO, "iso  is {0}", isoCode);

                    GeoDao.storeGeometryChild(con, "boundary", "shape", id, "area", wrapper.getShapeString());
                    GeoDao.updateSimplifiedGeometryChild(con, "boundary", "simple", id, "area", wrapper.getShapeString());
                    log.log(Level.INFO, "id for {0} is {1}", new Object[]{isoCode, id});

                }

//                TreeMap<String, String> updateMap = new TreeMap<String, String>();
//                updateMap.put("name", countryName);
//
//                TreeMap<String, String> whereMap = new TreeMap<String, String>();
//                whereMap.put("iso_3", isoCode);
//
//                if (id != -1) {
//                    GeoDao.updateEntityData(con, "country", updateMap, whereMap);
//                }else{
//                    updateMap.putAll(whereMap);
//                    GeoDao.storeEntityData(con, "country", updateMap);
//                    log.log(Level.INFO, "storing {0}  {1}", new Object[]{isoCode, countryName});
//                }



//                log.log(Level.INFO, "id for {0} is {1}", new Object[]{isoCode, id});
//
//                log.log(Level.FINE, "feature {0} geom is {1}", wrapper.getShapeString());
//
//                int countryBoundaryId = GeoDao.getEntityId(con, "country_boundary", "country_id", id);
//                log.log(Level.INFO, "country boundary id  is {0}", new Integer(countryBoundaryId).toString());
//
//                if ((id == 528) ) {
//                    GeoDao.storeGeometryChild(con, "boundary", "shape", id, "area", wrapper.getShapeString());
//                    GeoDao.updateSimplifiedGeometryChild(con, "boundary", "simple", id, "area", wrapper.getShapeString());
//                    log.log(Level.INFO, "id for {0} is {1}", new Object[]{isoCode, id});
//                }

                count++;
            }
            log.log(Level.FINE, "total features is {0}", count);

        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {

            fi.close();
            DBUtils.close(con);

        }
    }

    public void readAllBoundaries(File f) {
        if (f.isDirectory()) {
            String[] childFiles = f.list();
            for (String s : childFiles) {
                File child = new File(f.getAbsolutePath() + "/" + s);
                if (child.isDirectory()) {
                    readAllBoundaries(child);
                } else if (child.getPath().endsWith("_adm0.shp")) {
                    readBoundaryData(child.getAbsolutePath());
                }
            }
        }
    }

    public static void main(String[] args) {
        //File f = new File("somefile");

//        new ReadBoundaryData().readBoundaryData("S:\\GLOBAL\\ADMIN\\WB Shapefiles 2010\\High Resolution\\World_Polys_High.shp");

//         new ReadBoundaryData().readBoundaryData("C:\\climate data\\countries.shp");

        DBUtils.closeAll();
    }
}

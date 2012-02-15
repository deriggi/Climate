/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
public class RainDataReader{

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

public void readSquareData(String path) {


        FeatureIterator fi = null;
        Connection con = null;
        try {
            con = getConnection();
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            List<String> propNames = new ArrayList<String>();
            propNames.add("FID");
            ShapeFileUtils shapeUtil = new ShapeFileUtils( "M\\d{1,2}y\\d{4}", propNames);
            fi = fc.features();
            int count = 0;


            while (fi.hasNext()) {
                Feature f = fi.next();
                ShapeWrapper wrapper = shapeUtil.extractFeatureProperties(f);
                HashMap<String,String> propMap = wrapper.getPropertyMap();
                log.log(Level.INFO, "feature {0} geom is {1}", new Object[]{propMap.get("FID"),wrapper.getShapeString()});
                log.log(Level.INFO, "feature {0} has {1} data points", new Object[]{propMap.get("FID"),propMap.size()});

                count++;
            }
            log.log(Level.INFO, "total features is {0}", count);

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


  public static void main(String[] args) {
        new RainDataReader().readSquareData("C:\\climate data\\rainfall 1901 - 1920\\pre1901_1920Merge.shp");
    }


}


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
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
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
public class BasinBoundaryLoader {

    private static final Logger log = Logger.getLogger(RainDataReader.class.getName());
    String wbhuc = "WBHUC";

    public void readBoundaryData(String path) {
        System.out.println("trying to get " + path);
        FeatureIterator fi = null;
        Connection con = null;
        try {
            con = DBUtils.getConnection();
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));
            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            List<String> propNames = new ArrayList<String>();
            propNames.add(wbhuc);

            ShapeFileUtils shapeUtil = new ShapeFileUtils( null, propNames);
            fi = fc.features();
            int count = 0;

            while (fi.hasNext()) {
                Feature f = fi.next();
                ShapeWrapper wrapper = shapeUtil.extractFeatureProperties(f);
                String basinCode = wrapper.getPropertyMap().get(wbhuc);
                int basinId = GeoDao.storeGeometry(con, "basin", "geom",  wrapper.getShapeString());
                TreeMap<String,Integer> propMap = new TreeMap<String,Integer>();
                propMap.put("code",Integer.parseInt(basinCode));
                
                TreeMap<String,Integer> whereMap = new TreeMap<String,Integer>();
                whereMap.put("id",basinId);

                GeoDao.updateEntityData(con, "basin", propMap, whereMap);
                count++;
            }
            
            log.log(Level.FINE, "total features is {0}", count);

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
                } else if (child.getPath().endsWith(".shp") || child.getPath().endsWith(".SHP")) {
                    readBoundaryData(child.getAbsolutePath());
                }
            }
        }
    }

    public static void main(String[] args) {
        //File f = new File("somefile");

        new BasinBoundaryLoader().readAllBoundaries(new File("C:\\climate data\\World_Basins_eliminate\\World_Basins_eliminate\\"));

//         new ReadBoundaryData().readBoundaryData("C:\\climate data\\countries.shp");

        DBUtils.closeAll();
    }
}

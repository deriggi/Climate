/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader;

import com.vividsolutions.jts.geom.Geometry;
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
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import sdnis.wb.util.ShapeFileUtils;
import sdnis.wb.util.ShapeWrapper;

/**
 *
 * @author wb385924
 */
public class CKPRegionBoundaryLoader {

    private static final Logger log = Logger.getLogger(CKPRegionBoundaryLoader.class.getName());
    String ckpRegion = "CKP_Region";

    public void readBoundaryData(String path) {
        System.out.println("trying to get " + path);
        SimpleFeatureIterator fi = null;
        Connection con = null;
        try {
            con = DBUtils.getConnection();
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));
            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection fc = featureSource.getFeatures();
            List<String> propNames = new ArrayList<String>();
            propNames.add(ckpRegion);

            ShapeFileUtils shapeUtil = new ShapeFileUtils( null, propNames);
            fi = fc.features();
            int count = 0;

            while (fi.hasNext()) {
                SimpleFeature f = fi.next();
                ShapeWrapper wrapper = shapeUtil.extractFeatureProperties(f);
                String basinCode = wrapper.getPropertyMap().get(ckpRegion);
//                log.info(((Geometry)f.getDefaultGeometry()).toText());
                int ckpRegionId = GeoDao.storeGeometry(con, "ckp_region", "geom",  ((Geometry)f.getDefaultGeometry()).toText());
                TreeMap<String,String> propMap = new TreeMap<String,String>();
                propMap.put("code",basinCode);
                
                TreeMap<String,Integer> whereMap = new TreeMap<String,Integer>();
                whereMap.put("id",ckpRegionId);

                GeoDao.updateEntityData(con, "ckp_region", propMap, whereMap);
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

        new CKPRegionBoundaryLoader().readBoundaryData("S:\\REGIONAL\\ADMIN_all_Regions\\CKP_regions_dissolved.shp");

//         new ReadBoundaryData().readBoundaryData("C:\\climate data\\countries.shp");

        DBUtils.closeAll();
    }
}

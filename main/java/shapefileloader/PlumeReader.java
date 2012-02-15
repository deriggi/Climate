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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class PlumeReader {

    private static final Logger log = Logger.getLogger(PlumeReader.class.getName());

    public void readBoundaryData(String path) {


        FeatureIterator fi = null;
        Connection con = null;
        try {
            con = null;//DBUtils.get("city_risk").getConnection();

            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            List<String> propNames = new ArrayList<String>();
            propNames.add("Sat_Source");
            propNames.add("Date");
            propNames.add("Cloud_dens");
            propNames.add("Rank");

            ShapeFileUtils shapeUtil = new ShapeFileUtils( null, propNames);
            fi = fc.features();
            int count = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            while (fi.hasNext()) {
                Feature f = fi.next();
                ShapeWrapper wrapper = shapeUtil.extractFeatureProperties(f);

                float rankf = Float.parseFloat(wrapper.getPropertyMap().get("Rank"));
                int rank = new Float(rankf).intValue();
                Date date = sdf.parse(wrapper.getPropertyMap().get("Date"));
                String satSource = wrapper.getPropertyMap().get("Sat_Source");
                String cloudDensity = wrapper.getPropertyMap().get("Cloud_dens");

                int id = GeoDao.storeGeometry(con, "plume", "shape", wrapper.getShapeString());
                if(id != -1){
                    GeoDao.storeEntityDatePropertyById(con, "plume", "date", date, id);
                    GeoDao.storeEntitySinglePropertyById(con, "plume", "rank", rank, id);
                    GeoDao.storeEntitySinglePropertyById(con, "plume", "sat_source", satSource, id);
                    GeoDao.storeEntitySinglePropertyById(con, "plume", "cloud_density", cloudDensity, id);
                }
                


                //log.log(Level.INFO, "grid id is {0}", cellId);
                
                
                log.log(Level.INFO, "feature {0} geom is {1}", wrapper.getShapeString());

                

                count++;
            }
            log.log(Level.INFO, "total features is {0}", count);

        } catch (ParseException pe){
            log.severe(pe.getMessage());
        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {

            fi.close();
            DBUtils.close(con);

        }
    }


     /**public void loadData(String path) {


        FeatureIterator fi = null;
        Connection con = null;
        try {

            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            List<String> propNames = new ArrayList<String>();
            propNames.add("Grid_ID");
            ShapeFileUtils shapeUtil = new ShapeFileUtils(fc, "M\\d{1,2}y\\d{4}", propNames);
            fi = fc.features();
            int count = 0;


            while (fi.hasNext()) {
                Feature f = fi.next();
                ShapeWrappers wrapper = shapeUtil.extractFeatureProperties(f);

                int cellId = Integer.parseInt(wrapper.getPropertyMap().get("Grid_ID"));
                String shapeString  = wrapper.getShapeString();
                log.log(Level.INFO, "grid id is {0}", cellId);
                //int id = GeoDao.getEntityId(con, "country", "iso_3", isoCode);

                log.log(Level.INFO, "feature {0} geom is {1}", shapeString);

                if(!doesCellIdExist(cellId)){
                    int returnId = insertGeometry(shapeString);
                    if(returnId != -1){
                        updateCell(cellId, returnId);
                    }
                }

                count++;
            }
            log.log(Level.INFO, "total features is {0}", count);


        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {

            fi.close();
            DBUtils.get().close(con);

        }
    }**/


    private int insertGeometry(String shapeString){
        DBUtils db = null;//DBUtils.get("city_risk");
        Connection c = db.getConnection();
        int returnId = GeoDao.storeGeometry(c, "grid_cell","bounds", shapeString);
        
        db.close(c);
        return returnId;
    }

    private boolean doesCellIdExist(int id){
        DBUtils db = null;//DBUtils.get("city_risk");
        Connection c = db.getConnection();

        int returnId = GeoDao.getEntityId(c, "grid_cell", "id", id);

        db.close(c);
        return returnId != -1;
    }

    private void updateCell(int id, int assignedId){
        DBUtils db =null;// DBUtils.get("city_risk");
        Connection c = db.getConnection();
        TreeMap<String,Integer> dataMap = new TreeMap<String,Integer>();
        dataMap.put("assigned_id", assignedId);

        TreeMap<String,Integer> whereMap = new TreeMap<String,Integer>();
        whereMap.put("id", id);


        GeoDao.updateEntityData(c, "grid_cell", dataMap, whereMap);

        db.close(c);
    }
    public static void main(String[] args) {
        //File f = new File("somefile");

        new PlumeReader().readBoundaryData("C:\\Users\\wb385924\\Desktop\\Plume_test\\Plume_test.shp");

        // new ReadBoundaryData().readAllBoundaries(new File("C:\\climate data\\countries"));nb2010_me_people.shp

        DBUtils.closeAll();
    }
}

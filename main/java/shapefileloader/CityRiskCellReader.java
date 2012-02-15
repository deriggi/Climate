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
public class CityRiskCellReader {

    private static final Logger log = Logger.getLogger(CityRiskCellReader.class.getName());

    public void readBoundaryData(String path) {


        FeatureIterator fi = null;
        Connection con = null;
        try {

            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            List<String> propNames = new ArrayList<String>();
            propNames.add("Grid_ID");
            ShapeFileUtils shapeUtil = new ShapeFileUtils( "M\\d{1,2}y\\d{4}", propNames);
            fi = fc.features();
            int count = 0;


            while (fi.hasNext()) {
                Feature f = fi.next();
                ShapeWrapper wrapper = shapeUtil.extractFeatureProperties(f);

                int cellId = Integer.parseInt(wrapper.getPropertyMap().get("Grid_ID"));
                String shapeString  = wrapper.getShapeString();
                log.log(Level.INFO, "grid id is {0}", cellId);
                //int id = GeoDao.getEntityId(con, "country", "iso_3", isoCode);
                
                log.log(Level.INFO, "feature {0} geom is {1}", shapeString);

                 if(!doesCellIdExist(cellId)){
                    int returnId = insertGeometry(shapeString);
                    if(returnId != -1){
                        updateCell(returnId, cellId);
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
        DBUtils db = null;//DBUtils.get("city_risk");
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

        //new CityRiskDataReader().readBoundaryData("C:\\Users\\wb385924\\Documents\\CityRisk\\manilla\\shapes\\Grid_500m_gcs.shp");

        // new ReadBoundaryData().readAllBoundaries(new File("C:\\climate data\\countries"));nb2010_me_people.shp

    }
}

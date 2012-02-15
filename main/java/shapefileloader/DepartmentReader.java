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
public class DepartmentReader {

    private static final Logger log = Logger.getLogger(ArgentineProvinceReader.class.getName());

    public void readBoundaryData(String path) {


        FeatureIterator fi = null;
        Connection con = null;
        try {
            con = null;//DBUtils.get("city_risk").getConnection();

            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            FeatureSource featureSource = store.getFeatureSource();
            FeatureCollection fc = featureSource.getFeatures();
            List<String> propNames = new ArrayList<String>();
            propNames.add("NAME_2");

            ShapeFileUtils shapeUtil = new ShapeFileUtils( null, propNames);
            fi = fc.features();
            int count = 0;

            while (fi.hasNext()) {
                Feature f = fi.next();
                ShapeWrapper wrapper = shapeUtil.extractFeatureProperties(f);



                String name = wrapper.getPropertyMap().get("NAME_2");


                int id = GeoDao.storeGeometry(con, "department", "shape", wrapper.getShapeString());
                if (id != -1) {

                    GeoDao.storeEntitySinglePropertyById(con, "department", "name", name, id);
                }


                log.log(Level.INFO, "feature {0} geom is {1}", wrapper.getShapeString());



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

    public static void main(String[] args) {
        //File f = new File("somefile");

        new DepartmentReader().readBoundaryData("C:\\Users\\wb385924\\Documents\\Volcano\\Departments_affected.shp");

        // new ReadBoundaryData().readAllBoundaries(new File("C:\\climate data\\countries"));nb2010_me_people.shp

        DBUtils.closeAll();
    }
}

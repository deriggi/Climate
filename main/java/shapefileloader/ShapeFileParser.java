/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import sdnis.wb.util.ShapeFileUtils;

/**
 *
 * @author wb385924
 */
public class ShapeFileParser {

    private static final Logger log = Logger.getLogger(RainDataReader.class.getName());

   

    public void readShapeFile(String path, List<String> propertyNames, String regexPattern, FeatureHandler fh) {


        SimpleFeatureIterator fi = null;
        try {
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection fc = featureSource.getFeatures();
//            featureSource.
           

            ShapeFileUtils shapeUtil = new ShapeFileUtils(regexPattern, propertyNames);
            fi = fc.features();
            int count = 0;


            while (fi.hasNext()) {
                SimpleFeature f = fi.next();
                fh.handleFeature( shapeUtil.extractFeatureProperties(f));
                
//                ShapeWrappers wrapper = shapeUtil.extractFeatureProperties(f);

//                wrapper.getPropertyMap();
//                wrapper.getShapeString();
                

                count++;
            }
            log.log(Level.INFO, "total features is {0}", count);

        
        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {

            fi.close();

        }
    }

   
}

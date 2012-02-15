/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import sdnis.wb.util.GeomShapeFileUtils;
import sdnis.wb.util.GeomShapeWrapper;

/**
 *
 * @author wb385924
 */
public class ShapeFileParserGeometryExtractor {

    private static final Logger log = Logger.getLogger(RainDataReader.class.getName());
    public List<GeomShapeWrapper> readShapeFile(String path, List<String> propertyNames, String regexPattern) {

        List<GeomShapeWrapper> shapeWrappers = new ArrayList<GeomShapeWrapper>();
        SimpleFeatureIterator fi = null;
        try {
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));
            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection fc = featureSource.getFeatures();
            GeomShapeFileUtils shapeUtil = new GeomShapeFileUtils(regexPattern, propertyNames);
            fi = fc.features();
            int count = 0;
            while (fi.hasNext()) {
                SimpleFeature f = fi.next();
                GeomShapeWrapper wrapper = shapeUtil.extractFeatureProperties(f, true);
                
                shapeWrappers.add(wrapper);
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
        return shapeWrappers;
    }
}

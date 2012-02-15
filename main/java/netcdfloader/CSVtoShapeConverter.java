/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author wb385924
 */
public class CSVtoShapeConverter {

    private SimpleFeatureType TYPE;

    private SimpleFeatureType getSimpleFeatureType() {
        if (TYPE == null) {
            try {
                TYPE = DataUtilities.createType("Location", "location:Point:srid=4326," + "Precip(mm):Float," + "Day:Float");
            } catch (SchemaException ex) {
                Logger.getLogger(CSVtoShapeConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return TYPE;
    }

    private void writeToShapeFile(File shapefile, SimpleFeatureCollection collection) {
        try {
            /*
             * Get an output file name and create the new shapefile
             */
            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put("url", shapefile.toURI().toURL());
            params.put("create spatial index", Boolean.TRUE);
            ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
            newDataStore.createSchema(getSimpleFeatureType());
            /*
             * You can comment out this line if you are using the createFeatureType method (at end of
             * class file) rather than DataUtilities.createType
             */
            newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);


            /*
             * Write the features to the shapefile
             */
            Transaction transaction = new DefaultTransaction("create");

            String typeName = newDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

                featureStore.setTransaction(transaction);
                try {
                    featureStore.addFeatures(collection);
                    transaction.commit();

                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();

                } finally {
                    transaction.close();
                }
//                System.exit(0); // success!
            } else {
                System.out.println(typeName + " does not support read/write access");
//                System.exit(1);
            }
        } catch (IOException ex) {
            Logger.getLogger(CSVtoShapeConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private SimpleFeatureCollection createFeatureCollectionFromCSV(File file) {


        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        /*
         * GeometryFactory will be used to create the geometry attribute of each feature (a Point
         * object for the location)
         */
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(getSimpleFeatureType());

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            /* First line of the data file is the header */
            String line = reader.readLine();
            System.out.println("Header: " + line);

            float lastDay = Float.MIN_VALUE;
            int attempt = 0;
            for (line = reader.readLine(); line
                    != null; line = reader.readLine()) {
                if (line.trim().length() > 0) {
                    // skip blank lines
                    String[] tokens = line.split("\\,");

                    double precip = Double.parseDouble(tokens[0]);

                    float day = Float.parseFloat(tokens[1]);
                    if ((attempt > 0) && (day != lastDay)) {
                        System.out.println("writing to file");
                        // write to a shape file and clear the collection
                        writeToShapeFile(new File("C:\\climate data\\NCShapeConverterOutput\\MOZ_CCCMA_PR_" +day + ".shp"), collection);
                        collection.clear();
                        featureBuilder = new SimpleFeatureBuilder(getSimpleFeatureType());
                    }

                    double latitude = Double.parseDouble(tokens[2]);

                    double longitude = Double.parseDouble(tokens[3]);


                    /* Longitude (= x coord) first ! */
                    Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                    featureBuilder.add(point);
                    featureBuilder.add(precip);
                    featureBuilder.add(day);
                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    collection.add(feature);
                    
                    lastDay = day;
                    attempt++;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(CSVtoShapeConverter.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return collection;

    }

    public static void main(String[] args) {
        
            new CSVtoShapeConverter().createFeatureCollectionFromCSV(new File("C:\\climate data\\MOZ_cccma_cgcm3_1.20c3m.run1-run3.pr_BCSD_0.5_2deg_1961-1998\\MOZ_cccma_cgcm3_1.20c3m.run1.pr_BCSD_0.5_2deg_1998.txt"));
       
    }
}

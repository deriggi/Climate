/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import dao.GeoDao;
import database.DBUtils;
import domain.Country;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import service.CountryService;

/**
 *
 * @author wb385924
 */
public class MonthlyCSVtoShapeConverter {

    private SimpleFeatureType TYPE;

    private SimpleFeatureType getSimpleFeatureType() {
//        if (TYPE == null) {
//            try {
//                TYPE = DataUtilities.createType("Location", "location:MultiPolygon:srid=4326" );
//            } catch (SchemaException ex) {
//                Logger.getLogger(MonthlyCSVtoShapeConverter.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return TYPE;

        // =========================================================================
         SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
        builder.srid(4326);
//        // add attributes in order
        builder.add("Country", MultiPolygon.class);
        builder.add("Jan", Double.class); // <- 15 chars width for name field
//        builder.add("Feb", Double.class); // <- 15 chars width for name field
//        builder.add("Mar", Double.class); // <- 15 chars width for name field
//        builder.add("Apr", Double.class); // <- 15 chars width for name field
//        builder.add("May", Double.class); // <- 15 chars width for name field
//        builder.add("Jun", Double.class); // <- 15 chars width for name field
//        builder.add("Jul", Double.class); // <- 15 chars width for name field
//        builder.add("Aug", Double.class); // <- 15 chars width for name field
//        builder.add("Sep", Double.class); // <- 15 chars width for name field
//        builder.add("Oct", Double.class); // <- 15 chars width for name field
//        builder.add("Nov", Double.class); // <- 15 chars width for name field
//        builder.add("Dec", Double.class); // <- 15 chars width for name field

        // build the type

         SimpleFeatureType LOCATION = builder.buildFeatureType();

        return LOCATION;
    }

    private void writeToShapeFile(File shapefile, SimpleFeatureCollection collection) {
        try {
            /*
             * Get an output file name and create the new shapefile
             */
            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put("url", shapefile.toURI().toURL());
            params.put("create spatial index", Boolean.FALSE);
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
            Logger.getLogger(MonthlyCSVtoShapeConverter.class.getName()).log(Level.SEVERE, null, ex);
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
            int counter = 0;
            for (line = reader.readLine(); line
                    != null; line = reader.readLine()) {
                if (line.trim().length() > 0) {
                    // skip blank lines
                    String[] tokens = line.split("\\,");
                    Geometry g = getGeometry(CountryService.get().getId(tokens[0]));
                    
                    /* Longitude (= x coord) first ! */
                    
                    featureBuilder.add(g);
                    featureBuilder.add(Double.parseDouble(tokens[1]));
//                    for(int i = 1; i < 13; i++){
//                        featureBuilder.add(Double.parseDouble(tokens[i]));
//                    }
                    
                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    collection.add(feature);
                    String csvName = file.getName();
                    csvName = csvName.replaceAll("\\.", "\\_");
                    String outName = csvName.substring(0,csvName.indexOf("_csv"));
                    outName  = tokens[0]+"_" + outName;
                    outName = outName.replaceAll("\\_shp", ".shp");
                    
                    writeToShapeFile(new File("C:\\Users\\wb385924\\monthlycountry\\" +outName), collection);
                    collection.clear();

                }
                if(counter++ == 4){
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(MonthlyCSVtoShapeConverter.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return collection;

    }

    public Geometry getGeometry(int  id) {

        Connection connection = DBUtils.getConnection();
        Geometry g = getGeometry(GeoDao.getGeometryAsText(connection, "boundary", "shape", "area_id", id));

        DBUtils.close(connection);

        return g;
    }

    public Geometry getGeometry(String wkt) {
        Geometry geom = null;
        if (wkt == null) {
            return null;
        }
        try {
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
            WKTReader2 reader = new WKTReader2(geometryFactory);
            geom = reader.read(wkt);

        } catch (ParseException ex) {
            Logger.getLogger(MonthlyCSVtoShapeConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return geom;
    }

    public void runList(String root){
        String[] files = new File(root).list();
       
//        for(String f: files){
            createFeatureCollectionFromCSV(new File(root + files[0]));
           
//        }
        System.exit(0);
    }
    public static void main(String[] args) {

        new MonthlyCSVtoShapeConverter().runList("C:\\Users\\wb385924\\climateoutput\\climate csv\\country\\gcm\\monthly\\loaded\\");

    }
}

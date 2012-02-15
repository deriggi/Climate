/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.graphics;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import javax.xml.parsers.ParserConfigurationException;
import shapefileloader.*;
import database.DBUtils;
import export.util.FileExportHelper;
import java.awt.Color;
import org.geotools.styling.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import sdnis.wb.util.ShapeFileUtils;

/**
 *
 * @author wb385924
 */
public class ShapeFeaturesToSVGParser {

    private static final Logger log = Logger.getLogger(RainDataReader.class.getName());
    private Geometry countryGeom = null;
    private String iso3 = null;

    public ShapeFeaturesToSVGParser(String iso3, Geometry filterGeometry) {
        this.countryGeom = filterGeometry;
        this.iso3 = iso3;
    }

    public void readShapeFile(String path, List<String> propertyNames, String regexPattern) {
        SimpleFeatureIterator fi = null;
        Connection con = null;
        try {
            con = DBUtils.getConnection();
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection fc = featureSource.getFeatures();
            log.info("have features");
            ShapeFileUtils shapeUtil = new ShapeFileUtils( regexPattern, propertyNames);
            fi = fc.features();
            int count = 0;

            long t0 = new Date().getTime();
            int numContainingPoints = 0;
            MapContext map = new DefaultMapContext();
            map.setTitle("Quickstart");
            SimpleFeatureCollection gridCellCollection = FeatureCollections.newCollection();

            

            ArrayList<Geometry> cells = new ArrayList<Geometry>();
            while (fi.hasNext()) {
                SimpleFeature f = fi.next();
                Geometry gridCell = (Geometry) f.getDefaultGeometry();
                if (countryGeom.intersects(gridCell)) {
                    numContainingPoints++;
//                    gridCellCollection.add(f);
                    cells.add(gridCell);
                }
                count++;
            }
            
            //geom collection
            GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);

            // note the following geometry collection may be invalid (say with overlapping polygons)
            GeometryCollection geometryCollection =
                    (GeometryCollection) factory.buildGeometry(cells);
            Geometry unionCells = geometryCollection.union();
            System.out.println(unionCells.toText());
            
            SimpleFeatureBuilder cellFeatureBuilder = new SimpleFeatureBuilder(createFeatureType());
            cellFeatureBuilder.add(unionCells);
            cellFeatureBuilder.add("cells");
            gridCellCollection.add(cellFeatureBuilder.buildFeature(null));


            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
            featureBuilder.add(countryGeom);
            featureBuilder.add("MOZ");

            SimpleFeatureCollection countryCollection = FeatureCollections.newCollection();
            countryCollection.add(featureBuilder.buildFeature(null));

            map.addLayer(gridCellCollection, createGridCellStyle());
            map.addLayer(countryCollection, createCountryStyle());


            ByteArrayOutputStream boas = new ByteArrayOutputStream();
//            GeneratePDF.generatePDF(map);
            GenerateSVG.exportSVG(map, map.getLayerBounds(), boas, new Dimension(new Double(map.getLayerBounds().getWidth()).intValue() * 100, new Double(map.getLayerBounds().getHeight()).intValue() * 100));

            log.log(Level.FINE, "points within {0} is {1}", new Object[]{iso3, numContainingPoints});
            long t1 = new Date().getTime();
            log.log(Level.FINE, "total features is {0}", count);
            log.log(Level.FINE, "took  {0}", (t1 - t0) / 1000.0f);
            FileExportHelper.writeToFile("grids.svg", new String(boas.toByteArray()));

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ShapeFeaturesToSVGParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {

            fi.close();
            DBUtils.close(con);

        }
    }

   

    private static SimpleFeatureType createFeatureType() {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

        // add attributes in order
        builder.add("Location", MultiPolygon.class);
        builder.length(15).add("Name", String.class); // <- 15 chars width for name field

        // build the type
        final SimpleFeatureType LOCATION = builder.buildFeatureType();

        return LOCATION;
    }
    private StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    private FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);

    private Style createGridCellStyle() {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0),
                filterFactory.literal(0.5));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0.5));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();

        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private Style createCountryStyle() {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.WHITE),
                filterFactory.literal(2),
                filterFactory.literal(0.8));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0.0));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();

        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private String listToCSVLine(List<Double> vals) {
        StringBuilder sb = new StringBuilder();
        String cma = ",";
        for (double d : vals) {
            sb.append(d);
            sb.append(cma);
        }
        return sb.toString();
    }
}

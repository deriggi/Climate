/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.graphics;

import asciipng.GridCell;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import shapefileloader.*;
import java.awt.Color;
import org.geotools.styling.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import sdnis.wb.util.BasicAverager;

/**
 *
 * @author wb385924
 */
public class ShapeFeaturesToPNGParser {

    private static final Logger log = Logger.getLogger(RainDataReader.class.getName());
    private Geometry countryGeom = null;
    private String iso3 = null;
    private String annual = "annual";

    public ShapeFeaturesToPNGParser(String iso3, Geometry filterGeometry) {
        this.countryGeom = filterGeometry;
        this.iso3 = iso3;
        createColors();
    }

    public void readShapeFile(String path, List<String> propertyNames, String imageOutput, int numClasses) {
        SimpleFeatureIterator fi = null;
        try {

            log.info("top fo the try ");

            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));
            log.info("have store ");
            SimpleFeatureSource featureSource = store.getFeatureSource();
            log.info("have source ");
            SimpleFeatureCollection fc = featureSource.getFeatures();
            log.info("have collection ");

            log.info("have feature cells to parse ");
            int count = 0;

            long t0 = new Date().getTime();
            int numContainingPoints = 0;
            MapContext map = new DefaultMapContext();


            fi = fc.features();

//            ArrayList<Geometry> cells = new ArrayList<Geometry>();
            ArrayList<SimpleFeature> cellFeatures = new ArrayList<SimpleFeature>();
            BasicAverager ba = new BasicAverager();
            log.info("about to parse features ");
            while (fi.hasNext()) {
                SimpleFeature f = fi.next();

                Geometry gridCell = (Geometry) f.getDefaultGeometry();
                if (countryGeom != null && countryGeom.intersects(gridCell)) {
                    numContainingPoints++;
                    Object obj = f.getAttribute(annual);
                    if (obj != null) {
                        String val = obj.toString();
                        if (val != null && val.length() > 0) {
                            double cellval = Double.parseDouble(val);
                            ba.update(cellval);
                            cellFeatures.add(f);
                        }
                    }
//                    cells.add(gridCell);
                }
                count++;
            }
            if (countryGeom != null) {
                HashMap<Integer, SimpleFeatureCollection> classes = getClassesOfFeatures(cellFeatures, ba);

                SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
                featureBuilder.add(countryGeom);

                SimpleFeatureCollection countryCollection = FeatureCollections.newCollection();
                countryCollection.add(featureBuilder.buildFeature(null));

                Set<Integer> keys = classes.keySet();
                System.out.println("number of cells layers " + keys.size());
                for (Integer i : keys) {
                    map.addLayer(classes.get(i), createGridCellStyle(var.precip, i));
                    System.out.println("adding cell layer");
                }

                map.addLayer(countryCollection, createCountryStyle());


                GeneratePNG.saveImage(map, imageOutput, 400);//map, map.getLayerBounds(), boas, new Dimension(new Double(map.getLayerBounds().getWidth()).intValue() * 100, new Double(map.getLayerBounds().getHeight()).intValue() * 100));

                log.log(Level.FINE, "points within {0} is {1}", new Object[]{iso3, numContainingPoints});
                long t1 = new Date().getTime();
                log.log(Level.FINE, "total features is {0}", count);
                log.log(Level.FINE, "took  {0}", (t1 - t0) / 1000.0f);
//            FileExportHelper.writeToFile("grids.svg", new String(boas.toByteArray()));
            }

        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {
            if (fi != null) {
                fi.close();
            }


        }
    }

    private HashMap<Integer, SimpleFeatureCollection> getClassesOfFeatures(ArrayList<SimpleFeature> features, BasicAverager ba) {
        double[][] bounds = ClassifierHelper.getEqualIntervalBounds(ba.getMin(), ba.getMax(), 10);
        HashMap<Integer, SimpleFeatureCollection> classes = new HashMap<Integer, SimpleFeatureCollection>();
        for (SimpleFeature f : features) {
            Object obj = f.getAttribute(annual);
            if (obj != null) {
                String val = obj.toString();
                if (val != null && val.length() > 0) {
                    double cellval = Double.parseDouble(val);
                    int classs = ClassifierHelper.getClass(cellval, bounds)[0][0];
                    if (classs != -1) {
                        if (!classes.containsKey(classs)) {
                            classes.put(classs, FeatureCollections.newCollection());
                        }
                        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
                        featureBuilder.add(countryGeom.intersection((Geometry)f.getDefaultGeometry()));
                        classes.get(classs).add(featureBuilder.buildFeature(null));
                        classes.get(classs).add(f);
                    }
                }
            }
        }
        return classes;
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

    private Style createGridCellStyle(var var, int classs) {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0),
                filterFactory.literal(0));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(var.colorRamp.get(classs)),
                filterFactory.literal(1));

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
    private ArrayList<Color> greenSequence = new ArrayList<Color>();
    private ArrayList<Color> blueToRed = new ArrayList<Color>();

    private enum var {

        precip, temp;
        private ArrayList<Color> colorRamp = null;

        public ArrayList<Color> getColorRamp() {
            return colorRamp;
        }

        private void setRamp(ArrayList<Color> colors) {
            this.colorRamp = colors;
        }
    }

    private void createColors() {
        greenSequence.add(new Color(0xFFFF80));
        greenSequence.add(new Color(0xE8FC72));
        greenSequence.add(new Color(0xD0FA66));
        greenSequence.add(new Color(0xB6F558));
        greenSequence.add(new Color(0xA1F24B));
        greenSequence.add(new Color(0x87ED3E));
        greenSequence.add(new Color(0x71EB2F));
        greenSequence.add(new Color(0x55E620));
        greenSequence.add(new Color(0x38E009));
        greenSequence.add(new Color(0x3BD62D));
        var.precip.setRamp(greenSequence);

        blueToRed.add(new Color(33, 102, 172));
        blueToRed.add(new Color(67, 147, 195));
        blueToRed.add(new Color(146, 197, 222));
        blueToRed.add(new Color(209, 229, 240));
        blueToRed.add(new Color(247, 247, 247));
        blueToRed.add(new Color(253, 219, 199));
        blueToRed.add(new Color(244, 165, 130));
        blueToRed.add(new Color(214, 96, 77));
        blueToRed.add(new Color(178, 24, 43));
        var.temp.setRamp(blueToRed);
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

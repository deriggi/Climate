/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciipng;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import export.util.FileExportHelper;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import sdnis.wb.util.BasicAverager;
import shapefileloader.graphics.ClassifierHelper;
import shapefileloader.graphics.ColorRampHelper;
import shapefileloader.graphics.GeneratePNG;

/**
 *
 * @author wb385924
 */
public class CellMapMaker {

    private StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    private FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
    private ArrayList<Color> greenSequence = new ArrayList<Color>();
    private ArrayList<Color> blueToRed = new ArrayList<Color>();
    private static final String CELL_DATA = "CELL_DATA";

    public enum var {

        precip, temp;
        private ArrayList<Color> colorRamp = null;

        public ArrayList<Color> getColorRamp() {
            return colorRamp;
        }

        private void setRamp(ArrayList<Color> colors) {
            this.colorRamp = colors;
        }
    }

    public CellMapMaker() {
        greenSequence.add(new Color(247, 252, 253));
        greenSequence.add(new Color(229, 245, 249));
        greenSequence.add(new Color(204, 236, 230));
        greenSequence.add(new Color(153, 216, 201));
        greenSequence.add(new Color(102, 194, 164));
        greenSequence.add(new Color(65, 174, 118));
        greenSequence.add(new Color(35, 139, 69));
        greenSequence.add(new Color(0, 109, 44));
        greenSequence.add(new Color(0, 68, 27));
        CellMapMaker.var.precip.setRamp(greenSequence);

        blueToRed.add(new Color(33, 102, 172));
        blueToRed.add(new Color(67, 147, 195));
        blueToRed.add(new Color(146, 197, 222));
        blueToRed.add(new Color(209, 229, 240));
        blueToRed.add(new Color(247, 247, 247));
        blueToRed.add(new Color(253, 219, 199));
        blueToRed.add(new Color(244, 165, 130));
        blueToRed.add(new Color(214, 96, 77));
        blueToRed.add(new Color(178, 24, 43));
        CellMapMaker.var.temp.setRamp(blueToRed);

//        d
    }

    public void draw(Collection<GridCell> gridCells, String fileTitle) {
        SimpleFeatureCollection cellCollection = FeatureCollections.newCollection();


        for (GridCell cell : gridCells) {
            SimpleFeatureBuilder cellFeatureBuilder = new SimpleFeatureBuilder(createFeatureType());
            cellFeatureBuilder.add(cell.getPolygon());
            cellCollection.add(cellFeatureBuilder.buildFeature(null));
        }
        MapContext map = new DefaultMapContext();
        map.setTitle("Quickstart");
        map.addLayer(cellCollection, getGridCellStyle(0));
//        map.addLayer(cellCollection, getGridCellStyle(0));

        GeneratePNG.saveImage(map, fileTitle, 400);
        map.dispose();
    }

     private Style createCountryStyle() {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.GRAY),
                filterFactory.literal(1),
                filterFactory.literal(0.4));

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
    
    public void drawToStream(Collection<GridCell> gridCells, OutputStream os, Geometry regionGeometry) {
        try {
            //        SimpleFeatureCollection cellCollection = FeatureCollections.newCollection();
            ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>();
            BasicAverager ba = new BasicAverager();
            for (GridCell cell : gridCells) {
                ba.update(cell.getValue());
                SimpleFeatureBuilder cellFeatureBuilder = new SimpleFeatureBuilder(createFeatureType());
                cellFeatureBuilder.add(cell.getPolygon());
//                cellFeatureBuilder.add(cell.getPolygon().intersection(regionGeometry));
                SimpleFeature feature = cellFeatureBuilder.buildFeature(null);
                feature.setAttribute(CELL_DATA, new Double(cell.getValue()));
                //            cellCollection.add(feature);
                features.add(feature);
            }

            HashMap<Integer, SimpleFeatureCollection> classesOfGridCells = getClassesOfFeatures(features, ba);
            Set<Integer> classKeys = classesOfGridCells.keySet();
            System.out.println("about to add " + classKeys.size() + " layers to the map context ");

            MapContext map = new DefaultMapContext();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
            featureBuilder.add(regionGeometry);

            SimpleFeatureCollection countryCollection = FeatureCollections.newCollection();
            countryCollection.add(featureBuilder.buildFeature(null));
            
            for (Integer i : classKeys) {
//                map.addLayer(classesOfGridCells.get(i), getGridCellStyle(i));
                map.addLayer(classesOfGridCells.get(i), getGridCellStyle(i));
            }
            map.addLayer(countryCollection,createCountryStyle());

            double mapArea = map.getLayerBounds().getArea();
            System.out.println("map area is " + mapArea);

            map.setTitle("Quickstart");

            GeneratePNG.writeImageToStream(map, os, 400);
            double[][] bounds = ClassifierHelper.getEqualIntervalBounds(ba.getMin(), ba.getMax(), 10);
//            FileExportHelper.appendToFile("C:\\Users\\Johnny\\outputMaps\\Columbia\\legend.html", makeLegend(bounds,ColorRampHelper.getPrecipRamp()));

            map.dispose();
        } catch (IOException ex) {
            Logger.getLogger(CellMapMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @TODO maybe make another one that gets a max min
     * @param gridCells
     * @param os
     * @param regionGeometry
     * @param ba 
     */
    public void drawToStream(Collection<GridCell> gridCells, OutputStream os, Geometry regionGeometry, double max, double min) {
        try {
            //        SimpleFeatureCollection cellCollection = FeatureCollections.newCollection();
            ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>();
            BasicAverager ba = new BasicAverager();
            for (GridCell cell : gridCells) {
                SimpleFeatureBuilder cellFeatureBuilder = new SimpleFeatureBuilder(createFeatureType());
                cellFeatureBuilder.add(cell.getPolygon());
//                cellFeatureBuilder.add(cell.getPolygon().intersection(regionGeometry));
                SimpleFeature feature = cellFeatureBuilder.buildFeature(null);
                feature.setAttribute(CELL_DATA, new Double(cell.getValue()));
                //            cellCollection.add(feature);
                features.add(feature);
                ba.update(cell.getValue());
            }
            System.out.println(ba.getMax() + " " +ba.getMin());

            HashMap<Integer, SimpleFeatureCollection> classesOfGridCells = getClassesOfFeatures(features, max, min);
            Set<Integer> classKeys = classesOfGridCells.keySet();
            System.out.println("about to add " + classKeys.size() + " layers to the map context ");

            MapContext map = new DefaultMapContext();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
            featureBuilder.add(regionGeometry);

            SimpleFeatureCollection countryCollection = FeatureCollections.newCollection();
            countryCollection.add(featureBuilder.buildFeature(null));
            
            for (Integer i : classKeys) {
//                map.addLayer(classesOfGridCells.get(i), getGridCellStyle(i));
                map.addLayer(classesOfGridCells.get(i), getGridCellStyle(i));
            }
            map.addLayer(countryCollection,createCountryStyle());

            double mapArea = map.getLayerBounds().getArea();
            System.out.println("map area is " + mapArea);

            map.setTitle("Quickstart");

            GeneratePNG.writeImageToStream(map, os, 400);
            double[][] bounds = ClassifierHelper.getEqualIntervalBounds(min, max, 10);
//            FileExportHelper.appendToFile("C:\\Users\\Johnny\\outputMaps\\Columbia\\legend.html", makeLegend(bounds,ColorRampHelper.getPrecipRamp()));

            map.dispose();
        } catch (IOException ex) {
            Logger.getLogger(CellMapMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String makeLegend(double[][] bounds, ArrayList<Color> ramp){
        //iterate through bounds, get color at each index, write to string
        int i = 0;
        StringBuilder sb = new StringBuilder();
        
        for(double[] bound : bounds){
            sb.append("<td style = 'background-color:");
            sb.append(ramp.get(i));
            sb.append("'>");
            sb.append(bound[0]);
            sb.append("</td>");
            sb.append("<td style = 'background-color:");
            sb.append(ramp.get(i));
            sb.append("'>");
            sb.append(bound[1]);
            sb.append("</td>");
            i++;
        }
        return sb.toString();
    }

    private HashMap<Integer, SimpleFeatureCollection> getClassesOfFeatures(ArrayList<SimpleFeature> features, BasicAverager ba) {
        return getClassesOfFeatures(features, ba.getMax(), ba.getMin());
    }
    
    private HashMap<Integer, SimpleFeatureCollection> getClassesOfFeatures(ArrayList<SimpleFeature> features, double max, double min) {
        double[][] bounds = ClassifierHelper.getEqualIntervalBounds(min, max, 10);
        HashMap<Integer, SimpleFeatureCollection> classes = new HashMap<Integer, SimpleFeatureCollection>();
        for (SimpleFeature f : features) {
            Object obj = f.getAttribute(CELL_DATA);
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
                        featureBuilder.add(f.getDefaultGeometry());
                        classes.get(classs).add(featureBuilder.buildFeature(null));
//                        classes.get(classs).add(f);
                    }
                }
            }
        }
        return classes;
    }

    private Style getGridCellStyle(int classIndex) {
        Color thecolor = ColorRampHelper.getTemperatureRamp().get(classIndex);
        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(thecolor),
                filterFactory.literal(0),
                filterFactory.literal(1));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(thecolor),
                filterFactory.literal(1));

        // create a partially opaque outline stroke
//        Stroke stroke = styleFactory.createStroke(
//                filterFactory.literal(colors[classIndex]),
//                filterFactory.literal(0));
//
//        // create a partial opaque fill
//        Fill fill = styleFactory.createFill(
//                filterFactory.literal(colors[classIndex]),
//                filterFactory.literal(1));


        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();

        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private static SimpleFeatureType createFeatureType() {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

        // add attributes in order
        builder.add("Location", MultiPolygon.class);
        builder.length(15).add(CELL_DATA, Double.class); // <- 15 chars width for name field

        // build the type
        final SimpleFeatureType LOCATION = builder.buildFeatureType();

        return LOCATION;
    }
}

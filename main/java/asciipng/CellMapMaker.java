/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciipng;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import export.util.FileExportHelper;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.FactoryFinder;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
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
            map.addLayer(countryCollection, createCountryStyle());

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
            System.out.println(ba.getMax() + " " + ba.getMin());

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
            map.addLayer(countryCollection, createCountryStyle());

            double mapArea = map.getLayerBounds().getArea();
            System.out.println("map area is " + mapArea);

            map.setTitle("Quickstart");

            GeneratePNG.writeImageToStream(map, os, 400);
//            double[][] bounds = ClassifierHelper.getEqualIntervalBounds(min, max, 10);
//            FileExportHelper.appendToFile("C:\\Users\\Johnny\\outputMaps\\Columbia\\legend.html", makeLegend(bounds,ColorRampHelper.getPrecipRamp()));

            map.dispose();
        } catch (IOException ex) {
            Logger.getLogger(CellMapMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static Logger log = Logger.getLogger(CellMapMaker.class.getName());
//    private static final String pattern = "\\(\\-?\\d+(\\.\\d\\s{1,})+\\s\\-?\\d+(\\.\\d\\s{1,})+ \\,\\-?\\d+(\\.\\d\\s{1,})+\\s\\-?\\d+(\\.\\d\\s{1,})+\\)";
    private static final String pattern = "\\(((\\s*\\-?\\d+(\\.\\d+)?\\s+\\-?\\d+(\\.\\d+)?)\\s*\\,?\\s*)+\\s*\\)";

    /**
     * Lets just handle polygons or multiploygons here, turn them into geojson strings
     * @param wkt a multipolygon probably
     */
    private String extractGeoJsonFromWkt(String wkt) {
        //match ( number number,number number...)
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(wkt);

        StringBuilder sb = new StringBuilder();
        String start = " 'geometry': { 'type': 'MultiPolygon',coordinates: [[";
        sb.append(start);

        while (matcher.find()) {

            // a list of points
            String whatWasFound = matcher.group().replaceAll("[\\(\\)]", " ").trim();

            // add the polygon
            sb.append(convertListOfPointsToGeojsonPolygonPart(whatWasFound));
            sb.append(",");
        }
        
        // lamely delete the last comma
        sb.delete(sb.length() - 1, sb.length());
        sb.append("]]}");
        
        return sb.toString();

    }

    /**
     *          input... 
     * 
     *          -74 7.0000000000000995, -74 7.5000000000000995, -74 8.0000000000001, -73.5 8.0000000000001,
     * 
     * 
     *          output...
     * 
     * 
    [
    [-105.00432014465332, 39.74732195489861],
    [-105.00715255737305, 39.74620006835170],
    [-105.00921249389647, 39.74468219277038],....
    ]
    
     */
    private String convertListOfPointsToGeojsonPolygonPart(String inputCoordinates) {

        if (inputCoordinates == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        String[] parts = inputCoordinates.split(",");

        int count = 0;

        sb.append("[");
        for (String part : parts) {
            part = part.trim();

            String[] coords = part.split("\\s+");

            // we are good let's build this line
            if (coords.length == 2) {
                sb.append("[");
                sb.append(coords[0]);
                sb.append(",");
                sb.append(coords[1]);
                sb.append("]");
                if (count++ < parts.length - 1) {
                    sb.append(",");
                }
            } else {
                System.out.println("parts: 1: " + coords[0] + " 2:" + coords[1] + " 3:" + coords[2]);
            }
        }
        sb.append("]");
        return sb.toString();

    }

    public static void main(String[] args) {
        String input = "MULTIPOLYGON (((-74 7.0000000000000995, -74 7.5000000000000995, -74 8.0000000000001, -73.5 8.0000000000001, -73.5 7.5000000000000995, -73.5 7.0000000000000995, -74 7.0000000000000995)), ((-73.5 9.0000000000001, -73.5 8.5000000000001, -74 8.5000000000001, -74.5 8.5000000000001, -75 8.5000000000001, -75 9.0000000000001, -75 9.5000000000001, -75 10.0000000000001, -74.5 10.0000000000001, -74 10.0000000000001, -73.5 10.0000000000001, -73.5 9.5000000000001, -73.5 9.0000000000001)), ((-68.5 4.0000000000000995, -68.5 4.5000000000000995, -69 4.5000000000000995, -69 5.0000000000000995, -69.5 5.0000000000000995, -69.5 5.5000000000000995, -69 5.5000000000000995, -69 6.0000000000000995, -69 6.5000000000000995, -68.5 6.5000000000000995, -68 6.5000000000000995, -67.5 6.5000000000000995, -67 6.5000000000000995, -67 6.0000000000000995, -67.5 6.0000000000000995, -67.5 5.5000000000000995, -67.5 5.0000000000000995, -67.5 4.5000000000000995, -67.5 4.0000000000000995, -68 4.0000000000000995, -68.5 4.0000000000000995)))";
        log.info(new CellMapMaker().extractGeoJsonFromWkt(input));
    }
    
    private String getBasicFeatureStart(int clazz){
        String top = "  allClasses[ " + clazz + " ] = { 'type': 'Feature','properties': { 'popupContent': 'This is the Auraria West Campus', ";
        return top;
    }
    
    
    
    

    public String drawGeoJsonClassesToStream(Collection<GridCell> gridCells, Geometry regionGeometry, double max, double min) {
        // get classes of cells
        HashMap<Integer, HashSet<Geometry>> cells = getClassesOfFeatures(gridCells, max, min);

        // take each class and make it a geojson string
        HashMap<Integer, Geometry> classesAsSingleGeometries = unionClassesOfGeometries(cells);

        // javascriptize the strings
        // but before we javascriptize them, print them to screen mean
        Set<Integer> classKeys = classesAsSingleGeometries.keySet();

        
        // iterate through each class
        int indexOfClass = 0;
        StringBuilder sb = new StringBuilder();
        for (Integer i : classKeys) {
            String wkt = classesAsSingleGeometries.get(i).toText();

            // get style properties for this class
            sb.append(getBasicFeatureStart(indexOfClass++));
            sb.append(getCSSGridCellStyle(i));
            sb.append(",");
            
            
            // get a multipolygon geojson object for this class
            sb.append(extractGeoJsonFromWkt(wkt));
            sb.append("};");
        }
        
        return sb.toString();


    }

    public static String makeLegend(double[][] bounds, ArrayList<Color> ramp) {
        //iterate through bounds, get color at each index, write to string
        int i = 0;
        StringBuilder sb = new StringBuilder();

        for (double[] bound : bounds) {
            sb.append("<td style = 'background-color:rgb(");
            sb.append(ramp.get(i).getRed());
            sb.append(",");
            sb.append(ramp.get(i).getGreen());
            sb.append(",");
            sb.append(ramp.get(i).getBlue());
            sb.append(")'>");
            sb.append(bound[0]);
            sb.append("</td>");
            sb.append("<td style = 'background-color:rgb(");
            sb.append(ramp.get(i).getRed());
            sb.append(",");
            sb.append(ramp.get(i).getGreen());
            sb.append(",");
            sb.append(ramp.get(i).getBlue());
            sb.append(")'>");
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

    private HashMap<Integer, Geometry> unionClassesOfGeometries(HashMap<Integer, HashSet<Geometry>> classes) {
        Set<Integer> classKeys = classes.keySet();
        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);
        HashMap<Integer, Geometry> unionizedCells = new HashMap<Integer, Geometry>();

        for (Integer clazz : classKeys) {
            Geometry unioned = factory.buildGeometry(classes.get(clazz)).union();
            unionizedCells.put(clazz, unioned);
        }
        return unionizedCells;



    }

    private HashMap<Integer, HashSet<Geometry>> getClassesOfFeatures(Collection<GridCell> gridCells, double max, double min) {
        double[][] bounds = ClassifierHelper.getEqualIntervalBounds(min, max, 10);
        HashMap<Integer, HashSet<Geometry>> classes = new HashMap<Integer, HashSet<Geometry>>();
        for (GridCell cell : gridCells) {
            double data = cell.getValue();

            int classs = ClassifierHelper.getClass(data, bounds)[0][0];
            if (classs != -1) {
                if (!classes.containsKey(classs)) {
                    classes.put(classs, new HashSet<Geometry>());
                }
                classes.get(classs).add(cell.getPolygon());
//                        classes.get(classs).add(f);
            }
        }
        return classes;
    }
    
    private String getCSSGridCellStyle(int classIndex){
        String firstPartStyle = " 'style': { weight: 0, color: '#efefef',opacity: 0.5, fillColor:";
        String secondPartStyle = " , fillOpacity: 0.5 }}";
        
        StringBuilder sb=  new StringBuilder();
        sb.append(firstPartStyle);
        
        Color thecolor = ColorRampHelper.getTemperatureRamp().get(classIndex);
        sb.append("'rgb(");
        sb.append(thecolor.getRed());
        sb.append(",");
        sb.append(thecolor.getGreen());
        sb.append(",");
        sb.append(thecolor.getBlue());
        sb.append(")'");
        sb.append(secondPartStyle);
        
        return sb.toString();
        
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

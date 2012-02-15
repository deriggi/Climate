/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciipng;

import com.vividsolutions.jts.geom.MultiPolygon;
import java.awt.Color;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
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
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
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

        GeneratePNG.saveImage(map, fileTitle, 400);
        map.dispose();
    }

    public void drawToStream(Collection<GridCell> gridCells, OutputStream os) {
        SimpleFeatureCollection cellCollection = FeatureCollections.newCollection();


        for (GridCell cell : gridCells) {
            SimpleFeatureBuilder cellFeatureBuilder = new SimpleFeatureBuilder(createFeatureType());
            cellFeatureBuilder.add(cell.getPolygon());
            cellCollection.add(cellFeatureBuilder.buildFeature(null));
        }
        MapContext map = new DefaultMapContext();
        map.setTitle("Quickstart");
        map.addLayer(cellCollection, getGridCellStyle(0));

        GeneratePNG.writeImageToStream(map, os, 400);


        map.dispose();
    }

    private Style getGridCellStyle(int classIndex) {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0),
                filterFactory.literal(0.0));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0.5));

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
        builder.length(15).add("Name", String.class); // <- 15 chars width for name field

        // build the type
        final SimpleFeatureType LOCATION = builder.buildFeatureType();

        return LOCATION;
    }
}

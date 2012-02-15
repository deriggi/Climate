/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.graphics;

import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.styling.Style;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;

/**
 *
 * @author wb385924
 */
public class NotSure {

//    public void dostuff() {
//        FileDataStore fds = null;
//        try {
//
//            fds = FileDataStoreFinder.getDataStore(new File("C:\\Users\\Johnny\\Desktop\\RWA_adm\\RWA_adm0.shp"));
//            SimpleFeatureSource featureSource = fds.getFeatureSource();
//            SimpleFeatureCollection c = featureSource.getFeatures();
//            SimpleFeatureIterator sfi = c.features();
//
//            MapContext map = new DefaultMapContext();
//            map.setTitle("Quickstart");
//
//            while (sfi.hasNext()) {
//                SimpleFeature sf = sfi.next();
//                DouglasPeuckerSimplifier s = new DouglasPeuckerSimplifier((Geometry) sf.getDefaultGeometry());
//                s.setDistanceTolerance(.05);
//                sf.setDefaultGeometry(s.getResultGeometry());
////            log.info(sf.getAttribute(1));
//
////
//            }
//
//
//            map.addLayer(c, createPolygonStyle());
//            ByteArrayOutputStream boas = new ByteArrayOutputStream();
//
////            GenerateSVG.exportSVG(map, map.getLayerBounds(), boas, new Dimension(400, 400));
////
////
//////                go(map,c.getBounds(),boas);
////            log.info(new String(boas.toByteArray()));
//        } finally {
//            fds.dispose();
//        }
//    }
//
//    private StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
//    private FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
//
//     private Style createPolygonStyle() {
//
//        // create a partially opaque outline stroke
//        Stroke stroke = styleFactory.createStroke(
//                filterFactory.literal(Color.BLUE),
//                filterFactory.literal(1),
//                filterFactory.literal(0.5));
//
//        // create a partial opaque fill
//        Fill fill = styleFactory.createFill(
//                filterFactory.literal(Color.CYAN),
//                filterFactory.literal(0.5));
//
//        /*
//         * Setting the geometryPropertyName arg to null signals that we want to
//         * draw the default geomettry of features
//         */
//        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);
//
//        Rule rule = styleFactory.createRule();
//        rule.symbolizers().add(sym);
//        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
//        Style style = styleFactory.createStyle();
//        style.featureTypeStyles().add(fts);
//
//        return style;
//    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ascii.tnc;

import ascii.AsciiDataLoader;
import ascii.NetAsciiLoader;
import asciipng.CellMapMaker;
import asciipng.CollectGeometryAsciiAction;
import asciipng.GridCell;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import dao.GeoDao;
import database.DBUtils;
import domain.DerivativeStats;
import domain.UnionedMapPart;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import shapefileloader.graphics.ClassifierHelper;

/**
 *
 * @author wb385924
 */
public class TNCEnsembleRequestBuilder {

    private final String root = "http://countryannual.s3-website-us-east-1.amazonaws.com/";
    private final String middlePart = "AR4_Global_Extr_50k";
    private final String ASC = ".asc";
    private final String l4 = "14";
    private final String _ = "_";

    private static TNCEnsembleRequestBuilder builder = null;
    private static HashMap<Integer, DerivativeStats.file_name> percentileMap = new HashMap<Integer, DerivativeStats.file_name>();
    private static HashMap<Integer, DerivativeStats.file_name> baselineMap = new HashMap<Integer, DerivativeStats.file_name>();
    private static final Logger log = Logger.getLogger(TNCEnsembleRequestBuilder.class.getName());

    private TNCEnsembleRequestBuilder() {
    }

    public static TNCEnsembleRequestBuilder get() {
        if (builder == null) {
            builder = new TNCEnsembleRequestBuilder();
            initMap();
        }
        return builder;
    }

    public String buildRequestName(String iso, int percentile, DerivativeStats.climatestat stat, DerivativeStats.scenario scenario, DerivativeStats.time_period timePeriod) {
        StringBuilder sb = new StringBuilder();
        sb.append(root);
        sb.append(iso.toUpperCase());
        sb.append(_);
        sb.append(percentileMap.get(percentile).toString());

        sb.append(middlePart);
        sb.append(_);
        sb.append(scenario.toString());
        sb.append(_);
        sb.append(stat.toString());
        sb.append(_);
        sb.append(l4);
        sb.append(_);
        sb.append(timePeriod.getFromYear());
        sb.append(_);
        sb.append(timePeriod.getToYear());
        sb.append(ASC);

        return sb.toString();
    }

    public List<UnionedMapPart> collectGeometry(int numClasses, String asciiFilePath) {
        log.info("trying for " + asciiFilePath);
        Map<Integer, Set<GridCell>> map = new TreeMap<Integer, Set<GridCell>>();
        List<UnionedMapPart> parts = new ArrayList<UnionedMapPart>();

        try {
            long t0 = Calendar.getInstance().getTimeInMillis();
            CollectGeometryAsciiAction caa = new CollectGeometryAsciiAction();
            URL oracle = new URL(asciiFilePath);
            URLConnection yc = oracle.openConnection();
            new AsciiDataLoader(caa).parseAsciiFile(null, yc.getInputStream(), null);
            log.log(Level.INFO, "file path is {0}", asciiFilePath);
            log.log(Level.INFO, " max val is {0}", caa.getBa().getMax());
            log.log(Level.INFO, " min val is {0}", caa.getBa().getMin());
            log.log(Level.INFO, " freq is {0}", caa.getBa().getCount());
            long t1 = Calendar.getInstance().getTimeInMillis();
            log.log(Level.INFO, "processing time :  {0} seconds", (t1 - t0) / 1000.0);
            double max = caa.getBa().getMax();
            double min = caa.getBa().getMin();
            double[][] bounds = ClassifierHelper.getEqualIntervalBounds(min, max, numClasses);
            Set<GridCell> cells = caa.getGridCells();
            for (GridCell cell : cells) {
                int mapClass = ClassifierHelper.getClass(cell.getValue(), bounds)[0][0];

                if (!map.containsKey(mapClass)) {
                    map.put(mapClass, new HashSet<GridCell>());
                }
                map.get(mapClass).add(cell);

            }
            // we have map with class and set
            Set<Integer> keyss = map.keySet();


            for (Integer key : keyss) {
                Geometry union = unionGridCells(map.get(key));
                UnionedMapPart mapPart = new UnionedMapPart();
                mapPart.setShape(union);
                String svg = getGeomFromText(union.toText());
                mapPart.setSvg(svg);

                mapPart.setMax(bounds[key][1]);
                mapPart.setMin(bounds[key][0]);
                parts.add(mapPart);
                log.info(mapPart.toString());
            }

            log.log(Level.INFO, " size is {0} ", parts.size());

        } catch (IOException ex) {
            Logger.getLogger(NetAsciiLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return parts;
    }

    private String getGeomFromText(String text) {
        
        Connection c = DBUtils.getConnection();
        String svg = GeoDao.getTextAsSVG(c, text);
        DBUtils.close(c);
        return svg;
    }

    private Geometry unionGridCells(Collection<GridCell> gridCells) {
        ArrayList<Geometry> cells = new ArrayList<Geometry>();
        for (GridCell cell : gridCells) {
            cells.add(cell.getPolygon());
        }

        return combineIntoOneGeometry(cells);

    }

    private static Geometry combineIntoOneGeometry(Collection<Geometry> cells) {
        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);

        // note the following geometry collection may be invalid (say with overlapping polygons)
        GeometryCollection geometryCollection =
                (GeometryCollection) factory.buildGeometry(cells);
        Geometry unionCells = geometryCollection.union();

        return unionCells;
    }

    private static void initMap() {

        percentileMap.put(0, DerivativeStats.map_file_name.map_mean_ensemble_0_);
        percentileMap.put(50, DerivativeStats.map_file_name.map_mean_ensemble_50_);
        percentileMap.put(100, DerivativeStats.map_file_name.map_mean_ensemble_100_);


        baselineMap.put(0, DerivativeStats.map_file_name.map_mean_baseline_ensemble_0_);
        baselineMap.put(50, DerivativeStats.map_file_name.map_mean_baseline_ensemble_50_);
        baselineMap.put(100, DerivativeStats.map_file_name.map_mean_baseline_ensemble_100_);


    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm.monthly;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import export.util.FileExportHelper;
import shapefileloader.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import sdnis.wb.util.ShapeFileUtils;

/**
 *
 * @author wb385924
 */
public class CachedMonthlyShapeFileParser {

    private static final Logger log = Logger.getLogger(RainDataReader.class.getName());
    private final String rwanda = "MULTIPOLYGON(((30.4822196506181 -1.06333359487734,30.8309365033605 -1.65489973926117,30.8307610114"
            + "535 -2.35430574847433,30.3931216877789 -2.30156259754803,30.1583328784793 -2.42833358583187,29.9522"
            + "208901386 -2.30944494768346,29.906456050798 -2.69368086953608,29.7391643860459 -2.8029167487225,29."
            + "3804835710402 -2.82548620489173,29.1405523384091 -2.58916707800032,29.0244407672777 -2.744722435442"
            + "87,28.9022216553365 -2.66000032897951,28.8674277884897 -2.39868073415671,29.1183319298341 -2.241111"
            + "53227345,29.12992865331 -1.85104178065957,29.362012915819 -1.51090290896167,29.8352776246639 -1.319"
            + "72234236882,29.9797210176193 -1.46222229887023,30.4822196506181 -1.06333359487734)))";

    public static void main(String[] args) {
//        new OldMonthlyShapeFileParser().readShapeFile("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1920-1939.shp", null, null, null);
    }
    private Geometry geom = null;
    private String regionIdentifier = null;
    private String outputDirectory;

    public CachedMonthlyShapeFileParser(String identifier, Geometry filterGeometry, String outputDirectory) {
        this.geom = filterGeometry;
        this.regionIdentifier = identifier;
        this.outputDirectory = outputDirectory;

    }

    private class FeatureGeom implements Comparable<FeatureGeom>{
        private SimpleFeature sf = null;

        public MultiPolygon getG() {
            return g;
        }

        public void setG(MultiPolygon g) {
            this.g = g;
        }

        public SimpleFeature getSf() {
            return sf;
        }

        public void setSf(SimpleFeature sf) {
            this.sf = sf;
        }
        private MultiPolygon g = null;
        public FeatureGeom(SimpleFeature sf, MultiPolygon g) {
            this.g = g;
            this.sf = sf;
        }

        public int compareTo(FeatureGeom o) {
           return this.getG().compareTo(o.g);
        }
    }

    private List<FeatureGeom> loadCache(String path) {
        SimpleFeatureIterator fi = null;
        List<FeatureGeom> cache = new ArrayList<FeatureGeom>();
        try {
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection fc = featureSource.getFeatures();
            
            fi = fc.features();
            int count = 0;

            long t0 = new Date().getTime();
            int numContainingPoints = 0;
            while (fi.hasNext()) {
                SimpleFeature sf = fi.next();
                cache.add(new FeatureGeom(sf,(MultiPolygon)sf.getDefaultGeometry()));
            }
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {

            fi.close();
        }
        return cache;
    }

    private void createRegionSpecificCache(HashMap<String,Geometry> regionGeometryCache, List<FeatureGeom> gridCells){
        HashMap<String,List<FeatureGeom>> areaGeoms = new HashMap<String,List<FeatureGeom>>();
        Collection<Geometry> areas = regionGeometryCache.values();
//        Iterator<Geometry> geoms = areas.iterator();

    }

    public void readShapeFile(String path, List<String> propertyNames, String regexPattern, FeatureHandler fh) {
        List<FeatureGeom> features = loadCache(path);

        ShapeFileUtils utils = new ShapeFileUtils(regexPattern, propertyNames);


        long t0 = new Date().getTime();
        int numContainingPoints = 0;
        
        for (FeatureGeom f : features) {
            if (geom.intersects(f.getG())) {
                numContainingPoints++;
                fh.handleFeature(utils.extractFeatureProperties(f.getSf()));

            }
        }
        MonthlyFeatureHandler mfh = (MonthlyFeatureHandler)fh;
        log.info(mfh.getMonthAveragesAsList().size() + " is the size of the month avg list ");
        log.log(Level.INFO, "contaiing ponits {0} for {1}", new Object[]{numContainingPoints, regionIdentifier});
        log.log(Level.INFO, "points within {0} is {1}", new Object[]{regionIdentifier, numContainingPoints});
        List<Double> vals = ((MonthlyFeatureHandler) fh).getMonthAveragesAsList();

//            SimpleGcmCache.get().addToCache(iso3, path.substring(path.lastIndexOf("\\")+1), vals);
        log.log(Level.INFO, "about to write to {0}{1}.csv", new Object[]{outputDirectory, path.substring(path.lastIndexOf("\\") + 1)});

        String scenarioAndTypeIdentifier = getScenarioAndTypeFromPath(path);
        String csvLine = listToCSVLine(vals);
        if (csvLine != null) {

            FileExportHelper.appendToFile(outputDirectory + scenarioAndTypeIdentifier + path.substring(path.lastIndexOf("\\") + 1) + ".csv", regionIdentifier + "," + csvLine);

        }
    }

    private String getScenarioAndTypeFromPath(String path) {
        StringBuilder sb = new StringBuilder();
        String anom = ".anom";
        String clim = ".clim";
        String a2 = ".sresa2";
        String b1 = ".sresb1";
        String dot = ".";
        String _ = "_";


        String fileName = path.substring(path.lastIndexOf("\\") + 1);
        if (fileName.contains(anom) || fileName.contains(clim) || fileName.contains(a2) || fileName.contains(b1)) {
            return sb.toString();
        }

        sb.append(_);

        if (path.contains(anom)) {
            sb.append(anom);
        } else if (path.contains(clim)) {
            sb.append(clim);
        }

        if (path.contains(a2)) {
            sb.append(a2);
            sb.append(dot);
        } else if (path.contains(b1)) {
            sb.append(b1);
            sb.append(dot);
        }

        String returnString = sb.toString();
        if (returnString.length() == 1) {
            return "";
        }
        return sb.toString();
    }

    private String listToCSVLine(List<Double> vals) {
        StringBuilder sb = new StringBuilder();
        String cma = ",";
        if (vals == null) {
            return null;
        }
        for (Double d : vals) {
            sb.append(d);
            sb.append(cma);
        }
        return sb.toString();
    }
}

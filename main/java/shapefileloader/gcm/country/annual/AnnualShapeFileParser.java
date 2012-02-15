/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm.country.annual;

import com.vividsolutions.jts.geom.Geometry;
import shapefileloader.*;
import database.DBUtils;
import export.util.FileExportHelper;
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
import org.opengis.feature.simple.SimpleFeature;
import sdnis.wb.util.BasicAverager;
import sdnis.wb.util.ShapeFileUtils;

/**
 *
 * @author wb385924
 */
public class AnnualShapeFileParser {

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
    private Geometry regionBoundary = null;
    private String iso3 = null;
    private String outputDirectory;

    public AnnualShapeFileParser(String iso3, Geometry filterGeometry, String outputDirectory) {
        this.regionBoundary = filterGeometry;
        this.iso3 = iso3;
        this.outputDirectory = outputDirectory;

    }

    public void readShapeFile(String path, List<String> propertyNames, String regexPattern, FeatureHandler fh) {
        SimpleFeatureIterator fi = null;
        Connection con = null;
        try {
            con = DBUtils.getConnection();
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection fc = featureSource.getFeatures();
            ShapeFileUtils shapeUtil = new ShapeFileUtils( regexPattern, propertyNames);
            fi = fc.features();
            int count = 0;

            long t0 = new Date().getTime();
            int numContainingPoints = 0;
            while (fi.hasNext()) {
                SimpleFeature f = fi.next();
                if (regionBoundary.intersects((Geometry) f.getDefaultGeometry())) {
                    numContainingPoints++;
                    fh.handleFeature( shapeUtil.extractFeatureProperties(f));
                }
                count++;
            }
            log.log(Level.FINE, "points within {0} is {1}", new Object[]{iso3, numContainingPoints});
//            List<Double> vals = ((OldAnnualFeaturesWithinPolygonFeatureHandle)fh);
            BasicAverager ba = ((AnnualFeatureHandler) fh).getAverager();
//            SimpleGcmCache.get().addToCache(iso3, path.substring(path.lastIndexOf("\\")+1), vals);
            log.log(Level.FINE, "about to write to {0}{1}.csv", new Object[]{outputDirectory, path.substring(path.lastIndexOf("\\") + 1)});

            String scenarioAndTypeIdentifier = getScenarioAndTypeFromPath(path);

            if (ba.getAvg() != null) {
                FileExportHelper.appendToFile(outputDirectory + scenarioAndTypeIdentifier +  path.substring(path.lastIndexOf("\\") + 1) + ".csv", iso3 + "," + ba.getAvg());
            }else{
                log.warning("extracted null data for " + iso3);
            }
            long t1 = new Date().getTime();

            log.log(Level.FINE, "total features is {0}", count);
            log.log(Level.FINE, "took  {0}", (t1 - t0) / 1000.0f);

        } catch (FileNotFoundException ex) {
            log.severe(ex.getMessage());
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        } finally {

            fi.close();
            DBUtils.close(con);

        }
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

     private String getScenarioAndTypeFromPath(String path) {
        StringBuilder sb = new StringBuilder();
        String anom = ".anom";
        String clim = ".clim";
        String a2 = ".sresa2";
        String b1 = ".sresb1";
        String dot = ".";
        String _ = "_";

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

        return sb.toString();
    }
}

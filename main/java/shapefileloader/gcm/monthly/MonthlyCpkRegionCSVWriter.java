/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm.monthly;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import dao.GeoDao;
import dao.ckpregion.CkpRegionDao;
import database.DBUtils;
import domain.CkpRegion;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;

/**
 *
 * @author wb385924
 */
public class MonthlyCpkRegionCSVWriter implements Runnable {

    private static final Logger log = Logger.getLogger(MonthlyCpkRegionCSVWriter.class.getName());
    private List<String> names = null;
    private String rootPath = null;
    private String outputDirectory = null;

    public MonthlyCpkRegionCSVWriter(String path, String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.rootPath = path;
        names = new ArrayList<String>();
        names.add("Jan");
        names.add("Feb");
        names.add("Mar");
        names.add("Apr");
        names.add("May");
        names.add("Jun");
        names.add("July");
        names.add("Aug");
        names.add("Sep");
        names.add("Oct");
        names.add("Nov");
        names.add("Dec");
    }

    public static void main(String[] args) {
        new MonthlyCpkRegionCSVWriter(args[0], args[1]).run();
    }

    public void run() {
        File rootDir = new File(rootPath);
        rootDir.mkdir();

        String[] files = rootDir.list();
        CkpRegionDao regionDao = CkpRegionDao.get();
        Set<CkpRegion> regions = regionDao.getCkpRegions();
        
        for (String file : files) {
            if (file.endsWith(".shp") || file.endsWith(".SHP")) {
                // loop through each country
                for (CkpRegion region : regions) {
                    Geometry geom = getGeometry(region);
                    if (geom == null) {
                        log.log(Level.WARNING, "null geometry for country {0} {1}", new Object[]{region.getCkpRegionCode(), region.getCkpRegionId()});
                        continue;
                    }
                    MonthlyShapeFileParser parser = new MonthlyShapeFileParser(region.getCkpRegionCode(), geom, outputDirectory);
                    MonthlyFeatureHandler fh = new MonthlyFeatureHandler();
                    parser.readShapeFile(rootPath + file, names, null, fh);
                }
            }
        }
    }

    public Geometry getGeometry(CkpRegion c) {

        Connection connection = DBUtils.getConnection();
        Geometry g = getGeometry(GeoDao.getGeometryAsText(connection, "ckp_region", "geom", "id", c.getCkpRegionId()));

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
            Logger.getLogger(MonthlyCpkRegionCSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return geom;
    }
}

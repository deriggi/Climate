/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm.monthly;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import dao.GeoDao;
import dao.basin.BasinDao;
import database.DBUtils;
import domain.Basin;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;

/**
 *
 * @author wb385924
 */
public class MonthlyBasinCSVWriter implements Runnable {

    private static final Logger log = Logger.getLogger(MonthlyBasinCSVWriter.class.getName());
    private List<String> names = null;
    private String rootPath = null;
    private String outputDirectory = null;

    
    public MonthlyBasinCSVWriter(String path, String outputDirectory) {
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
        new MonthlyBasinCSVWriter(args[0], args[1]).run();
    }

    public void run() {
        File rootDir = new File(rootPath);
        rootDir.mkdir();

        String[] files = rootDir.list();
        BasinDao basinDao = BasinDao.get();
        List<Basin> basins = basinDao.getBasins();
        for (String file : files) {
            if (file.endsWith(".shp") || file.endsWith(".SHP")) {
                // loop through each country
                for (Basin basin : basins) {
                    Geometry geom = getGeometry(basin);
                    if (geom == null) {
                        log.log(Level.WARNING, "null geometry for country {0} {1}", new Object[]{basin.getCode(), basin.getId()});
                        continue;
                    }
                    MonthlyShapeFileParser parser = new MonthlyShapeFileParser(Integer.toString(basin.getCode()), geom, outputDirectory);
                    MonthlyFeatureHandler fh = new MonthlyFeatureHandler();
                    parser.readShapeFile(rootPath + file, names, null, fh);
                }
            }
        }
    }

    public Geometry getGeometry(Basin c) {

        Connection connection = DBUtils.getConnection();
        Geometry g = getGeometry(GeoDao.getGeometryAsText(connection, "basin", "geom", "id", c.getId()));

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
            Logger.getLogger(MonthlyBasinCSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return geom;
    }
}

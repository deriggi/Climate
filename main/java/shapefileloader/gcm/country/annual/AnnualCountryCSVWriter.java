/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm.country.annual;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import dao.GeoDao;
import dao.country.CountryDao;
import database.DBUtils;
import domain.Country;
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
public class AnnualCountryCSVWriter implements Runnable {

    private static final Logger log = Logger.getLogger(AnnualCountryCSVWriter.class.getName());
    private List<String> names = null;
    private String rootPath = null;
    private String outputDirectory = null;//"C:\\Users\\wb385924\\statsAnnualCsv\\";

    public AnnualCountryCSVWriter(String path, String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.rootPath = path;
        names = new ArrayList<String>();
        names.add("Annual");
        
    }
    public static void main(String[] args) {
        new AnnualCountryCSVWriter(args[0], args[1]).run();
    }

    public void run() {
        String[] files = new File(rootPath).list();
        CountryDao countryDao = CountryDao.get();
        List<Country> countries = countryDao.getCountries();
        for (String file : files) {
            if (file.endsWith(".shp")) {
                for (Country c : countries) {
                    Geometry geom = getGeometry(c);
                    if (geom == null) {
                        log.log(Level.WARNING, "null geometry for country {0} {1}", new Object[]{c.getName(), c.getIso3()});
                        continue;
                    }
                    AnnualShapeFileParser parser = new AnnualShapeFileParser(c.getIso3(), geom, outputDirectory);
                    AnnualFeatureHandler fh = new AnnualFeatureHandler();
                    parser.readShapeFile(rootPath + file, names, null, fh);
                }
            }

        }
    }

//    public void filterFile(String path, String wkt) {
//    }

    public List<String> getCountryWKT() {
        CountryDao countryDao = CountryDao.get();
        List<Country> countries = countryDao.getCountries();
        Connection connection = DBUtils.getConnection();
        List<String> wkts = new ArrayList<String>();

        for (Country c : countries) {
            wkts.add(GeoDao.getGeometryAsText(connection, "boundary", "shape", "area_id", c.getId()));
        }
        DBUtils.close(connection);
        return wkts;

    }

    public Geometry getGeometry(Country c) {

        Connection connection = DBUtils.getConnection();
        Geometry g = getGeometry(GeoDao.getGeometryAsText(connection, "boundary", "shape", "area_id", c.getId()));

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
            Logger.getLogger(AnnualCountryCSVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return geom;
    }
}

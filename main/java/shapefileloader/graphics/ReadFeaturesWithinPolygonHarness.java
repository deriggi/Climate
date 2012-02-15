/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.graphics;

import shapefileloader.oldclimate.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;

/**
 *
 * @author wb385924
 */
public class ReadFeaturesWithinPolygonHarness implements Runnable {

    private static final Logger log = Logger.getLogger(ReadFeaturesWithinPolygonHarness.class.getName());
    private List<String> names = null;
    private String rootPath = null;

    public ReadFeaturesWithinPolygonHarness(String path) {
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



        names.add("annual");


    }

    public static void main(String[] args) {
//        for (String path : args) {
//            new Thread(new ReadFeaturesWithinPolygonHarness(path)).start();
//        }
//        new Thread(new ReadFeaturesWithinPolygonSpeedTest("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\gfdl_cm2_1\\")).start();
//        new Thread(new ReadFeaturesWithinPolygonSpeedTest("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\ingv_echam4\\")).start();

        new ReadFeaturesWithinPolygonHarness("C:/Users/wb385924/Dropbox/GCM_long_anom_annual.shp/gfdl_cm2_1/").run();
    }

    public void run() {
//        try {
//        String file = "C:\\testgrids\\pcmdi_long_anom_cccma_cgcm3_1_pr_sresa2_2020_2039.shp";

        //String x = "select count(*) from o_cell where st_equals(o_cell_geom,ST_GeomFromEWKT('SRID=4326;POLYGON((-173 -81, -173 -79, -171 -79, -171 -81, -173 -81))'))";
        //        String rootFile = "C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\";
        String[] files = new File(rootPath).list();
        CountryDao countryDao = CountryDao.get();
        List<Country> countries = countryDao.getCountries();
        HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countries);
        for (String file : files) {
//        String file = "pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1920-1939.shp";
            if (file.endsWith(".shp")) {
                // loop through each country
                for (Country c : countries) {
//            Country c = countryMap.get("MOZ");
                    Geometry geom = getGeometry(c);
                    if (geom == null) {
                        log.log(Level.WARNING, "null geometry for country {0} {1}", new Object[]{c.getName(), c.getIso3()});
//                        continue;
                    }
                    ShapeFeaturesToPNGParser parser = new ShapeFeaturesToPNGParser(c.getIso3(), geom);
                    log.info("about to read stuff");
                    parser.readShapeFile(rootPath+file, names, "C:/Users/wb385924/images/" + c.getIso3() + "_" + file.substring(file.lastIndexOf("\\") + 1, file.lastIndexOf(".")) + ".png", 10);
                }
            }
        }
//            }
//            System.out.println(SimpleGcmCache.get().getValues("MOZ", file));
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//            String line = null;
//            System.out.println("ready to read");
//            while ((line = br.readLine()) != null) {
//                if(line.equals("exit")){
//                    break;
//                }
//
//            }
        //        }
        //        }
//        } catch (IOException ex) {
//            Logger.getLogger(ReadFeaturesWithinPolygonSpeedTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void filterFile(String path, String wkt) {
    }

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
            Logger.getLogger(ReadFeaturesWithinPolygonHarness.class.getName()).log(Level.SEVERE, null, ex);
        }
        return geom;
    }
}

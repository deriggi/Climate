/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip;

import ascii.AsciiDataLoader;
import asciipng.CollectGeometryAsciiAction;
import asciipng.GridCell;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import dao.GeoDao;
import dao.basin.BasinDao;
import database.DBUtils;
import domain.Basin;
import export.util.FileExportHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;
import sdnis.wb.util.BasicAverager;
import shapefileloader.gcm.country.annual.AnnualCountryCSVWriter;

/**
 *
 * @author wb385924
 */
public class BasinCruCsvWriter {

    private static ArrayList<Integer> badBasins = new ArrayList<Integer>();
    private static final Logger log = Logger.getLogger(BasinCruCsvWriter.class.getName());

    public static void main(String[] args) {
        BasinCruCsvWriter cruWriter = new BasinCruCsvWriter();


        String starterFile = "C:\\Users\\wb385924\\Dropbox\\CRU\\tmp\\tmp\\cru_ts_3_10.1901.2009.tmp_1901_1.asc";
//        String starterFile = args[0];


        String cruDirectory = "C:\\Users\\wb385924\\Dropbox\\CRU\\tmp\\tmp\\";
//        String cruDirectory = args[1];

        String outputDirectory = "C:\\Users\\wb385924\\crubasintemperatureoutput\\";
//        String outputDirectory = args[2];


        badBasins.add(366);
        badBasins.add(304);
        badBasins.add(79);
        badBasins.add(1);
        badBasins.add(92);
        badBasins.add(83);
        badBasins.add(385);
        badBasins.add(94);
        badBasins.add(173);
        badBasins.add(176);
        badBasins.add(162);
        badBasins.add(226);
        badBasins.add(25);
        badBasins.add(90);
        HashMap<String, List<GridCell>> countryCellCache = cruWriter.createCountryCellCache(starterFile, outputDirectory);
        cruWriter.findCells(countryCellCache, cruDirectory, outputDirectory);
    }

    public void findCells(HashMap<String, List<GridCell>> cache, String rootDirectory, String outputDirectory) {


        File rootFile = new File(rootDirectory);
        String[] subFiles = rootFile.list();
        // get countries
        HashMap<String, Geometry> countryGeometries = collectCountryGeoms();

        // iterate through countries picking out cells
        Set<String> isos = countryGeometries.keySet();
        for (String s : subFiles) {

            // get grid cells
            log.info("abound to get cru cells for next file " + s);
            Set<GridCell> cruCells = getGridCells(rootDirectory + s);
            List<GridCell> cellList = new ArrayList<GridCell>(cruCells);
            Collections.sort(cellList);

            for (String iso : isos) {
                
                long t0 = new Date().getTime();
                log.log(Level.INFO, "intersecting with  {0}", iso);
                List<GridCell> countryCells = cache.get(iso);
                BasicAverager ba = new BasicAverager();
                if (countryCells != null) {
                    for (GridCell countryCell : countryCells) {
                        int index = Collections.binarySearch(cellList, countryCell);
                        if (index == -1) {
                            log.warning(" could not find cell from binary search");
                        } else {
                            ba.update(cellList.get(index).getValue());
                        }
                    }
                }
                if (countryCells != null) {
                    FileExportHelper.appendToFile(outputDirectory + s + ".csv", iso + "," + ba.getAvg() + " , " + ba.getMax() + " , " + ba.getMin() + " , " + ba.getCount());
                }
                long t1 = new Date().getTime();
                log.log(Level.INFO, "processing  {0} took  ", new Object[]{(t1 - t0) / 1000.0});
            }

        }

    }

    public HashMap<String, List<GridCell>> createCountryCellCache(String starterFile, String outputDirectory) {
        HashMap<String, List<GridCell>> countryCells = new HashMap<String, List<GridCell>>();

        // get countries
        HashMap<String, Geometry> countryGeometries = collectCountryGeoms();

        // iterate through countries picking out cells
        Set<String> isos = countryGeometries.keySet();

        // get grid cells
        Set<GridCell> cruCells = getGridCells(starterFile);

        for (String iso : isos) {
            
            long t0 = new Date().getTime();
            log.log(Level.INFO, "creating cache for {0}", iso);
            Geometry countryGeometry = countryGeometries.get(iso);
            Iterator<GridCell> cellIterator = cruCells.iterator();
            while (cellIterator.hasNext()) {

                GridCell gc = cellIterator.next();
                if (countryGeometry != null && countryGeometry.intersects(gc.getPolygon())) {
                    if (!countryCells.containsKey(iso)) {
                        countryCells.put(iso, new ArrayList<GridCell>());
                    }
                    countryCells.get(iso).add(gc);
                }
            }
            long t1 = new Date().getTime();
            log.log(Level.INFO, "creating cache    took {0} for {1}", new Object[]{(t1 - t0) / 1000.0, iso});

//                FileExportHelper.appendToFile(outputDirectory + s + ".csv", iso + "," + ba.getAvg());

            if (countryCells.containsKey(iso)) {
                Collections.sort(countryCells.get(iso));
            }
            log.log(Level.INFO, "{0} has {1}", new Object[]{iso, countryCells.get(iso).size()});
        }

//        new CellMapMaker().draw(countryCells.get("MOZ"), "mozcruprecip.png");



        return countryCells;
    }

    private Set<GridCell> getGridCells(String cruPath) {
        CollectGeometryAsciiAction caa = new CollectGeometryAsciiAction();
        try {
            long t0 = Calendar.getInstance().getTimeInMillis();
            new AsciiDataLoader(caa).parseAsciiFile(null, new FileInputStream(new File(cruPath)), null);
            long t1 = Calendar.getInstance().getTimeInMillis();
            log.log(Level.INFO, "collecting cells took :  {0} seconds", (t1 - t0) / 1000.0);
            log.log(Level.INFO, "have {0}", caa.getSize());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return caa.getGridCells();
    }

    private HashMap<String, Geometry> collectCountryGeoms() {
        BasinDao basinDao = BasinDao.get();
        List<Basin> basins = basinDao.getBasins();
        HashMap<String, Geometry> basinMap = new HashMap<String, Geometry>();
        for (Basin b : basins) {

            Geometry geom = getGeometry(b);
            basinMap.put(Integer.toString(b.getCode()), geom);

        }
        return basinMap;
    }

    private Geometry getGeometry(Basin b) {
        log.info("getting basin geometry");
        Connection connection = DBUtils.getConnection();
        Geometry g = getGeometry(GeoDao.getGeometryAsText(connection, "basin", "geom", "id", b.getId()));
        DBUtils.close(connection);
        return g;
    }

    private Geometry getGeometry(String wkt) {
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

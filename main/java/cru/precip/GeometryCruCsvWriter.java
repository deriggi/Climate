/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import ascii.AsciiDataLoader;
import asciipng.CollectGeometryAsciiAction;
import asciipng.GridCell;
import com.vividsolutions.jts.geom.Geometry;
import cru.precip.moneymaker.StatusCache;
import export.util.FileExportHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import sdnis.wb.util.BasicAverager;
import sdnis.wb.util.StatsAverager;
import shapefileloader.ShapeFileParserGeometryExtractor;

/**
 *
 * @author wb385924
 */
public class GeometryCruCsvWriter {

    private static final Logger log = Logger.getLogger(GeometryCruCsvWriter.class.getName());
    private BasicAverager masterAverager = new BasicAverager();

    public static void main(String[] args) {
        GeometryCruCsvWriter cruWriter = new GeometryCruCsvWriter();

        String starterFile = "C:\\Users\\Johnny\\Dropbox\\CRU\\tmp\\tmp\\cru_ts_3_10.1901.2009.tmp_1961_1.asc";

        String cruDirectory = "C:\\Users\\Johnny\\Dropbox\\CRU\\tmp\\tmp\\";

        String outputDirectory = "C:\\Users\\Johnny\\monthlyCRUoutput\\";


        String outputFileName = "TemperatureSDOutputFile.csv";

        String shapeFilePath = "C:\\Users\\Johnny\\boundary data\\columbia\\Municipios_SIGOT2009_region.shp";

        HashMap<String, Geometry> geometryMap = new ShapeFileParserGeometryExtractor().readShapeFile(shapeFilePath, "NOM_MUNICI");

        HashMap<String, List<GridCell>> countryCellCache = cruWriter.createCountryCellCache(geometryMap, starterFile);
        cruWriter.createMonthlyOutput(geometryMap, countryCellCache, cruDirectory, outputDirectory, outputFileName);
    }

    /**
     * Creates a CSV file for each raster with the BasicAverager data for each geometry
     */
    public void createRawOutput(HashMap<String, Geometry> geometryMap, HashMap<String, List<GridCell>> cache,
            String rootDirectory, String outputDirectory, String statusCacheKey) {

        File rootFile = new File(rootDirectory);
        String[] subFiles = rootFile.list();
        // get countries

        
        StatusCache.setFinalRestingPlace(statusCacheKey, outputDirectory);
        
        // iterate through countries picking out cells
        Set<String> locationNames = geometryMap.keySet();
        int subFileCounter = 0;
        for (String s : subFiles) {

            // get grid cells
            log.log(Level.INFO, "abound to get cru cells for next file {0}", s);
            Set<GridCell> cruCells = getGridCells(rootDirectory + s);
            List<GridCell> cellList = new ArrayList<GridCell>(cruCells);
            Collections.sort(cellList);
            new File(outputDirectory).mkdirs();
            FileExportHelper.appendToFile(outputDirectory + s + ".csv", "Municipio" + "," + "Average" + " , " + "Max" + " , " + "Min" + " , " + "Frequency");


            for (String locationName : locationNames) {


                long t0 = new Date().getTime();
                log.log(Level.INFO, "intersecting with  {0}", locationName);
                List<GridCell> countryCells = cache.get(locationName);
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

                if (countryCells != null && countryCells.size() > 0) {
                    locationName = locationName.replaceAll("\\,", " ");
                    FileExportHelper.appendToFile(outputDirectory + s + ".csv", locationName + "," + ba.getAvg() + " , " + ba.getMax() + " , " + ba.getMin() + " , " + ba.getCount());
                    StatusCache.setLastFile(statusCacheKey, s);
                    StatusCache.setPercentComplete(statusCacheKey, ((float) subFileCounter) /  (subFiles.length-1) );

                }

                long t1 = new Date().getTime();
                log.log(Level.INFO, "processing  {0} took  ", new Object[]{(t1 - t0) / 1000.0});
            }

            subFileCounter++;
        }

    }

    private int getMonthFromCruFileName(String name) {
        if (name == null) {
            return -1;
        }

        int indexOfLastUnderscore = name.lastIndexOf("_");
        int numericalMonth = -1;
        String month = name.substring(indexOfLastUnderscore + 1, name.lastIndexOf(".asc"));
        try {
            numericalMonth = Integer.parseInt(month);
        } catch (NumberFormatException nfe) {
            log.warning(nfe.getMessage());
            return numericalMonth;
        }

        return numericalMonth;
    }

    /**
     * Creates a monthly output
     */
    public void createMonthlyOutput(HashMap<String, Geometry> geometryMap, HashMap<String, List<GridCell>> cache, String rootDirectory, String outputDirectory, String outputFileName) {
        File rootFile = new File(rootDirectory);
        String[] subFiles = rootFile.list();
        // get countries

        // Grab a location, then iterate through each raster file.  Create a seprate StatsAverager for each month%12 value
        // Output one file for precip, one file for temperature

        Set<String> locationNames = geometryMap.keySet();

        HashMap<String, HashMap<Integer, StatsAverager>> monthAveragers = new HashMap<String, HashMap<Integer, StatsAverager>>();

        for (String subFile : subFiles) {
            Set<GridCell> cruCells = getGridCells(rootDirectory + subFile);
            List<GridCell> cellList = new ArrayList<GridCell>(cruCells);
            Collections.sort(cellList);

            log.log(Level.INFO, "handling raster file {0}", subFile);

            for (String locationName : locationNames) {

                // get grid cells
                log.log(Level.INFO, "abound to get cru cells for next location {0}", locationName);

                long t0 = new Date().getTime();
                log.log(Level.INFO, "intersecting with  {0}", locationName);
                List<GridCell> countryCells = cache.get(locationName);
                int month = getMonthFromCruFileName(subFile);

                if (!monthAveragers.containsKey(locationName)) {
                    monthAveragers.put(locationName, new HashMap<Integer, StatsAverager>());

                }

                if (!monthAveragers.get(locationName).containsKey(month)) {
                    monthAveragers.get(locationName).put(month, new StatsAverager());
                }

                if (countryCells != null) {
                    for (GridCell countryCell : countryCells) {
                        masterAverager.update(countryCell.getValue());
                        int index = Collections.binarySearch(cellList, countryCell);
                        if (index == -1) {
                            log.warning(" could not find cell from binary search");
                        } else {
                            monthAveragers.get(locationName).get(month).update(cellList.get(index).getValue());
                        }
                    }
                }

                long t1 = new Date().getTime();
                log.log(Level.INFO, "processing  {0} took  ", new Object[]{(t1 - t0) / 1000.0});
            }
        }

        // for each location, for each month, do the output
        for (String locationName : locationNames) {
            HashMap<Integer, StatsAverager> avergaerMap = monthAveragers.get(locationName);
            Set<Integer> monthKeys = avergaerMap.keySet();
            StringBuilder sb = new StringBuilder();
            List<Integer> monthList = new ArrayList<Integer>(monthKeys);
            Collections.sort(monthList);

            sb.append(locationName);
            sb.append(",");
            for (Integer i : monthList) {


                sb.append(avergaerMap.get(i).getAvg());
                sb.append(",");

            }
            for (Integer i : monthList) {


                sb.append(avergaerMap.get(i).getStandardDeviation());
                sb.append(",");

            }
            for (Integer i : monthList) {
                sb.append(avergaerMap.get(i).getCount());
                sb.append(",");
            }
            sb.delete(sb.length() - 1, sb.length() - 1);

            log.info("about to append to the precip csv file......");
            FileExportHelper.appendToFile(outputDirectory + outputFileName, sb.toString());
        }

        System.out.println("================");
        System.out.println("ALL TIME MAX MIN AVG IS " + masterAverager.getMax() + " " + masterAverager.getMin() + " " + masterAverager.getAvg());
        System.out.println("================");
    }

    public HashMap<String, List<GridCell>> createCountryCellCache(HashMap<String, Geometry> geometryMap, String starterFile) {

        HashMap<String, List<GridCell>> countryCells = new HashMap<String, List<GridCell>>();
        // iterate through countries picking out cells
        Set<String> geomNames = geometryMap.keySet();

        // get grid cells
        Set<GridCell> cruCells = getGridCells(starterFile);

        for (String iso : geomNames) {

            long t0 = new Date().getTime();
            log.log(Level.INFO, "creating cache for {0}", iso);
            Geometry areaGeometry = geometryMap.get(iso);
            Iterator<GridCell> cellIterator = cruCells.iterator();
            while (cellIterator.hasNext()) {

                GridCell gc = cellIterator.next();
                if (areaGeometry != null && areaGeometry.intersects(gc.getPolygon())) {
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
//    private HashMap<String, Geometry> collectCountryGeoms() {
//        BasinDao basinDao = BasinDao.get();
//        List<Basin> basins = basinDao.getBasins();
//        HashMap<String, Geometry> basinMap = new HashMap<String, Geometry>();
//        for (Basin b : basins) {
//
//            Geometry geom = getGeometry(b);
//            basinMap.put(Integer.toString(b.getCode()), geom);
//
//        }
//        return basinMap;
//    }
//    private Geometry getGeometry(Basin b) {
//        log.info("getting basin geometry");
//        Connection connection = DBUtils.getConnection();
//        Geometry g = getGeometry(GeoDao.getGeometryAsText(connection, "basin", "geom", "id", b.getId()));
//        DBUtils.close(connection);
//        return g;
//    }
//
//    private Geometry getGeometry(String wkt) {
//        Geometry geom = null;
//        if (wkt == null) {
//            return null;
//        }
//        try {
//            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
//            WKTReader2 reader = new WKTReader2(geometryFactory);
//            geom = reader.read(wkt);
//
//        } catch (ParseException ex) {
//            Logger.getLogger(AnnualCountryCSVWriter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return geom;
//    }
}

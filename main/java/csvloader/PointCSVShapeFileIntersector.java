/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csvloader;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import export.util.FileExportHelper;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdnis.wb.util.BasicAverager;
import shapefileloader.ShapeFileParserGeometryExtractor;

/**
 * Take a weather csv file like a weather station file and intersect it with 
 * @author Johnny
 */
public class PointCSVShapeFileIntersector {

    public static void main(String[] args) {
        String shapefile = "C:\\Users\\Johnny\\boundary data\\columbia\\Municipios_SIGOT2009_region.shp";
        String weatherStations = "C:\\Users\\Johnny\\ClimateDataInput\\Precipitation\\ColumbiaPrecipitation-2009.csv";
        
        String outputFile = weatherStations.substring(0,weatherStations.length()-3) + "-output.csv";
        String shapeFileAttributeOfImportance = "NOM_MUNICI";

        // get a map with key : attribute value,   value: geometry
        HashMap<String, Geometry> geomMap = new ShapeFileParserGeometryExtractor().readShapeFile(shapefile, shapeFileAttributeOfImportance);

        HashMap<Integer, Integer> columnToMonthMap = new HashMap<Integer, Integer>();
        populateColumnMonthMap(columnToMonthMap);

        new PointCSVShapeFileIntersector().iterateThroughShapeFile(outputFile, geomMap, weatherStations, 7, 6, columnToMonthMap);

    }

    //  map the columns in the csv file to month index
    private static void populateColumnMonthMap(HashMap<Integer, Integer> columnMonthMap) {

        columnMonthMap.put(8, 0);
        columnMonthMap.put(9, 1);
        columnMonthMap.put(10, 2);
        columnMonthMap.put(11, 3);
        columnMonthMap.put(12, 4);
        columnMonthMap.put(13, 5);
        columnMonthMap.put(14, 6);
        columnMonthMap.put(15, 7);
        columnMonthMap.put(16, 8);
        columnMonthMap.put(17, 9);
        columnMonthMap.put(18, 10);
        columnMonthMap.put(19, 11);

    }
    private static final Logger log = Logger.getLogger(PointCSVShapeFileIntersector.class.getName());

    public void iterateThroughShapeFile(String outputFile, HashMap<String, Geometry> mapOfStuffShapes, String weatherStationsFile, int latColumnIndex, int lonColumnIndex, HashMap<Integer, Integer> columnMonthMap) {
        // grab the keys
        Set<String> keys = mapOfStuffShapes.keySet();

        // go through the map
        for (String regionName : keys) {

            Geometry regionGeom = mapOfStuffShapes.get(regionName);

            // get a geom, give it a month hashmap with 12 elements with basic averagers
            HashMap<Integer, BasicAverager> monthAverager = new HashMap<Integer, BasicAverager>();

            // intersect this geometry against the csv file
            BufferedReader csvReader = getBufferedReader(weatherStationsFile);

            // get all the intersecting rows
            Set<HashMap<Integer, String>> rows = csvRowGeometryIntersector(csvReader, regionGeom, latColumnIndex, lonColumnIndex);

            // how many rows do we have
            if (rows.size() > 1) {
                log.log(Level.INFO, "there are {0} for {1}", new Object[]{rows.size(), regionName});
            }

            // get the month vals from each row and ding this geometries monthAveragerMap
            for (HashMap<Integer, String> row : rows) {
                Set<Integer> columnKeys = columnMonthMap.keySet();

                // for each column
                for (Integer column : columnKeys) {

                    try {
                        // get the month
                        int month = columnMonthMap.get(column);

                        // does this row have this column
                        if (row.containsKey(column)) {
                            String dataString = row.get(column);

                            // is it a blank row
                            if (dataString != null && dataString.length() > 0) {
                                double data = Double.parseDouble(dataString);

                                // populate the averager map if necessary
                                if (!monthAverager.containsKey(month)) {
                                    monthAverager.put(month, new BasicAverager());
                                }

                                // hit the averager with the data
                                monthAverager.get(month).update(data);
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
            }
            if (rows.size() > 0) {
                log.log(Level.INFO, "month averager size is {0}", monthAverager.size());

                // here we have a map of averagers with the data we need for this geometry, let's write it out to a file
                StringBuilder sb = new StringBuilder();
                sb.append(regionName.replaceAll("\\,", " "));

                sb.append(",");
                int z = 0;
                while (z < 12) {
                    if (monthAverager.containsKey(z)) {
                        sb.append(monthAverager.get(z).getAvg());
                        
                    }
                    z++;
                    sb.append(",");
                }
                
                z = 0;
                while (z < 12) {
                    if (monthAverager.containsKey(z)) {
                        sb.append(monthAverager.get(z).getCount());
                    }
                    if (z < 11) {
                        sb.append(",");
                    }
                    z++;
                }
                FileExportHelper.appendToFile(outputFile, sb.toString());
            }
        }

    }

    private BufferedReader getBufferedReader(String filePath) {
        FileInputStream fis = null;
        BufferedReader br = null;
        try {

            fis = new FileInputStream(filePath);
            br = new BufferedReader(new InputStreamReader(fis));

        } catch (IOException ex) {
            Logger.getLogger(PointCSVShapeFileIntersector.class.getName()).log(Level.SEVERE, null, ex);
        }

        return br;

    }

    private Set<HashMap<Integer, String>> csvRowGeometryIntersector(BufferedReader br, Geometry geom, int latColumnIndex, int lonColumnIndex) {
        Set<HashMap<Integer, String>> rowsFromIntersectingPoints = new HashSet<HashMap<Integer, String>>();
        try {
            String line = null;

            int i = 0;
            
            // run through each row
            while ((line = br.readLine()) != null) {
                if (i++ == 0) {
                    continue;
                }

                // get point from line
                Point p = GeoCSVUtil.getPointFromRow(line, latColumnIndex, lonColumnIndex);

                // for a row, if it intersects with this geom, run through its 12 elements and tick off each basic averager
                if (geom.intersects(p)) {
                    rowsFromIntersectingPoints.add(GeoCSVUtil.getRowAsTable(line));
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(PointCSVShapeFileIntersector.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(PointCSVShapeFileIntersector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return rowsFromIntersectingPoints;

    }
}

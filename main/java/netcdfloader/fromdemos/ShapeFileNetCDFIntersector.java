/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader.fromdemos;

import asciipng.GeometryBuilder;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import domain.DerivativeStats;
import export.util.FileExportHelper;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdnis.wb.util.BasicAverager;
import sdnis.wb.util.GeomShapeWrapper;
import shapefileloader.ShapeFileParserGeometryExtractor;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 *
 * @author Johnny
 */
public class ShapeFileNetCDFIntersector {

    private static final Logger log = Logger.getLogger(ShapeFileNetCDFIntersector.class.getName());

    private static List<GeomShapeWrapper> getWrappers(String pathToShapeFile, String shapefileAttributeName) {
//        String pathToShapeFile = "C:\\Users\\Johnny\\boundary data\\columbia\\Municipios_SIGOT2009_region.shp";

        ShapeFileParserGeometryExtractor geomExtractor = new ShapeFileParserGeometryExtractor();
        List<String> props = new ArrayList<String>();
        props.add(shapefileAttributeName);
        List<GeomShapeWrapper> wrappers = geomExtractor.readShapeFile(pathToShapeFile, props, null);
        return wrappers;
    }

//    private static List<List<GeomShapeWrapper>> getSeveralWrappers(int amount, String shapefileAttributeName) {
//        List<List<GeomShapeWrapper>> allWrappers = new ArrayList<List<GeomShapeWrapper>>();
//        int i = 0;
//        while (i++ < amount) {
//            allWrappers.add(getWrappers(shapefileAttributeName));
//
//        }
//        return allWrappers;
//    }
//    private static String getFileNameIdentifier(DerivativeStats.climatestat variable){
//        variable.toString();
//        
//    }
    public static void main(String[] args) {
        String shapeFileAttributeName = "ISO_CODES";
        String pathToShapeFile = "C:\\Users\\Johnny\\BoundaryData\\wbshapes2010\\World_Polys_High.shp";
        List<GeomShapeWrapper> wrappers = getWrappers(pathToShapeFile, shapeFileAttributeName);
        String precipFilesBase = "F:\\TEMP Derivative stats- precip is bad\\";
        Collection<DerivativeStats.gcm> gcms = DerivativeStats.getInstance().gcmMap.values();

        //r02
        //r90p
        //r90ptot
        //SDII

        // =====================================================================
        String varId = "FD_BCSD_0";
        DerivativeStats.climatestat stat = DerivativeStats.getInstance().getClimateStat("fd");
        // =====================================================================
        HashSet<String> mustHaves = new HashSet<String>();
        mustHaves.add("2046-2065");
        mustHaves.add(varId);
        mustHaves.add("run1");
        mustHaves.add(".monthly.");

        for (DerivativeStats.gcm g : gcms) {


            String netcdfPrimer = precipFilesBase + g.toString() + "\\out_stats\\" + g.toString() + ".sresa2.run1." + varId + ".5_2deg_2046-2065.monthly.nc";
            log.log(Level.INFO, "trying {0} ", netcdfPrimer);
            String rootNetCDF = precipFilesBase + g.toString() + "\\out_stats\\";
            new ShapeFileNetCDFIntersector().getShapeWrappers(wrappers, shapeFileAttributeName, netcdfPrimer, rootNetCDF, mustHaves, stat);


        }

//        new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i), shapeFileAttributeName, netcdfPrimer, rootNetCDF);
//
//
//        netcdfPrimer = "E:\\updated precip deriv stats - precip is good\\cnrm_cm3\\out_stats\\cnrm_cm3.20c3m.run1.SDII_BCSD_0.5_2deg_1961-1999.monthly.nc";
//        rootNetCDF = "E:\\updated precip deriv stats - precip is good\\cnrm_cm3\\out_stats\\";
//        new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i), shapeFileAttributeName, netcdfPrimer, rootNetCDF);


//        netcdfPrimer = "E:\\updated precip deriv stats - precip is good\\gfdl_cm2_0\\out_stats\\gfdl_cm2_0.20c3m.run1.SDII_BCSD_0.5_2deg_1961-1999.monthly.nc";
//        rootNetCDF = "E:\\updated precip deriv stats - precip is good\\gfdl_cm2_0\\out_stats\\";
//        new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i), shapeFileAtStributeName, netcdfPrimer, rootNetCDF);


//        netcdfPrimer = "E:\\updated precip deriv stats - precip is good\\gfdl_cm2_1\\out_stats\\gfdl_cm2_1.20c3m.run1.SDII_BCSD_0.5_2deg_1961-1999.monthly.nc";
//        rootNetCDF = "E:\\updated precip deriv stats - precip is good\\gfdl_cm2_1\\out_stats\\";
///        new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i++), shapeFileAttributeName, netcdfPrimer, rootNetCDF);
//
//        
//        netcdfPrimer = "E:\\updated precip deriv stats - precip is good\\ipsl_cm4\\out_stats\\ipsl_cm4.20c3m.run1.SDII_BCSD_0.5_2deg_1961-1999.monthly.nc";
//        rootNetCDF = "E:\\updated precip deriv stats - precip is good\\ipsl_cm4\\out_stats\\";
//         new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i), shapeFileAttributeName, netcdfPrimer, rootNetCDF);
//
//        
//        netcdfPrimer = "E:\\updated precip deriv stats - precip is good\\miroc3_2_medres\\out_stats\\miroc3_2_medres.20c3m.run1.SDII_BCSD_0.5_2deg_1961-1999.monthly.nc";
//        rootNetCDF = "E:\\updated precip deriv stats - precip is good\\miroc3_2_medres\\out_stats\\";
//         new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i), shapeFileAttributeName, netcdfPrimer, rootNetCDF);
//
//        
//        String netcdfPrimer = "E:\\updated precip deriv stats - precip is good\\miub_echo_g\\out_stats\\miub_echo_g.20c3m.run1.SDII_BCSD_0.5_2deg_1961-1999.monthly.nc";
//        String rootNetCDF = "E:\\updated precip deriv stats - precip is good\\miub_echo_g\\out_stats\\";
//        new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i), shapeFileAttributeName, netcdfPrimer, rootNetCDF);
//
//      

//         netcdfPrimer = "E:\\updated precip deriv stats - precip is good\\mpi_echam5\\out_stats\\mpi_echam5.20c3m.run1.SDII_BCSD_0.5_2deg_1961-1999.monthly.nc";
//         rootNetCDF = "E:\\updated precip deriv stats - precip is good\\mpi_echam5\\out_stats\\";
//        new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i), shapeFileAttributeName, netcdfPrimer, rootNetCDF);


//        netcdfPrimer = "E:\\updated precip deriv stats - precip is good\\mri_cgcm2_3_2a\\out_stats\\mri_cgcm2_3_2a.20c3m.run1.SDII_BCSD_0.5_2deg_1961-1999.monthly.nc";
//        rootNetCDF = "E:\\updated precip deriv stats - precip is good\\mri_cgcm2_3_2a\\out_stats\\";
//        new ShapeFileNetCDFIntersector().getShapeWrappers(allWrappers.get(i), shapeFileAttributeName, netcdfPrimer, rootNetCDF);
    }

    // driver
    public void getShapeWrappers(List<GeomShapeWrapper> wrappers, String shapeFileAttributeName, String netcdfPrimer, String rootNetCDF, Set<String> fileNameConstraints, DerivativeStats.climatestat stat) {
        new Thread(new Intersector(wrappers, shapeFileAttributeName, netcdfPrimer, rootNetCDF, fileNameConstraints, stat)).run();

//        ShapeFileParserGeometryExtractor geomExtractor = new ShapeFileParserGeometryExtractor();
//        List<String> props = new ArrayList<String>();
//        props.add(shapeFileAttributeName);
//        List<GeomShapeWrapper> wrappers = geomExtractor.readShapeFile(pathShapeFile, props, null);
//        log.log(Level.INFO, "have a list of {0} region geometries", wrappers.size());
//        // the cache is a string with a list of indexes
//        HashMap<String, List<ArrayList<Integer>>> shapeCache = new HashMap<String, List<ArrayList<Integer>>>();
//
//        for (GeomShapeWrapper shapeWrapper : wrappers) {
//
//            // build the cache
//            String regionAttrValue = shapeWrapper.getPropertyMap().get(shapeFileAttributeName);
//            log.log(Level.INFO, " building cache for {0} ", regionAttrValue);
//
//            Geometry regionGeom = shapeWrapper.getGeom();
//            List<ArrayList<Integer>> indexes = buildCache(netcdfPrimer, regionGeom);
//
//            log.log(Level.INFO, " cache size is {0} for {1} of geom {2}", new Object[]{indexes.size(), regionAttrValue, regionGeom.toText()});
//
//            shapeCache.put(shapeWrapper.getPropertyMap().get(shapeFileAttributeName), indexes);
//
//        }
//
//        // do the processing
//        // for each raster, iterate through all shapeCaches
//        File rootFile = new File(rootNetCDF);
//        String[] subFiles = rootFile.list();
//        for (String subFile : subFiles) {
//            if (subFile.contains("pr_BCSD")) {
//                Set<String> municipioNames = shapeCache.keySet();
//
//                for (String municipioName : municipioNames) {
//                    HashMap<Integer, BasicAverager> averagers = getDataFromCachedIndexes(rootFile.getAbsolutePath() + "/" + subFile, shapeCache.get(municipioName));
//                    Set<Integer> timekeys = averagers.keySet();
//                    FileExportHelper.appendToFile("C:\\Users\\Johnny\\output_columbia\\" + municipioName + "_" + subFile, "time index, average, frequency");
//                    for (Integer i : timekeys) {
//                        FileExportHelper.appendToFile("C:\\Users\\Johnny\\output_columbia\\" + municipioName + "_" + subFile, i + "," + averagers.get(i).getAvg() + " , " + averagers.get(i).getCount());
//
//                    }
//                }
//            }
//        }


    }

    private class Intersector implements Runnable {

        private String shapeFileAttributeName;
        private String netcdfPrimer;
        private String rootNetCDF;
        private List<GeomShapeWrapper> wrappers;
        private Set<String> fileNameConstraints;
        private DerivativeStats.climatestat stat;

        public Intersector(List<GeomShapeWrapper> wrappers, String shapeFileAttributeName, String netcdfPrimer, String rootNetCDF, Set<String> fileNameConstraints, DerivativeStats.climatestat stat) {
            this.shapeFileAttributeName = shapeFileAttributeName;
            this.netcdfPrimer = netcdfPrimer;
            this.rootNetCDF = rootNetCDF;
            this.wrappers = wrappers;
            this.fileNameConstraints = fileNameConstraints;
            this.stat = stat;
        }

        public void run() {
//            ShapeFileParserGeometryExtractor geomExtractor = new ShapeFileParserGeometryExtractor();
//            List<String> props = new ArrayList<String>();
//            props.add(shapeFileAttributeName);
//            List<GeomShapeWrapper> wrappers = geomExtractor.readShapeFile(pathShapeFile, props, null);
            log.log(Level.INFO, "have a list of {0} region geometries", wrappers.size());
            // the cache is a string with a list of indexes
            HashMap<String, List<ArrayList<Integer>>> shapeCache = new HashMap<String, List<ArrayList<Integer>>>();
            int count = 0;
            for (GeomShapeWrapper shapeWrapper : wrappers) {

                // build the cache
                String regionAttrValue = shapeWrapper.getPropertyMap().get(shapeFileAttributeName);

                log.log(Level.INFO, " building cache for {0} ", regionAttrValue);

                Geometry regionGeom = shapeWrapper.getGeom();
                List<ArrayList<Integer>> indexes = buildCache(netcdfPrimer, regionGeom, stat);

//                log.log(Level.FINE, " cache size is {0} for {1} of geom {2}", new Object[]{indexes.size(), regionAttrValue, regionGeom.toText()});

                shapeCache.put(shapeWrapper.getPropertyMap().get(shapeFileAttributeName), indexes);
                count++;
                log.info(count + " out of " + wrappers.size() + " complete");
            }

            // do the processing
            // for each raster, iterate through all shapeCaches
            File rootFile = new File(rootNetCDF);

            String[] subFiles = rootFile.list();




            for (String subFile : subFiles) {
                if (StringChooser.hasAllCharacteristics(subFile, fileNameConstraints)) {
                    Set<String> municipioNames = shapeCache.keySet();

                    for (String municipioName : municipioNames) {

                        HashMap<Integer, BasicAverager> averagers = getDataFromCachedIndexes(rootFile.getAbsolutePath() + "/" + subFile, shapeCache.get(municipioName), new MonthlyTemporalAggregationStrategy(), stat);
                        Set<Integer> timekeys = averagers.keySet();
                        try {
                            String folderPath = "C:\\Users\\Johnny\\output_countries\\monthly\\" + stat.toString() + "\\";
                            String outFile = folderPath + municipioName + "_" + subFile + ".csv";
                            new File(folderPath).mkdirs();
                            FileExportHelper.appendToFile(outFile, "time index, average, frequency");
                            for (Integer i : timekeys) {
                                FileExportHelper.appendToFile(outFile, i + "," + averagers.get(i).getAvg() + " , " + averagers.get(i).getCount());
                            }

                        } catch (Exception fnfe) {
                            fnfe.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    public static List<ArrayList<Integer>> buildCache(String fname, Geometry region, DerivativeStats.climatestat stat) {


        NetcdfFile dataFile = null;
        List<ArrayList<Integer>> latLonIndexes = new ArrayList<ArrayList<Integer>>();


        try {

            dataFile = NetcdfFile.open(fname, null);
            //  log.info("detailed info " + dataFile.getDetailInfo());
            // Get the latitude and longitude Variables.
            Variable latVar = dataFile.findVariable("lat");
            if (latVar == null) {
                System.out.println("Cant find Variable latitude");
                return null;
            }

            Variable lonVar = dataFile.findVariable("lon");
            if (lonVar == null) {
                System.out.println("Cant find Variable longitude");
                return null;
            }

            Variable timeVar = dataFile.findVariable("time");
            if (timeVar == null) {
                System.out.println("Cant find Variable time");
                return null;
            }

            Variable dataVar = dataFile.findVariable(stat.toString());
            if (dataVar == null) {
                System.out.println("Cant find data variable ");
                return null;
            }

            // Get the lat/lon data from the file.
            ArrayDouble.D1 latArray;
            ArrayDouble.D1 lonArray;

            latArray = (ArrayDouble.D1) latVar.read();
            lonArray = (ArrayDouble.D1) lonVar.read();


//            time, lat, lon
            int[] howMuchToRead = new int[]{1, 360, 720};


            for (int i = 0; i < howMuchToRead[0]; i++) {
                for (int j = 0; j < howMuchToRead[1]; j++) {
                    for (int k = 0; k < howMuchToRead[2]; k++) {

                        double latitude = latArray.get(j) - .25;
                        double longitude = flipPostiveLongitude(lonArray.get(k)) - .25;

                        Polygon theGon = GeometryBuilder.createGridCellFromLowerLeftPoint(longitude, latitude, 0.5);


                        if (theGon.intersects(region)) {
                            ArrayList<Integer> latLonIndex = new ArrayList<Integer>();
                            latLonIndex.add(j);
                            latLonIndex.add(k);
                            latLonIndexes.add(latLonIndex);
//                            log.info("lat " + latitude);
//                            log.info("lon " + longitude);

                        }
                    }
                }
            }


            // The file is closed no matter what by putting inside a try/catch block.
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (dataFile != null) {
                try {
                    dataFile.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return latLonIndexes;
    }

    public static HashMap<Integer, BasicAverager> getDataFromCachedIndexes(String fname, List<ArrayList<Integer>> indexes, TemporalAggregationStrategy temporalStrategy, DerivativeStats.climatestat variable) {


        NetcdfFile dataFile = null;
        HashMap<Integer, BasicAverager> averagers = new HashMap<Integer, BasicAverager>();

        try {

            dataFile = NetcdfFile.open(fname, null);
//            log.info("detailed info when " + dataFile.getDetailInfo());
            // Get the latitude and longitude Variables.
            Variable latVar = dataFile.findVariable("lat");
            if (latVar == null) {
                System.out.println("Cant find Variable latitude");
                return null;
            }

            Variable lonVar = dataFile.findVariable("lon");
            if (lonVar == null) {
                System.out.println("Cant find Variable longitude");
                return null;
            }

            Variable timeVar = dataFile.findVariable("time");
            if (timeVar == null) {
                System.out.println("Cant find Variable time");
                return null;
            }

            // Get the lat/lon data from the file.
            ArrayDouble.D1 latArray;
            ArrayDouble.D1 lonArray;

            latArray = (ArrayDouble.D1) latVar.read();
            lonArray = (ArrayDouble.D1) lonVar.read();

            // Get the pressure and temperature variables.
            Variable presVar = dataFile.findVariable(variable.toString());
            if (presVar == null) {
                System.out.println("Cant find data variabel");
                return null;
            }


            int[] shape = presVar.getShape();
            System.out.println("shape of var is " + shape[0] + " " + shape[1] + " " + shape[2]);

//            all time, one lat, one lon
            int[] howMuchToRead = new int[]{shape[0], 1, 1};
            float nullVal = new BigDecimal("1.E20").floatValue();

            for (ArrayList<Integer> latLonIndex : indexes) {
                int[] origin = new int[]{0, latLonIndex.get(0), latLonIndex.get(1)};

                ArrayFloat.D3 presArray = (ArrayFloat.D3) presVar.read(origin, howMuchToRead);

                for (int i = 0; i < howMuchToRead[0]; i++) {
                    for (int j = 0; j < howMuchToRead[1]; j++) {
                        for (int k = 0; k < howMuchToRead[2]; k++) {

                            double latitude = latArray.get(latLonIndex.get(0));
                            double longitude = flipPostiveLongitude(lonArray.get(latLonIndex.get(1)));
                            float val = presArray.get(i, j, k);
                            if (val != nullVal) {

                                int averagersIndex = temporalStrategy.generateKey(i);

                                if (!averagers.containsKey(averagersIndex)) {
                                    averagers.put(averagersIndex, new BasicAverager());
                                }

                                averagers.get(averagersIndex).update(val);

                            }
                        }
                    }
                }
            }


            // The file is closed no matter what by putting inside a try/catch block.
        } catch (InvalidRangeException ex) {
            Logger.getLogger(ShapeFileNetCDFIntersector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (dataFile != null) {
                try {
                    dataFile.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return averagers;
    }

    private static double flipPostiveLongitude(double lon) {
        if (lon > 180) {
            return lon - 360;
        }
        return lon;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader.fromdemos;

import export.util.FileExportHelper;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdnis.wb.util.BasicAverager;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 *
 * @author Johnny
 */
public class ReadNetcdf extends Thread{
    
    private String rootDirectory = null;

    public ReadNetcdf(String rootDirectory){
        this.rootDirectory = rootDirectory;
    }
    
    @Override
    public void run() {
        File rootFile = new File(rootDirectory);
        String[] subFiles = rootFile.list();
        for (String subFile : subFiles) {
            if (subFile.contains("pr_BCSD") ) {
                iterateThroughFile(rootFile.getAbsolutePath() + "/" + subFile);
            }
        }
    }

    public static void main(String[] args) {
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\bccr_bcm2_0\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\cccma_cgcm3_1\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\cnrm_cm3\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\gfdl_cm2_0\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\gfdl_cm2_1\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\ipsl_cm4\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\miroc3_2_medres\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\miub_echo_g\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\mpi_echam5\\out_stats\\").start();
        new ReadNetcdf("E:\\updated precip deriv stats - precip is good\\mri_cgcm2_3_2a\\out_stats\\").start();
    }
    
     private static double flipPostiveLongitude(double lon) {
        if (lon > 180) {
            return lon - 360;
        }
        return lon;
    }

    public static void iterateThroughFile(String fname) {

        System.out.println("handling " + fname);
        // These are used to construct some example data.

        // Open the file.
//        String filename = "E:\\updated precip deriv stats - precip is good\\miroc3_2_medres\\out_stats\\miroc3_2_medres.sresa1b.run1.pr_BCSD_0.5_2deg_2081-2100.monthly.nc";
        NetcdfFile dataFile = null;
        try {

            dataFile = NetcdfFile.open(fname, null);
            // Get the latitude and longitude Variables.
            Variable latVar = dataFile.findVariable("lat");
            if (latVar == null) {
                System.out.println("Cant find Variable latitude");
                return;
            }

            Variable lonVar = dataFile.findVariable("lon");
            if (lonVar == null) {
                System.out.println("Cant find Variable longitude");
                return;
            }
            
            Variable timeVar = dataFile.findVariable("time");
            if (timeVar == null) {
                System.out.println("Cant find Variable time");
                return;
            }

            // Get the lat/lon data from the file.
            ArrayDouble.D1 latArray;
            ArrayDouble.D1 lonArray;
            ArrayDouble.D1 timeArray;

            latArray =  (ArrayDouble.D1) latVar.read();
            lonArray =  (ArrayDouble.D1) lonVar.read();
            timeArray = (ArrayDouble.D1) timeVar.read();


            // Get the pressure and temperature variables.
            Variable presVar = dataFile.findVariable("pr");
            if (presVar == null) {
                System.out.println("Cant find Variable pressure");
                return;
            }
            

            int[] shape = presVar.getShape();
            System.out.println("shape of var is " + shape[0] + " " + shape[1] + " " + shape[2]);
//            int recLen = shape[0]; // number of times

            FileExportHelper.appendToFile("C:\\Users\\Johnny\\output\\" + fname.substring(fname.lastIndexOf("/")) + ".csv", "timeVal, max , maxLoc , min , minLoc"  );
            int timeVal = 0;
            while (timeVal < shape[0]) {
                // Read the data. Since we know the contents of the file we know
                // that the data arrays in this program are the correct size to
                // hold all the data.
                ;

//            float nullVal = new BigDecimal("1.0000000200408773E20").floatValue();
                float nullVal = new BigDecimal("1.E20").floatValue();

//            time, lat, lon
                double startMax = -1000;
                double startMin = 100000;
                String maxLoc = null;
                String minLoc = null;
                int[] howMuchToRead = new int[]{1, 360, 720};
                int[] origin = new int[]{timeVal++, 0, 0};
//                long t0 = new Date().getTime();
                ArrayFloat.D3 presArray =  (ArrayFloat.D3) presVar.read(origin, howMuchToRead);
                BasicAverager ba = new BasicAverager();
                float[][][] presIn = new float[howMuchToRead[0]][howMuchToRead[1]][howMuchToRead[2]];
                for (int i = 0; i < howMuchToRead[0]; i++) {
                    for (int j = 0; j < howMuchToRead[1]; j++) {
                        for (int k = 0; k < howMuchToRead[2]; k++) {

                            float val = presArray.get(i, j, k);
                            if (val != nullVal) {
                                presIn[i][j][k] = val;  
                                ba.update(val);
                                if (startMax < ba.getMax()) {
                                    startMax = ba.getMax();
                                    maxLoc = latArray.get(j) + " " + flipPostiveLongitude(lonArray.get(k));
                                }
                                if (startMin > ba.getMin()) {
                                    startMin = ba.getMin();
                                    minLoc = latArray.get(j) + " " + flipPostiveLongitude(lonArray.get(k));
                                }
                            }
                        }

                    }
                }
                
//                long t1 = new Date().getTime();
//                System.out.println("read time " + (t1 - t0) / 1000.0f);
//                System.out.println("max " + ba.getMax());
//                System.out.println("min " + ba.getMin());
//                System.out.println("avg " + ba.getAvg());
//                System.out.println("locationOfMax " + maxLoc);
//                System.out.println("locationOfMin " + minLoc);
                System.out.println(timeArray.get(timeVal) + " " + timeVar.getUnitsString());
                
//                FileExportHelper.appendToFile("C:\\Users\\Johnny\\output\\" + fname.substring(fname.lastIndexOf("/")) + ".csv",    timeVal-1 + ", " +  ba.getMax() + " ," +maxLoc + ","+ ba.getMin() + "," + minLoc  );
            }
            // The file is closed no matter what by putting inside a try/catch block.
        } catch (InvalidRangeException ex) {
            Logger.getLogger(ReadNetcdf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return;
        } finally {
            if (dataFile != null) {
                try {
                    dataFile.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    
   
}

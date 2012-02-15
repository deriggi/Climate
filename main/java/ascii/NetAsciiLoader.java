/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ascii;

import asciipng.CellMapMaker;
import asciipng.CollectGeometryAsciiAction;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class NetAsciiLoader {

//     public static void testDataStoreCache(){
//        try {
//            long t0 = Calendar.getInstance().getTimeInMillis();
//
//            CacheDataStoreAsciiAction cacheDataStoreAction = new CacheDataStoreAsciiAction();
//
//            URL oracle = new URL("http://204.236.240.64/afrMENA_countries_monthly_a2b1_midCen/afrMENA_countries_a2b1_monthly_midCen/CAF/fd/cccma_cgcm3_1.1_b1/map_mean_AR4_Global_Extr_50k_cccma_cgcm3_1.1_b1_fd_9_2046_2065.asc");
//            URLConnection yc = oracle.openConnection();
//
//            new AsciiDataLoader(cacheDataStoreAction).parseAsciiFile( yc.getInputStream());
//            System.out.println(cacheDataStoreAction.toString());
//
//        } catch (IOException ex) {
//            Logger.getLogger(NetAsciiLoader.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }



     public void testRegularCacheAction(){
         try {
            long t0 = Calendar.getInstance().getTimeInMillis();

            CacheAsciiAction caa = new CacheAsciiAction();
//            URL oracle = new URL("http://www.climatewizardcustom.org/WorldBank/afrMENA_countries_annual_midCen/AGO/cd18/ensemble_b1/map_mean_baseline_ensemble_25_AR4_Global_Extr_50k_b1_cd18_14_1961_1990.asc");
//            URLConnection yc = oracle.openConnection();

            new AsciiDataLoader(caa).parseAsciiFile(null, new FileInputStream(new File("C:\\Users\\wb385924\\ensembleAnnualAsc\\AGO\\map_mean_ensemble_0_AR4_Global_Extr_50k_a1b_cd18_14_2046_2065.asc")), null);
//            new AsciiDataLoader(caa).parseAsciiFile(null, yc.getInputStream(), null);
            System.out.println("average " + caa.getAverage());
            System.out.println("max " + caa.getMax());
            System.out.println("min " + caa.getMin());
            System.out.println("frequency " + caa.getCount());
            long t1 = Calendar.getInstance().getTimeInMillis();
            System.out.println("processing time :  " + (t1-t0)/1000.0 + " seconds");

        } catch (IOException ex) {
            Logger.getLogger(NetAsciiLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
     }

     public void testCollectGeometryAction(){
         try {
            long t0 = Calendar.getInstance().getTimeInMillis();

            CollectGeometryAsciiAction caa = new CollectGeometryAsciiAction();
//            URL oracle = new URL("http://204.236.240.64/afrMENA_countries_monthly_a2b1_midCen/afrMENA_countries_a2b1_monthly_midCen/AGO/pr/cccma_cgcm3_1.1_a2/map_mean_baseline_AR4_Global_Extr_50k_cccma_cgcm3_1.1_a2_pr_11_1961_1990.asc");
//            URLConnection yc = oracle.openConnection();

            new AsciiDataLoader(caa).parseAsciiFile(null, new FileInputStream(new File("C:\\MASSIVE_DATA\\cru_ts_3_10.1901.2009.pre_1901_1.asc")), null);
            new CellMapMaker().draw(caa.getGridCells(),"crutest.png");
            System.out.println("frequency " + caa.getSize());
            long t1 = Calendar.getInstance().getTimeInMillis();
            System.out.println("processing time :  " + (t1-t0)/1000.0 + " seconds");

        } catch (IOException ex) {
            Logger.getLogger(NetAsciiLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
     }


    public static void main(String[] args){
        new NetAsciiLoader().testRegularCacheAction();
    }

}

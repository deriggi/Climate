/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip;

import ascii.AsciiDataLoader;
import ascii.CacheAsciiAction;
import export.util.FileExportHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class CruAggregateData {

    private static Logger log = Logger.getLogger(CruAggregateData.class.getName());

    public static void main(String[] args){
        new CruAggregateData().read("C:\\CRU\\cru_ts_3_10.1901.2009.raster_ascii.tmp\\tmp\\");
    }

    public void read(String rootDirectory) {
        File rootFile = new File(rootDirectory);
        String[] subFiles = rootFile.list();

        for (String s : subFiles) {
            getGridCells(rootDirectory + s);
        }
    }

    private void getGridCells(String cruPath) {
        CacheAsciiAction caa = new CacheAsciiAction();
        try {
            long t0 = Calendar.getInstance().getTimeInMillis();
            new AsciiDataLoader(caa).parseAsciiFile(null, new FileInputStream(new File(cruPath)), null);
            long t1 = Calendar.getInstance().getTimeInMillis();
//            log.log(Level.INFO, "max {0} : ", caa.getMax());
//            log.log(Level.INFO, "min {0} : ", caa.getMin());
//            log.log(Level.INFO, "time {0} : ", (t1 - t0) / 1000.0);
            FileExportHelper.appendToFile("crutempmaxmin.csv",cruPath+","+ caa.getMax() + " , " + caa.getMin());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}

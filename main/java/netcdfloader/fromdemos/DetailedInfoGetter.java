/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader.fromdemos;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ucar.nc2.NetcdfFile;

/**
 *
 * @author Johnny
 */
public class DetailedInfoGetter {
    private static Logger log = Logger.getLogger("DetailedInfoGetter");
    public static void main(String[] args){
        try {
            NetcdfFile dataFile = NetcdfFile.open("F:\\TEMP Derivative stats- precip is bad\\gfdl_cm2_0\\out_stats\\gfdl_cm2_0.20c3m.run1.TN10P_BCSD_0.5_2deg_1961-1999.monthly.nc", null);
            log.info( dataFile.getDetailInfo() );
        } catch (IOException ex) {
            Logger.getLogger(DetailedInfoGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}

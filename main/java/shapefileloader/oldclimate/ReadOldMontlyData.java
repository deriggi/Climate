/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.oldclimate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import shapefileloader.ShapeFileParser;

/**
 *
 * @author wb385924
 */
public class ReadOldMontlyData {

    public static void main(String[] args) {
        new ReadOldMontlyData().readIt(args[0]);
    }

    public void readIt(String rootFile) {
//        String testShapeFile = "C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1920-1939.shp";

        ShapeFileParser parser = new ShapeFileParser();
        List<String> names = new ArrayList<String>();
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
//String x = "select count(*) from o_cell where st_equals(o_cell_geom,ST_GeomFromEWKT('SRID=4326;POLYGON((-173 -81, -173 -79, -171 -79, -171 -81, -173 -81))'))";

//        String rootFile = "C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\";
        String[] files = new File(rootFile).list();
        
        for (String file : files) {

            if (file.endsWith(".shp")) {
                
                parser.readShapeFile(rootFile + file, names, null, new OldMonthlyFeatureHandler());
            }
        }
    }
}

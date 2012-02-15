/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.oldclimate;

import export.util.FileExportHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import shapefileloader.ShapeFileParser;

/**
 *
 * @author wb385924
 */
public class ReadMaxMinAnnualData {

    private static final Logger log = Logger.getLogger(ReadMaxMinAnnualData.class.getName());
    private ShapeFileParser parser = new ShapeFileParser();
    private List<String> names = new ArrayList<String>();

    public ReadMaxMinAnnualData() {

        names.add("annual");
        
    }

    public static void main(String[] args) {


//       new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\bccr_bcm2_0\\", "maxminannual.txt");
       new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\cccma_cgcm3_1\\", "maxminannual.txt");
        new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\cnrm_cm3\\", "maxminannual.txt");
        new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\csiro_mk3_5\\", "maxminannual.txt");
        new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\gfdl_cm2_0\\", "maxminannual.txt");
        new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\gfdl_cm2_1\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\ingv_echam4\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\inmcm3_0\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\ipsl_cm4\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\miroc3_2_medres\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\miub_echo_g\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\mpi_echam5\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\mri_cgcm2_3_2a\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\ukmo_hadcm3\\", "maxminannual.txt");
         new ReadMaxMinAnnualData().readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\ukmo_hadgem1\\", "maxminannual.txt");


    }

    private List<String> getNames(){
        return names;
    }
    public void readIt(String rootFile, String ouptputName) {




//String x = "select count(*) from o_cell where st_equals(o_cell_geom,ST_GeomFromEWKT('SRID=4326;POLYGON((-173 -81, -173 -79, -171 -79, -171 -81, -173 -81))'))";

        String[] files = new File(rootFile).list();

        for (String file : files) {

            handleMedianFile(rootFile, file, names, ouptputName);
        }
    }

    public void handleMedianFile(String rootFile, String file, List<String> names, String outputName) {

        if (file.endsWith(".shp") /**&& file.contains("median")**/) {
            log.info(file);
            OldMaxMinFeatureHandler fh = new OldMaxMinFeatureHandler();
            parser.readShapeFile(rootFile + file, names, null, fh);
            FileExportHelper.appendToFile(outputName + ".csv", file + "," + fh.ba.getMax() + "," + fh.ba.getMin() + "," + fh.ba.getAvg());
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.oldclimate;

import export.util.FileExportHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.ShapeFileParser;

/**
 *
 * @author wb385924
 */
public class ReadMaxMinOfShapes {

    private static final Logger log = Logger.getLogger(ReadMaxMinOfShapes.class.getName());
    private ShapeFileParser parser = new ShapeFileParser();
    private List<String> names = new ArrayList<String>();
    private File theOutputFile = null;

    public ReadMaxMinOfShapes(String theOutputFile) {
        this.theOutputFile = new File(theOutputFile);
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
    }

    public static void main(String[] args) {
        new ReadMaxMinOfShapes("annualmaxmins.txt").readIt("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM_long_clim_annual.shp\\bccr_bcm2_0\\");

    }

    private List<String> getNames() {
        return names;
    }

    public void readIt(String rootFile) {

        String[] files = new File(rootFile).list();

        for (String file : files) {

            handleMedianFile(rootFile, file, names);
        }
    }

    

    public void handleMedianFile(String rootFile, String file, List<String> names) {

        if (file.endsWith(".shp") /*&& file.contains("median")*/) {
            log.info(file);
            OldMaxMinFeatureHandler fh = new OldMaxMinFeatureHandler();
            parser.readShapeFile(rootFile + file, names, null, fh);
            FileExportHelper.appendToFile(theOutputFile.getName() + ".csv", file + "," + fh.ba.getMax() + "," + fh.ba.getMin() + "," + fh.ba.getAvg());
        }
    }
}

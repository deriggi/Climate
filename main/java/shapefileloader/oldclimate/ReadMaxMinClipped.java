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
public class ReadMaxMinClipped {

    private static final Logger log = Logger.getLogger(ReadMaxMinClipped.class.getName());
    private ShapeFileParser parser = new ShapeFileParser();
    private List<String> month = new ArrayList<String>();
    private List<String> annual = new ArrayList<String>();
    private File theOutputFile = null;

    public ReadMaxMinClipped(String theOutputFile) {
        this.theOutputFile = new File(theOutputFile);
        month.add("Jan");
        month.add("Feb");
        month.add("Mar");
        month.add("Apr");
        month.add("May");
        month.add("Jun");
        month.add("July");
        month.add("Aug");
        month.add("Sep");
        month.add("Oct");
        month.add("Nov");
        month.add("Dec");

        annual.add("annual");
    }

    public static void main(String[] args) {
        new ReadMaxMinClipped("clippedmaxmin2.txt").readIt("S:\\GLOBAL\\ClimatePortal_2\\RENDERING\\GCM_2deg\\GCMs\\");
    }

    public void readIt(String rootFile) {
        String[] files = new File(rootFile).list();
        for (String file : files) {
            File f = new File(rootFile + file);
            if (f.isDirectory() && f.getName().equalsIgnoreCase("Clipped")) {
                log.log(Level.INFO, "clipped {0}", f.getAbsolutePath());
                handleClippedFolder(f);

            } else if (f.isDirectory()) {
                
                readIt(f.getAbsolutePath() + "\\");
            }
        }
    }

    public void handleClippedFolder(File clippedFolder) {
        File[] subfiles = clippedFolder.listFiles();
        for (File f : subfiles) {
            if (f.getName().endsWith(".shp") && f.getName().contains("median")) {
                OldMaxMinFeatureHandler fh = new OldMaxMinFeatureHandler();
                if(f.getAbsolutePath().contains("annual")){
                    parser.readShapeFile(f.getAbsolutePath(), annual, null, fh);
                }else{
                    parser.readShapeFile(f.getAbsolutePath(), month, null, fh);
                }
                
                FileExportHelper.appendToFile(theOutputFile.getName() + ".csv", clippedFolder.getAbsolutePath() + ","  + f.getName()+ "," + fh.ba.getMax() + "," + fh.ba.getMin() + "," + fh.ba.getAvg());
            }
        }
    }
}

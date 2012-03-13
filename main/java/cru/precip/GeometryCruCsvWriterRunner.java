/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip;

import asciipng.GridCell;
import com.vividsolutions.jts.geom.Geometry;
import cru.precip.moneymaker.StatusCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Johnny
 */
public class GeometryCruCsvWriterRunner extends Thread {

    private String projectName;
    private String starterFile;
    private String cruDirectory;
    private String outputDirectory;
    private HashMap<String, Geometry> inputMap = null;
    private static final ArrayList<GeometryCruCsvWriterRunner> threads = new ArrayList<GeometryCruCsvWriterRunner>();
    private GeometryCruCsvWriter writer = new GeometryCruCsvWriter();

    public synchronized static void init() {
        int i = 0;
        if (threads.isEmpty()) {
            while (i < 5) {

                threads.add(new GeometryCruCsvWriterRunner());
                i++;
            }
        }
        System.out.println("size of cru runner pool is " + threads.size());
    }

    public static void doTask(String starterFile, String cruDirectory, String outputDirectory, HashMap<String, Geometry> inputMap, String projectName) {
        synchronized (threads) {
            if (threads.isEmpty()) {
                threads.add(new GeometryCruCsvWriterRunner());
                threads.add(new GeometryCruCsvWriterRunner());
            }
            StatusCache.setPercentComplete(projectName, 0);
            GeometryCruCsvWriterRunner t = threads.remove(0);
            t.initialize(starterFile, cruDirectory, outputDirectory, inputMap,  projectName );
            t.start();

        }
    }

    private void initialize(String starterFile, String cruDirectory, String outputDirectory, HashMap<String, Geometry> inputMap, String projectName) {
        this.starterFile = starterFile;
        this.cruDirectory = cruDirectory;
        this.outputDirectory = outputDirectory;
        this.inputMap = inputMap;
        this.projectName = projectName;
    }

    @Override
    public void run() {
        HashMap<String, List<GridCell>> cells = writer.createCountryCellCache(inputMap, starterFile);
        writer.createRawOutput(inputMap, cells, cruDirectory, outputDirectory, projectName);
    }
}

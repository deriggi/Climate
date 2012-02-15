/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package netcdfloader;

import database.DBUtils;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 *
 * @author wb385924
 */
public class MultiLoaderManager {

    private final int NUM_THREADS = 100;
    private static ArrayList<String> files = new ArrayList<String>();
//    private String file = null;
    private Date minDate = null;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private ArrayList<ParserTask> tasks = new ArrayList<ParserTask>();
    private int doneWorkers = 0;
    private static MultiLoaderManager loader = new MultiLoaderManager();

//
//    public static void main(String[] args){
//        String fileArg = null;
//        String dateArg = null;
//        String vararg = null;
//
//
//        if(args.length == 3){
//            fileArg = args[0];
//            dateArg = args[1];
//            vararg = args[2];
//        }
//
//        else if (args.length == 2){
//            fileArg = args[0];
//            dateArg = null;
//            vararg = args[1];
//        }
//
//        MultiLoaderManager lm = getInstance();
//        lm.init();
//
//    }

    private void init(){
        doThis("bccr_bcm2_0.sresa2.run1.tasmax_BCSD_0.5_2deg_2046-2065.monthly.nc", null, "tasmax");
        doThis("bccr_bcm2_0.sresa2.run1.tasmin_BCSD_0.5_2deg_2046-2065.monthly.nc", null, "tasmin");
        doThis("bccr_bcm2_0.sresa2.run1.TXX_BCSD_0.5_2deg_2046-2065.monthly.nc", null, "txx");
        doThis("bccr_bcm2_0.sresa2.run1.TNN_BCSD_0.5_2deg_2046-2065.monthly.nc", null, "tnn");
        doThis("bccr_bcm2_0.sresa2.run1.CD18_BCSD_0.5_2deg_2046-2065.monthly.nc", null, "cooling_days");
        doThis("bccr_bcm2_0.sresa2.run1.R02_BCSD_0.5_2deg_2046-2065.monthly.nc", null, "wet_days");

//        doThis("bccr_bcm2_0.sresa2.run1.pr_BCSD_0.5_2deg_2046-2065.monthly.nc", null, "precipitation");
    }
    
    public static MultiLoaderManager getInstance(){
        return loader;
    }
    
    private MultiLoaderManager(){

    }
    private void addTask(ParserTask t){
        tasks.add(t);
    }

    public synchronized void notifyJobFinished(){

        if(++doneWorkers == NUM_THREADS && tasks.size() > 0){
            System.out.println("starting work on workers of size  " + tasks.size());
            for(ParserTask pt:tasks){
                pt = tasks.remove(0);
                doThis(pt.filename, pt.date, pt.varname);
            }
        }

        System.out.println("workers done count is now " + doneWorkers);

    }

    private static Date parseDateArg(String date){
        if(date == null) {return null;}
        try {
            
            return sdf.parse(date);

        } catch (ParseException ex) {
            Logger.getLogger(MultiLoaderManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void buildFileList(String baseName, int size, String extension){
        int i = 0;
        while(i++ < size){
            push(baseName + "_"+size + "."+extension);
        }
    }

    private void push(String file){
        files.add(file);
    }
    
    public void doThis(String file, Date minDate, String var){
//        DBUtils.get();

        int fileIndex = 0;
        int range = getRange(file, 0);
        int width = range /(NUM_THREADS);
        System.out.println(range + " is the range");
        int i=0;
        System.out.println("width is " + width);
        int numThreads = 0;
        for(i = 0; i < range; i+=width ){
            System.out.println("starting " + numThreads++);
            int stop = width+i;
            if(stop > range){
                stop = range;
            }
            System.out.println(i + " " + (stop));
            new Thread(new MultiLoader(var ,file, i, stop, minDate)).start();
        }
    }

     public int getRange(String filename, int rangeIndex) {
        NetcdfFile ncfile = null;
        int[] range = new int[3];
        try {

            // setup
            ncfile = NetcdfFile.open(filename);
            Variable data = null;
            List<Variable> variables = ncfile.getVariables();


            int counter = 0;
            for (Variable v : variables) {

                int[] shape = v.getShape();
                if (shape.length == 3) {
                    System.out.println(counter++ + " This is our three dimensional variable");
                    System.out.println(v.getName().toString() + '\t' + v.getDimensionsString());
                    for (int i = 0; i < shape.length; i++) {
                        System.out.println(shape[i]);
                    }
                    data = v;
                }


            }


            range = new int[3];
            for (int i = 0; i < range.length; i++) {
                range[i] = data.getDimension(i).getLength() - 1;
                System.out.println(data.getDimension(i).getName() + " max index is " + range[i]);
            }
        } catch (IOException ex) {
            Logger.getLogger(ParameterizedLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (null != ncfile) {
                try {
                    ncfile.close();
                } catch (IOException ioe) {
                    //log("trying to close " + filename, ioe);
                }
            }
        }

        return range[rangeIndex];

    }

     public class ParserTask{
         private String filename  = null;
         private String varname = null;
         private Date date = null;

         public ParserTask(String filename, Date minDate, String var){

         }

     }

}

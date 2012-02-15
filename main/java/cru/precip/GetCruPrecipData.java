/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip;

import ascii.AsciiDataLoader;
import ascii.DatabaseAsciiAction;
import database.DBUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
@Deprecated
public class GetCruPrecipData implements Runnable {

    private float startPercent, endPercent;
    private Date minDate;

    public GetCruPrecipData(Order order, float startPercent, float endPercent, Date minDate) {
        this.order = order;
        this.startPercent = startPercent;
        this.endPercent = endPercent;
        this.minDate = minDate;
    }

    public void run() {
        getCruPrecip(this.order, startPercent, endPercent, minDate);
    }

    public enum Order {

        ASC, DESC
    };

    public static void main(String[] args) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

            Date a1minDate = sdf.parse("1919.01.01");
            Thread asc1 = new Thread(new GetCruPrecipData(Order.ASC, 0, .52f, a1minDate));

//            Date a2minDate = sdf.parse("1967.01.01");
//            Thread asc2 = new Thread(new GetCruPrecipData(Order.ASC, .50f, .88f, a2minDate));
            asc1.start();
//            asc2.start();
//
            //        new GetCruPrecipData(Order.ASC).parseDate("cru_ts_3_10.1901.2009.pre_1901_1.asc");
        } catch (ParseException ex) {
            Logger.getLogger(GetCruPrecipData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private Order order;
    private static final String PRE = "pre_";
     private static final String TMP = "tmp_";
    private static final Logger log = Logger.getLogger(GetCruPrecipData.class.getName());

    public static Date parseDate(String fileName) {
        return parseCalendar(fileName).getTime();
    }

    private static CruDao.VAR determineVar(String filePath) {
        String tempIdentifier = "2009.tmp_";
        String prIdentifier = "2009.pre_";
        CruDao.VAR var = null;
        if (filePath.contains(tempIdentifier)) {
            var = CruDao.VAR.temp;
        } else if (filePath.contains(prIdentifier)) {
            var = CruDao.VAR.pr;
        }
        
        return var;
    }
    private static String getVarId(CruDao.VAR var){
        if(var.equals(CruDao.VAR.pr)){
            return PRE;
        }
        if(var.equals(CruDao.VAR.temp)){
            return TMP;
        }
        return null;

    }

    public static Calendar parseCalendar(String fileName) {
        CruDao.VAR var = determineVar(fileName);

        String[] parts = fileName.substring(fileName.indexOf(getVarId(var)) + 4, fileName.lastIndexOf(".")).split("_");
        Calendar c = Calendar.getInstance();
        log.log(Level.FINE, "trying to parse 0: {0} {1}", new Object[]{parts[0], parts[parts.length - 1]});
        c.set(Calendar.YEAR, Integer.parseInt(parts[0]));
        c.set(Calendar.MONTH, Integer.parseInt(parts[parts.length - 1]) - 1);
        c.set(Calendar.DATE, 1);
        Date d = c.getTime();
        log.log(Level.FINE, "parsed date as {0}", d);




        return c;




    }

    /**
     * To start halfway, reduction factor = 2
     * @param list
     * @param reductionFactor
     * @return
     */
    private List<String> cutList(List<String> list, float startPercent, float endPercent) {


        int size = list.size();
        log.log(Level.INFO, "starting from {0} to {1} liste size {2}", new Object[]{new Float((size * startPercent)).intValue(), new Float((size * endPercent)).intValue(), list.size()});







        return list.subList(new Float((size * startPercent)).intValue(), new Float((size * endPercent)).intValue());




    }

    private boolean shouldSkip(String rootPath, Order order, Date minDate) {
        if (minDate == null) {
            return false;




        }
        if (parseDate(rootPath).before(minDate) && order.equals(Order.ASC)) {
            return true;




        }
        if (parseDate(rootPath).after(minDate) && order.equals(Order.DESC)) {
            return true;




        }
        return false;




    }

    public void getCruPrecip(Order order, float startPercent, float endPercent, Date minDate) {
        try {



//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
//            Date minDate = sdf.parse("1984.01.01");

            String rootPath = "S:\\GLOBAL\\NEW_CRU\\cru_ts_3_10.1901.2009.raster_ascii.pre\\pre\\";

            File dir = new File(rootPath);
            String[] shorts = dir.list();




            int count = 0;
            List<String> names = Arrays.asList(shorts);
            names = cutList(names, startPercent, endPercent);




            if (this.order.equals(Order.DESC)) {
                Collections.reverse(names);




            }
            Iterator<String> fileIterator = names.iterator();





            while (fileIterator.hasNext()) {
                String s = fileIterator.next();




                if (shouldSkip(s, order, minDate)) {
                    log.log(Level.INFO, "skipping {0}", s);




                    continue;




                }
                File f = new File(rootPath + s);
                DatabaseAsciiAction databaseAction = new DatabaseAsciiAction();
                AsciiDataLoader loader = new AsciiDataLoader(databaseAction);

                loader.parseAsciiFile(parseDate(f.getName()), new FileInputStream(f), null);

                log.log(Level.INFO, "trying {0}", s);







            }
            DBUtils.closeAll();








        } catch (FileNotFoundException ex) {
            Logger.getLogger(AsciiDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.closeAll();


        }


    }
}

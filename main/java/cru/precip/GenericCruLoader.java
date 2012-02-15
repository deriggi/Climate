/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip;

import dao.basin.BasinDao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.gcm.areaidstrategy.AreaIdGetterStrategyFactory;

/**
 *
 * @author wb385924
 */
public class GenericCruLoader {

    private static final Logger log = Logger.getLogger(CruFileComparer.class.getName());
    boolean foundYet = false;
    double[] target = new double[3];
    int count = 0;
    private CruDao cruDao = new CruDao();
    private String tempIdentifier = "2009.tmp_";
    private String prIdentifier = "2009.pre_";

    
    public static void main(String[] args) {
        new GenericCruLoader().loadFromRoot("C:\\Users\\wb385924\\Dropbox\\clusteroutput\\region 1\\region\\cru\\cruoutput\\temp\\");
    }

    public void loadFromRoot( String rootPath) {
        String[] children = new File(rootPath).list();
        for (String child : children) {
            readFile(rootPath + child);
        }
        log.log(Level.INFO, "logged {0} files", count);
    }

    private void readFile(String filePath) {
        CruDao.VAR var = null;

        if(filePath.contains(tempIdentifier)){
            var = CruDao.VAR.temp;
        }else if(filePath.contains(prIdentifier)){
            var = CruDao.VAR.pr;
        }

        InputStreamReader isr = null;
        BufferedReader br = null;
        String commma = "\\,";

        try {
            isr = new InputStreamReader(new FileInputStream(new File(filePath)));
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(commma);
                if(parts.length != 5){
                    log.warning("line is not having all elements");
                }else{
                    double val = Double.parseDouble(parts[1]);
                    Calendar cal = GetCruPrecipData.parseCalendar(filePath.substring(0,filePath.indexOf(".csv")));
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);

                    String identifier = parts[0];
                    int actualId = AreaIdGetterStrategyFactory.getIdStrategy(identifier).getAreaId(identifier);
                    cruDao.insertCru(actualId, year, month, val, var);


                    log.log(Level.FINE,"month {0} {1}", new Object[]{year,month});
                    count++;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

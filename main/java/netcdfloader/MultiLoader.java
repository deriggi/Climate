/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package netcdfloader;

import database.DBUtils;
import java.sql.Connection;
import java.util.Date;

/**
 *
 * @author wb385924
 */
public class MultiLoader implements Runnable{
    private int start = 0;
    private int stop = 0;
    private String path = null;
    private String var = null;
    private Connection c = DBUtils.getConnection();
    private Date minDate = null;

    public MultiLoader(String variable, String file, int start, int stop, Date minDate){
        this.var = variable; this.path = file; this.start = start; this.stop = stop;
    }

    public void run() {
        new CachedPramaterizedLoader(c, minDate).readNCData(path, 2, 1, var, start, stop);
        
    }

}

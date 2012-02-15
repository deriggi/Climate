/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author wb385924
 */
public class ClimateDateUtils {

    public static Date getFirstDateOfYear(int year){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH,0);
        c.set(Calendar.HOUR,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

      public static Date getLastDateOfYear(int year){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.DATE, 31);
        c.set(Calendar.MONTH,11);
        c.set(Calendar.HOUR,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ascii;

import java.sql.Connection;
import java.util.Date;

/**
 *
 * @author wb385924
 */
public interface AsciiAction {

    public void handleNonNullData(double y, double x,  Date date, double data);

    
}

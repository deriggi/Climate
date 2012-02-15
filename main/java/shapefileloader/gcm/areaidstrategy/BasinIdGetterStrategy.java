/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.gcm.areaidstrategy;

import dao.basin.BasinDao;
import java.util.regex.Pattern;

/**
 *
 * @author wb385924
 */
public class BasinIdGetterStrategy implements AreaIdGetterStrategy  {
    private String pattern  = "\\d{1,10}";
    public int getAreaId(String identifer) {
        if(identifer  == null || !Pattern.compile(pattern).matcher(identifer).matches()){
            return -1;
        }
        int wbhuc = Integer.parseInt(identifer);
        int basinId = BasinDao.get().getBasinIdFromCode(wbhuc);
        return basinId;
    }

}

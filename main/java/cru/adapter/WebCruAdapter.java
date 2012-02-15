/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cru.adapter;

import domain.Cru;
import domain.web.V1WebCru;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class WebCruAdapter {

    public List<V1WebCru> eatCrus(Collection<Cru> crus){


        List<V1WebCru> webCrus = new ArrayList<V1WebCru>();
        if(crus == null || crus.isEmpty()){
            return webCrus;
        }
        for(Cru c: crus){
            int year = c.getYear();
            int month = c.getMonth();
            double data = c.getData();
            V1WebCru vwc = null;
            if(year == -1 && month == -1){
                vwc = new V1WebCru(new Double(data).floatValue(), year, month);
            }else if(year != -1 && month == -1){
                vwc = new V1WebCru(new Double(data).floatValue(), year);
            }else if(year == -1 && month != -1){
                vwc = new V1WebCru(new Double(data).floatValue(), month);
            }
            else{
                vwc = new V1WebCru(new Double(data).floatValue(), year, month);
            }
            webCrus.add(vwc);
        }
        return webCrus;
    }

}

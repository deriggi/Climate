/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author wb385924
 */
@XmlRootElement
public class ClimateDatum {

    public List<CountryRain> getData() {
        return data;
    }
    public ClimateDatum(){
        
    }


    public void addData(CountryRain rain){
        if(data  == null){
            data = new ArrayList<CountryRain>();
        }
        data.add(rain);
    }
    public void setData(List<CountryRain> data) {
        this.data = data;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, String> metadata) {
        this.metadata = metadata;
    }
    private HashMap<String,String> metadata ;

    private List<CountryRain> data;


}

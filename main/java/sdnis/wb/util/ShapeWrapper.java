/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sdnis.wb.util;

import java.util.HashMap;

/**
 *
 * @author wb385924
 */
public class ShapeWrapper {
    
    private HashMap<String,String> propertyMap;
    private String shapeString = null;

    public ShapeWrapper(String shapeString, HashMap<String,String> propMap){
        this.shapeString = shapeString;
        this.propertyMap = propMap;
    }
    
    public String getShapeString() {
        return shapeString;
    }

    public void setShapeString(String shapeString) {
        this.shapeString = shapeString;
    }


    public HashMap<String, String> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(HashMap<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }


}

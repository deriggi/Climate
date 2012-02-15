/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader.fromdemos;

import java.util.Set;

/**
 *
 * @author Johnny
 */
public class StringChooser {
    
    public static boolean hasAllCharacteristics(String target, Set<String> toMatch){
        
        for (String substring : toMatch){
            if(!target.contains(substring)){
                return false;
            }
        }
        return true;
    }
    
}

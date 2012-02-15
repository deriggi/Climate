/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader.fromdemos;

/**
 *
 * @author Johnny
 * Just take each index and return it.  This will produce aggregation in the most basic way, refelecting the time values within the netcdf file
 */
public class RawTemporalAggregationStrategy implements TemporalAggregationStrategy{
    
    /**
     * wow
     * 
     * @param i
     * @return 
     */
    public int generateKey(int i){
        return i;
    }
    
}

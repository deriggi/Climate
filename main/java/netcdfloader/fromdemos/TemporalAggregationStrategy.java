/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader.fromdemos;

/**
 *
 * @author Johnny
 */
public interface TemporalAggregationStrategy {
    
    public int generateKey(int i);
    
}

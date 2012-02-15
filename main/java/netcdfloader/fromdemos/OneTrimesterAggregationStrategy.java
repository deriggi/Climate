/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader.fromdemos;

/**
 *
 * @author Johnny
 * 
 * helps produce averages for each month
 */
public class OneTrimesterAggregationStrategy implements TemporalAggregationStrategy{
    private int base = -1;
    public int generateKey(int i){
        if(i%3 == 0){
            return ++base;
        }
        return base;
    }
    
    public static void main(String[] args){
        OneTrimesterAggregationStrategy ts = new OneTrimesterAggregationStrategy();
        int i = 0;
        while(i < 27){
            System.out.println(i + " " + ts.generateKey(i));
            i++;
        }
        
    }
    
}

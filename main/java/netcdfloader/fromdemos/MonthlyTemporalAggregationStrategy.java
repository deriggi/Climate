/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netcdfloader.fromdemos;

/**
 *
 * @author Johnny
 * 
 * assumes i is a month index among a set of who-cares how many months.  should always return the month index regardless of year
 * helps produce averages for each month
 */
public class MonthlyTemporalAggregationStrategy implements TemporalAggregationStrategy {

    
    public int generateKey(int i) {
        return i % 12;
    }

    public static void main(String[] args) {
        MonthlyTemporalAggregationStrategy st = new MonthlyTemporalAggregationStrategy();
        int i = 0;
        while(i < 24){
            System.out.println(i + "  " +  st.generateKey(i));
            i++;
        }
    }
}

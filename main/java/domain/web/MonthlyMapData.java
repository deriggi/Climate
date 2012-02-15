/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;

import java.util.HashMap;

/**
 *
 * @author wb385924
 */
public class MonthlyMapData implements MapData {

    private transient static HashMap<Integer, String> months = null;
    private Double jan;
    private Double feb;
    private Double mar;
    private Double apr;
    private Double may;
    private Double jun;
    private Double jul;
    private Double aug;
    private Double sep;
    private Double oct;
    private Double nov;
    private Double dec;



    public void assignValuesFromMap(HashMap<Integer, Double> map) {
//        HashMap<String, Double> monthMap = new HashMap<String, Double>();

       
        if (map.containsKey(1)) {
            this.jan = map.get(1);
        }
        if (map.containsKey(2)) {
            this.feb = map.get(2);
        }
        if (map.containsKey(3)) {
            this.mar = map.get(3);
        }
        if (map.containsKey(4)) {
            this.apr = map.get(4);
        }
        if (map.containsKey(5)) {
            this.may = map.get(5);
        }
        if (map.containsKey(6)) {
            this.jun = map.get(6);
        }
        if (map.containsKey(7)) {
            this.jul = map.get(7);
        }
        if (map.containsKey(8)) {
            this.aug = map.get(8);
        }
        if (map.containsKey(9)) {
            this.sep = map.get(9);
        }
        if (map.containsKey(10)) {
            this.oct = map.get(10);
        }
        if (map.containsKey(11)) {
            this.nov = map.get(11);
        }
        if (map.containsKey(12)) {
            this.dec = map.get(12);
        }


    }
}

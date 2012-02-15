/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv.requestrunner;

import java.util.ArrayList;
import tnccsv.RequestBuilders.MonthlyRequestBuilder;


/**
 *
 * @author wb385924
 */
public class MonthlyCSVGetter implements Runnable{

    private MonthlyRequestBuilder.order order;
    private ArrayList<String> ignoreList;
    

    public void setOrder(ArrayList<String> ignoreList, MonthlyRequestBuilder.order order) {
        this.order = order;
        this.ignoreList = ignoreList;
       
    }

    public void run(){

        new MonthlyRequestBuilder().downloadMonthlyCSV(ignoreList, order);
    }


    public static void main(String[] args) {
        ArrayList<String> ignoreList = new ArrayList<String>();
        ignoreList.add("AGO");
        ignoreList.add("ARE");
        ignoreList.add("ZWE");
        ignoreList.add("BDI");
        ignoreList.add("ZMB");


        MonthlyCSVGetter ascendingCSVGetter = new MonthlyCSVGetter();
        MonthlyCSVGetter descendingCSVGetter = new MonthlyCSVGetter();

        ascendingCSVGetter.setOrder(ignoreList, MonthlyRequestBuilder.order.ASC);
        descendingCSVGetter.setOrder(ignoreList, MonthlyRequestBuilder.order.DESC);

        Thread asc = new Thread(ascendingCSVGetter);
        Thread desc = new Thread(descendingCSVGetter);
        
        asc.start();
        desc.start();

    }
}

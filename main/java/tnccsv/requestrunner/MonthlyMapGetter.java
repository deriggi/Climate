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
public class MonthlyMapGetter implements Runnable {

    private MonthlyRequestBuilder.order order;
    private ArrayList<String> ignoreList;

    public void setOrder(ArrayList<String> ignoreList, MonthlyRequestBuilder.order order) {
        this.order = order;
        this.ignoreList = ignoreList;
    }

    public void run() {
        new MonthlyRequestBuilder().downloadMonthlyMapData(ignoreList, order);
    }

    public static void main(String[] args) {
        ArrayList<String> ignoreList = new ArrayList<String>();

        ignoreList.add("NGA");
        ignoreList.add("OMN");
        ignoreList.add("PSE");
        ignoreList.add("QAT");
        ignoreList.add("RWA");
        ignoreList.add("SAU");
        ignoreList.add("SDN");
        ignoreList.add("SEN");
        ignoreList.add("SLE");
        ignoreList.add("SOM");
        ignoreList.add("STP");
        ignoreList.add("SWZ");
        ignoreList.add("SYR");
        ignoreList.add("TCD");
        ignoreList.add("TGO");
        ignoreList.add("TUN");
        ignoreList.add("TZA");
        ignoreList.add("UGA");
        ignoreList.add("YEM");
        ignoreList.add("ZAF");
        ignoreList.add("ZMB");
        ignoreList.add("ZWE");
        ignoreList.add("AGO");
        ignoreList.add("ARE");
        ignoreList.add("BDI");
        ignoreList.add("BEN");
        ignoreList.add("BFA");
        ignoreList.add("BHR");
        ignoreList.add("BWA");
        ignoreList.add("CAF");
        ignoreList.add("CIV");
        ignoreList.add("CMR");
        ignoreList.add("COD");
        ignoreList.add("COG");
        ignoreList.add("COM");
        ignoreList.add("CPV");
        ignoreList.add("DJI");
        ignoreList.add("DZA");
        ignoreList.add("EGY");
        ignoreList.add("ERI");


//        ignoreList.add("DJI"); //?
//        ignoreList.add("UGA");

        MonthlyMapGetter ascMonthlyMapGetter = new MonthlyMapGetter();
        ascMonthlyMapGetter.setOrder(ignoreList, MonthlyRequestBuilder.order.ASC);

        MonthlyMapGetter descMonthlyMapGetter = new MonthlyMapGetter();
        descMonthlyMapGetter.setOrder(ignoreList, MonthlyRequestBuilder.order.DESC);

        Thread asc = new Thread(ascMonthlyMapGetter);
        asc.start();

        Thread desc = new Thread(descMonthlyMapGetter);
        desc.start();

    }
}

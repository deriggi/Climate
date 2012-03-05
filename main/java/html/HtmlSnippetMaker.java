/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package html;

import asciipng.CellMapMaker;
import cru.precip.GeometryCruPNGMapMaker;
import export.util.FileExportHelper;
import java.util.HashMap;
import shapefileloader.graphics.ClassifierHelper;
import shapefileloader.graphics.ColorRampHelper;

/**
 * just a simlpe utility for making some sinppets for the columbia precipitation maps
 * 
 * @author Johnny
 */
public class HtmlSnippetMaker {

    public static void main(String[] args) {

        String outputFile = "C:\\Users\\Johnny\\Documents\\ColumbiaCoverSite\\snippet.txt";
        monthMap.put(1, "January");
        monthMap.put(2, "February");
        monthMap.put(3, "March");
        monthMap.put(4, "April");
        monthMap.put(5, "May");
        monthMap.put(6, "June");
        monthMap.put(7, "July");
        monthMap.put(8, "August");
        monthMap.put(9, "September");
        monthMap.put(10, "October");
        monthMap.put(11, "November");
        monthMap.put(12, "December");

        new HtmlSnippetMaker().writeIt(outputFile);
        
        
//        String legendSnippet = CellMapMaker.makeLegend(ClassifierHelper.getEqualIntervalBounds(106, 296, 10), ColorRampHelper.getTemperatureRamp());
//        FileExportHelper.appendToFile("C:\\Users\\Johnny\\Documents\\ColumbiaCoverSite\\legendsnippet.txt", legendSnippet);
    }

    private String getTopOfMonth(int month) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='row'>");
    	sb.append("<div class='span8'>");
        sb.append("<p><h3>");
        sb.append(monthMap.get(month));
        sb.append("</h3></p><ul class='thumbnails'>");
        return sb.toString();
    }
    private static HashMap<Integer, String> monthMap = new HashMap<Integer, String>();
    private static final String bottomOfMonth = "</ul></div></div>";

    public void writeIt(String outputFile) {
        for (int month = 1; month < 13; month++) {
            FileExportHelper.appendToFile(outputFile, getTopOfMonth(month));
            for (int year = 1970; year < 2010; year++) {
                StringBuilder sb = new StringBuilder();
                sb.append(getFirstPart());
                sb.append(year);
                sb.append("_");
                sb.append(month);
                sb.append(getSecondPart(year));
                FileExportHelper.appendToFile(outputFile, sb.toString());
            }
            FileExportHelper.appendToFile(outputFile, bottomOfMonth);
        }



    }

    private String getSecondPart(int year) {
        StringBuilder secondPart = new StringBuilder();
        secondPart.append(".asc.png'/>");
        secondPart.append("<div class='caption'><p>");
        secondPart.append(year);
        secondPart.append("</p></div></div></li>");
        return secondPart.toString();
    }

    private String getFirstPart() {
        StringBuilder firstPart = new StringBuilder();
        firstPart.append("<li class='span2'>");
        firstPart.append("<div class='thumbnail'>");
        firstPart.append("<img src='cruimages/");
        firstPart.append("cru_ts_3_10.1901.2009.tmp_");

        return firstPart.toString();

    }
}

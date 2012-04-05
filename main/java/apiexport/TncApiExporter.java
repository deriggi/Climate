/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apiexport;

import com.google.gson.Gson;
import domain.DerivativeStats;
import domain.web.MonthlyGcmDatum;
import export.util.FileExportHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Johnny
 */
public class TncApiExporter {

    private static final String a1b = "a1b.run";
    private static final String a2 = "a2.run";
    private static final String b1 = "b1.run";
    private static final String s_20c3m = "20c3m.run";
    private static final String t_2046_2065 = "2046_2065";
    private static final String t_2081_2100 = "2081_2100";
    //

    public static void main(String[] args) {
//        String root = "C:\\Users\\Johnny\\output_countries\\monthly\\precipitation\\";
        String root = "C:\\Users\\Johnny\\output_countries\\monthly\\temperature\\";
        
        
//        String[] climateVars = {"sdii", "pr", "r90ptot", "r90p", "r02"};
        String[] climateVars = {"cd18", "fd", "gd10", "hd18", "tasmin", "tasmax", "TN10P", "TN90P", "tnn", "TX10P", "TX90P", "txx"};
        String[] scenarios = {a1b, a2, b1};
        
        for(String precipVar: climateVars){
            for(String scenario: scenarios){
                new TncApiExporter().go(root+precipVar, scenario);
            }
        }
        
        
        
    }

    public void go(String rootDirectory, String scenarioFilter) {
        String[] files = new File(rootDirectory).list();
        List<String> fileList = Arrays.asList(files);

        Collections.sort(fileList);

        HashMap<String, List<MonthlyGcmDatum>> countryCache = new HashMap<String, List<MonthlyGcmDatum>>();
        String currentIso = null;
        String var = getFolderName(rootDirectory);
        for (String s : fileList) {


            // export the list if it has something
//                    FileExportHelper.writeToFile("C:\\monthlycountryjson\\" + currentIso + "_"+ scenarioFilter+"_pr.json", new Gson().toJson(datum));

            // clear the list
            if(!s.contains(scenarioFilter)){
                continue;
            }

            currentIso = s.substring(0, 3);

            String gcm = getGcmFromName(s);

            MonthlyGcmDatum monthlyDatum = new MonthlyGcmDatum(gcm);
            monthlyDatum.setScenario(getScenarioFromName(s));

            int[] years = getYearRange(s);
            monthlyDatum.setFromYear(years[0]);
            monthlyDatum.setToYear(years[1]);
            monthlyDatum.setVariable(var);

            List<Double> dataPoints = getMonthlyData(rootDirectory + "\\" + s);
            
            if (dataPoints.size() == 12) {
                int month = 0;
                for (Double d : dataPoints) {
                    monthlyDatum.addVal(month++, d);
                }

                if (!countryCache.containsKey(currentIso)) {
                    countryCache.put(currentIso, new ArrayList<MonthlyGcmDatum>());
                }
                countryCache.get(currentIso).add(monthlyDatum);


            }
        }
        exportCache(countryCache, scenarioFilter, var);
    }

    private void exportCache(HashMap<String, List<MonthlyGcmDatum>> cache, String scenarioFilter, String var) {
        Set<String> keySet = cache.keySet();
        for (String key : keySet) {

            FileExportHelper.writeToFile("C:\\monthlytempcountryjson\\" + key + "_" + scenarioFilter.replace("run", "") + var+".json", new Gson().toJson(cache.get(key)));
        }

    }

    private String getFolderName(String fullPath) {
        String fullParentPath = new File(fullPath).getAbsolutePath();

        String var = fullParentPath.substring(fullParentPath.lastIndexOf("\\") + 1);
        return var;
    }

    private List<Double> getMonthlyData(String fullFileName) {
        FileInputStream fis = null;
        List<Double> data = new ArrayList<Double>();
        try {
            fis = new FileInputStream(new File(fullFileName));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;

            int lineCounter = 0;

            while ((line = br.readLine()) != null && (lineCounter < 13)) {
                if (lineCounter++ == 0) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 3) {
                    System.out.println("alert, we are leaving because line is " + parts.length);
                    return data;
                }

                try {
                    double dataElement = Double.parseDouble(parts[1]);
                    data.add(dataElement);
                } catch (NumberFormatException nfe) {
                    System.out.println("could not get a number from line" + lineCounter + " " + line);
                    data.clear();
                    return data;
                }

//                lineCounter++;

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TncApiExporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioe) {
            Logger.getLogger(TncApiExporter.class.getName()).log(Level.SEVERE, null, ioe);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(TncApiExporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return data;
    }

    private String getScenarioFromName(String fileName) {
        if (fileName == null) {
            return null;
        }

        if (fileName.indexOf(a1b) != -1) {
            return DerivativeStats.scenario.a1b.toString();
        }

        if (fileName.indexOf(a2) != -1) {
            return DerivativeStats.scenario.a2.toString();
        }

        if (fileName.indexOf(b1) != -1) {
            return DerivativeStats.scenario.b1.toString();
        }

        if (fileName.indexOf(s_20c3m) != -1) {
            return DerivativeStats.scenario.s_20c3m.toString();
        }

        return null;
    }

    private int[] getYearRange(String fileName) {
        String yearsPattern = "\\d{4}\\-\\d{4}";
        int[] years = new int[2];
        Pattern p = Pattern.compile(yearsPattern);
        Matcher matcher = p.matcher(fileName);
        if (matcher.find()) {
            String yearsPart = matcher.group();

            if (yearsPart != null && yearsPart.length() >= 9) {
                try {
                    years[0] = Integer.parseInt(yearsPart.substring(0, 4));
                } catch (NumberFormatException nfe) {
                    years = null;
                }
            }

            if (yearsPart != null && yearsPart.length() >= 9) {
                try {
                    years[1] = Integer.parseInt(yearsPart.substring(5));
                } catch (NumberFormatException nfe) {
                    years = null;
                }
            }
        }

        return years;
    }

    private String getGcmFromName(String fileName) {
        if (fileName == null || fileName.length() < 4) {
            return null;
        }

        // from 4 to the first dot
        String gcm = fileName.substring(4, fileName.indexOf("."));
        return gcm;

    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class TNCCsvProcessor implements TNCBufferedReaderProcessor {

    private static final Logger log = Logger.getLogger(TNCCsvProcessor.class.getName());
    private TreeMap<Integer, Double> yearDataMap = null;

    public TreeMap<Integer, Double> getYearDataMap() {
        return yearDataMap;
    }

    public void readFromBuffer(String parentURL, BufferedReader br) throws IOException {
        if(yearDataMap == null){
            yearDataMap = new TreeMap<Integer,Double>();
        }
        
        String line = null;
        int lineCount = 0;
        while ((line = br.readLine()) != null) {

            if (lineCount > 0) {
                String[] parts = line.split("\\,");
                
                parts[0] = parts[1].replaceAll("\"", "").trim();
                parts[1] = parts[2].replaceAll("\"", "").trim();
                try{
                    yearDataMap.put(Integer.parseInt(parts[0]), Double.parseDouble(parts[1]));
                } catch (NumberFormatException nfe){
                    log.warning("couldn not extarct data from parts " + parts );
//                    nfe.printStackTrace();
                }
//                log.info("size is now "  + yearDataMap.size());
            }

            lineCount++;
        }
    }
}

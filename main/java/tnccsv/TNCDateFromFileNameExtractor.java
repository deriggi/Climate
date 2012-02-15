/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tnccsv;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author wb385924
 */
public class TNCDateFromFileNameExtractor {

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
    private int month = -1;
    private int startYear = -1;
    private int endYear = -1;
    private boolean isYearly = false;
    private boolean isMonthly = false;

    public boolean isIsMonthly() {
        return isMonthly;
    }
    private String datePattern = "\\d{1,2}\\_\\d{4}\\_\\d{4}[\\.\\_]";
    private Pattern p = null;
    private final static Logger log=  Logger.getLogger(TNCDateFromFileNameExtractor.class.getName());

    public TNCDateFromFileNameExtractor(){
        p = Pattern.compile(datePattern);
    }

    private void reset(){
        this.isYearly = false;
        this.isMonthly = false;
        this.month = -1;
        this.startYear = -1;
        this.endYear = -1;
        
    }

    public boolean isProperlySet(){
        if  (
                isYearly == isMonthly   || (
                month ==        -1      ||
                endYear ==      -1      ||
                startYear ==    -1
            
                )){

            return false;
        }
        return true;
    }
    
    public void extratDateProperties(String fileName){
        reset();
        Matcher matcher = p.matcher(fileName);
        if(matcher.find()){
            String foundMatch = matcher.group();
//            System.out.println(foundMatch);
            String datepart = foundMatch.substring(0, foundMatch.length()-1);
            String[] parts = datepart.split("\\_");

            if(parts.length == 3){
                month       = Integer.parseInt(parts[0]);
                startYear   = Integer.parseInt(parts[1]);
                endYear     = Integer.parseInt(parts[2]);

                if(month == 14){
                    isYearly = true;
                    isMonthly = false;

                }else if(1 <= month && month <= 12){
                    isYearly = false;
                    isMonthly = true;
                    
                }
            }else{
                log.warning("could not properly extract date info from file name");
            }


            log.log(Level.FINE, "start month: {0} startyear: {1} endyear: {2}", new Object[]{month, startYear, endYear});

        }
        else{
            log.log(Level.WARNING, "date extractor could not match pattern at all to: {0}", fileName);
        }
        
    }
    public static void main(String[] args){
        TNCDateFromFileNameExtractor td = new TNCDateFromFileNameExtractor();
        td.extratDateProperties("table_yearly_AR4_Global_Extr_50k_cccma_cgcm3_1.1_a1b_txx_10_2081_2100.csv");
        System.out.println(td.isProperlySet());

    }

}

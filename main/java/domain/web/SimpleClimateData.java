/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain.web;

/**
 *
 * @author wb385924
 */
public class SimpleClimateData {

    public SimpleClimateData(String iso, String gcm, String statType, String climateStatType, String scenario, int fromYear, int toYear, int month, float data) {
        this.iso = iso;
        this.gcm = gcm;
        this.statType = statType;
        this.climateStatType = climateStatType;
        this.scenario = scenario;
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.month = month;
        this.data = data;
    }
    

    public String getClimateStatType() {
        return climateStatType;
    }

    public void setClimateStatType(String climateStatType) {
        this.climateStatType = climateStatType;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public int getFromYear() {
        return fromYear;
    }

    public void setFromYear(int fromYear) {
        this.fromYear = fromYear;
    }

    public String getGcm() {
        return gcm;
    }

    public void setGcm(String gcm) {
        this.gcm = gcm;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getStatType() {
        return statType;
    }

    public void setStatType(String statType) {
        this.statType = statType;
    }

    public int getToYear() {
        return toYear;
    }

    public void setToYear(int toYear) {
        this.toYear = toYear;
    }

    private String iso;
    private String gcm;
    private String statType;
    private String climateStatType;
    private String scenario;
    private int fromYear;
    private int toYear;
    private int month;
    private float data;

}

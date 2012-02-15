/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.oldclimate;

import java.util.HashMap;
import shapefileloader.gcm.P_GcmStatsProperties;
import shapefileloader.gcm.P_GcmStatsProperties.climatestat;
import shapefileloader.gcm.P_GcmStatsProperties.gcm;
import shapefileloader.gcm.P_GcmStatsProperties.scenario;
import shapefileloader.gcm.P_GcmStatsProperties.stat_type;

/**
 *
 * @author wb385924
 */
public class OldMonthlyCellularConfig {

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OldMonthlyCellularConfig other = (OldMonthlyCellularConfig) obj;
        if (this.gcm != other.gcm) {
            return false;
        }
        if (this.scenario != other.scenario) {
            return false;
        }
        if (this.stat != other.stat && (this.stat == null || !this.stat.equals(other.stat))) {
            return false;
        }
        if (this.statType != other.statType) {
            return false;
        }
        if (this.fYear != other.fYear) {
            return false;
        }
        if (this.tYear != other.tYear) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.gcm != null ? this.gcm.hashCode() : 0);
        hash = 17 * hash + (this.scenario != null ? this.scenario.hashCode() : 0);
        hash = 17 * hash + (this.stat != null ? this.stat.hashCode() : 0);
        hash = 17 * hash + (this.statType != null ? this.statType.hashCode() : 0);
        hash = 17 * hash + this.fYear;
        hash = 17 * hash + this.tYear;
        return hash;
    }

     public static final HashMap<String,Integer> monthMap = new HashMap<String,Integer>();

    static{
        monthMap.put("Jan", 1);
        monthMap.put("Feb", 2);
        monthMap.put("Mar", 3);
        monthMap.put("Apr", 4);
        monthMap.put("May", 5);
        monthMap.put("Jun", 6);
        monthMap.put("July", 7);
        monthMap.put("Aug", 8);
        monthMap.put("Sep",9);
        monthMap.put("Oct", 10);
        monthMap.put("Nov", 11);
        monthMap.put("Dec", 12);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String comma = ",";

        sb.append(getStatType());
        sb.append(comma);

        sb.append(getGcm());
        sb.append(comma);

        sb.append(getScenario());
        sb.append(comma);

        sb.append(getValue());
        sb.append(comma);

        sb.append(getStat());
        sb.append(comma);

        sb.append(getfYear());
        sb.append(comma);

        sb.append(gettYear());
        sb.append(comma);

        sb.append(getCellId());
        sb.append(comma);

        sb.append(getMonth());
        sb.append(comma);

        return sb.toString();
    }

    private long cellId;

    public long getCellId() {
        return cellId;
    }

    public void setCellId(long cellId) {
        this.cellId = cellId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
    private int month;

    public gcm getGcm() {
        return gcm;
    }

    public void setGcm(gcm gcm) {
        this.gcm = gcm;
    }

    public scenario getScenario() {
        return scenario;
    }

    public void setScenario(scenario scenario) {
        this.scenario = scenario;
    }
    private P_GcmStatsProperties.gcm gcm;
    private P_GcmStatsProperties.scenario scenario;
    private P_GcmStatsProperties.climatestat stat;
    private P_GcmStatsProperties.stat_type statType;

    public stat_type getStatType() {
        return statType;
    }

    public void setStatType(stat_type statType) {
        this.statType = statType;
    }

    public climatestat getStat() {
        return stat;
    }

    public void setStat(climatestat stat) {
        this.stat = stat;
    }
    private int fYear, tYear;
    private Double value = null;

    public OldMonthlyCellularConfig(stat_type statType, gcm gcm, scenario scenario, climatestat stat, int fYear, int tYear) {
        this.gcm = gcm;
        this.scenario = scenario;
        this.stat = stat;
        this.fYear = fYear;
        this.tYear = tYear;
        this.statType = statType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getfYear() {
        return fYear;
    }

    public void setfYear(int fYear) {
        this.fYear = fYear;
    }

    public int gettYear() {
        return tYear;
    }

    public void settYear(int tYear) {
        this.tYear = tYear;
    }
}

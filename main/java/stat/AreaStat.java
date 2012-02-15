/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stat;

import java.util.ArrayList;
import java.util.List;
import shapefileloader.gcm.P_GcmStatsProperties;
import shapefileloader.gcm.P_GcmStatsProperties.climatestat;
import shapefileloader.gcm.P_GcmStatsProperties.scenario;
import shapefileloader.gcm.P_GcmStatsProperties.stat_type;

/**
 *
 * @author wb385924
 */
public class AreaStat {
    
    private String areaId;
    private int fromYear;
    private int toYear;
    private List<Double> data = new ArrayList<Double>();
    private P_GcmStatsProperties.scenario scenario;
    private P_GcmStatsProperties.climatestat stat;
    private P_GcmStatsProperties.stat_type type;
    private int percentile;

    public int getPercentile() {
        return percentile;
    }

    public void setPercentile(int percentile) {
        this.percentile = percentile;
    }

    public stat_type getType() {
        return type;
    }

    public void setType(stat_type type) {
        this.type = type;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public int getFromYear() {
        return fromYear;
    }

    public void setFromYear(int fromYear) {
        this.fromYear = fromYear;
    }

    public scenario getScenario() {
        return scenario;
    }

    public void setScenario(scenario scenario) {
        this.scenario = scenario;
    }

    public climatestat getStat() {
        return stat;
    }

    public void setStat(climatestat stat) {
        this.stat = stat;
    }

    public int getToYear() {
        return toYear;
    }

    public void setToYear(int toYear) {
        this.toYear = toYear;
    }



}

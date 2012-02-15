/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm;

import shapefileloader.gcm.P_GcmStatsProperties;
import shapefileloader.gcm.P_GcmStatsProperties.climatestat;
import shapefileloader.gcm.P_GcmStatsProperties.gcm;
import shapefileloader.gcm.P_GcmStatsProperties.scenario;
import shapefileloader.gcm.P_GcmStatsProperties.stat_type;

/**
 *
 * @author wb385924
 */
public interface P_Config {

    public P_ConfigDao getConfigDao();

    public P_ConfigAreaDao getConfigAreaDao();
    
    public String toCompactString();

    public boolean isIsAnnual();

    public int getMonth();

    public void setMonth(int month);

    public gcm getGcm();

    public void setGcm(gcm gcm);

    public scenario getScenario();

    public void setScenario(scenario scenario);

    public String getIso3();

    public int getAreaId();

    public void setAreaId(int areaId);

    public stat_type getStatType();

    public void setStatType(stat_type statType);

    public climatestat getStat();

    public void setStat(climatestat stat);

    public Double getValue();

    public void setValue(Double value);

    public int getfYear();

    public void setfYear(int fYear);

    public int gettYear();

    public void settYear(int tYear);

    public boolean isComplete();

    public boolean isCompleteIgnoringAreaValueMonth();

    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;

/**
 *
 * @author wb385924
 */
public interface EnsembleDatum {

    public String getCsvHeader();

    public String getCsvLine();

    public Integer getFromYear();

    public void setFromYear(int fromYear);

    public Integer getToYear();

    public void setToYear(int toYear);

    public void addVal(int month, double val);

    public Double[] getMonthVals();

    public void setScenario(String scenario);

    public String getScenario();

//    public void setModel(String model);
//    public String getModel();
    public void setPercentile(int percentile);

    public int getPercentile();
}

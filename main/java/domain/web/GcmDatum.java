/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;

/**
 *
 * @author wb385924
 */
public interface GcmDatum extends Comparable<GcmDatum> {

    public void addVal(int month, double val);

    public Double[] getMonthVals();

    public Integer getFromYear();

    public void setFromYear(int fromYear);

    public Integer getToYear();

    public void setToYear(int toYear);

    public String getGcm();

    public void setScenario(String scenario);

    public String getScenario();

    public void setVariable(String varname);

    public String getVariable();

    public String getCsvLine();

    public String getCsvHeader();
//      public void setModel(String model);
//      public String getModel();
}

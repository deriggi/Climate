/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author wb385924
 */
public class PrecipitationData {
    private transient SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private transient Date rainDateObj;
    private String rainDate;

    private float sum;
    private String studyId;
    private String iso3;


    public Date getRainDate() {
        return rainDateObj;
    }

    public void setRainDate(Date rainDate) {
        this.rainDate = sdf.format(rainDate);
        this.rainDateObj = rainDate;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyCode) {
        this.studyId = studyCode;
    }
    

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }
    
    

    public float getSum() {
        return sum;
    }

    public void setSum(float data) {
        this.sum = data;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sdnis.wb.util;

import java.util.Date;

/**
 *
 * @author wb385924
 */
public class AreaMonth {
    private Date date = null;
    private int areaId = -1;
    
    public AreaMonth(Date date,int areaId){
        this.date = date;
        this.areaId = areaId;
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AreaMonth other = (AreaMonth) obj;
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        if (this.areaId != other.areaId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 61 * hash + this.areaId;
        return hash;
    }
    

    
    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }




}

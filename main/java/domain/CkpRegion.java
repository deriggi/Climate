/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain;

/**
 *
 * @author wb385924
 */
public class CkpRegion {
    private int ckpRegionId;
    private String ckpRegionCode;

    public CkpRegion(int ckpRegionId, String ckpRegionCode) {
        this.ckpRegionId = ckpRegionId;
        this.ckpRegionCode = ckpRegionCode;
    }
    
    

    public String getCkpRegionCode() {
        return ckpRegionCode;
    }

    public void setCkpRegionCode(String ckpRegionCode) {
        this.ckpRegionCode = ckpRegionCode;
    }

    public int getCkpRegionId() {
        return ckpRegionId;
    }

    public void setCkpRegionId(int ckpRegionId) {
        this.ckpRegionId = ckpRegionId;
    }


}

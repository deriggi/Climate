/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.gcm.areaidstrategy;

import dao.ckpregion.CkpRegionDao;

/**
 *
 * @author wb385924
 */
public class CkpRegionIdeGetterStrategy implements AreaIdGetterStrategy{


    public int getAreaId( String identifer) {
        return  CkpRegionDao.get().getRegionId(identifer);
    }

}

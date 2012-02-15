/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.gcm.areaidstrategy;

import service.CountryService;

/**
 *
 * @author wb385924
 */
public class CountryIdGetterStrategy implements AreaIdGetterStrategy  {

    public int getAreaId( String identifer) {
        return CountryService.get().getId(identifer);
    }

}

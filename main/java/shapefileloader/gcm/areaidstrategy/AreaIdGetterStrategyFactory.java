/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.gcm.areaidstrategy;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author wb385924
 */
public class AreaIdGetterStrategyFactory {
    private static String basinWbhucNumersPattern  = "\\d+";
    private static String cpkRegionCode  = "[a-zA-Z]{2}";
    private static String countryIso3Code  = "[a-zA-Z]{3}";
    
    private static final Logger log = Logger.getLogger(AreaIdGetterStrategyFactory.class.getName());

    public static AreaIdGetterStrategy getIdStrategy(String identifier){

        if(Pattern.matches(basinWbhucNumersPattern,identifier)){
            log.log(Level.FINE," handling id as basin {0}",identifier);
            return new BasinIdGetterStrategy();
        }

        if(Pattern.matches(cpkRegionCode,identifier)){
            log.log(Level.INFO," handling id as cpkRegion {0}",identifier);
            return new CkpRegionIdeGetterStrategy();
        }

        if(Pattern.matches(countryIso3Code,identifier)){
            log.log(Level.INFO," handling id as country {0}",identifier);
            return new CountryIdGetterStrategy();
        }

        log.log(Level.WARNING," tried to get id strategy for unknown type {0}",identifier);
        return null;

    }
}

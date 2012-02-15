/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package emdat;

import java.util.HashMap;

/**
 *
 * @author wb385924
 */
public class EmdatsSingleton {
    private static HashMap<String,Emdat> emdats = null;
    private static HashMap<String,Emdat[]> shorts = null;

    public static Emdat get(String emdatString){
        if(emdats == null){
            init();
        }
        if(emdats.containsKey(emdatString)){
            return emdats.get(emdatString);
        }
        return null;
    }


    public static Emdat[] getCombined(String emdatString){
        if(shorts == null){
            init();
        }
        if(shorts.containsKey(emdatString)){
            return shorts.get(emdatString);
        }
        return null;
    }
    
    private synchronized static void init(){
        emdats = new HashMap<String,Emdat>();
        shorts = new HashMap<String,Emdat[]>();
        
        emdats.put("flooddeath", Emdat.flood_death);
        emdats.put("floodaffected", Emdat.flood_affected);
        emdats.put("droughtdeath", Emdat.drought_death);
        emdats.put("droughtaffected", Emdat.drought_affected);

        emdats.put("stormdeath", Emdat.storm_death);
        emdats.put("stormaffected", Emdat.storm_affected);

        shorts.put("flood", new Emdat[]{Emdat.flood_affected, Emdat.flood_death});
        
        shorts.put("drought", new Emdat[]{Emdat.drought_affected, Emdat.drought_death});
        
        
        
        shorts.put("storm", new Emdat[]{Emdat.storm_affected, Emdat.storm_death});





    }

}

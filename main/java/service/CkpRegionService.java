/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package service;

import dao.ckpregion.CkpRegionDao;
import domain.CkpRegion;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author wb385924
 */
public class CkpRegionService {

    private static  Set<CkpRegion> ckpRegions  = null;;
    private static HashMap<String, CkpRegion> regionsMap = new HashMap<String,CkpRegion>();
     static{
         init();

     }

     private static void init(){
         ckpRegions = CkpRegionDao.get().getCkpRegions();
         for(CkpRegion r : ckpRegions){
             regionsMap.put(r.getCkpRegionCode(), r);
         }
     }
     public static int getId(String code){
         if(code == null ){
             return -1;
         }
         code = code.toUpperCase();
         if(regionsMap.containsKey(code)){
             return regionsMap.get(code).getCkpRegionId();
         }
         return -1;

     }

     public static CkpRegion getCkpRegion(String code){
         if(code == null ){
             return null;
         }
         code = code.toUpperCase();
         if(regionsMap.containsKey(code)){
             return regionsMap.get(code);
         }
         return null;

     }
}

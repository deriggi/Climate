/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;


import dao.GeoDao;
import dao.country.CountryDao;
import dao.country.RegionDao;

import database.DBUtils;
import domain.Country;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class CountryService {

    private Logger log = Logger.getLogger(CountryService.class.getName());
    private static CountryService cs = null;
    private CountryDao countryDao = null;
    private RegionDao regionDao = null;
    private HashMap<String, Country> countryMap = new HashMap<String, Country>();
    private HashMap<String, String> wbVariantMap = new HashMap<String, String>();

    private CountryService() {
    }

    public static CountryService get() {
        if (cs == null) {
            cs = new CountryService();
            cs.countryDao = CountryDao.get();
            cs.regionDao = RegionDao.get();
            cs.countryMap = cs.countryDao.getCountriesAsMap(cs.countryDao.getCountries());
            cs.initVariantMap();
        }
        return cs;
    }

    private  void initVariantMap(){
        if(cs == null){
            cs = new CountryService();
        }
        cs.wbVariantMap.put("ZAR", "COD");
        cs.wbVariantMap.put("ADO", "AND");
        cs.wbVariantMap.put("IMY", "IMN");
        cs.wbVariantMap.put("ROU", "ROM");
        cs.wbVariantMap.put("TMP", "TLS");
        cs.wbVariantMap.put("WBG", "PSE");
        cs.wbVariantMap.put("KSV", "XRK");
    }

    public Set<Country> getCountries() {
        TreeSet<Country> countries = new TreeSet<Country>();
        countries.addAll(countryMap.values());
        return countries;
    }

   

    public int getId(String iso3) {
        if (cs.countryMap == null) {
            log.warning("REally should not be creating this hashmap now");
            cs.countryMap = cs.countryDao.getCountriesAsMap(cs.countryDao.getCountries());
        }
        if (iso3 == null || iso3.length() == 0) {
            return -1;
        }
        iso3 = iso3.toUpperCase().trim();
        if(wbVariantMap.containsKey(iso3)){
            iso3 = wbVariantMap.get(iso3);
        }
        log.log(Level.FINE, "trying {0}", iso3);
        if (!countryMap.containsKey(iso3)) {
            log.info("no item found in map");
            return -1;
        }
        return countryMap.get(iso3).getId();

    }

    public void associateCountryToRegion(String iso3, String regionName) {
        int regionId = regionDao.getRegionId(regionName);
        Connection c = DBUtils.getConnection();

        int countryId = GeoDao.getEntityId(c, "country", "iso_3", iso3);
        if (regionId != -1 && countryId != -1) {
            countryDao.assignCountryToRegion(countryId, regionId);
            log.log(Level.INFO, "succesfully associated {0} to {1}", new Object[]{iso3, regionName});
        } else {
            log.log(Level.WARNING, "bad data {0}  {1} for {2} {3}", new Object[]{regionId, countryId, regionName, iso3});
        }
    }

    private void readRegionFile(String filePath) {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(new File(filePath)));
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                associateCountryToRegion(line.trim(), "Africa");
            }

        } catch (FileNotFoundException ioe) {
            ioe.printStackTrace();
        } catch (IOException ioe) {

            ioe.printStackTrace();
        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(CountryService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        CountryService cs = CountryService.get();

//        cs.associateCountryToRegion("MOZ", "Africa");
//        cs.readRegionFile("C:\\Users\\wb385924\\Documents\\Climate Prtal\\africancountries.csv");

//        Set<Country> countries = cs.getCountries();
//        CountryDao dao = CountryDao.get();
//        Iterator<Country> ci = countries.iterator();
//        Gson gson = new Gson();
//        while (ci.hasNext()) {
//            Country c = ci.next();
//            System.out.println(c.getName());
//            try {
//                ShapeSvg shapes = dao.getSVGBoundaryForCountry(c.getId());
//                if (shapes != null) {
//                    FileExportHelper.appendToFile("countriessimple.svg", "countries.push("+gson.toJson(shapes)+");");
//                }
//            } catch (Exception e){
//                System.out.println(e);
//                System.out.println("got error when tyring to load " + c.getName());
//            }
//
//            }

        

        }
    }

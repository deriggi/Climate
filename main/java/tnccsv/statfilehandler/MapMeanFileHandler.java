/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv.statfilehandler;

import ascii.AsciiDataLoader;
import ascii.CacheDataStoreAsciiAction;
import dao.derivedmapdata.DerivedMapDataDao;
import data.domain.DatePoint;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import domain.DerivativeStats;
import tnccsv.PageFetcher;
import tnccsv.TNCDateFromFileNameExtractor;

/**
 *
 * @author wb385924
 */
public class MapMeanFileHandler {
    private final static Logger log = Logger.getLogger(MapMeanFileHandler.class.getName());
    

    public void handle(String fileURL, DerivativeStats.temporal_aggregation temporalAggregation) {
        TNCDateFromFileNameExtractor dateGetter = new TNCDateFromFileNameExtractor();
        dateGetter.extratDateProperties(fileURL);
        CacheDataStoreAsciiAction cacheData = new CacheDataStoreAsciiAction();
        AsciiDataLoader loader = new AsciiDataLoader(cacheData);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, dateGetter.getStartYear());
        if (dateGetter.isIsMonthly()) {
            c.set(Calendar.MONTH, dateGetter.getMonth() - 1);
        } else {
            if (temporalAggregation.name().equals(DerivativeStats.temporal_aggregation.monthly.name())) {
                log.severe("tyring to save monthly data but found yearly data");
            }
            c.set(Calendar.MONTH, 0);
        }
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        loader.parseAsciiFile(c.getTime(), PageFetcher.getInputStream(fileURL), null);
        Set<DatePoint> datePoints = cacheData.getDatePoints();
        Iterator<DatePoint> datePointIterator = datePoints.iterator();
        DerivedMapDataDao mapDataDao = DerivedMapDataDao.get();

        while (datePointIterator.hasNext()) {
            DatePoint datePoint = datePointIterator.next();
            int cellId = mapDataDao.savePoint(datePoint.getLat(), datePoint.getLon());
            log.log(Level.FINE, "map data datepoint is {0} from file {1}", new Object[]{datePoint.toString(), fileURL});

//            mapDataDao.saveSpatialData(datePoint.getData(), countryMap.get(iso3Code).getId(), endOfCentury, mean, stat, monthly, scenario, gcm, run, dateGetter.getMonth(), dateGetter.getStartYear(), cellId);
        }
//        log.log(Level.FINE, "{0} {1}", new Object[]{s, cacheData.toString()});
    }
}

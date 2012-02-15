/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm;

import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class P_GcmConfigAreaMetadataRequestBuilder {

    private static final Logger log = Logger.getLogger(P_GcmConfigAreaMetadataRequestBuilder.class.getName());
    private static final String GET_P_GCM_AGGREGATED_CONFIG_AREA_OPTIONS =
            "select o_var_name, p_gcm_config_from_year, p_gcm_config_to_year from p_gcm_config "
            + " inner join o_stat_type on p_gcm_config_o_stat_type_id = o_stat_type_id "
            + " inner join o_var on p_gcm_config_o_var_id = o_var_id "
            + " inner join gcm on p_gcm_config_gcm_id = gcm_id "
            + " inner join p_gcm_config_area on p_gcm_config_id = p_gcm_config_area_gcm_config_id "
            + " on p_gcm_config_area_area_id = ? and o_stat_type_name = ? and p_gcm_config_month = 1 "
            + " group by  o_stat_type_name, o_var_name, p_gcm_config_from_year, p_gcm_config_to_year order by o_var_name, p_gcm_config_from_year";
    private final static String base = "select p_gcm_config_o_var_id, p_gcm_config_from_year, p_gcm_config_to_year from  p_gcm_config inner join p_gcm_config_area on p_gcm_config_id = p_gcm_config_area_gcm_config_id ";
    private final static String area_id = " p_gcm_config_area_area_id = ";
    private final static String stat_type_id = " p_gcm_config_o_stat_type_id = ";
    private final static String var_id = " p_gcm_config_o_var_id = ";
    private final static String gcm_id = " p_gcm_config_gcm_id =  ";
    private final static String scenario_id = " p_gcm_config_scenario_id =  ";
    private final static String from_year = " p_gcm_config_from_year =  ";
    private final static String ge_from_year = " p_gcm_config_from_year >=  ";
    private final static String le_to_year = " p_gcm_config_to_year <=  ";
    private final static String to_year = " p_gcm_config_to_year = ";
    private final static String month = " p_gcm_config_month = ";
    private final static String and = " and ";
    private final static String comma = ", ";

    public static String request(P_GcmConfig config, boolean isAnnual) {
        StringBuilder sb = new StringBuilder();
        sb.append(base);

        if (config.getAreaId() != -1) {
            sb.append(and);
            sb.append(area_id);
            sb.append(config.getAreaId());
        }

        if (config.getGcm() != null) {
            sb.append(and);
            sb.append(gcm_id);
            sb.append(config.getGcm().getGcmId());
        }


        sb.append(and);
        sb.append(month);
        if (isAnnual) {
            sb.append(-1);
        } else {
            sb.append(1);
        }

        if (config.getScenario() != null) {
            sb.append(and);
            sb.append(scenario_id);
            sb.append(config.getScenario().getId());
        }


        if (config.getStatType() != null) {
            sb.append(and);
            sb.append(stat_type_id);
            sb.append(config.getStatType().getId());
        }

        if (config.getStat() != null) {
            sb.append(and);
            sb.append(var_id);
            sb.append(config.getStat().getId());
        }

        if (config.getfYear() != -1 && config.gettYear() != -1) {
            sb.append(and);
            sb.append(from_year);
            sb.append(config.getfYear());

            sb.append(and);
            sb.append(to_year);
            sb.append(config.gettYear());

        } else if (config.getfYear() != -1 && config.gettYear() == -1) {

            sb.append(and);
            sb.append(ge_from_year);
            sb.append(config.getfYear());

        } else if (config.getfYear() == -1 && config.gettYear() != -1) {

            sb.append(and);
            sb.append(le_to_year);
            sb.append(config.gettYear());

        }
        sb.append(groupBy(config));
        String query = sb.toString();
        log.info(query);
        return query;
    }

    private static String groupBy(P_GcmConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append(" group by ");
        sb.append("p_gcm_config_from_year,");

        sb.append("p_gcm_config_to_year,");

        sb.append("p_gcm_config_o_var_id");


//        if(config.getGcm() != null){
//            sb.append(comma);
//            sb.append(gcm_id);
//        }
//
//        if(config.getScenario() != null){
//            sb.append(comma);
//            sb.append(scenario_id);
//        }
//
//
//        if(config.getStat() != null){
//            sb.append(comma);
//            sb.append(var_id);
//        }
//
//        if(config.getfYear() != -1 ){
//            sb.append(comma);
//            sb.append(from_year);
//
//        }else if(config.gettYear() != -1){
//
//            sb.append(comma);
//            sb.append(to_year);
//        }
        String query = sb.toString();
        log.info(query);
        return query;
    }
}

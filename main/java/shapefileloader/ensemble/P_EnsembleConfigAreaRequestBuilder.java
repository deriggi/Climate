/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.ensemble;

import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class P_EnsembleConfigAreaRequestBuilder {

    private final static String base = "select p_ensemble_config_from_year, p_ensemble_config_to_year, p_ensemble_config_scenario_id, p_ensemble_config_percentile, p_ensemble_config_month, p_ensemble_config_area_value from  p_ensemble_config inner join p_ensemble_config_area on p_ensemble_config_id = p_ensemble_config_area_config_id ";
    private final static String area_id = " p_ensemble_config_area_area_id = ";
    private final static String stat_type_id = " p_ensemble_config_o_stat_type_id = ";
    private final static String percentile = " p_ensemble_config_percentile = ";
    private final static String var_id = " p_ensemble_config_o_var_id = ";
    private final static String scenario_id = " p_ensemble_config_scenario_id =  ";
    private final static String from_year = " p_ensemble_config_from_year =  ";
    private final static String to_year = " p_ensemble_config_to_year = ";
    private final static String ge_from_year = " p_ensemble_config_from_year >=  ";
    private final static String le_to_year = " p_ensemble_config_to_year <=  ";
    private final static String and = " and ";
    private static final Logger log = Logger.getLogger(P_EnsembleConfigAreaRequestBuilder.class.getName());

    public static String request(P_EnsembleConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append(base);

        if (config.getAreaId() != -1) {
            sb.append(and);
            sb.append(area_id);
            sb.append(config.getAreaId());
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

        if (config.getPercentile() != -1) {
            sb.append(and);
            sb.append(percentile);
            sb.append(config.getPercentile());
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
        String query = sb.toString();
        log.info(query);
        return query;
    }
}

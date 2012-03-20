/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip.moneymaker;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * @author Johnny
 */
public class StatusCache {

    private static HashMap<String, ProjectStatus> statusMap = new HashMap<String, ProjectStatus>();
    private static Logger log = Logger.getLogger(StatusCache.class.getName());
    
    private StatusCache() {
    }
    
    public static synchronized void setFinalRestingPlace(String projectKey, String restingPlacePath){
        if(restingPlacePath == null){
            return;
        }
        if (!statusMap.containsKey(projectKey)) {
            ProjectStatus ps = new ProjectStatus();
            ps.setTitle(projectKey);
            statusMap.put(projectKey, ps);
        }
        
        ProjectStatus ps = statusMap.get(projectKey);
        restingPlacePath = restingPlacePath.replaceAll("\\\\", "/");
        ps.setFinalRestingPlace(restingPlacePath);
        
    }
    public static synchronized String getFinalRestingPlace(String projectKey){
        if (!statusMap.containsKey(projectKey)) {
            return null;
        }
        
        ProjectStatus ps = statusMap.get(projectKey);
        return ps.getFinalRestingPlace();
        
    }

    public static synchronized void setStatus(String projectKey, ProjectStatus.PROJECT_STATUS_CODE status) {
        if (!statusMap.containsKey(projectKey)) {
            ProjectStatus ps = new ProjectStatus();
            ps.setTitle(projectKey);
            statusMap.put(projectKey, ps);
        }

        ProjectStatus ps = statusMap.get(projectKey);
        ps.setStatus(status);

    }

    public static synchronized void setLastFile(String projectKey, String lastFile) {
        if (!statusMap.containsKey(projectKey)) {
            ProjectStatus ps = new ProjectStatus();
            ps.setTitle(projectKey);
            statusMap.put(projectKey, ps);
        }

        ProjectStatus ps = statusMap.get(projectKey);
        ps.setLastFileProcessed(lastFile);

    }
    
    public static synchronized void setUserEmail(String projectKey, String userEmail) {
        if (!statusMap.containsKey(projectKey)) {
            ProjectStatus ps = new ProjectStatus();
            ps.setTitle(projectKey);
            statusMap.put(projectKey, ps);
            log.info("when setting the email, there were actually no  other jobs for "  + projectKey);
        }
        
        ProjectStatus ps = statusMap.get(projectKey);
        ps.setUserEmailAddress(userEmail);
    }
    
    public static synchronized String getUserEmail(String projectKey) {
        if (!statusMap.containsKey(projectKey)) {
            return null;
        }

        ProjectStatus ps = statusMap.get(projectKey);
        return ps.getUserEmailAddress();

    }

      public static synchronized void setPercentComplete(String projectKey, float percentComplete) {
        if (!statusMap.containsKey(projectKey)) {
            ProjectStatus ps = new ProjectStatus();
            ps.setTitle(projectKey);
            statusMap.put(projectKey, ps);
        }

        ProjectStatus ps = statusMap.get(projectKey);
        ps.setPercentComplete(percentComplete);

    }
      
      
    public static synchronized float getPercentComplete(String projectKey) {
        if (!statusMap.containsKey(projectKey)) {
            return -1;
        }
        return statusMap.get(projectKey).getPercentComplete();
    }
    
    public static synchronized String getLastFile(String projectKey) {
        if (!statusMap.containsKey(projectKey)) {
            return null;
        }
        
        return statusMap.get(projectKey).getLastFileProcessed();
    }
    
}

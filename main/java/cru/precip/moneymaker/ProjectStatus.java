/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip.moneymaker;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Johnny
 */
public class ProjectStatus {

    public enum PROJECT_STATUS_CODE {

        started, running, done;
    }
    private Date startTimeYall;
    private String title;
    private PROJECT_STATUS_CODE status;
    private String finalRestingPlace;
    private String lastFileProcessed;

    public String getLastFileProcessed() {
        return lastFileProcessed;
    }

    public void setLastFileProcessed(String lastFileProcessed) {
        this.lastFileProcessed = lastFileProcessed;
    }
    private float percentComplete;
    private String[] circularMessageBuffer = new String[5];
    private int nextMessageIndex = 0;
    private int firstMessageIndex = 0;

    public static void main(String[] args) {
        ProjectStatus sp = new ProjectStatus();
        sp.addLogMessage("a");
        sp.addLogMessage("b");
        System.out.println(sp.getSize());
        sp.addLogMessage("c");
        System.out.println(sp.getSize());
        sp.addLogMessage("d");
        System.out.println(sp.getSize());
        sp.addLogMessage("e");

        System.out.println(sp.getSize());
        sp.addLogMessage("f");
        System.out.println(sp.getSize());
        sp.addLogMessage("a");
        sp.addLogMessage("b");
        System.out.println(sp.getSize());
        sp.addLogMessage("c");
        sp.addLogMessage("d");
        sp.addLogMessage("e");
        sp.addLogMessage("f");
        System.out.println(sp.getSize());
    }

    public int getSize() {
        int count = 0;
        for (int i = 0; i < circularMessageBuffer.length; i++) {
            if (circularMessageBuffer[i] != null) {
                count++;
            }
        }
        return count;
    }

    public String getFinalRestingPlace() {
        return finalRestingPlace;
    }

    public PROJECT_STATUS_CODE getStatus() {
        return status;
    }

    public void setStatus(PROJECT_STATUS_CODE status) {
        this.status = status;
    }

    public void setFinalRestingPlace(String finalRestingPlace) {
        this.finalRestingPlace = finalRestingPlace;
    }

    public synchronized void addLogMessage(String message) {
        circularMessageBuffer[nextMessageIndex] = message;
        nextMessageIndex = (nextMessageIndex + 1) % circularMessageBuffer.length;
    }

    //NOT DONE
    private ArrayList<String> getLogMessages() {
        ArrayList<String> messages = new ArrayList<String>();
        // if size is less than length then go from 0 to nextMessage

        // else go from nextMessage+1 to end, then 0 to nextMessage 

        return messages;
    }

    public float getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(float percentComplete) {
        this.percentComplete = percentComplete;
    }

    public Date getStartTimeYall() {
        return startTimeYall;
    }

    public void setStartTimeYall(Date startTimeYall) {
        this.startTimeYall = startTimeYall;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

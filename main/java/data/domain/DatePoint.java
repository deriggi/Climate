/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.domain;

import java.util.Date;

/**
 *
 * @author wb385924
 */
public class DatePoint {
    private double lat;
    private double lon;
    private Date date;
    private double data;

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    @Override
    public String toString(){
        
        String lineSep = System.getProperty("line.separator");
        
        StringBuilder sb = new StringBuilder();

        sb.append("latitude: ");
        sb.append(lat);
        sb.append(lineSep);

        sb.append("longitude: ");
        sb.append(lon);
        sb.append(lineSep);

        sb.append("date: ");
        sb.append(date.toString());
        
        return sb.toString();
        
    }


    public DatePoint(double lat, double lon, Date date, double data){
        this.lat = lat;
        this.lon = lon;
        this.date = date;
        this.data = data;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DatePoint other = (DatePoint) obj;
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 11 * hash + (this.date != null ? this.date.hashCode() : 0);
        return hash;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

}

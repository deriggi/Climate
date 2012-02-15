/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain.web;

import java.util.Date;

/**
 *
 * @author wb385924
 */
public class MinimizedData {

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(getDate());
            sb.append(" ");
            sb.append(getValue());

            return sb.toString();
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public MinimizedData(double value, Date date) {
            this.value = value;
            this.date = date;
        }
        private double value;
        private Date date;
    }

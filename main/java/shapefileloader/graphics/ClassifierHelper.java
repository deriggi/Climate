/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.graphics;

/**
 *
 * @author wb385924
 */
public class ClassifierHelper {
    public static void main(String[] args){
        double[][] breaks = ClassifierHelper.getEqualIntervalBounds(0, 30, 10);
        for(int i = 0; i < breaks.length; i++){
            System.out.println(breaks[i][0] + " " + breaks[i][1] );
        }
        
        System.out.println(" -1 : " + ClassifierHelper.getClass(-1, breaks)[0][0]);
        System.out.println(" 1 : " + ClassifierHelper.getClass(1, breaks)[0][0]);
        System.out.println(" 6 : " + ClassifierHelper.getClass(6, breaks)[0][0]);
        System.out.println(" 9 : " + ClassifierHelper.getClass(9, breaks)[0][0]);
        System.out.println(" 10 : " + ClassifierHelper.getClass(10, breaks)[0][0]);
        System.out.println(" 12 : " + ClassifierHelper.getClass(12, breaks)[0][0]);
        System.out.println(" 100 : " + ClassifierHelper.getClass(100, breaks)[0][0]);
        System.out.println(" 101 : " + ClassifierHelper.getClass(101, breaks)[0][0]);


    }

     public static double[][] getEqualIntervalBounds(double min, double max, int numClasses) {
        double[][] bounds = new double[new Double(numClasses).intValue()][2];
        double width = (max - min) / numClasses;
        for (int i = 0; i < numClasses; i++) {
            bounds[i][0] = min + (width * i);
            
            bounds[i][1] = bounds[i][0] + width;
        }

        return bounds;
    }

    /**
     * returns class [index] , [out of ]
     * @param val
     * @param bounds
     * @return
     */
    public static int[][] getClass(double val, double[][] bounds) {
        int[][] classOutOf = new int[1][2];
        int classIndex = 0;
        boolean isInRange = false;
        for (double[] classBounds : bounds) {
            double min = classBounds[0];
            double max = classBounds[1];
            if( (classIndex == bounds.length-1) && (val >= min) && (val <= max) ){
                classOutOf[0][0] = classIndex;
                classOutOf[0][1] = bounds.length;
                isInRange = true;
            }
            else if((val >= min) && (val < max)) {
                classOutOf[0][0] = classIndex;
                classOutOf[0][1] = bounds.length;
                isInRange = true;
            }
            classIndex++;
        }
        if (!isInRange) {
            classOutOf[0][0] = -1;
            classOutOf[0][1] = bounds.length;
        }

        return classOutOf;
    }

}

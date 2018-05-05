package ui;


import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class AppUITest {

    private String mockTextArea;
    private String mockTextArea2;
    private Map<String, String> dataLabels;
    private Map<String, Point2D> dataPoints;
    String tsdString;
    private int testMaxIt;
    private int testUpIntervals;
    private int testClusters;

    @Before
    public void initialize(){
        mockTextArea = ("@c    label1  2,2 \n");
        mockTextArea2 = ("@a    label1  2,2 \n@b    label3  9,8\n");
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();

        testMaxIt = 0;
        testUpIntervals = 0;
        testClusters=0;

    }


    @Test(expected = NullPointerException.class)
    public void testSaveDataWhenFileDoesNotExists(){
        /**This test tests for saving data to a file when a file does not exist or when the file is null
         * which would cause a NullPointerException**/
        File file = null;
        try{
            FileWriter fw = new FileWriter(file);
            fw.write(mockTextArea);
            fw.close();
        }catch (IOException e){
            System.out.println("File does not exist");
        }
    }

    @Test
    public void testSaveDataToNewFile(){
        /**This test tests saving data to a new file for the first time
         * String fileName represents the name of the file that user chooses after choosing a name and location
         * for the file
         * The tests passes if the content of the new file created matches the mockTextArea which
         * represents the input in the text area**/
        String fileName = "testing1.tsd";
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(mockTextArea);
            fw.close();

            List<String> list = new ArrayList<>();
            BufferedReader fileReader = new BufferedReader(new FileReader("testing1.tsd"));
            String s;
            while((s=fileReader.readLine()) != null){
                list.add(s);
            }
            fileReader.close();
            String fileInput="";
            for (String aList : list) {
                fileInput += aList;
                fileInput += "\n";
            }

            assertEquals(mockTextArea,fileInput);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testSavingDataToExistingFile() throws FileNotFoundException{
        File file = new File("testing.tsd");

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(mockTextArea2);
            fw.close();

            List<String> list = new ArrayList<>();
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String s;
            while((s=fileReader.readLine()) != null){
                list.add(s);
            }
            fileReader.close();
            String fileInput="";
            for (String aList : list) {
                fileInput += aList;
                fileInput += "\n";
            }

            assertEquals(mockTextArea2,fileInput);
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    @Test
    public void testParsingInputData(){
        /**This test tests for parsing an TSD string from a TSD file
         * if pass test it means that the hashmaps dataLabels and dataPoints contains the name, label and point
         * of the input TSD string*
         * tsdString is the mock input in this test case, tsdString represents the input from TSD file
         * that has one input TSD string
         * Note that the parsing method is taken from TSDProcessor processString(TSDString tsdString)*/
        tsdString="@a\tlabel1\t1,1\n";

        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    String name = list.get(0);
                    String label = list.get(1);
                    String[] pair = list.get(2).split(",");
                    Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));

                    dataLabels.put(name, label);
                    dataPoints.put(name, point);
                });
        assertEquals(true,dataLabels.containsKey("@a"));
        assertEquals(true,dataLabels.containsValue("label1"));
        assertEquals(true,dataPoints.containsKey("@a"));
        assertEquals(true,dataPoints.containsValue(new Point2D(1,1)));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testParsingTSDFileEmpty(){
        /**This test tests for parsing an TSD string when the string is empty
         * which would return an ArrayIndexOutOfBoundsException*
         * tsdString is the mock input in this test case, tsdString represents the input from an empty TSD file
         * Note that the parsing method is taken from TSDProcessor processString(TSDString tsdString)*/
        tsdString = "";
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    String name = list.get(0);
                    String label = list.get(1);
                    String[] pair = list.get(2).split(",");
                    Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));

                    dataLabels.put(name, label);
                    dataPoints.put(name, point);
                });
    }

    @Test
    public void testInputValuesForClassifcationCorrectValues(){
        /**This test tests for the input values to be correct values meaning the values are positive integers
         * If both input values maxIt =  maximumIterations and upIntervals = update Intervals
         * are positive integers then the values are valid
         * In this test case testMaxIt and testUpIntervals represent the input values that are to be passed
         * to the classifier algorithm, if this test passes it means that testMaxIt and testUpIntervals
         * are positive integers and can be passed to the algorithm**/
        testMaxIt = Integer.MAX_VALUE;
        testUpIntervals = Integer.MAX_VALUE;

        String maxIt = "50";
        String upIntervals = "23";

        int maxIterations = Integer.parseInt(maxIt);
        int intervals = Integer.parseInt(upIntervals);

        if(maxIterations > 0 && intervals >0){
            testMaxIt = maxIterations;
            testUpIntervals = intervals;
        }

        assertEquals(maxIterations,testMaxIt);
        assertEquals(intervals,testUpIntervals);
    }

    @Test
    public void testInputValuesForClassificationForPosIntegers(){
        /**This test tests for the input values for positive integers
         * If any one of the input values maxIt =  maximumIterations or upIntervals = update Intervals
         * is negative or zero then the values are not valid
         * In this test case testMaxIt and testUpIntervals represent the input values that are to be passed
         * to the classifier algorithm, if this test passes it means that testMaxIt and testUpIntervals
         * cannot be a negative or zero value**/
        testMaxIt = Integer.MAX_VALUE;
        testUpIntervals = Integer.MAX_VALUE;

        String maxIt = "-1";
        String upIntervals = "3";

//        String maxIt = "1";
//        String upIntervals = "-3";

//        String maxIt = "0";
//        String upIntervals = "3";

        int maxIterations = Integer.parseInt(maxIt);
        int intervals = Integer.parseInt(upIntervals);

        if(maxIterations > 0 && intervals >0){
            testMaxIt = maxIterations;
            testUpIntervals = intervals;
        }

        assertEquals(Integer.MAX_VALUE,testMaxIt);
        assertEquals(Integer.MAX_VALUE,testUpIntervals);

    }

    @Test(expected = NumberFormatException.class)
    public void testInputValuesForClassificationForIntegers() {
        /**This test tests for the input values for integers (no decimals, no commas, no letters....)
         * If any one of the input values maxIt =  maximumIterations or upIntervals = update Intervals
         * is a decimal or contais a comma, letter, space.... then the values are not valid
         * In this test case testMaxIt and testUpIntervals represent the input values that are to be passed
         * to the classifier algorithm, if this test passes it means that testMaxIt and testUpIntervals
         * are not integers values which causes a NumberFormatException**/
        String maxIt = "a";
        String upIntervals = "3.8";

        int maxIterations = Integer.parseInt(maxIt);
        int intervals = Integer.parseInt(upIntervals);

        if(maxIterations > 0 && intervals >0){
            testMaxIt = maxIterations;
            testUpIntervals = intervals;
        }
    }

    @Test
    public void testInputValuesForClusteringCorrectValues(){
        /**This test tests for the input values to be correct values meaning the values are positive integers
         * If both input values maxIt =  maximumIterations and upIntervals = update Intervals and clusters = numOfClusters
         * are positive integers then the values are valid
         * In this test case testMaxIt, testUpIntervals and testClusters represent the input values that are to be passed
         * to the clusterer algorithm, if this test passes it means that testMaxIt, testUpIntervals and testClusters
         * are positive integers and can be passed to the algorithm**/
        testMaxIt = Integer.MAX_VALUE;
        testUpIntervals = Integer.MAX_VALUE;
        testClusters = Integer.MAX_VALUE;

        String maxIt = "50";
        String upIntervals = "23";
        String clusters = "10";

        int maxIterations = Integer.parseInt(maxIt);
        int intervals = Integer.parseInt(upIntervals);
        int numClusters = Integer.parseInt(clusters);

        if(maxIterations > 0 && intervals >0){
            testMaxIt = maxIterations;
            testUpIntervals = intervals;
            testClusters = numClusters;
        }

        assertEquals(maxIterations,testMaxIt);
        assertEquals(intervals,testUpIntervals);
        assertEquals(numClusters,testClusters);
    }

    @Test
    public void testInputValuesForClusteringForPosIntegers() {
        /**This test tests for the input values for positive integers
         * If any one of the input values maxIt =  maximumIterations or upIntervals = update Intervals or
         * clusters = numOfClusters is negative or zero then the values are not valid
         * In this test case testMaxIt, testUpIntervals and testClusters represent the input values that are to be passed
         * to the clusterer algorithm, if this test passes it means that testMaxIt, testUpIntervals and testClusters
         * cannot be a negative or zero value**/
        testMaxIt = Integer.MAX_VALUE;
        testUpIntervals = Integer.MAX_VALUE;
        testClusters = Integer.MAX_VALUE;

        String maxIt = "-1";
        String upIntervals = "3";
        String clusters = "0";

//        String maxIt = "1";
//        String upIntervals = "-3";
//        String clusters = "-2";

//        String maxIt = "0";
//        String upIntervals = "3";
//        String clusters = "-2";

        int maxIterations = Integer.parseInt(maxIt);
        int intervals = Integer.parseInt(upIntervals);
        int numClusters = Integer.parseInt(clusters);

        if(maxIterations > 0 && intervals >0){
            testMaxIt = maxIterations;
            testUpIntervals = intervals;
            testClusters = numClusters;
        }

        assertEquals(Integer.MAX_VALUE,testMaxIt);
        assertEquals(Integer.MAX_VALUE,testUpIntervals);
        assertEquals(Integer.MAX_VALUE,testClusters);
    }

    @Test(expected = NumberFormatException.class)
    public void testInputValuesForClustererForIntegers() {
        /**This test tests for the input values for integers (no decimals, no commas, no letters....)
         * If any one of the input values maxIt =  maximumIterations or upIntervals = update Intervals
         * or clusters = numOfClusters is a decimal or contains a comma, letter, space....
         * then the values are not valid
         * In this test case testMaxIt, testUpIntervals and testClusters represent the input values that are to be passed
         * to the clusterer algorithm, if this test passes it means that testMaxIt, testUpIntervals or testClusters
         * are not integers values which causes a NumberFormatException**/
        String maxIt = "a";
        String upIntervals = "3.8";
        String clusters = " ";

        int maxIterations = Integer.parseInt(maxIt);
        int intervals = Integer.parseInt(upIntervals);
        int numClusters = Integer.parseInt(clusters);

        if(maxIterations > 0 && intervals >0){
            testMaxIt = maxIterations;
            testUpIntervals = intervals;
            testClusters = numClusters;
        }
    }

}
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
    private Map<String, String> dataLabels;
    private Map<String, Point2D> dataPoints;
    String tsdString;

    @Before
    public void initialize(){
        mockTextArea = ("@c    label1  2,2 \n");
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();

    }

    @Test
    public void testSaveDataWhenFirstTimeSavingFile() {
        //testing saveData(Path fileDataPath from AppData) when first time saving file to a certain location
        File file = null;
        String fileName = "testing.tsd";

        if(file != null){
            try {
                FileWriter fw = new FileWriter(file);
                String input = mockTextArea;
                fw.write(input);
                fw.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }else {
            try {
                file = new File(fileName);
                FileWriter fw = new FileWriter(file);
                String input = mockTextArea;
                fw.write(input);
                fw.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testSaveDataWhenFileDoesNotExists(){
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
    public void testParsingInputData() throws FileNotFoundException{
        List<String> temp = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(new FileReader("sample-data.tsd"));
        String s;
        try {
            while((s=fileReader.readLine()) != null){
                temp.add(s);
            }
            fileReader.close();
            tsdString="";
            for (String aList : temp) {
                tsdString += aList;
                tsdString += "\n";
            }
        }catch (IOException e){
            e.printStackTrace();
        }

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
    public void testSaveDataToNewFile(){
        try {
            FileWriter fw = new FileWriter("testing1.tsd");
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
}
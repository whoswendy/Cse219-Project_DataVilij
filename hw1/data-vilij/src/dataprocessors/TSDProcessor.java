package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Line;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;
    private int lineNumber = 0;
    private double average;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        lineNumber = 0;
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();

        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      lineAdd();
                      String   name  = checkedname(list.get(0),0);
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      if(!dataLabels.containsKey(name)){
                          dataLabels.put(name, label);
                          dataPoints.put(name, point);
                      }else{
                            throw new Exception("Duplicated name: " + name);
                      }

                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0) {
            throw new Exception(errorMessage.toString() + " at line " + lineNumber);
        }
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
     void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                XYChart.Data p = new XYChart.Data<>(point.getX(), point.getY());
                p.setExtraValue(entry);
                //System.out.println(p.getExtraValue());
                series.getData().add(p);
                average += point.getY();
            });
            chart.getData().add(series);
        }

        average = average / dataLabels.size();
        System.out.println("average = " + average);
    }


    void clear() {
        dataPoints.clear();
        dataLabels.clear();
        average = 0;
    }

    private String checkedname(String name, int i) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
            //throw new InvalidDataNameException(name + " line " + i+1);
        return name;
    }

    private void lineAdd(){
        lineNumber++;
    }

    public double getAverage() {
        return average;
    }
}

package dataprocessors;

import actions.AppActions;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import static vilij.settings.PropertyTypes.*;
import static settings.AppPropertyTypes.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;


/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    private int lineNumber;
    private XYChart.Series line;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
        AppActions appActions = (AppActions)(applicationTemplate.getActionComponent());
        String fileInput = appActions.getFileInput();
        String[] list = fileInput.split("\n");
        lineNumber =0;

        if(list.length > 10){
            String temp = "";
            while(lineNumber<10){
                temp+= list[lineNumber];
                if(lineNumber != 9) {
                    temp += "\n";
                }
                lineNumber++;
            }

            uiActions(fileInput,dataFilePath.toString(),temp);
            processor.clear();
            //loadData(fileInput);
            if(ui.getChartUpdated()) ui.getScrnshotButton().setDisable(false);
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(
                    applicationTemplate.manager.getPropertyValue(TITLE_FOR_GREATER_THAN_TEN_LINES.name()),
                    applicationTemplate.manager.getPropertyValue(MESSAGE_PT_1.name())
                            + list.length +
                            applicationTemplate.manager.getPropertyValue(MESSAGE_PT_2.name()));

            ui.getTextArea().textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                String[] tempArr = newValue.split("\n");
                int num = 10 - tempArr.length;
                    if(tempArr.length < 10 && lineNumber < list.length && !appActions.getFileInput().equals("")){
                        changeText(list,ui.getTextArea(),num);
                    }
            });
        }else{
            uiActions(fileInput,dataFilePath.toString(),fileInput);
            processor.clear();
            //loadData(fileInput);
            if(ui.getChartUpdated()) ui.getScrnshotButton().setDisable(false);
        }

    }

    private void uiActions(String fileInput, String dataFile, String text){
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
        ui.setTextArea(false);
        ui.getTextArea().setText(text);
        ui.getSave().setDisable(true);
        ui.setLoadedData(fileInput,dataFile);
        ui.showAlgorithmTypes();
    }

    private void changeText(String[] list, TextArea textArea, int num){
        String text = textArea.getText();
        for(int i = 0 ; i< num; i++){
            if(lineNumber < list.length){
                text += list[lineNumber];
                text += "\n";
                lineNumber++;
            }else{
                break;
            }
        }
        textArea.setText(text);
    }

    @Override
    public void loadData(String dataString) {
        // TODO for homework 1
        try {
            AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
            clear();
            processor.processString(dataString);
            displayData();
            ui.installToolTips();
            ui.setChartUpdated(true);
            //createLine();
        } catch (Exception e) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.
                    getPropertyValue(EXCEPTION_LABEL.name()),e.getMessage());
        }
    }

    public void createLine(int xCo, int yCo, int constant, Point2D min, Point2D max){
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());

        if(ui.getChart().getData().contains(line)) ui.getChart().getData().remove(line);
        line = new XYChart.Series();
        line.setName("line");

        double x1 = min.getX();
        double y1 = Math.abs(((-1 * xCo * x1) + constant)/yCo);

        double x2 = max.getX();
        double y2 = Math.abs(((-1 * xCo * x2) + constant)/yCo);

        XYChart.Data<Number,Number> point1 = new XYChart.Data<>(x1, y1);
        XYChart.Data<Number,Number> point2 = new XYChart.Data<>(x2, y2);
        line.getData().add(point1);
        line.getData().add(point2);

        System.out.println(point1.toString() + " " + point2.toString());
        ui.getChart().getData().add(line);
        //ui.getChart().getStyleClass().add(applicationTemplate.manager.getPropertyValue(AVERAGE_LINE.name()));

        int i = 0;
        for(XYChart.Series series: ui.getChart().getData()){
            if(series.equals(line)){
                javafx.scene.Node temp = ui.getChart().lookup(".default-color"+i+".chart-series-line");
                temp.setStyle("-fx-stroke: green");
            }else{
                i++;
            }
        }

        point1.getNode().setVisible(false);
        point2.getNode().setVisible(false);


    }
    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        System.out.println("saving");
        File file = dataFilePath.toFile();
        if(file != null){
            try {
                FileWriter fw = new FileWriter(file);
                System.out.println(file.getName());
                AppUI ui = (AppUI) (applicationTemplate.getUIComponent());
                TextArea input = ui.getTextArea();
                fw.write(input.getText());
                fw.close();
            }
            catch (Exception e){
            }
        }
    }

    @Override
    public void clear() {
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
        processor.clear();
        ui.getChart().getData().clear();
    }


    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }

    public boolean processData(String data){
        try {
            processor.clear();
            processor.processString(data);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show("Exception",e.getMessage());
            processor.clear();
            return false;
        }

        return true;
    }

    public boolean processLoadData(String data, String fileName){
        try {
            processor.processString(data);
        } catch (Exception e) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(LOAD_ERROR_MSG.name()) + fileName + " "
                            + e.getMessage());
            processor.clear();
            applicationTemplate.getUIComponent().clear();
            return false;
        }

        return true;
    }
}

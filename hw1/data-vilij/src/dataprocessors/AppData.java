package dataprocessors;

import actions.AppActions;
import javafx.beans.value.ObservableValue;
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
            ui.setTextArea(false);
            ui.getTextArea().setText(temp);
            //processor.clear();
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
            ui.setTextArea(false);
            ui.getTextArea().setText(fileInput);
            //processor.clear();
            //loadData(fileInput);
            if(ui.getChartUpdated()) ui.getScrnshotButton().setDisable(false);
        }

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
            processor.processString(dataString);
            displayData();
            ui.installToolTips();
            ui.setChartUpdated(true);
            createLine();
        } catch (Exception e) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(applicationTemplate.manager.
                    getPropertyValue(EXCEPTION_LABEL.name()),e.getMessage());
        }
    }

    private void createLine(){
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());

        XYChart.Series line = new XYChart.Series();
        line.setName(applicationTemplate.manager.getPropertyValue(AVERAGE_LABEL.name()));

        XYChart.Data<Number,Number> point1 = new XYChart.Data<>(0, processor.getAverage());
        XYChart.Data<Number,Number> point2 = new XYChart.Data<>(20, processor.getAverage());
        line.getData().add(point1);
        line.getData().add(point2);

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
        //ui.getChart().getData().clear();
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
            //applicationTemplate.getUIComponent().clear();
            return false;
        }

        return true;
    }
}

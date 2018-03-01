package dataprocessors;

import actions.AppActions;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;
import static vilij.settings.PropertyTypes.*;
import static settings.AppPropertyTypes.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
        AppActions appActions = (AppActions)(applicationTemplate.getActionComponent());
        String fileInput = appActions.getFileInput();
        String[] list = fileInput.split("\n");
        if(list.length > 10){
            String temp = "";
            for(int i = 0; i< 10; i++ ){
                temp+= list[i];
                temp+= "\n";
            }
            ui.getTextArea().setText(temp);
            processor.clear();
            loadData(fileInput);
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show("Showing only 10 lines",
                    "Loaded data consists of " + list.length + "lines. Showing only the first 10 in the text area.");

        }else{
            ui.getTextArea().setText(fileInput);
            processor.clear();
            loadData(fileInput);
        }

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
            //createLine();
        } catch (Exception e) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show("Exception",e.getMessage());
        }
    }

    private void createLine(){
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
        int i = ui.getChart().getData().size();
        XYChart.Series line = new XYChart.Series();
        line.getData().add(new XYChart.Data<>(0,processor.getAverage()));
        line.getData().add(new XYChart.Data<>(10,processor.getAverage()));

        ui.getChart().getData().add(line);
        //need to figure out how to make line

    }
    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
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
            //applicationTemplate.getUIComponent().clear();
            return false;
        }

        return true;
    }
}

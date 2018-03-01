package ui;

import actions.AppActions;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.*;
import static vilij.settings.PropertyTypes.EXIT_TOOLTIP;


/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    private static final String SEPARATOR = "/";

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    //private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private LineChart<Number,Number>        chart;
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private boolean                      chartUpdated;
    private CheckBox                     checkBox;
    private boolean                      checked;
    private String                       iconsPath;
    private String                       scrnshoticonPath;

    //public ScatterChart<Number, Number> getChart() { return chart; }
    public LineChart<Number,Number> getChart(){return chart;}

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        newButton = setToolbarButton(newiconPath, manager.getPropertyValue(NEW_TOOLTIP.name()), true);
        saveButton = setToolbarButton(saveiconPath, manager.getPropertyValue(SAVE_TOOLTIP.name()), true);
        loadButton = setToolbarButton(loadiconPath, manager.getPropertyValue(LOAD_TOOLTIP.name()), false);
        printButton = setToolbarButton(printiconPath, manager.getPropertyValue(PRINT_TOOLTIP.name()), false);
        exitButton = setToolbarButton(exiticonPath, manager.getPropertyValue(EXIT_TOOLTIP.name()), false);

        cssPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(CSS_PATH.name()),
                manager.getPropertyValue(CSS_FILE_PATH.name()));



        iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()) + SEPARATOR + manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshoticonPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));

        scrnshotButton = setToolbarButton(scrnshoticonPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);

        toolBar = new ToolBar(newButton, saveButton, loadButton, printButton, exitButton,scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());

        scrnshotButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                try{
                    applicationTemplate.getActionComponent().handleScreenshotRequest();
                }catch (IOException ie){
                    ie.printStackTrace();
                }

            }
        });

    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        textArea.clear();
        chart.getData().clear();
        applicationTemplate.getDataComponent().clear();
        newButton.setDisable(true);
        saveButton.setDisable(true);
        setChartUpdated(false);
        scrnshotButton.setDisable(true);

    }

    private void layout() {
        // TODO for homework 1
        StackPane stackPane = new StackPane();

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("X Values");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Y Values");

        //chart = new ScatterChart<>(xAxis,yAxis);
        chart = new LineChart<>(xAxis,yAxis);

        chart.setAlternativeRowFillVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);

        chart.getStyleClass().addAll("chart-plot-background","chart-background","axis","axis-tick-mark",
                "axis-minor-tick-mark");


        stackPane.getChildren().addAll(chart);

        //StackPane ends here

        BorderPane borderPane = new BorderPane();

        textArea = new TextArea();
        double HEIGHT = windowHeight / 4;
        System.out.println("Window Height = " + windowHeight);
        double WIDTH = HEIGHT*2 ;
        System.out.println("Window Width = " + windowWidth);
        textArea.setMaxHeight(HEIGHT);
        textArea.setMaxWidth(WIDTH);

        displayButton = new Button("Display");
        checkBox = new CheckBox("Read Only");
        checkBox.setSelected(false);

        borderPane.setLeft(textArea);
        borderPane.setBottom(displayButton);
        borderPane.setCenter(checkBox);

        //BorderPane ends here

        appPane.getChildren().addAll(stackPane,borderPane);
        primaryScene.getStylesheets().add(getClass().getResource(cssPath).toString());

    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.equals(oldValue)){
                    if(!newValue.isEmpty()){
                        if (newValue.charAt(newValue.length() - 1) == '\n')
                            hasNewText = true;
                        newButton.setDisable(false);
                        saveButton.setDisable(false);
                    }else{
                        hasNewText = true;
                        newButton.setDisable(true);
                        saveButton.setDisable(true);
                    }
                }
//                if(!newValue.equals("") && !hasNewText){
//                    newButton.setDisable(false);
//                    saveButton.setDisable(false);
//                    hasNewText = true;
//                    //System.out.println(oldValue);
//                    //System.out.println(newValue);
//                }else if(newValue.equals("") || oldValue.equals("")){
//                    newButton.setDisable(true);
//                    saveButton.setDisable(true);
//                    hasNewText = false;
//                }
            }
        });
        displayButton.setOnAction(ActionEvent->{
            String inputData = textArea.getText();
            DataComponent dataComponent= this.applicationTemplate.getDataComponent();
            dataComponent.clear();
            dataComponent.loadData(inputData);

            if(chartUpdated)scrnshotButton.setDisable(false);

        });

        checkBox.setOnAction(ActionEvent->{
            checked = !checked;
            checkBox.setSelected(checked);
            if(checkBox.isSelected()){
                textArea.setEditable(false);
                textArea.getStyleClass().add("text-area:readonly");
                checkBox.setText(applicationTemplate.manager.getPropertyValue(READ_ONLY_ON.name()));
            }else{
                textArea.setEditable(true);
                checkBox.setText(applicationTemplate.manager.getPropertyValue(READ_ONLY_OFF.name()));
            }

        });
    }

    public boolean getHasNewText(){
        return hasNewText;
    }
    public TextArea getTextArea(){
        return textArea;
    }

    public Button getSave(){
        return saveButton;
    }

    public void setChartUpdated(boolean b){
        chartUpdated = b;
    }

    public boolean getChartUpdated(){
        return chartUpdated;
    }

    public void installToolTips(){
        for (final XYChart.Series<Number, Number> s : chart.getData()) {
            for (final XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip t = new Tooltip();
                t.setText("" + d.getExtraValue());
                Tooltip.install(d.getNode(),t);
                d.getNode().setOnMouseEntered(event -> t.isActivated());
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));


            }
        }
    }


}

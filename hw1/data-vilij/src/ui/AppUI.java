package ui;

import actions.AppActions;

import dataprocessors.AppData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vilij.components.DataComponent;

import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


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
    private Button                       toggleButton;//to indicate done or not creating new data
    private boolean                      toggle;
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private boolean                      chartUpdated;
    private String                       iconsPath;
    private String                       scrnshoticonPath;
    private BorderPane                  borderPane;
    private VBox                        vBox;
    private VBox                        vBox2;
    private Label                       label = new Label();

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
        newButton = setToolbarButton(newiconPath, manager.getPropertyValue(NEW_TOOLTIP.name()), false);
        saveButton = setToolbarButton(saveiconPath, manager.getPropertyValue(SAVE_TOOLTIP.name()), true);
        loadButton = setToolbarButton(loadiconPath, manager.getPropertyValue(LOAD_TOOLTIP.name()), false);
        //printButton = setToolbarButton(printiconPath, manager.getPropertyValue(PRINT_TOOLTIP.name()), false);
        exitButton = setToolbarButton(exiticonPath, manager.getPropertyValue(EXIT_TOOLTIP.name()), false);

        cssPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(CSS_PATH.name()),
                manager.getPropertyValue(CSS_FILE_PATH.name()));



        iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()) + SEPARATOR + manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshoticonPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));

        scrnshotButton = setToolbarButton(scrnshoticonPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);

        toolBar = new ToolBar(newButton, saveButton, loadButton, exitButton,scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        //printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());

        scrnshotButton.setOnAction(event -> {
            try{
                applicationTemplate.getActionComponent().handleScreenshotRequest();
            }catch (IOException ie){
                ie.printStackTrace();
            }

        });

    }

    @Override
    public void initialize() {
        layout();
        //setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        //chart.getData().clear();
        applicationTemplate.getDataComponent().clear();
        newButton.setDisable(false);
        saveButton.setDisable(true);
        setChartUpdated(false);
        scrnshotButton.setDisable(true);
        AppActions appActions = (AppActions) (applicationTemplate.getActionComponent());
        appActions.clearFileInput();
        textArea.setEditable(true);
        textArea.clear();
    }

    private void layout() {
//        StackPane stackPane = new StackPane();
//
//        NumberAxis xAxis = new NumberAxis();
//        xAxis.setLabel("X Values");
//
//        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("Y Values");
//
//        chart = new LineChart<>(xAxis,yAxis);
//
//        chart.setAlternativeRowFillVisible(false);
//        chart.setAlternativeColumnFillVisible(false);
//        chart.setHorizontalGridLinesVisible(false);
//        chart.setVerticalGridLinesVisible(false);
//
//        chart.getStyleClass().addAll(applicationTemplate.manager.getPropertyValue(CHART_PLOT_BACKGROUND.name()),
//                applicationTemplate.manager.getPropertyValue(CHART_BACKGROUND.name()),
//                applicationTemplate.manager.getPropertyValue(AXIS.name()),
//                applicationTemplate.manager.getPropertyValue(AXIS_TICK_MARK.name()),
//                applicationTemplate.manager.getPropertyValue(AXIS_MINOR_TICK_MARK.name()));
//
//
//        stackPane.getChildren().addAll(chart);
//
//        //StackPane ends here
//        appPane.getChildren().addAll(stackPane);
//        primaryScene.getStylesheets().add(getClass().getResource(cssPath).toString());

        borderPane = new BorderPane();
        vBox = new VBox();

        Label label = new Label();
        label.setText("                Plot");
        label.setFont(Font.font(30));
        Rectangle rectangle = new Rectangle();
        rectangle.setHeight(400);
        rectangle.setWidth(400);
        vBox.getChildren().addAll(label,rectangle);
        borderPane.setRight(vBox);
        appPane.getChildren().add(borderPane);

    }

    private void setWorkspaceActions() {
        AppData appData = (AppData) (applicationTemplate.getDataComponent());

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (!newValue.isEmpty()) {
                    if (newValue.charAt(newValue.length() - 1) == '\n')
                        hasNewText = true;
                    newButton.setDisable(false);
                    saveButton.setDisable(false);
                } else {
                    hasNewText = true;
                    newButton.setDisable(true);
                    saveButton.setDisable(true);
                }
            }
        });

        toggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toggle = !toggle;
                if(toggle){
                    if(appData.processData(textArea.getText())) {
                        toggleButton.setText("Edit Data?");
                        textArea.setEditable(false);
                        textArea.getStyleClass().add("text-area:readonly");
                        getTextInfo();
                    }else
                        toggle = !toggle;
                }else
                {
                    toggleButton.setText("Done with creating data?");
                    textArea.setEditable(true);
                }

            }
        });

    }

    public void setTextArea(boolean enabled){
        vBox2 = new VBox();
        textArea = new TextArea();
        double HEIGHT = windowHeight / 4;
        System.out.println("Window Height = " + windowHeight);
        double WIDTH = HEIGHT * 2;
        System.out.println("Window Width = " + windowWidth);
        textArea.setMaxHeight(HEIGHT);
        textArea.setMaxWidth(WIDTH);
        vBox2.getChildren().add(textArea);

        if(enabled){
            toggleButton = new Button();
            toggleButton.setText("Done with creating data");
            toggle = false;
            vBox2.getChildren().add(toggleButton);
            setWorkspaceActions();
        }else{
            textArea.setEditable(false);
        }
        borderPane.setLeft(vBox2);
    }

    public void getTextInfo(){
        String text = textArea.getText();
        if(vBox2.getChildren().contains(label)) vBox2.getChildren().remove(label);
        if(!text.equals("")) {
            String[] lines = text.split("\n");
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            for (String line : lines) {
                String[] temp = line.split("\t");
                if (!names.contains(temp[0])) names.add(temp[0]);
                if (!labels.contains(temp[1])) labels.add(temp[1]);
            }
            String unfinishedData = lines.length + " instances with " + labels.size() + " labels. " +
                    "The labels are: \n";
            for (String label : labels) {
                unfinishedData += label + "\n";
            }
            label.setText(unfinishedData);
            vBox2.getChildren().add(label);
        }
    }

    public void setLoadedData(String loadedData, String fileName){
        if(vBox2.getChildren().contains(label)) vBox2.getChildren().remove(label);
        if(!loadedData.equals("")) {
            String[] lines = loadedData.split("\n");
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            for (String line : lines) {
                String[] temp = line.split("\t");
                if (!names.contains(temp[0])) names.add(temp[0]);
                if (!labels.contains(temp[1])) labels.add(temp[1]);
            }
            String unfinishedData = lines.length + " instances with " + labels.size() + " labels. " +
                    "The data is loaded from " + fileName +
                    "The labels are: \n";
            for (String label : labels) {
                unfinishedData += label + "\n";
            }
            label.setText(unfinishedData);
            vBox2.getChildren().add(label);
        }
    }

    public boolean getHasNewText(){
        return hasNewText;
    }
    public void setHasNewText(boolean b){
        hasNewText = b;
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

    public Button getScrnshotButton(){
        return scrnshotButton;
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

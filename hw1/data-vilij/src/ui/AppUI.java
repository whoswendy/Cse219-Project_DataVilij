package ui;

import actions.AppActions;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import vilij.components.DataComponent;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import static settings.AppPropertyTypes.SCREENSHOT_ICON;
import static settings.AppPropertyTypes.SCREENSHOT_TOOLTIP;

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
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display

    public ScatterChart<Number, Number> getChart() { return chart; }

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

        PropertyManager manager1 = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()) + SEPARATOR + manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String scrnshoticonPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
        System.out.println(scrnshoticonPath);

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

    }

    private void layout() {
        // TODO for homework 1
        StackPane stackPane = new StackPane();

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("X Values");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Y Values");


        chart = new ScatterChart<>(xAxis,yAxis);

        stackPane.getChildren().add(chart);

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

        //borderPane.getChildren().add(textBox); CAUSES AN ERROR DONT KNOW WHY
        borderPane.setLeft(textArea);
        borderPane.setBottom(displayButton);

        //BorderPane ends here

        appPane.getChildren().addAll(stackPane,borderPane);


    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                boolean changed = false;
                if(!newValue.equals("") && !hasNewText){
                    newButton.setDisable(false);
                    saveButton.setDisable(false);
                    hasNewText = true;
                    //System.out.println(oldValue);
                    //System.out.println(newValue);
                }else if(newValue.equals("") || oldValue.equals("")){
                    newButton.setDisable(true);
                    saveButton.setDisable(true);
                    hasNewText = false;
                }
            }
        });
        displayButton.setOnAction(ActionEvent->{
            String inputData = textArea.getText();
            DataComponent dataComponent= this.applicationTemplate.getDataComponent();
            dataComponent.loadData(inputData);


        });


    }

    public boolean getHasNewText(){
        return hasNewText;
    }
    public TextArea getTextArea(){
        return textArea;
    }
}

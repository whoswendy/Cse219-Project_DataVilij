package ui;

import actions.AppActions;

import algorithm.*;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


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
    private static final String CLUSTERING = "Clustering";
    private static final String CLASSIFICATION = "Classification";

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
    private MenuButton                  algorithmMenu;
    private int                         numLabels;
    private MenuItem                    classification;
    private MenuItem                    clustering;
    private String                      algorithmType;
    private HBox                        hBox = new HBox();
    private String                      algorithm;
    private Button                      runButton;
    private boolean                     isConfigured;
    private boolean                     classificationContinuousRun;
    private boolean                     clusteringContinuousRun;
    private int                         classificationMaximumIterations;
    private int                         classificationIntervals;
    private int                         clusteringnMaximumIterations;
    private int                         clusteringIntervals;
    private int                         clusteringNumberOfClusters;
    private boolean                     isRunning;
    private boolean                     dataLoaded;
    private Button                         next;
    private boolean                     paused;
    private  int                        clicked;


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
        chart.getData().clear();
        applicationTemplate.getDataComponent().clear();
        newButton.setDisable(false);
        saveButton.setDisable(true);
        setChartUpdated(false);
        scrnshotButton.setDisable(true);
        AppActions appActions = (AppActions) (applicationTemplate.getActionComponent());
        appActions.clearFileInput();
        textArea.setEditable(true);
        textArea.clear();

        vBox = new VBox();
        if(toggle) toggle = !toggle;
        toggleButton.setText("Done with creating data?");

    }

    private void layout() {
        borderPane = new BorderPane();
        vBox = new VBox();

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("X Values");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Y Values");

        chart = new LineChart<>(xAxis,yAxis);

        chart.setAlternativeRowFillVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);

        chart.getStyleClass().addAll(applicationTemplate.manager.getPropertyValue(CHART_PLOT_BACKGROUND.name()),
                applicationTemplate.manager.getPropertyValue(CHART_BACKGROUND.name()),
                applicationTemplate.manager.getPropertyValue(AXIS.name()),
                applicationTemplate.manager.getPropertyValue(AXIS_TICK_MARK.name()),
                applicationTemplate.manager.getPropertyValue(AXIS_MINOR_TICK_MARK.name()));


        Label label = new Label();
        label.setText("                Plot");
        label.setFont(Font.font(30));

        vBox.getChildren().addAll(label,chart);
        borderPane.setRight(vBox);

        appPane.getChildren().add(borderPane);

        primaryScene.getStylesheets().add(getClass().getResource(cssPath).toString());

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

        toggleButton.setOnAction(event -> {
            toggle = !toggle;
            if(toggle){
                if(appData.processData(textArea.getText())) {
                    toggleButton.setText("Edit Data?");
                    textArea.setEditable(false);
                    textArea.getStyleClass().add("text-area:readonly");
                    if(vBox2.getChildren().contains(hBox)) vBox2.getChildren().remove(hBox);
                    if(vBox2.getChildren().contains(runButton)) vBox2.getChildren().remove(runButton);
                    getTextInfo();
                    showAlgorithmTypes();
                }else
                    toggle = !toggle;
            }else
            {
                toggleButton.setText("Done with creating data?");
                textArea.setEditable(true);
                if(vBox2.getChildren().contains(next)) vBox2.getChildren().remove(next);
            }

        });

    }

    public void setTextArea(boolean enabled){
        vBox2 = new VBox();
        vBox2.setSpacing(30);
        textArea = new TextArea();
        double HEIGHT = windowHeight / 4;
        double WIDTH = HEIGHT * 2;
        textArea.setMaxHeight(HEIGHT);
        textArea.setMaxWidth(WIDTH);
        vBox2.getChildren().add(textArea);

        if(enabled){
            toggleButton = new Button();
            toggleButton.setText("Done with creating data?");
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
                if (!labels.contains(temp[1]) && !temp[1].toLowerCase().equals("null")) labels.add(temp[1]);
            }
            String unfinishedData = lines.length + " instances with " + labels.size() + " labels. " +
                    "The labels are: \n";
            for (String label : labels) {
                unfinishedData += label + "\n";
            }
            label.setText(unfinishedData);
            vBox2.getChildren().add(label);
            numLabels =  labels.size();
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
                if (!labels.contains(temp[1]) && !temp[1].toLowerCase().equals("null")) labels.add(temp[1]);
            }
            String unfinishedData = lines.length + " instances with " + labels.size() + " labels. " +
                    "The data is loaded from " + fileName +
                    "The labels are: \n";
            for (String label : labels) {
                unfinishedData += label + "\n";
            }
            label.setText(unfinishedData);
            vBox2.getChildren().add(label);
            numLabels = labels.size();
            dataLoaded = true;
        }
    }

    public void showAlgorithmTypes(){
        if(vBox2.getChildren().contains(algorithmMenu)) {
            vBox2.getChildren().remove(algorithmMenu);
        }
        if(vBox2.getChildren().contains(next)) vBox2.getChildren().remove(next);

        classification = new MenuItem(CLASSIFICATION);
        clustering = new MenuItem(CLUSTERING);
        algorithmMenu = new MenuButton("Algorithm Types", null, classification, clustering);
        if (numLabels < 2) {
            classification.setDisable(true);
        }
        classification.setOnAction(event -> {
            if(vBox2.getChildren().contains(next)) vBox2.getChildren().remove(next);
            algorithmMenu.setText(classification.getText());
            algorithmType = classification.getText();
            createMenuOfAlgorithms(algorithmType);
        });
        clustering.setOnAction(event -> {
            if(vBox2.getChildren().contains(next)) vBox2.getChildren().remove(next);
            algorithmMenu.setText(clustering.getText());
            algorithmType = clustering.getText();
            createMenuOfAlgorithms(algorithmType);
        });
        vBox2.getChildren().add(algorithmMenu);
    }

    public void createMenuOfAlgorithms(String type){
        if(vBox2.getChildren().contains(hBox)) vBox2.getChildren().remove(hBox);
        ArrayList<RadioButton> radioButtons = new ArrayList<>();
        ArrayList<Button> configs = new ArrayList<>();
        if (type.equals(CLASSIFICATION)) {
            hBox = new HBox();
            hBox.setSpacing(20);
            ToggleGroup toggleGroup = new ToggleGroup();
            ArrayList<String> classificationAls = new ArrayList<>();
            try {
                Class aClass = Class.forName("settings.AppPropertyTypes");
                Field[] fields = aClass.getFields();
                for(Field f: fields){
                    if(f.getName().contains("CLASSIFICATION")) {
                        classificationAls.add(f.getName());
                        System.out.println(f.getName());
                    }
                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }

            for(String s: classificationAls){
                RadioButton classificationBtn = new RadioButton(applicationTemplate.manager.getPropertyValue(s));
                classificationBtn.setToggleGroup(toggleGroup);
                radioButtons.add(classificationBtn);
                Button config = new Button("config");
                configs.add(config);
                config.setDisable(true);

                classificationBtn.setOnAction(event -> {
                    classificationBtn.setSelected(true);
                    config.setDisable(false);
                    algorithm = classificationBtn.getText();
                });

            }
        } else {
            hBox = new HBox();
            ToggleGroup toggleGroup = new ToggleGroup();
            ArrayList<String> clusteringAl = new ArrayList<>();
            try {
                Class aClass = Class.forName("settings.AppPropertyTypes");
                Field[] fields = aClass.getFields();
                for(Field f: fields){
                    if(f.getName().contains("CLUSTERING")) {
                        clusteringAl.add(f.getName());
                        System.out.println(f.getName());
                    }
                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }

            for(String s: clusteringAl){
                RadioButton clusteringBtn = new RadioButton(applicationTemplate.manager.getPropertyValue(s));
                radioButtons.add(clusteringBtn);
                clusteringBtn.setToggleGroup(toggleGroup);
                Button config = new Button("Config");
                configs.add(config);
                config.setDisable(true);

                clusteringBtn.setOnAction(event -> {
                    clusteringBtn.setSelected(true);
                    config.setDisable(false);
                    algorithm = clusteringBtn.getText();
                });

            }
        }

        for(int i = 0; i< radioButtons.size(); i++){
            hBox.getChildren().add(radioButtons.get(i));
            hBox.getChildren().add(configs.get(i));
        }

        for(Button config: configs) {
            config.setOnAction((ActionEvent event) -> {
                final Stage dialog = new Stage();
                dialog.setTitle("Run Configurations");
                VBox vBoxTemp1 = new VBox();
                HBox hBoxTemp1 = new HBox();
                HBox hBoxTemp2 = new HBox();
                HBox hBoxTemp3 = new HBox();

                ChangeListener<String> forceNumberListener = (observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*"))
                        ((StringProperty) observable).set(oldValue);
                };

                Label maxIterations = new Label("Max. Iterations");
                maxIterations.setFont(Font.font(20));
                TextField maxIt = new TextField();

                maxIt.textProperty().addListener(forceNumberListener);
                maxIt.setPrefSize(60, 40);

                Label updateIntervals = new Label("Update Intervals");
                updateIntervals.setFont(Font.font(20));
                TextField upIntervals = new TextField();

                upIntervals.textProperty().addListener(forceNumberListener);
                upIntervals.setPrefSize(60, 40);

                hBoxTemp1.getChildren().addAll(maxIterations, maxIt);
                hBoxTemp2.getChildren().addAll(updateIntervals, upIntervals);
                hBoxTemp1.setSpacing(25);
                hBoxTemp2.setSpacing(25);

                CheckBox continuous = new CheckBox("Continuous Run?");
                continuous.setFont(Font.font(20));

                TextField numClusters = new TextField("" + clusteringNumberOfClusters);

                if (algorithmType.equals(CLASSIFICATION)) {
                    maxIt.setText("" + classificationMaximumIterations);
                    upIntervals.setText("" + classificationIntervals);
                    continuous.setSelected(classificationContinuousRun);
                } else if (algorithmType.equals(CLUSTERING)) {
                    maxIt.setText("" + clusteringnMaximumIterations);
                    upIntervals.setText("" + clusteringIntervals);
                    continuous.setSelected(clusteringContinuousRun);
                    Label clusters = new Label("Clusters");
                    clusters.setFont(Font.font(20));
                    numClusters.textProperty().addListener(forceNumberListener);
                    numClusters.setPrefSize(60, 40);
                    hBoxTemp3.getChildren().addAll(clusters, numClusters);
                    hBoxTemp3.setSpacing(25);

                }

                Button done = new Button("Done with Configurations");
                done.setFont(Font.font(15));
                done.setOnAction(event1 -> {
                    if (algorithmType.equals(CLASSIFICATION)) {
                        classificationMaximumIterations = Integer.parseInt(maxIt.getText());
                        classificationIntervals = Integer.parseInt(upIntervals.getText());
                        classificationContinuousRun = continuous.isSelected();
                    } else if (algorithmType.equals(CLUSTERING)) {
                        clusteringnMaximumIterations = Integer.parseInt(maxIt.getText());
                        clusteringIntervals = Integer.parseInt(upIntervals.getText());
                        clusteringNumberOfClusters = Integer.parseInt(numClusters.getText());
                        clusteringContinuousRun = continuous.isSelected();
                    }

                    isConfigured = true;
                    checkRunButton();
                    dialog.close();
                });

                if (hBoxTemp3.getChildren().size() > 0) {
                    vBoxTemp1.getChildren().addAll(hBoxTemp1, hBoxTemp2, hBoxTemp3, continuous, done);
                } else
                    vBoxTemp1.getChildren().addAll(hBoxTemp1, hBoxTemp2, continuous, done);

                vBoxTemp1.setSpacing(25);

                Scene dialogScene = new Scene(vBoxTemp1, 300, 300);
                dialog.setScene(dialogScene);
                dialog.show();

            });
        }
        vBox2.getChildren().add(hBox);
        addRunButton();
    }

    private void addRunButton(){
        if(vBox2.getChildren().contains(runButton)) vBox2.getChildren().remove(runButton);
        runButton = new Button("Run");
        runButton.setDisable(true);
        if(isConfigured){
            checkRunButton();
        }
        vBox2.getChildren().add(runButton);

        runButton.setOnAction(event -> {
            if(algorithmType.equals(CLASSIFICATION)){
                if(dataLoaded){
                    AppActions appActions = (AppActions)(applicationTemplate.getActionComponent());
                    Path loadedFile = appActions.getDataFilePath();
                    String loadedData = appActions.getFileInput();
                    runClassification(loadedFile,loadedData);
                }else{
                    File temp = new File("temp.tsd");
                    try{
                        FileWriter fw = new FileWriter(temp);
                        fw.write(textArea.getText());
                        fw.close();
                    }catch(IOException e){
                        System.out.println("not working");
                    }
                    Path file = temp.toPath();
                    runClassification(file,textArea.getText());
                }
            }
            else if(algorithmType.equals(CLUSTERING)){
                if(dataLoaded){
                    AppActions appActions = (AppActions)(applicationTemplate.getActionComponent());
                    Path loadedFile = appActions.getDataFilePath();
                    String loadedData = appActions.getFileInput();
                    runClustering(loadedFile,loadedData);
                }else{
                    File temp = new File("temp.tsd");
                    try{
                        FileWriter fw = new FileWriter(temp);
                        fw.write(textArea.getText());
                        fw.close();
                    }catch(IOException e){
                        System.out.println("not working");
                    }
                    Path file = temp.toPath();
                    runClustering(file,textArea.getText());
                }

            }
        });

    }

    private void runClustering(Path file, String data){
        AppData appData = (AppData)applicationTemplate.getDataComponent();
        try {
            DataSet dataSet = DataSet.fromTSDFile(file);
            Clusterer clusterer = null;
            try {
                Class c = Class.forName(algorithm);
                Constructor[] constructors = c.getDeclaredConstructors();
                Constructor constructor = constructors[0];
                clusterer = (Clusterer) constructor.newInstance(dataSet, clusteringnMaximumIterations,
                        clusteringIntervals, clusteringNumberOfClusters, clusteringContinuousRun);
            } catch (Exception e) {
                e.printStackTrace();
            }
            appData.loadData(data);
            runClusteringAlgorithmContinuous(clusterer, dataSet);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void runClassification(Path file, String data){
        AppData appData = (AppData)applicationTemplate.getDataComponent();
        try{
            DataSet dataSet = DataSet.fromTSDFile(file);
            Classifier classifier = null;
            try {
                Class c = Class.forName(algorithm);
                Constructor[] constructors = c.getDeclaredConstructors();
                Constructor constructor = constructors[0];
                classifier = (Classifier) constructor.newInstance(dataSet,classificationMaximumIterations,
                        classificationIntervals,classificationContinuousRun);
            }catch (Exception e){
                e.printStackTrace();
            }
            appData.loadData(data);
            if(classificationContinuousRun) {
                runClassifierAlgorithmContinuous(classifier, dataSet);
            }
            else {
                runClassifierAlgorithm(classifier, dataSet);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void runClusteringAlgorithmContinuous(Clusterer a, DataSet dataSet){
        AppData appData = (AppData)(applicationTemplate.getDataComponent());
        runButton.setDisable(true);
        scrnshotButton.setDisable(true);
        if(!dataLoaded)
            toggleButton.setDisable(true);
        isRunning = true;

        Thread newThread = new Thread(){
            public synchronized void run(){
                a.run();
            }
        };

        Thread thread2 = new Thread(){
            public synchronized void run() {
                while (true) {
                    try{
                        sleep(2000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if (a.getStop()) {
                        DataSet d = a.getDataset();
                        String data = d.writeToString();
                        String[] temp = data.split("\n");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(data);
                                appData.loadData(data);
                            }
                        });
                        a.resume();
                    }
                }
            }
        };

        newThread.start();
        thread2.start();
    }

    private void runClassifierAlgorithmContinuous(Classifier a, DataSet dataSet){
        if(vBox2.getChildren().contains(next)) vBox2.getChildren().remove(next);
        AppData appData = (AppData)(applicationTemplate.getDataComponent());
        runButton.setDisable(true);
        scrnshotButton.setDisable(true);
        if(!dataLoaded)
            toggleButton.setDisable(true);
        isRunning = true;

        ArrayList<Point2D> points = dataSet.findRangeOfSet();
        Thread newThread = new Thread(){
            public synchronized void run(){
                a.run();
            }
        };

        Thread thread2 = new Thread(){
            public synchronized void run(){

                while(true){
                    try{
                        sleep(2000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(a.getStop()) {
                        List<Integer> output = a.getOutput();
                        System.out.println(output.size() + "");
                        Platform.runLater(() -> appData.createLine(output.get(0),output.get(1),output.get(2),points.get(0),points.get(1)));

                        a.resume();
                    }
                    if(newThread.getState() == State.TERMINATED){
                        break;
                    }
                }
                runButton.setDisable(false);
                scrnshotButton.setDisable(false);
                if(!dataLoaded)
                    toggleButton.setDisable(false);
                isRunning = false;
            }
        };

        newThread.start();
        thread2.start();
    }

    private void runClassifierAlgorithm(Classifier a, DataSet dataSet){
        if(vBox2.getChildren().contains(next)) vBox2.getChildren().remove(next);
        AppData appData = (AppData)(applicationTemplate.getDataComponent());
        runButton.setDisable(true);
        if(!dataLoaded)
            toggleButton.setDisable(true);
        isRunning = true;
        scrnshotButton.setDisable(true);
        next = new Button("Next");
        next.setDisable(true);
        vBox2.getChildren().add(next);
        clicked = 1;

        ArrayList<Point2D> points = dataSet.findRangeOfSet();
        Thread runThread = new Thread(){
            public synchronized void run(){
                a.run();
            }
        };

        Thread thread2 = new Thread(){
            public synchronized void run(){
                while(true){
                    try{
                        sleep(1500);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(a.getStop()) {
                        List<Integer> classOutput = a.getOutput();
                        Platform.runLater(() -> {
                            List<Integer> output = classOutput;
                            appData.createLine(output.get(0),output.get(1),output.get(2),points.get(0),points.get(1));
                            clicked ++;
                        });

                        if(clicked % classificationIntervals == 0) {
                            if (clicked == classificationMaximumIterations) {
                                next.setDisable(false);
                            } else {
                                next.setDisable(false);
                                scrnshotButton.setDisable(false);
                                paused = true;
                            }
                        }
                        if (paused){
                            try{
                                wait();
                            }
                            catch (InterruptedException e){
                                a.resume();
                            }
                        }else{
                            a.resume();
                        }
                    }
                    if(runThread.getState() == State.TERMINATED){
                        next.setDisable(true);
                        break;
                    }
                }
                runButton.setDisable(false);
                scrnshotButton.setDisable(false);
                isRunning = false;
                if(!dataLoaded)
                    toggleButton.setDisable(false);
            }
        };

        runThread.start();
        thread2.start();

        next.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public synchronized void handle(ActionEvent event) {
                paused = false;
                next.setDisable(true);
                thread2.interrupt();
                scrnshotButton.setDisable(true);
            }
        });
    }

    private void checkRunButton(){
        if(algorithmType.equals(CLASSIFICATION) && classificationIntervals != 0 && classificationMaximumIterations != 0)
            runButton.setDisable(false);
        if(algorithmType.equals(CLUSTERING) && clusteringIntervals != 0 && clusteringnMaximumIterations != 0 && clusteringNumberOfClusters != 0)
            runButton.setDisable(false);

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

    public boolean getIsRunning(){
        return isRunning;
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

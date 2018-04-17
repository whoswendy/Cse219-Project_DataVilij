package actions;

import dataprocessors.AppData;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static vilij.settings.PropertyTypes.*;
import static settings.AppPropertyTypes.*;
import static settings.AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE;
import static settings.AppPropertyTypes.SAVE_UNSAVED_WORK;
import static settings.AppPropertyTypes.DATA_FILE_EXT;
import static settings.AppPropertyTypes.DATA_FILE_EXT_DESC;
import static settings.AppPropertyTypes.EXIT_WHILE_RUNNING_WARNING;


/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    private String fileInput;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    public void clearFileInput(){
        fileInput = "";
    }
    @Override
    public void handleNewRequest() {
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
        AppData appData = (AppData)(applicationTemplate.getDataComponent());
        if(ui.getHasNewText()) {
            Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
            confirm.show(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
            ConfirmationDialog.Option op = ConfirmationDialog.getDialog().getSelectedOption();
            if (op.equals(ConfirmationDialog.Option.YES)) {
                System.out.println("YES");
                try {
                    if (appData.processData(ui.getTextArea().getText())) {
                        boolean saved = promptToSave();
                        ui.getSave().setDisable(true);
                        //dataFilePath = null;
                    }
                } catch (Exception e) {

                }
            } else if (op.equals(ConfirmationDialog.Option.NO)) {
                applicationTemplate.getUIComponent().clear();
            }
        }else{
            ui.setTextArea(true);
            ui.clear();
        }
    }

    @Override
    public void handleSaveRequest() {
        AppUI ui = (AppUI) (applicationTemplate.getUIComponent());
        AppData appData = (AppData) (applicationTemplate.getDataComponent());
        if(dataFilePath == null) {
            try {
                if (appData.processData(ui.getTextArea().getText())) {
                    boolean saved = promptToSave();
                    if (saved) {
                        ui.getSave().setDisable(true);
                    }
                }
            } catch (Exception e) {

            }
        }else{
            if(appData.processData(ui.getTextArea().getText())){
                appData.saveData(dataFilePath);
            }
        }
    }

    @Override
    public void handleLoadRequest() {

        AppData appData = (AppData) (applicationTemplate.getDataComponent());
        appData.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(OPEN_LABEL.name()));
        File file = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

        try{
            if(file != null){
                List<String> list = new ArrayList<>();
                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                String s;
                while((s=fileReader.readLine()) != null){
                    list.add(s);
                }
                fileReader.close();
                fileInput="";
                for (String aList : list) {
                    fileInput += aList;
                    fileInput += "\n";
                }

                if(appData.processLoadData(fileInput, file.getName())){
                    appData.loadData(file.toPath());
                    dataFilePath = file.toPath();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void handleExitRequest() {
        AppUI ui = (AppUI) (applicationTemplate.getUIComponent());
        boolean newText = ui.getHasNewText();
        boolean isRunning = ui.getIsRunning();
        Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        if(newText && !ui.getTextArea().getText().equals("")){
            confirm.show("",applicationTemplate.manager.getPropertyValue(EXIT_WITHOUT_SAVING.name()));
            ConfirmationDialog.Option op = ConfirmationDialog.getDialog().getSelectedOption();
            if(op.equals(ConfirmationDialog.Option.YES)){
                System.exit(0);
            }
        }else if(isRunning){
            confirm.show("",applicationTemplate.manager.getPropertyValue(EXIT_WHILE_RUNNING_WARNING.name()));
            ConfirmationDialog.Option op = ConfirmationDialog.getDialog().getSelectedOption();
            if(op.equals(ConfirmationDialog.Option.YES)){
                System.exit(0);
            }
        }else{
            System.exit(0);
        }

    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
        AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(IMAGE_FILE_EXT_DESC.name()),
                applicationTemplate.manager.getPropertyValue(IMAGE_FILE_EXT.name()));
        fileChooser.getExtensionFilters().add(ex);

        File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        WritableImage chartImg = ui.getChart().snapshot(new SnapshotParameters(), null);

        if(file != null){
            ImageIO.write(SwingFXUtils.fromFXImage(chartImg,null),"png",file);
        }
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        // TODO for homework 1

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(applicationTemplate.manager.getPropertyValue(SAVE_WORK_TITLE.name()));

        URL url = applicationTemplate.manager.getClass().getClassLoader().getResource(applicationTemplate.manager
        .getPropertyValue(DATA_RESOURCE_PATH.name()));

        File path = null;
        try{
            if(url != null){
                path = new File(url.toURI());
                fileChooser.setInitialDirectory(path);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()));
        fileChooser.getExtensionFilters().add(ex);
        File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

        if(file != null){
            try{
                FileWriter fw = new FileWriter(file);
                System.out.println(file.getName());
                AppUI ui = (AppUI)(applicationTemplate.getUIComponent());
                TextArea input = ui.getTextArea();
                fw.write(input.getText());
                //System.out.println(file.getAbsolutePath());
                fw.close();
                dataFilePath = file.toPath();
            }catch(IOException e){
                System.out.println("not working");
            }
            return true;
        }

        return false;
    }

    public String getFileInput(){
        return fileInput;
    }

    public Path getDataFilePath(){
        return dataFilePath;

    }}

package actions;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

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

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        confirm.show(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()),
                applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        ConfirmationDialog.Option op = ConfirmationDialog.getDialog().getSelectedOption();
        if(op.equals(ConfirmationDialog.Option.YES)){
            System.out.println("YES");
            try{
                boolean saved = promptToSave();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(op.equals(ConfirmationDialog.Option.NO)){
            applicationTemplate.getUIComponent().clear();
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        AppUI ui = (AppUI) (applicationTemplate.getUIComponent());
        boolean newText = ui.getHasNewText();
        if(newText){
            Dialog confirm = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
            confirm.show("",applicationTemplate.manager.getPropertyValue(EXIT_WHILE_RUNNING_WARNING.name()));
            ConfirmationDialog.Option op = ConfirmationDialog.getDialog().getSelectedOption();
            if(op.equals(ConfirmationDialog.Option.YES)){
                System.exit(0);
            }else{

            }
        }else {
            System.exit(0);
        }

    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
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

        File path = new File(System.getProperty("user.dir") + File.separator + applicationTemplate.manager.getPropertyValue(HW.name()) + File.separator+
        applicationTemplate.manager.getPropertyValue(DATA_VILIJ.name()) + File.separator + applicationTemplate.manager.getPropertyValue(RESOURCES.name())
                + File.separator + applicationTemplate.manager.getPropertyValue(DATA_RESOURCE_PATH.name()));

        fileChooser.setInitialDirectory(path);

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
            }catch(IOException e){
                System.out.println("not working");
            }
            return true;
        }

        return false;
    }
}

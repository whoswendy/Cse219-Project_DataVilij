package actions;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.templates.ApplicationTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

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
        confirm.show("New?","Would you like to save the data?");
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
        System.exit(0);

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
        // TODO remove the placeholder line below after you have implemented this method

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter(".tsd",".tsd");
        fileChooser.getExtensionFilters().add(ex);
        //fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("tsd"));
        File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());


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

        if (file != null)
                return true;
        return false;
    }
}

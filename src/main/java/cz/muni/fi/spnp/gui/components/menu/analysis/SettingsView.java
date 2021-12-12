package cz.muni.fi.spnp.gui.components.menu.analysis;

import cz.muni.fi.spnp.gui.components.common.UIWindowComponent;
import cz.muni.fi.spnp.gui.components.mainwindow.Model;
import cz.muni.fi.spnp.gui.components.mainwindow.fileoperations.ModelSaver;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

/**
 * Window showing settings associated with the SPNP package (paths).
 */
public class SettingsView extends UIWindowComponent {

    private final Model model;

    private Label pathSPNPLabel;
    private TextField pathSPNPTextField;
    private Label pathSPNPExamplesLabel;
    private TextField pathSPNPExamplesTextField;
    private Label pathPlotsLibraryLabel;
    private TextField pathPlotsLibraryTextField;

    public SettingsView(Model model) {
        this.model = model;

        createView();
        bindViewModel();
    }

    private void createView() {
        pathSPNPLabel = new Label("Path of the interface directory:");
        pathSPNPTextField = new TextField();
        pathSPNPTextField.setEditable(false);
        pathSPNPTextField.setOnMouseClicked(mouseEvent -> chooseDirectoryPathSPNP());
        pathSPNPTextField.setOnKeyPressed(keyEvent -> chooseDirectoryPathSPNP());

        pathSPNPExamplesLabel = new Label("Directory for the SPNP examples:");
        pathSPNPExamplesTextField = new TextField();
        pathSPNPExamplesTextField.setEditable(false);
        pathSPNPExamplesTextField.setOnMouseClicked(mouseEvent -> chooseDirectoryPathSPNPExamples());
        pathSPNPExamplesTextField.setOnKeyPressed(keyEvent -> chooseDirectoryPathSPNPExamples());

        pathPlotsLibraryLabel = new Label("Directory for the plots library:");
        pathPlotsLibraryTextField = new TextField();
        pathPlotsLibraryTextField.setEditable(false);
        pathPlotsLibraryTextField.setOnMouseClicked(mouseEvent -> chooseDirectoryPathPlotsLibrary());
        pathPlotsLibraryTextField.setOnKeyPressed(keyEvent -> chooseDirectoryPathPlotsLibrary());

        var okButton = new Button("OK");
        HBox.setHgrow(okButton, Priority.ALWAYS);
        okButton.setMaxWidth(Double.MAX_VALUE);
        okButton.setOnAction(this::onOkButtonClickedHandler);

        var saveButton = new Button("Save");
        HBox.setHgrow(saveButton, Priority.ALWAYS);
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(this::onSaveButtonClickedHandler);

        var buttonsHbox = new HBox(okButton, saveButton);
        buttonsHbox.setSpacing(5);

        var vbox = new VBox();
        vbox.setPadding(new Insets(5));
        vbox.setSpacing(5);
        vbox.setPrefWidth(300);

        vbox.getChildren().add(pathSPNPLabel);
        vbox.getChildren().add(pathSPNPTextField);
        vbox.getChildren().add(pathSPNPExamplesLabel);
        vbox.getChildren().add(pathSPNPExamplesTextField);
        vbox.getChildren().add(pathPlotsLibraryLabel);
        vbox.getChildren().add(pathPlotsLibraryTextField);
        vbox.getChildren().add(buttonsHbox);

        var scene = new Scene(vbox);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

        stage.setTitle("Settings");
        stage.setScene(scene);
        stage.setMinWidth(370);
        stage.setMinHeight(230);
    }

    private void chooseDirectoryPathSPNP() {
        chooseDirectory(model.pathSPNPProperty());
    }

    private void chooseDirectoryPathSPNPExamples() {
        chooseDirectory(model.pathSPNPExamplesProperty());
    }

    private void chooseDirectoryPathPlotsLibrary() {
        chooseDirectory(model.pathPlotsLibraryProperty());
    }

    private void chooseDirectory(StringProperty stringProperty) {
        var directoryChooser = new DirectoryChooser();
        var file = directoryChooser.showDialog(stage);
        if (file != null) {
            stringProperty.set(file.getPath());
        }
    }

    private void onOkButtonClickedHandler(ActionEvent actionEvent) {
        stage.close();
    }

    private void onSaveButtonClickedHandler(ActionEvent actionEvent) {
        var modelSaver = new ModelSaver();
        modelSaver.savePathsSPNP(model);
    }

    private void bindViewModel() {
        pathSPNPTextField.textProperty().bind(model.pathSPNPProperty());
        pathSPNPExamplesTextField.textProperty().bind(model.pathSPNPExamplesProperty());
        pathPlotsLibraryTextField.textProperty().bind(model.pathPlotsLibraryProperty());
    }

}

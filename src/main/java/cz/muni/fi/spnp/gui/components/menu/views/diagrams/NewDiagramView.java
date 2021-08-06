package cz.muni.fi.spnp.gui.components.menu.views.diagrams;

import cz.muni.fi.spnp.gui.components.menu.views.DialogMessages;
import cz.muni.fi.spnp.gui.components.menu.views.UIWindowComponent;
import cz.muni.fi.spnp.gui.model.Model;
import cz.muni.fi.spnp.gui.viewmodel.DiagramViewModel;
import cz.muni.fi.spnp.gui.viewmodel.ProjectViewModel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NewDiagramView extends UIWindowComponent {
    private final Model model;
    private final ChoiceBox<ProjectViewModel> choiceBoxProject;

    public NewDiagramView(Model model) {
        this.model = model;

        var vbox = new VBox();
        var gridPane = new GridPane();

        var labelName = new Label("Name");
        gridPane.add(labelName, 0, 0);
        var textFieldName = new TextField();
        gridPane.add(textFieldName, 1, 0);
        var labelProject = new Label("Project");
        gridPane.add(labelProject, 0, 1);
        choiceBoxProject = new ChoiceBox<>();
        choiceBoxProject.setConverter(new ProjectViewModelStringConverter(model));
        choiceBoxProject.setItems(model.getProjects());
        gridPane.add(choiceBoxProject, 1, 1);
        vbox.getChildren().add(gridPane);

        var buttonsPanel = new HBox();
        var buttonCreate = new Button("Create");
        buttonCreate.setOnMouseClicked(mouseEvent -> {
            var name = textFieldName.getText();
            if (name.isBlank()) {
                DialogMessages.showError("Diagram name cannot be blank.");
                return;
            }

            var project = choiceBoxProject.getSelectionModel().getSelectedItem();
            if (project.diagramExists(name)) {
                DialogMessages.showError("Diagram with given name already exists.");
                return;
            }

            var diagram = new DiagramViewModel(project);
            diagram.nameProperty().set(name);
            project.getDiagrams().add(diagram);
            model.selectedDiagramProperty().set(diagram);
            stage.close();
        });
        buttonsPanel.getChildren().add(buttonCreate);
        var buttonCancel = new Button("Cancel");
        buttonCancel.setOnMouseClicked(mouseEvent -> {
            stage.close();
        });
        buttonsPanel.getChildren().add(buttonCancel);
        vbox.getChildren().add(buttonsPanel);

        stage.setTitle("New Diagram");
        stage.setScene(new Scene(vbox));
    }

    public void prepare() {
        var selectedDiagram = model.selectedDiagramProperty().get();
        if (selectedDiagram == null) {
            if (!model.getProjects().isEmpty()) {
                choiceBoxProject.getSelectionModel().select(0);
            }
        } else {
            choiceBoxProject.getSelectionModel().select(selectedDiagram.getProject());
        }
    }
}

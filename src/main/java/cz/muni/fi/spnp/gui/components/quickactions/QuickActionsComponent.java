package cz.muni.fi.spnp.gui.components.quickactions;

import cz.muni.fi.spnp.gui.components.ApplicationComponent;
import cz.muni.fi.spnp.gui.model.Model;
import cz.muni.fi.spnp.gui.notifications.Notifications;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class QuickActionsComponent extends ApplicationComponent {

    private final HBox hbox;

    public QuickActionsComponent(Model model, Notifications notifications) {
        super(model, notifications);

        hbox = new HBox();

        Button buttonNewProject = new Button("new project");
        hbox.getChildren().add(buttonNewProject);
    }

    @Override
    public Node getRoot() {
        return hbox;
    }
}

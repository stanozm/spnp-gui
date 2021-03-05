package cz.muni.fi.spnp.gui.propertieseditor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class EntityViewPlaceholder extends Component {

    private final VBox vbox;

    public EntityViewPlaceholder() {
        vbox = new VBox();
        vbox.getChildren().add(new Label("No element to edit."));
        vbox.setAlignment(Pos.CENTER);
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.setBorder(new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    @Override
    public Node getRoot() {
        return vbox;
    }
}

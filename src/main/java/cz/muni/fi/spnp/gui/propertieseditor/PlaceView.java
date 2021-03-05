package cz.muni.fi.spnp.gui.propertieseditor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class PlaceView extends Component {

    private final GridPane gridPane;

    private final Label nameLabel;

    private final TextField nameTextField;

    public PlaceView() {
        gridPane = new GridPane();

        nameLabel = new Label("Name:");
        nameTextField = new TextField();
//        nameLabel.setAlignment(Pos.BASELINE_CENTER);
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameTextField, 1, 0);
    }

    public void bindEntity(PlaceViewModel placeViewModel) {
        nameTextField.textProperty().bindBidirectional(placeViewModel.name);
    }

    @Override
    public Node getRoot() {
        return gridPane;
    }

}

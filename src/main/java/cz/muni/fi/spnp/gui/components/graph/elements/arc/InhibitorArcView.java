package cz.muni.fi.spnp.gui.components.graph.elements.arc;

import cz.muni.fi.spnp.gui.components.graph.elements.place.PlaceView;
import cz.muni.fi.spnp.gui.components.graph.elements.transition.TransitionView;
import cz.muni.fi.spnp.gui.viewmodel.InhibitorArcViewModel;

public class InhibitorArcView extends ArcView<InhibitorArcViewModel> {
    public InhibitorArcView(InhibitorArcViewModel inhibitorArcViewModel, PlaceView from, TransitionView to) {
        super(inhibitorArcViewModel, from, to);
        createView();
    }

    private void createView() {
        ending = new ArcEndingCircle(lines.get(0));
        groupSymbols.getChildren().add(ending.getShape());
    }
}

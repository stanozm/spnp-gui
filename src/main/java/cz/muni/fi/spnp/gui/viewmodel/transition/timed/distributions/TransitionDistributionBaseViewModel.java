package cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions;

import cz.muni.fi.spnp.core.models.places.Place;
import cz.muni.fi.spnp.core.models.transitions.distributions.TransitionDistributionType;
import cz.muni.fi.spnp.gui.components.menu.views.functions.FunctionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.PlaceViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public abstract class TransitionDistributionBaseViewModel implements TransitionDistributionViewModel {

    protected final List<FunctionViewModel> functions;
    private final ObjectProperty<TransitionDistributionType> distributionType;
    private PlaceViewModel dependentPlace;

    public TransitionDistributionBaseViewModel(TransitionDistributionType distributionType, PlaceViewModel dependentPlace) {
        this.distributionType = new SimpleObjectProperty<>(distributionType);
        this.dependentPlace = dependentPlace;
        this.functions = createFunctionsArray();
    }

    protected abstract List<FunctionViewModel> createFunctionsArray();

    public List<FunctionViewModel> getFunctions() {
        return functions;
    }

    @Override
    public ObjectProperty<TransitionDistributionType> distributionTypeProperty() {
        return this.distributionType;
    }

    @Override
    public PlaceViewModel getDependentPlace() {
        return this.dependentPlace;
    }

    @Override
    public void setDependentPlace(PlaceViewModel dependentPlace) {
        this.dependentPlace = dependentPlace;
    }
}
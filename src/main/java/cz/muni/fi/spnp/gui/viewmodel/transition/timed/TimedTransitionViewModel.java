package cz.muni.fi.spnp.gui.viewmodel.transition.timed;

import cz.muni.fi.spnp.gui.viewmodel.transition.TimedDistributionType;
import cz.muni.fi.spnp.gui.viewmodel.transition.TransitionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.TransitionDistributionViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TimedTransitionViewModel extends TransitionViewModel {

    private final ObjectProperty<TimedDistributionType> timedDistributionType = new SimpleObjectProperty<>();
    private TransitionDistributionViewModel transitionDistribution;


//
//    public TimedTransitionViewModel(String name, double x, double y, int priority, TransitionDistributionType transitionDistributionType, DistributionType distributionType) {
//        super(name, x, y, priority);
//        this.transitionDistributionType.set(transitionDistributionType);
//        this.distributionType.set(distributionType);
//    }

    public ObjectProperty<TimedDistributionType> timedDistributionTypeProperty() {
        return timedDistributionType;
    }

    public TransitionDistributionViewModel getTransitionDistribution() {
        return transitionDistribution;
    }

    public void setTransitionDistribution(TransitionDistributionViewModel transitionDistribution) {
        this.transitionDistribution = transitionDistribution;
    }
}
package cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.twovalues;

import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.distributions.TransitionDistributionType;
import cz.muni.fi.spnp.core.transformators.spnp.distributions.PoissonTransitionDistribution;
import cz.muni.fi.spnp.gui.components.menu.views.functions.FunctionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.PlaceViewModel;
import javafx.beans.property.DoubleProperty;

public class PoissonTransitionDistributionViewModel extends TwoValuesTransitionDistributionBaseViewModel<Double, Double> {

    /**
     * Creates new {@link PoissonTransitionDistribution} object with {@link TransitionDistributionType#Constant} distribution type.
     *
     * @param lambdaValue lambda value of Poisson distribution
     * @param tValue      T value of Poisson distribution
     */
    public PoissonTransitionDistributionViewModel(Double lambdaValue, Double tValue) {
        super(lambdaValue, tValue);
    }

    /**
     * Creates new {@link PoissonTransitionDistribution} object with {@link TransitionDistributionType#Functional} distribution type.
     *
     * @param lambdaValueFunction reference to a function which calculates lambda value of Poisson distribution
     * @param tValueFunction      reference to a function which calculates T value of Poisson distribution
     */
    public PoissonTransitionDistributionViewModel(FunctionViewModel lambdaValueFunction, FunctionViewModel tValueFunction) {
        super(lambdaValueFunction, tValueFunction);
    }

    /**
     * Creates new {@link PoissonTransitionDistribution} object with {@link TransitionDistributionType#PlaceDependent} distribution type.
     *
     * @param lambdaValue    lambda value of Poisson distribution
     * @param tValue         T value of Poisson distribution
     * @param dependentPlace reference to a {@link StandardPlace} object which is used for distribution
     */
    public PoissonTransitionDistributionViewModel(Double lambdaValue, Double tValue, PlaceViewModel dependentPlace) {
        super(lambdaValue, tValue, dependentPlace);
    }

    public DoubleProperty lambdaValueProperty() {
        return DoubleProperty.doubleProperty(firstValueProperty());
    }

    public DoubleProperty TValueProperty() {
        return DoubleProperty.doubleProperty(secondValueProperty());
    }

    public FunctionViewModel getLambdaValueFunction() {
        return this.getFirstFunction();
    }

    public void setLambdaValueFunction(FunctionViewModel lambdaValueFunction) {
        this.setFirstFunction(lambdaValueFunction);
    }

    public FunctionViewModel getTValueFunction() {
        return this.getSecondFunction();
    }

    public void setTValueFunction(FunctionViewModel tValueFunction) {
        this.setFirstFunction(tValueFunction);
    }

}
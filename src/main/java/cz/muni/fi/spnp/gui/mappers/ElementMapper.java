package cz.muni.fi.spnp.gui.mappers;

import cz.muni.fi.spnp.core.models.arcs.Arc;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.places.Place;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.ImmediateTransition;
import cz.muni.fi.spnp.core.models.transitions.TimedTransition;
import cz.muni.fi.spnp.core.models.transitions.Transition;
import cz.muni.fi.spnp.core.models.transitions.distributions.TransitionDistribution;
import cz.muni.fi.spnp.core.models.transitions.probabilities.ConstantTransitionProbability;
import cz.muni.fi.spnp.core.models.transitions.probabilities.FunctionalTransitionProbability;
import cz.muni.fi.spnp.core.models.transitions.probabilities.PlaceDependentTransitionProbability;
import cz.muni.fi.spnp.core.models.transitions.probabilities.TransitionProbability;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import cz.muni.fi.spnp.core.transformators.spnp.distributions.*;
import cz.muni.fi.spnp.core.transformators.spnp.elements.SPNPInhibitorArc;
import cz.muni.fi.spnp.core.transformators.spnp.elements.SPNPStandardArc;
import cz.muni.fi.spnp.core.transformators.spnp.elements.SPNPStandardPlace;
import cz.muni.fi.spnp.gui.components.menu.views.functions.FunctionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.*;
import cz.muni.fi.spnp.gui.viewmodel.transition.TimedDistributionType;
import cz.muni.fi.spnp.gui.viewmodel.transition.TransitionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.immediate.*;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.TimedTransitionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.TransitionDistributionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.fourvalues.FourValuesTransitionDistributionBaseViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.singlevalue.SingleValueTransitionDistributionBaseViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.threevalues.ThreeValuesTransitionDistributionBaseViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.twovalues.TwoValuesTransitionDistributionBaseViewModel;

import java.util.HashMap;
import java.util.Map;

public class ElementMapper {

    private final Map<FunctionViewModel, FunctionSPNP<?>> functionsMapping;
    private final Map<PlaceViewModel, StandardPlace> placesMapping;
    private final Map<TransitionViewModel, Transition> transitionsMapping;
    private int currentId;

    public ElementMapper(Map<FunctionViewModel, FunctionSPNP<?>> functionsMapping) {
        this.functionsMapping = functionsMapping;
        this.placesMapping = new HashMap<>();
        this.transitionsMapping = new HashMap<>();
        currentId = 0;
    }

    public Place mapPlace(PlaceViewModel placeViewModel) {
        var place = new SPNPStandardPlace(
                getId(),
                placeViewModel.getName(),
                placeViewModel.getNumberOfTokens()
        );
        placesMapping.put(placeViewModel, place);
        return place;
    }

    public Transition mapTransition(TransitionViewModel transitionViewModel) {
        Transition transition = null;
        if (transitionViewModel instanceof ImmediateTransitionViewModel) {
            transition = mapImmediateTransition((ImmediateTransitionViewModel) transitionViewModel);
        } else if (transitionViewModel instanceof TimedTransitionViewModel) {
            transition = mapTimedTransition((TimedTransitionViewModel) transitionViewModel);
        } else {
            throw new AssertionError("Unkown transition view model class " + transitionViewModel.getClass());
        }
        transitionsMapping.put(transitionViewModel, transition);
        return transition;
    }

    public ImmediateTransition mapImmediateTransition(ImmediateTransitionViewModel immediateTransitionViewModel) {
        return new ImmediateTransition(
                getId(),
                immediateTransitionViewModel.getName(),
                immediateTransitionViewModel.getPriority(),
                findFunction(immediateTransitionViewModel.getGuardFunction()),
                convertTransitionProbability(immediateTransitionViewModel.getTransitionProbability())
        );
    }

    private TransitionProbability convertTransitionProbability(TransitionProbabilityViewModel transitionProbability) {
        if (transitionProbability instanceof ConstantTransitionProbabilityViewModel) {
            var constantTransitionProbabilityViewModel = (ConstantTransitionProbabilityViewModel) transitionProbability;
            return new ConstantTransitionProbability(
                    constantTransitionProbabilityViewModel.getValue()
            );
        } else if (transitionProbability instanceof FunctionalTransitionProbabilityViewModel) {
            var functionalTransitionProbabilityViewModel = (FunctionalTransitionProbabilityViewModel) transitionProbability;
            return new FunctionalTransitionProbability(
                    findFunction(functionalTransitionProbabilityViewModel.getFunction())
            );
        } else if (transitionProbability instanceof PlaceDependentTransitionProbabilityViewModel) {
            var placeDependentTransitionProbabilityViewModel = (PlaceDependentTransitionProbabilityViewModel) transitionProbability;
            return new PlaceDependentTransitionProbability(
                    placeDependentTransitionProbabilityViewModel.getValue(),
                    findPlace(placeDependentTransitionProbabilityViewModel.getDependentPlace())
            );
        }
        throw new AssertionError("Unknown transition probability view model class " + transitionProbability.getClass());
    }

    public TimedTransition mapTimedTransition(TimedTransitionViewModel timedTransitionViewModel) {
        return new TimedTransition(
                getId(),
                timedTransitionViewModel.getName(),
                timedTransitionViewModel.getPriority(),
                findFunction(timedTransitionViewModel.getGuardFunction()),
                convertTransitionDistribution(timedTransitionViewModel.getTransitionDistribution(), timedTransitionViewModel.timedDistributionTypeProperty().get())
        );
    }

    private TransitionDistribution convertTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        switch (timedDistributionType.getNumberOfValuesType()) {
            case ONE:
                return convertSingleValueTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case TWO:
                return convertTwoValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case THREE:
                return convertThreeValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case FOUR:
                return convertFourValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
        }
        return null;
    }

    private TransitionDistribution convertSingleValueTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var distributionType = transitionDistributionViewModel.distributionTypeProperty().get();
        switch (distributionType) {
            case Constant:
                return convertConstantSingleValueTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case Functional:
                return convertFunctionalSingleValueTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case PlaceDependent:
                return convertPlaceDependentSingleValueTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
        }
        throw new AssertionError("Unknown single value distribution type " + distributionType);
    }

    private TransitionDistribution convertConstantSingleValueTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var singleValueViewModel = (SingleValueTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstValue = singleValueViewModel.getValue();

        switch (timedDistributionType) {
            case Constant:
                return new SPNPConstantTransitionDistribution(firstValue);
            case Exponential:
                return new SPNPExponentialTransitionDistribution(firstValue);
        }
        throw new AssertionError("Unknown constant single value transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertFunctionalSingleValueTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var singleValueViewModel = (SingleValueTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstFunction = (FunctionSPNP<Double>) findFunction(singleValueViewModel.getFirstFunction());

        switch (timedDistributionType) {
            case Constant:
                return new SPNPConstantTransitionDistribution(firstFunction);
            case Exponential:
                return new SPNPExponentialTransitionDistribution(firstFunction);
        }
        throw new AssertionError("Unknown functional single value transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertPlaceDependentSingleValueTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var singleValueViewModel = (SingleValueTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstValue = singleValueViewModel.getValue();
        var dependentPlace = (StandardPlace) findPlace(singleValueViewModel.getDependentPlace());

        switch (timedDistributionType) {
            case Constant:
                return new SPNPConstantTransitionDistribution(firstValue, dependentPlace);
            case Exponential:
                return new SPNPExponentialTransitionDistribution(firstValue, dependentPlace);
        }
        throw new AssertionError("Unknown place dependent single value transition distribution type " + timedDistributionType);

    }

    private TransitionDistribution convertTwoValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var distributionType = transitionDistributionViewModel.distributionTypeProperty().get();
        switch (distributionType) {
            case Constant:
                return convertConstantTwoValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case Functional:
                return convertFunctionalTwoValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case PlaceDependent:
                return convertPlaceDependentTwoValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
        }
        throw new AssertionError("Unknown two values distribution type " + distributionType);
    }

    private TransitionDistribution convertConstantTwoValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var twoValuesViewModel = (TwoValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstValue = twoValuesViewModel.getFirstValue();
        var secondValue = twoValuesViewModel.getSecondValue();

        switch (timedDistributionType) {
            case Beta:
                return new SPNPBetaTransitionDistribution(firstValue, secondValue);
            case Cauchy:
                return new SPNPCauchyTransitionDistribution(firstValue, secondValue);
            case Erlang:
                return new SPNPErlangTransitionDIstribution(firstValue, secondValue);
            case Gamma:
                return new SPNPGammaTransitionDistribution(firstValue, secondValue);
            case Geometric:
                return new SPNPGeometricTransitionDistribution(firstValue, secondValue);
            case LogarithmicNormal:
                return new SPNPLogarithmicNormalTransitionDistribution(firstValue, secondValue);
            case Pareto:
                return new SPNPParetoTransitionDistribution(firstValue, secondValue);
            case Poisson:
                return new SPNPPoissonTransitionDistribution(firstValue, secondValue);
            case TruncatedNormal:
                return new SPNPTruncatedNormalTransitionDistribution(firstValue, secondValue);
            case Uniform:
                return new SPNPUniformTransitionDistribution(firstValue, secondValue);
            case Weibull:
                return new SPNPWeibullTransitionDistribution(firstValue, secondValue);
        }
        throw new AssertionError("Unknown constant two values transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertFunctionalTwoValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var twoValuesViewModel = (TwoValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        if (timedDistributionType == TimedDistributionType.Erlang) {
            var firstFunction = (FunctionSPNP<Double>) findFunction(twoValuesViewModel.getFirstFunction());
            var secondFunction = (FunctionSPNP<Integer>) findFunction(twoValuesViewModel.getSecondFunction());
            return new SPNPErlangTransitionDIstribution(firstFunction, secondFunction);
        }

        var firstFunction = (FunctionSPNP<Double>) findFunction(twoValuesViewModel.getFirstFunction());
        var secondFunction = (FunctionSPNP<Double>) findFunction(twoValuesViewModel.getSecondFunction());

        switch (timedDistributionType) {
            case Beta:
                return new SPNPBetaTransitionDistribution(firstFunction, secondFunction);
            case Cauchy:
                return new SPNPCauchyTransitionDistribution(firstFunction, secondFunction);
            case Gamma:
                return new SPNPGammaTransitionDistribution(firstFunction, secondFunction);
            case Geometric:
                return new SPNPGeometricTransitionDistribution(firstFunction, secondFunction);
            case LogarithmicNormal:
                return new SPNPLogarithmicNormalTransitionDistribution(firstFunction, secondFunction);
            case Pareto:
                return new SPNPParetoTransitionDistribution(firstFunction, secondFunction);
            case Poisson:
                return new SPNPPoissonTransitionDistribution(firstFunction, secondFunction);
            case TruncatedNormal:
                return new SPNPTruncatedNormalTransitionDistribution(firstFunction, secondFunction);
            case Uniform:
                return new SPNPUniformTransitionDistribution(firstFunction, secondFunction);
            case Weibull:
                return new SPNPWeibullTransitionDistribution(firstFunction, secondFunction);
        }
        throw new AssertionError("Unknown functional two values transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertPlaceDependentTwoValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var twoValuesViewModel = (TwoValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstValue = twoValuesViewModel.getFirstValue();
        var secondValue = twoValuesViewModel.getSecondValue();
        var dependentPlace = findPlace(twoValuesViewModel.getDependentPlace());

        switch (timedDistributionType) {
            case Beta:
                return new SPNPBetaTransitionDistribution(firstValue, secondValue, dependentPlace);
            case Cauchy:
                return new SPNPCauchyTransitionDistribution(firstValue, secondValue, dependentPlace);
            case Erlang:
                return new SPNPErlangTransitionDIstribution(firstValue, secondValue, dependentPlace);
            case Gamma:
                return new SPNPGammaTransitionDistribution(firstValue, secondValue, dependentPlace);
            case Geometric:
                return new SPNPGeometricTransitionDistribution(firstValue, secondValue, dependentPlace);
            case LogarithmicNormal:
                return new SPNPLogarithmicNormalTransitionDistribution(firstValue, secondValue, dependentPlace);
            case Pareto:
                return new SPNPParetoTransitionDistribution(firstValue, secondValue, dependentPlace);
            case Poisson:
                return new SPNPPoissonTransitionDistribution(firstValue, secondValue, dependentPlace);
            case TruncatedNormal:
                return new SPNPTruncatedNormalTransitionDistribution(firstValue, secondValue, dependentPlace);
            case Uniform:
                return new SPNPUniformTransitionDistribution(firstValue, secondValue, dependentPlace);
            case Weibull:
                return new SPNPWeibullTransitionDistribution(firstValue, secondValue, dependentPlace);
        }
        throw new AssertionError("Unknown place dependent two values transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertThreeValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var distributionType = transitionDistributionViewModel.distributionTypeProperty().get();
        switch (distributionType) {
            case Constant:
                return convertConstantThreeValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case Functional:
                return convertFunctionalThreeValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case PlaceDependent:
                return convertPlaceDependentThreeValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
        }
        throw new AssertionError("Unknown three values distribution type " + distributionType);
    }

    private TransitionDistribution convertConstantThreeValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var threeValuesViewModel = (ThreeValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstValue = threeValuesViewModel.getFirstValue();
        var secondValue = threeValuesViewModel.getSecondValue();
        var thirdValue = threeValuesViewModel.getThirdValue();

        switch (timedDistributionType) {
            case Binomial:
                return new SPNPBinomialTransitionDistribution(firstValue, secondValue, thirdValue);
            case HyperExponential:
                return new SPNPHyperExponentialTransitionDistribution(firstValue, secondValue, thirdValue);
            case NegativeBinomial:
                return new SPNPNegativeBinomialTransitionDistribution(firstValue, secondValue, thirdValue);
        }
        throw new AssertionError("Unknown constant three values transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertFunctionalThreeValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var threeValuesViewModel = (ThreeValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstFunction = (FunctionSPNP<Double>) findFunction(threeValuesViewModel.getFirstFunction());
        var secondFunction = (FunctionSPNP<Double>) findFunction(threeValuesViewModel.getSecondFunction());
        var thirdFunction = (FunctionSPNP<Double>) findFunction(threeValuesViewModel.getThirdFunction());

        switch (timedDistributionType) {
            case Binomial:
                return new SPNPBinomialTransitionDistribution(firstFunction, secondFunction, thirdFunction);
            case HyperExponential:
                return new SPNPHyperExponentialTransitionDistribution(firstFunction, secondFunction, thirdFunction);
            case NegativeBinomial:
                return new SPNPNegativeBinomialTransitionDistribution(firstFunction, secondFunction, thirdFunction);
        }
        throw new AssertionError("Unknown functional three values transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertPlaceDependentThreeValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var threeValuesViewModel = (ThreeValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstValue = threeValuesViewModel.getFirstValue();
        var secondValue = threeValuesViewModel.getSecondValue();
        var thirdValue = threeValuesViewModel.getThirdValue();
        var dependentPlace = findPlace(threeValuesViewModel.getDependentPlace());

        switch (timedDistributionType) {
            case Binomial:
                return new SPNPBinomialTransitionDistribution(firstValue, secondValue, thirdValue, dependentPlace);
            case HyperExponential:
                return new SPNPHyperExponentialTransitionDistribution(firstValue, secondValue, thirdValue, dependentPlace);
            case NegativeBinomial:
                return new SPNPNegativeBinomialTransitionDistribution(firstValue, secondValue, thirdValue, dependentPlace);
        }
        throw new AssertionError("Unknown constant three values transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertFourValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var distributionType = transitionDistributionViewModel.distributionTypeProperty().get();
        switch (distributionType) {
            case Constant:
                return convertConstantFourValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case Functional:
                return convertFunctionalFourValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
            case PlaceDependent:
                return convertPlaceDependentFourValuesTransitionDistribution(transitionDistributionViewModel, timedDistributionType);
        }
        throw new AssertionError("Unknown four values distribution type " + distributionType);
    }

    private TransitionDistribution convertConstantFourValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var fourValuesViewModel = (FourValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstValue = fourValuesViewModel.getFirstValue();
        var secondValue = fourValuesViewModel.getSecondValue();
        var thirdValue = fourValuesViewModel.getThirdValue();
        var fourthValue = fourValuesViewModel.getFourthValue();

        switch (timedDistributionType) {
            case HypoExponential:
                return new SPNPHypoExponentialTransitionDistribution(firstValue, secondValue, thirdValue, fourthValue);
        }
        throw new AssertionError("Unknown constant four values transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertFunctionalFourValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var fourValuesViewModel = (FourValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstFunction = (FunctionSPNP<Integer>) findFunction(fourValuesViewModel.getFirstFunction());
        var secondFunction = (FunctionSPNP<Double>) findFunction(fourValuesViewModel.getSecondFunction());
        var thirdFunction = (FunctionSPNP<Double>) findFunction(fourValuesViewModel.getThirdFunction());
        var fourthFunction = (FunctionSPNP<Double>) findFunction(fourValuesViewModel.getFourthFunction());

        switch (timedDistributionType) {
            case HypoExponential:
                return new SPNPHypoExponentialTransitionDistribution(firstFunction, secondFunction, thirdFunction, fourthFunction);
        }
        throw new AssertionError("Unknown functional four values transition distribution type " + timedDistributionType);
    }

    private TransitionDistribution convertPlaceDependentFourValuesTransitionDistribution(TransitionDistributionViewModel transitionDistributionViewModel, TimedDistributionType timedDistributionType) {
        var fourValuesViewModel = (FourValuesTransitionDistributionBaseViewModel) transitionDistributionViewModel;
        var firstValue = fourValuesViewModel.getFirstValue();
        var secondValue = fourValuesViewModel.getSecondValue();
        var thirdValue = fourValuesViewModel.getThirdValue();
        var fourthValue = fourValuesViewModel.getFourthValue();
        var dependentPlace = findPlace(fourValuesViewModel.getDependentPlace());

        switch (timedDistributionType) {
            case HypoExponential:
                return new SPNPHypoExponentialTransitionDistribution(firstValue, secondValue, thirdValue, fourthValue, dependentPlace);
        }
        throw new AssertionError("Unknown place dependent four values transition distribution type " + timedDistributionType);
    }

    public Arc mapArc(ArcViewModel arcViewModel) {
        var from = arcViewModel.getFromViewModel();
        var to = arcViewModel.getToViewModel();

        if (from instanceof TransitionViewModel && to instanceof PlaceViewModel) {
            var fromTransition = findTransition((TransitionViewModel) from);
            var toPlace = findPlace((PlaceViewModel) to);
            return createOutputArc(arcViewModel, toPlace, fromTransition);

        } else if (from instanceof PlaceViewModel && to instanceof TransitionViewModel) {
            var fromPlace = findPlace((PlaceViewModel) from);
            var toTransition = findTransition((TransitionViewModel) to);
            return createInputArc(arcViewModel, fromPlace, toTransition);
        } else {
            throw new AssertionError("Arc is incorrectly connected (both ends to the same element type) " + from.getClass() + ", " + to.getClass());
        }
    }

    private Arc createOutputArc(ArcViewModel arcViewModel, StandardPlace toPlace, Transition fromTransition) {
        if (arcViewModel instanceof StandardArcViewModel) {
            return createOutputStandardArc(arcViewModel, toPlace, fromTransition);
        } else {
            throw new AssertionError("Output arc can be created only for standard arc not for " + arcViewModel.getClass());
        }
    }

    private Arc createOutputStandardArc(ArcViewModel arcViewModel, StandardPlace toPlace, Transition fromTransition) {
        switch (arcViewModel.getMultiplicityType()) {
            case CONSTANT:
                return new SPNPStandardArc(
                        getId(),
                        ArcDirection.Output,
                        toPlace,
                        fromTransition,
                        arcViewModel.getMultiplicity()
                );
            case FUNCTION:
                return new SPNPStandardArc(
                        getId(),
                        ArcDirection.Output,
                        toPlace,
                        fromTransition,
                        findFunction(arcViewModel.getMultiplicityFunction())
                );
        }
        throw new AssertionError("Unknown arc multiplicity type " + arcViewModel.getMultiplicityType());
    }

    private Arc createInputArc(ArcViewModel arcViewModel, StandardPlace fromPlace, Transition toTransition) {
        if (arcViewModel instanceof StandardArcViewModel) {
            return createInputStandardArc(arcViewModel, fromPlace, toTransition);
        } else {
            return createInhibitorArc(arcViewModel, fromPlace, toTransition);
        }
    }

    private Arc createInhibitorArc(ArcViewModel arcViewModel, StandardPlace toPlace, Transition fromTransition) {
        switch (arcViewModel.getMultiplicityType()) {
            case CONSTANT:
                return new SPNPInhibitorArc(
                        getId(),
                        toPlace,
                        fromTransition,
                        arcViewModel.getMultiplicity()
                );
            case FUNCTION:
                return new SPNPInhibitorArc(
                        getId(),
                        toPlace,
                        fromTransition,
                        findFunction(arcViewModel.getMultiplicityFunction())
                );
        }
        throw new AssertionError("Unknown arc multiplicity type " + arcViewModel.getMultiplicityType());
    }

    private Arc createInputStandardArc(ArcViewModel arcViewModel, StandardPlace fromPlace, Transition toTransition) {
        switch (arcViewModel.getMultiplicityType()) {
            case CONSTANT:
                return new SPNPStandardArc(
                        getId(),
                        ArcDirection.Input,
                        fromPlace,
                        toTransition,
                        arcViewModel.getMultiplicity()
                );
            case FUNCTION:
                return new SPNPStandardArc(
                        getId(),
                        ArcDirection.Input,
                        fromPlace,
                        toTransition,
                        findFunction(arcViewModel.getMultiplicityFunction())
                );
        }
        throw new AssertionError("Unknown arc multiplicity type " + arcViewModel.getMultiplicityType());
    }

    private int getId() {
        return currentId++;
    }

    private Transition findTransition(TransitionViewModel transitionViewModel) {
        return transitionsMapping.get(transitionViewModel);
    }

    private StandardPlace findPlace(PlaceViewModel placeViewModel) {
        return placesMapping.get(placeViewModel);
    }

    private FunctionSPNP<?> findFunction(FunctionViewModel functionViewModel) {
        return functionsMapping.get(functionViewModel);
    }

}
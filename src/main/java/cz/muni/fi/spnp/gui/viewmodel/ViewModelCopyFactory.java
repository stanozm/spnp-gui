package cz.muni.fi.spnp.gui.viewmodel;

import cz.muni.fi.spnp.gui.viewmodel.transition.TransitionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.immediate.*;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.TimedTransitionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.TransitionDistributionBaseViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.TransitionDistributionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.fourvalues.FourValuesTransitionDistributionBaseViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.fourvalues.HypoExponentialDistributionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.singlevalue.ConstantTransitionDistributionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.singlevalue.ExponentialTransitionDistributionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.singlevalue.SingleValueTransitionDistributionBaseViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.threevalues.BinomialTransitionDistributionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.threevalues.HyperExponentialTransitionDistributionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.threevalues.NegativeBinomialTransitionDistributionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.threevalues.ThreeValuesTransitionDistributionBaseViewModel;
import cz.muni.fi.spnp.gui.viewmodel.transition.timed.distributions.twovalues.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewModelCopyFactory {

    private final Map<ElementViewModel, ElementViewModel> originalToCopy;

    public ViewModelCopyFactory() {
        originalToCopy = new HashMap<>();
    }

    private void addToMapping(ElementViewModel original, ElementViewModel copy) {
        if (originalToCopy.containsKey(original)) {
            throw new AssertionError(String.format("Trying to create two copies of one element for %s, possibly forgot to use findOrCreate", original));
        }
        originalToCopy.put(original, copy);
    }

    private void copyTo(DisplayableViewModel copy, DisplayableViewModel displayableViewModel) {
        copy.nameProperty().set(displayableViewModel.getName());
    }

    public DisplayableViewModel createCopy(DisplayableViewModel displayableViewModel) {
        var copy = new DisplayableViewModel();
        copyTo(copy, displayableViewModel);
        return copy;
    }

    private void copyTo(ElementViewModel copy, ElementViewModel elementViewModel) {
        copyTo(copy, (DisplayableViewModel) elementViewModel);
    }

    public ElementViewModel createCopy(ElementViewModel elementViewModel) {
        if (elementViewModel == null) {
            return null;
        }

        if (elementViewModel instanceof PlaceViewModel) {
            return createCopy((PlaceViewModel) elementViewModel);
        } else if (elementViewModel instanceof StandardArcViewModel) {
            return createCopy((StandardArcViewModel) elementViewModel);
        } else if (elementViewModel instanceof InhibitorArcViewModel) {
            return createCopy((InhibitorArcViewModel) elementViewModel);
        } else if (elementViewModel instanceof ImmediateTransitionViewModel) {
            return createCopy((ImmediateTransitionViewModel) elementViewModel);
        } else if (elementViewModel instanceof TimedTransitionViewModel) {
            return createCopy((TimedTransitionViewModel) elementViewModel);
        } else {
            throw new AssertionError("Unsupported view model class for copying " + elementViewModel);
        }
    }

    private void copyTo(ConnectableViewModel copy, ConnectableViewModel connectableViewModel) {
        copyTo(copy, (ElementViewModel) connectableViewModel);
        copy.positionXProperty().set(connectableViewModel.getPositionX());
        copy.positionYProperty().set(connectableViewModel.getPositionY());
    }

    private void copyTo(PlaceViewModel copy, PlaceViewModel placeViewModel) {
        copyTo(copy, (ConnectableViewModel) placeViewModel);
        copy.numberOfTokensProperty().set(placeViewModel.getNumberOfTokens());
    }

    public PlaceViewModel createCopy(PlaceViewModel placeViewModel) {
        var copy = new PlaceViewModel();
        copyTo(copy, placeViewModel);
        addToMapping(placeViewModel, copy);
        return copy;
    }

    private void copyTo(TransitionViewModel copy, TransitionViewModel transitionViewModel) {
        copyTo(copy, (ConnectableViewModel) transitionViewModel);
        copy.priorityProperty().set(transitionViewModel.getPriority());
        copy.guardFunctionProperty().set(transitionViewModel.getGuardFunction());
    }

    private void copyTo(ImmediateTransitionViewModel copy, ImmediateTransitionViewModel immediateTransitionViewModel) {
        copyTo(copy, (TransitionViewModel) immediateTransitionViewModel);
        copy.setTransitionProbability(createCopy(immediateTransitionViewModel.getTransitionProbability()));
    }

    public ImmediateTransitionViewModel createCopy(ImmediateTransitionViewModel immediateTransitionViewModel) {
        var copy = new ImmediateTransitionViewModel();
        copyTo(copy, immediateTransitionViewModel);
        addToMapping(immediateTransitionViewModel, copy);
        return copy;
    }

    private void copyTo(TimedTransitionViewModel copy, TimedTransitionViewModel timedTransitionViewModel) {
        copyTo(copy, (TransitionViewModel) timedTransitionViewModel);
        copy.timedDistributionTypeProperty().set(timedTransitionViewModel.getTimedDistributionType());
        copy.setTransitionDistribution(createCopy(timedTransitionViewModel.getTransitionDistribution()));
    }

    public TimedTransitionViewModel createCopy(TimedTransitionViewModel timedTransitionViewModel) {
        var copy = new TimedTransitionViewModel();
        copyTo(copy, timedTransitionViewModel);
        addToMapping(timedTransitionViewModel, copy);
        return copy;
    }

    private void copyTo(ArcViewModel copy, ArcViewModel arcViewModel) {
        copyTo(copy, (ElementViewModel) arcViewModel);
        copy.setFromViewModel(findOrReturn(arcViewModel.getFromViewModel()));
        copy.setToViewModel(findOrReturn(arcViewModel.getToViewModel()));
        copy.multiplicityTypeProperty().set(arcViewModel.getMultiplicityType());
        copy.multiplicityProperty().set(arcViewModel.getMultiplicity());
        copy.getDragPoints().addAll(createCopyDragPoints(arcViewModel));
    }

    private void copyTo(DragPointViewModel copy, DragPointViewModel dragPointViewModel) {
        copy.positionXProperty().set(dragPointViewModel.getPositionX());
        copy.positionYProperty().set(dragPointViewModel.getPositionY());
    }

    public DragPointViewModel createCopy(DragPointViewModel dragPointViewModel) {
        var copy = new DragPointViewModel();
        copyTo(copy, dragPointViewModel);
        return copy;
    }

    private List<DragPointViewModel> createCopyDragPoints(ArcViewModel arcViewModel) {
        return arcViewModel.getDragPoints().stream()
                .map(this::createCopy)
                .collect(Collectors.toList());
    }

    private void copyTo(StandardArcViewModel copy, StandardArcViewModel standardArcViewModel) {
        copyTo(copy, (ArcViewModel) standardArcViewModel);
    }

    public StandardArcViewModel createCopy(StandardArcViewModel standardArcViewModel) {
        var copy = new StandardArcViewModel();
        copyTo(copy, (ArcViewModel) standardArcViewModel);
        addToMapping(standardArcViewModel, copy);
        return copy;
    }

    private void copyTo(InhibitorArcViewModel copy, InhibitorArcViewModel inhibitorArcViewModel) {
        copyTo(copy, (ArcViewModel) inhibitorArcViewModel);
    }

    public InhibitorArcViewModel createCopy(InhibitorArcViewModel inhibitorArcViewModel) {
        var copy = new InhibitorArcViewModel();
        copyTo(copy, (ArcViewModel) inhibitorArcViewModel);
        addToMapping(inhibitorArcViewModel, copy);
        return copy;
    }

    public TransitionProbabilityViewModel createCopy(TransitionProbabilityViewModel transitionProbabilityViewModel) {
        if (transitionProbabilityViewModel == null) {
            return null;
        }

        if (transitionProbabilityViewModel instanceof ConstantTransitionProbabilityViewModel) {
            return createCopy((ConstantTransitionProbabilityViewModel) transitionProbabilityViewModel);
        } else if (transitionProbabilityViewModel instanceof FunctionalTransitionProbabilityViewModel) {
            return createCopy((FunctionalTransitionProbabilityViewModel) transitionProbabilityViewModel);
        } else if (transitionProbabilityViewModel instanceof PlaceDependentTransitionProbabilityViewModel) {
            return createCopy((PlaceDependentTransitionProbabilityViewModel) transitionProbabilityViewModel);
        } else {
            throw new AssertionError("Unsupported transition probability view model class " + transitionProbabilityViewModel);
        }
    }

    public ConstantTransitionProbabilityViewModel createCopy(ConstantTransitionProbabilityViewModel constantTransitionDistributionViewModel) {
        var copy = new ConstantTransitionProbabilityViewModel();
        copy.valueProperty().set(constantTransitionDistributionViewModel.getValue());
        return copy;
    }

    public FunctionalTransitionProbabilityViewModel createCopy(FunctionalTransitionProbabilityViewModel functionalTransitionProbabilityViewModel) {
        var copy = new FunctionalTransitionProbabilityViewModel();
        copy.setFunction(functionalTransitionProbabilityViewModel.getFunction());
        return copy;
    }

    public PlaceDependentTransitionProbabilityViewModel createCopy(PlaceDependentTransitionProbabilityViewModel placeDependentTransitionProbabilityViewModel) {
        var copy = new PlaceDependentTransitionProbabilityViewModel();
        copy.valueProperty().set(placeDependentTransitionProbabilityViewModel.getValue());
        copy.setDependentPlace((PlaceViewModel) findOrReturn(placeDependentTransitionProbabilityViewModel.getDependentPlace()));
        return copy;
    }

    private ElementViewModel findOrReturn(ElementViewModel elementViewModel) {
        if (originalToCopy.containsKey(elementViewModel)) {
            return originalToCopy.get(elementViewModel);
        } else {
            return elementViewModel;
        }
    }

    public TransitionDistributionViewModel createCopy(TransitionDistributionViewModel transitionDistributionViewModel) {
        if (transitionDistributionViewModel == null) {
            return null;
        }

        if (transitionDistributionViewModel instanceof BetaTransitionDistributionViewModel) {
            return createCopy((BetaTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof BinomialTransitionDistributionViewModel) {
            return createCopy((BinomialTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof CauchyTransitionDistributionViewModel) {
            return createCopy((CauchyTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof ConstantTransitionDistributionViewModel) {
            return createCopy((ConstantTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof ErlangTransitionDistributionViewModel) {
            return createCopy((ErlangTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof ExponentialTransitionDistributionViewModel) {
            return createCopy((ExponentialTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof GammaTransitionDistributionViewModel) {
            return createCopy((GammaTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof GeometricTransitionDistributionViewModel) {
            return createCopy((GeometricTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof HyperExponentialTransitionDistributionViewModel) {
            return createCopy((HyperExponentialTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof HypoExponentialDistributionViewModel) {
            return createCopy((HypoExponentialDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof LogarithmicNormalTransitionDistributionViewModel) {
            return createCopy((LogarithmicNormalTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof NegativeBinomialTransitionDistributionViewModel) {
            return createCopy((NegativeBinomialTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof ParetoTransitionDistributionViewModel) {
            return createCopy((ParetoTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof PoissonTransitionDistributionViewModel) {
            return createCopy((PoissonTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof TruncatedNormalTransitionDistributionViewModel) {
            return createCopy((TruncatedNormalTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof UniformTransitionDistributionViewModel) {
            return createCopy((UniformTransitionDistributionViewModel) transitionDistributionViewModel);
        } else if (transitionDistributionViewModel instanceof WeibullTransitionDistributionViewModel) {
            return createCopy((WeibullTransitionDistributionViewModel) transitionDistributionViewModel);
        } else {
            throw new AssertionError("Unsupported transition distribution view model class " + transitionDistributionViewModel);
        }
    }

    private void copyTo(TransitionDistributionBaseViewModel copy, TransitionDistributionBaseViewModel transitionDistributionBaseViewModel) {
        copy.getFunctions().clear();
        copy.getFunctions().addAll(new ArrayList<>(transitionDistributionBaseViewModel.getFunctions()));
        copy.distributionTypeProperty().set(transitionDistributionBaseViewModel.getDistributionType());
        copy.setDependentPlace((PlaceViewModel) findOrReturn(transitionDistributionBaseViewModel.getDependentPlace()));
    }

    private void copyTo(SingleValueTransitionDistributionBaseViewModel copy, SingleValueTransitionDistributionBaseViewModel singleValueViewModel) {
        copyTo(copy, (TransitionDistributionBaseViewModel) singleValueViewModel);
        copy.valueProperty().setValue(singleValueViewModel.getValue());
    }

    private void copyTo(TwoValuesTransitionDistributionBaseViewModel copy, TwoValuesTransitionDistributionBaseViewModel twoValuesViewModel) {
        copyTo(copy, (TransitionDistributionBaseViewModel) twoValuesViewModel);
        copy.firstValueProperty().set(twoValuesViewModel.getFirstValue());
        copy.secondValueProperty().set(twoValuesViewModel.getSecondValue());
    }

    private void copyTo(ThreeValuesTransitionDistributionBaseViewModel copy, ThreeValuesTransitionDistributionBaseViewModel threeValuesViewModel) {
        copyTo(copy, (TransitionDistributionBaseViewModel) threeValuesViewModel);
        copy.firstValueProperty().set(threeValuesViewModel.getFirstValue());
        copy.secondValueProperty().set(threeValuesViewModel.getSecondValue());
        copy.thirdValueProperty().set(threeValuesViewModel.getThirdValue());
    }

    private void copyTo(FourValuesTransitionDistributionBaseViewModel copy, FourValuesTransitionDistributionBaseViewModel fourValueViewModel) {
        copyTo(copy, (TransitionDistributionBaseViewModel) fourValueViewModel);
        copy.firstValueProperty().set(fourValueViewModel.getFirstValue());
        copy.secondValueProperty().set(fourValueViewModel.getSecondValue());
        copy.thirdValueProperty().set(fourValueViewModel.getThirdValue());
        copy.fourthValueProperty().set(fourValueViewModel.getFourthValue());
    }

    public BetaTransitionDistributionViewModel createCopy(BetaTransitionDistributionViewModel betaTransitionDistributionViewModel) {
        var copy = new BetaTransitionDistributionViewModel();
        copyTo(copy, betaTransitionDistributionViewModel);
        return copy;
    }

    public BinomialTransitionDistributionViewModel createCopy(BinomialTransitionDistributionViewModel binomialTransitionDistributionViewModel) {
        var copy = new BinomialTransitionDistributionViewModel();
        copyTo(copy, binomialTransitionDistributionViewModel);
        return copy;
    }

    public CauchyTransitionDistributionViewModel createCopy(CauchyTransitionDistributionViewModel cauchyTransitionDistributionViewModel) {
        var copy = new CauchyTransitionDistributionViewModel();
        copyTo(copy, cauchyTransitionDistributionViewModel);
        return copy;
    }

    public ConstantTransitionDistributionViewModel createCopy(ConstantTransitionDistributionViewModel constantTransitionDistributionViewModel) {
        var copy = new ConstantTransitionDistributionViewModel();
        copyTo(copy, constantTransitionDistributionViewModel);
        return copy;
    }

    public ErlangTransitionDistributionViewModel createCopy(ErlangTransitionDistributionViewModel erlangTransitionDistributionViewModel) {
        var copy = new ErlangTransitionDistributionViewModel();
        copyTo(copy, erlangTransitionDistributionViewModel);
        return copy;
    }

    public ExponentialTransitionDistributionViewModel createCopy(ExponentialTransitionDistributionViewModel exponentialTransitionDistributionViewModel) {
        var copy = new ExponentialTransitionDistributionViewModel();
        copyTo(copy, exponentialTransitionDistributionViewModel);
        return copy;
    }

    public GammaTransitionDistributionViewModel createCopy(GammaTransitionDistributionViewModel gammaTransitionDistributionViewModel) {
        var copy = new GammaTransitionDistributionViewModel();
        copyTo(copy, gammaTransitionDistributionViewModel);
        return copy;
    }

    public GeometricTransitionDistributionViewModel createCopy(GeometricTransitionDistributionViewModel geometricTransitionDistributionViewModel) {
        var copy = new GeometricTransitionDistributionViewModel();
        copyTo(copy, geometricTransitionDistributionViewModel);
        return copy;
    }

    public HyperExponentialTransitionDistributionViewModel createCopy(HyperExponentialTransitionDistributionViewModel hyperExponentialTransitionDistributionViewModel) {
        var copy = new HyperExponentialTransitionDistributionViewModel();
        copyTo(copy, hyperExponentialTransitionDistributionViewModel);
        return copy;
    }

    public HypoExponentialDistributionViewModel createCopy(HypoExponentialDistributionViewModel hypoExponentialDistributionViewModel) {
        var copy = new HypoExponentialDistributionViewModel();
        copyTo(copy, hypoExponentialDistributionViewModel);
        return copy;
    }

    public LogarithmicNormalTransitionDistributionViewModel createCopy(LogarithmicNormalTransitionDistributionViewModel logarithmicNormalTransitionDistributionViewModel) {
        var copy = new LogarithmicNormalTransitionDistributionViewModel();
        copyTo(copy, logarithmicNormalTransitionDistributionViewModel);
        return copy;
    }

    public NegativeBinomialTransitionDistributionViewModel createCopy(NegativeBinomialTransitionDistributionViewModel negativeBinomialTransitionDistributionViewModel) {
        var copy = new NegativeBinomialTransitionDistributionViewModel();
        copyTo(copy, negativeBinomialTransitionDistributionViewModel);
        return copy;
    }

    public ParetoTransitionDistributionViewModel createCopy(ParetoTransitionDistributionViewModel paretoTransitionDistributionViewModel) {
        var copy = new ParetoTransitionDistributionViewModel();
        copyTo(copy, paretoTransitionDistributionViewModel);
        return copy;
    }

    public PoissonTransitionDistributionViewModel createCopy(PoissonTransitionDistributionViewModel poissonTransitionDistributionViewModel) {
        var copy = new PoissonTransitionDistributionViewModel();
        copyTo(copy, poissonTransitionDistributionViewModel);
        return copy;
    }

    public TruncatedNormalTransitionDistributionViewModel createCopy(TruncatedNormalTransitionDistributionViewModel truncatedNormalTransitionDistributionViewModel) {
        var copy = new TruncatedNormalTransitionDistributionViewModel();
        copyTo(copy, truncatedNormalTransitionDistributionViewModel);
        return copy;
    }

    public UniformTransitionDistributionViewModel createCopy(UniformTransitionDistributionViewModel uniformTransitionDistributionViewModel) {
        var copy = new UniformTransitionDistributionViewModel();
        copyTo(copy, uniformTransitionDistributionViewModel);
        return copy;
    }

    public WeibullTransitionDistributionViewModel createCopy(WeibullTransitionDistributionViewModel weibullTransitionDistributionViewModel) {
        var copy = new WeibullTransitionDistributionViewModel();
        copyTo(copy, weibullTransitionDistributionViewModel);
        return copy;
    }

}
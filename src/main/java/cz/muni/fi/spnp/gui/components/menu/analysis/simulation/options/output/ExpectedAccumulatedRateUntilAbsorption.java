package cz.muni.fi.spnp.gui.components.menu.analysis.simulation.options.output;

import cz.muni.fi.spnp.gui.components.menu.analysis.simulation.options.OutputOptionsResult;
import cz.muni.fi.spnp.gui.viewmodel.DiagramViewModel;

public class ExpectedAccumulatedRateUntilAbsorption extends OutputOptionViewModel {

    public ExpectedAccumulatedRateUntilAbsorption() {
        super("Expected accumulated rate until absorption");

        initFunction();
    }

    @Override
    public void addToResult(OutputOptionsResult result, DiagramViewModel diagramViewModel) {
        addSolve(result);
        result.lines.add(String.format("pr_cum_expected(\"Expected accumulated reward until absorption using %s\", %s);",
                getFunction().getName(), getFunction().getName()));
    }

}

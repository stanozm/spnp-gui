package cz.muni.fi.spnp.gui.model;

import cz.muni.fi.spnp.gui.components.menu.view.functions.FunctionViewModel;
import cz.muni.fi.spnp.gui.viewmodel.ElementViewModel;

import java.util.ArrayList;
import java.util.List;

public class Clipboard {

    private final List<FunctionViewModel> functions;
    private final List<ElementViewModel> elements;
    private OperationType operationType;

    public Clipboard() {
        functions = new ArrayList<>();
        elements = new ArrayList<>();
    }

    public List<FunctionViewModel> getFunctions() {
        return functions;
    }

    public List<ElementViewModel> getElements() {
        return elements;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public enum OperationType {
        COPY,
        CUT
    }

}

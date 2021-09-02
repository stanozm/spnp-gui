package cz.muni.fi.spnp.gui.components.menu.views.variables;

import cz.muni.fi.spnp.gui.components.menu.views.general.GeneralItemView;
import cz.muni.fi.spnp.gui.components.menu.views.general.GeneralItemsTableView;
import cz.muni.fi.spnp.gui.components.menu.views.general.ItemViewMode;
import cz.muni.fi.spnp.gui.model.Model;
import cz.muni.fi.spnp.gui.viewmodel.VariableViewModel;

public class VariablesTableView extends GeneralItemsTableView<VariableViewModel> {

    public VariablesTableView(Model model) {
        super(model, "variable");

        addColumn("Name", "name");
        addColumn("Kind", "kind");
        addColumn("Type", "type");
        addColumn("Value", "value");
    }

    @Override
    public VariableViewModel createViewModel() {
        return new VariableViewModel();
    }

    @Override
    public GeneralItemView<VariableViewModel> createItemViewAdd(VariableViewModel variableViewModel) {
        return new VariableView(model, variableViewModel, null, ItemViewMode.ADD);
    }

    @Override
    public GeneralItemView<VariableViewModel> createItemViewEdit(VariableViewModel variableViewModel) {
        var copy = viewModelCopyFactory.createCopy(variableViewModel);
        return new VariableView(model, copy, variableViewModel, ItemViewMode.EDIT);
    }

}


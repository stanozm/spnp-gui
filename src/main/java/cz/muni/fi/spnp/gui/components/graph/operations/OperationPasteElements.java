package cz.muni.fi.spnp.gui.components.graph.operations;

import cz.muni.fi.spnp.gui.components.menu.views.functions.FunctionViewModel;
import cz.muni.fi.spnp.gui.model.Clipboard;
import cz.muni.fi.spnp.gui.model.Model;
import cz.muni.fi.spnp.gui.viewmodel.*;

import java.util.Collection;
import java.util.List;

public class OperationPasteElements extends GraphElementsOperationBase {

    public OperationPasteElements(Model model, DiagramViewModel diagramViewModel) {
        super(model, diagramViewModel);
    }

    @Override
    public void execute() {
        switch (model.getClipboard().getOperationType()) {
            case COPY:
                var newCopies = createViewModelsCopies(model.getClipboardElements());
                removeUncutPlaceReferences(newCopies);
                var functionsCopies = redirectFunctionReferences(newCopies, diagramViewModel, Clipboard.OperationType.COPY);

                renameFunctionsIfNeeded(diagramViewModel, functionsCopies);
                diagramViewModel.getFunctions().addAll(functionsCopies);

                renameElementsIfNeeded(diagramViewModel, newCopies);
                offsetElements(newCopies);
                diagramViewModel.getElements().addAll(newCopies);
                diagramViewModel.select(ViewModelUtils.includeDragPoints(newCopies));
                break;

            case CUT:
                renameFunctionsIfNeeded(diagramViewModel, model.getClipboardFunctions());
                diagramViewModel.getFunctions().addAll(model.getClipboardFunctions());
                renameElementsIfNeeded(diagramViewModel, model.getClipboardElements());
                offsetElements(model.getClipboardElements());
                diagramViewModel.getElements().addAll(model.getClipboardElements());
                diagramViewModel.select(ViewModelUtils.includeDragPoints(model.getClipboardElements()));
                model.getClipboardElements().clear();
                break;
        }
    }

    private void offsetElements(List<ElementViewModel> elements) {
        final double offsetX = 20;
        final double offsetY = 20;

        for (var element : elements) {
            if (element instanceof ConnectableViewModel) {
                var connectableViewModel = (ConnectableViewModel) element;
                connectableViewModel.positionXProperty().set(connectableViewModel.getPositionX() + offsetX);
                connectableViewModel.positionYProperty().set(connectableViewModel.getPositionY() + offsetY);
            } else if (element instanceof ArcViewModel) {
                var arcViewModel = (ArcViewModel) element;
                arcViewModel.getDragPoints().forEach(dragPointViewModel -> {
                    dragPointViewModel.positionXProperty().set(dragPointViewModel.getPositionX() + offsetX);
                    dragPointViewModel.positionYProperty().set(dragPointViewModel.getPositionY() + offsetY);
                });
            }
        }
    }

    private void renameFunctionsIfNeeded(DiagramViewModel diagramViewModel, Collection<FunctionViewModel> clipboardFunctions) {
        for (var clipboardFunction : clipboardFunctions) {
            int id = 2;
            var oldName = clipboardFunction.getName();
            while (diagramViewModel.getFunctionByName(clipboardFunction.getName()) != null) {
                clipboardFunction.nameProperty().set(oldName + id);
                id++;
            }
        }
    }

    private void renameElementsIfNeeded(DiagramViewModel diagramViewModel, List<ElementViewModel> newCopies) {
        for (var copy : newCopies) {
            int id = 1;
            var oldName = copy.getName();
            while (diagramViewModel.containsElementNameType(copy)) {
                var suffix = id == 1 ? String.format("_%s", getSuffixWord()) : String.format("_%s_%d", getSuffixWord(), id);
                copy.nameProperty().set(oldName + suffix);
                id++;
            }
        }
    }

    private String getSuffixWord() {
        switch (model.getClipboard().getOperationType()) {
            case COPY:
                return "copy";
            case CUT:
                return "moved";
            default:
                throw new AssertionError("Unsupported clipboard operation type " + model.getClipboard().getOperationType());
        }
    }

}

/*******************************************************************************
 * Copyright (c) 2024 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.syson.easymod.form.fbstopbs.descriptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.representations.IStatus;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.tables.components.SelectCellComponent;
import org.eclipse.sirius.components.tables.elements.TextfieldCellElementProps;
import org.eclipse.syson.easymod.form.fbstopbs.services.EasymodFBSToPBSService;
import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.PartUsage;

/**
 * Helper class for Table construction.
 *
 * @author ebausson
 */
public class EasymodTableDescriptionBuilderHelper {

    private final EasymodFBSToPBSService easymodFBSToPBSService;

    public EasymodTableDescriptionBuilderHelper(EasymodFBSToPBSService easymodFBSToPBSService) {
        this.easymodFBSToPBSService = easymodFBSToPBSService;
    }

    /*********************
     *** Cell Helpers. ***
     *********************/

    protected BiFunction<VariableManager, Object, String> getCellTypeProvider() {
        return (variableManager, columnTargetObject) -> {
            return TextfieldCellElementProps.TYPE;
        };
    }

    protected BiFunction<VariableManager, Object, Object> getCellValueProvider() {
        return (variableManager, columnTargetObject) -> {
            Object value = "";
            Optional<EObject> optionalEObject = variableManager.get(VariableManager.SELF, EObject.class);
            if (optionalEObject.isPresent()) {
                EObject eObject = optionalEObject.get();
                if (EasymodTableDescriptionFactory.FUNCTIONS_COLUMN_LABEL.equals(columnTargetObject) && eObject instanceof ActionUsage function) {
                    value = function.getDeclaredName();
                } else if (EasymodTableDescriptionFactory.PRODUCTS_COLUMN_LABEL.equals(columnTargetObject) && eObject instanceof ActionUsage function) {
                    PartUsage product = easymodFBSToPBSService.getAllocatedProductIfExist(function);
                    if (product != null) {
                        value = product.getDeclaredName();
                    } else {
                        value = "";
                    }
                }
            }
            return value;
        };
    }

    protected Function<VariableManager, String> getCellOptionsIdProvider(IObjectService objectService) {
        return variableManager -> {
            Object candidate = variableManager.getVariables().get(SelectCellComponent.CANDIDATE_VARIABLE);
            if (candidate instanceof EEnumLiteral) {
                return objectService.getLabel(candidate);
            }
            return objectService.getId(candidate);
        };
    }

    protected Function<VariableManager, String> getCellOptionsLabelProvider(IObjectService objectService) {
        return variableManager -> {
            Object candidate = variableManager.getVariables().get(SelectCellComponent.CANDIDATE_VARIABLE);
            if (candidate instanceof EEnumLiteral) {
                return objectService.getLabel(candidate);
            }
            return objectService.getLabel(candidate);
        };
    }

    protected BiFunction<VariableManager, Object, List<Object>> getCellOptionsProvider() {
        return (variableManager, columnTargetObject) -> {
            return new ArrayList<Object>();
        };
    }

    BiFunction<VariableManager, Object, IStatus> getNewCellValueHandler() {
        BiFunction<VariableManager, Object, IStatus> newCellValueHandler = (variableManager, newValue) -> {
            return new Success();
        };
        return newCellValueHandler;
    }

    /***********************
     *** Widget Helpers. ***
     ***********************/

    protected Function<VariableManager, Boolean> isWidgetReadOnly() {
        return variableManager -> true;
    }

}

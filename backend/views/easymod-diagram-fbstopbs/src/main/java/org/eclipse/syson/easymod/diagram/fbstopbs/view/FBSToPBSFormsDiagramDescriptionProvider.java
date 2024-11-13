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
package org.eclipse.syson.easymod.diagram.fbstopbs.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IEditService;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.forms.ContainerBorderStyle;
import org.eclipse.sirius.components.forms.GroupDisplayMode;
import org.eclipse.sirius.components.forms.description.AbstractControlDescription;
import org.eclipse.sirius.components.forms.description.ButtonDescription;
import org.eclipse.sirius.components.forms.description.FormDescription;
import org.eclipse.sirius.components.forms.description.GroupDescription;
import org.eclipse.sirius.components.forms.description.PageDescription;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.interpreter.Result;
import org.eclipse.sirius.components.representations.GetOrCreateRandomIdProvider;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.emf.form.IFormIdProvider;
import org.eclipse.sirius.components.view.emf.form.IWidgetConverterProvider;

/**
 * Description of the FBStoPBS Form diagram .
 *
 * @author ebausson
 */
public class FBSToPBSFormsDiagramDescriptionProvider {

    public static final String FORM_NAME = "Function Allocation Form";

    private static final String DESCRIPTION_NAME = "Allocated Function on LogicalConstituent form";

    private static final String FORM_ID = "FBSTOPBS_FORM_DESCRIPTION";

    private static final String PAGE_ID = FORM_ID + "_PAGE";

    private static final String PIE_CHART_GROUP_ID = FORM_ID + "_PIE_CHART";

    public FormDescription createFormDescription(IObjectService objectService, IEditService editService, IFormIdProvider formIdProvider, List<IWidgetConverterProvider> customWidgetConverterProviders,
            IFeedbackMessageService feedbackMessageService, AQLInterpreter interpreter) {

        Function<VariableManager, String> targetObjectIdProvider = variableManager -> this.self(variableManager)
                .map(objectService::getId)
                .orElse(null);

        return FormDescription.newFormDescription(getIdFromName(FBSToPBSFormsDiagramDescriptionProvider.FORM_ID))
                .label(FBSToPBSFormsDiagramDescriptionProvider.DESCRIPTION_NAME)
                .idProvider(new GetOrCreateRandomIdProvider())
                .labelProvider(x -> FBSToPBSFormsDiagramDescriptionProvider.DESCRIPTION_NAME)
                .canCreatePredicate(x -> true)
                .targetObjectIdProvider(targetObjectIdProvider)
                .pageDescriptions(this.getPageDescriptions(interpreter))
                .build();
    }

    private List<PageDescription> getPageDescriptions(AQLInterpreter interpreter) {
        var onlyPage = PageDescription.newPageDescription(this.getIdFromName(FBSToPBSFormsDiagramDescriptionProvider.PAGE_ID))
                .idProvider(new GetOrCreateRandomIdProvider())
                .labelProvider(x -> "")
                .semanticElementsProvider(variableManager -> this.getSelfSemanticElementsProvider(variableManager, interpreter))
                .canCreatePredicate(x -> true)
                .groupDescriptions(List.of(getGroupDescription(interpreter)))
                .build();
        return List.of(onlyPage);
    }

    private GroupDescription getGroupDescription(AQLInterpreter interpreter) {
        return GroupDescription.newGroupDescription(this.getIdFromName(FBSToPBSFormsDiagramDescriptionProvider.PIE_CHART_GROUP_ID))
                .idProvider(new GetOrCreateRandomIdProvider())
                .labelProvider(x -> FBSToPBSFormsDiagramDescriptionProvider.DESCRIPTION_NAME)
                .displayModeProvider(x -> GroupDisplayMode.LIST)
                .semanticElementsProvider(variableManager -> this.getSelfSemanticElementsProvider(variableManager, interpreter))
                .controlDescriptions(new ArrayList<AbstractControlDescription>())
                .toolbarActionDescriptions(new ArrayList<ButtonDescription>())
                .displayModeProvider(variableManager -> GroupDisplayMode.LIST)
                .borderStyleProvider(this.getBorderStyleProvider())
                .build();
    }

    private String getIdFromName(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes()).toString();
    }

    private Optional<Object> self(VariableManager variableManager) {
        return variableManager.get(VariableManager.SELF, Object.class);
    }

    private List<?> getSelfSemanticElementsProvider(VariableManager variableManager, AQLInterpreter interpreter) {
        return getSemanticElementsProvider("aql:self", variableManager, interpreter);
    }

    private List<?> getSemanticElementsProvider(String semanticCandidatesExpression, VariableManager variableManager, AQLInterpreter interpreter) {
        Result result = interpreter.evaluateExpression(variableManager.getVariables(), semanticCandidatesExpression);
        List<Object> candidates = result.asObjects().orElse(List.of());
        return candidates.stream()
                .filter(EObject.class::isInstance)
                .map(EObject.class::cast)
                .toList();
    }

    private Function<VariableManager, ContainerBorderStyle> getBorderStyleProvider() {
        return variableManager -> {
            return ContainerBorderStyle.newContainerBorderStyle()
                    .build();
        };
    }

}

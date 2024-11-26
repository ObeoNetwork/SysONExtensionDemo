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
import java.util.UUID;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.forms.ContainerBorderLineStyle;
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
import org.eclipse.syson.easymod.form.fbstopbs.services.EasymodFBSToPBSService;
import org.eclipse.syson.sysml.SysmlPackage;

/**
 * Description of the FBStoPBS Form.
 *
 * @author ebausson
 */
public class FBSToPBSFormDescriptionProvider {

    public static final String FORM_NAME = "Function Allocation Form";

    public static final String DESCRIPTION_NAME = "Allocated Function on LogicalConstituent form";

    private static final String FORM_ID = "FBSTOPBS_FORM_DESCRIPTION";

    private static final String PAGE_ID = FORM_ID + "_PAGE";

    private static final String FBS_TO_PBS_FORM_GROUP_ID = FORM_ID + "_GROUP";

    public FormDescription createFormDescription(IObjectService objectService, IIdentityService identityService) {

        Function<VariableManager, String> targetObjectIdProvider = variableManager -> this.self(variableManager)
                .map(objectService::getId)
                .orElse(null);

        return FormDescription.newFormDescription(getIdFromName(FBSToPBSFormDescriptionProvider.FORM_ID))
                .label(FBSToPBSFormDescriptionProvider.DESCRIPTION_NAME)
                .idProvider(new GetOrCreateRandomIdProvider())
                .labelProvider(x -> FBSToPBSFormDescriptionProvider.DESCRIPTION_NAME)
                .canCreatePredicate(x -> true)
                .targetObjectIdProvider(targetObjectIdProvider)
                .pageDescriptions(this.getPageDescriptions(targetObjectIdProvider, objectService, identityService))
                .build();
    }

    private List<PageDescription> getPageDescriptions(Function<VariableManager, String> targetObjectIdProvider, IObjectService objectService, IIdentityService identityService) {

        AQLInterpreter interpreter = createAQLInterpreter();

        var onlyPage = PageDescription.newPageDescription(this.getIdFromName(FBSToPBSFormDescriptionProvider.PAGE_ID))
                .idProvider(new GetOrCreateRandomIdProvider())
                .labelProvider(x -> "")
                .semanticElementsProvider(variableManager -> this.getSelfSemanticElementsProvider(interpreter, variableManager))
                .canCreatePredicate(x -> true)
                .groupDescriptions(List.of(getGroupDescription(interpreter, targetObjectIdProvider, objectService, identityService)))
                .build();
        return List.of(onlyPage);
    }

    private GroupDescription getGroupDescription(AQLInterpreter interpreter, Function<VariableManager, String> targetObjectIdProvider, IObjectService objectService, IIdentityService identityService) {

        AbstractControlDescription piechartDescription = new EasymodPieChartDescriptionFactory(interpreter,
                targetObjectIdProvider).generateDescription();

        AbstractControlDescription tableDescription = new EasymodTableDescriptionFactory(interpreter, targetObjectIdProvider, objectService, identityService)
                .generateDescription();

        return GroupDescription.newGroupDescription(this.getIdFromName(FBSToPBSFormDescriptionProvider.FBS_TO_PBS_FORM_GROUP_ID))
                .idProvider(new GetOrCreateRandomIdProvider())
                .labelProvider(x -> "")
                .displayModeProvider(x -> GroupDisplayMode.LIST)
                .semanticElementsProvider(variableManager -> this.getSelfSemanticElementsProvider(interpreter, variableManager))
                .controlDescriptions(List.of(piechartDescription, tableDescription))
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

    private List<?> getSelfSemanticElementsProvider(AQLInterpreter interpreter, VariableManager variableManager) {
        return getSemanticElementsProvider(interpreter, "aql:self", variableManager);
    }

    private List<?> getSemanticElementsProvider(AQLInterpreter interpreter, String semanticCandidatesExpression, VariableManager variableManager) {
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
                    .lineStyle(ContainerBorderLineStyle.Solid)
                    .build();
        };
    }

    private AQLInterpreter createAQLInterpreter() {
        EasymodFBSToPBSService service = new EasymodFBSToPBSService();
        return new AQLInterpreter(List.of(), List.of(service), List.of(SysmlPackage.eINSTANCE));
    }

}

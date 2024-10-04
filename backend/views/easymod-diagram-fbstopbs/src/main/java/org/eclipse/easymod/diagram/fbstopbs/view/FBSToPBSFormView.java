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
package org.eclipse.easymod.diagram.fbstopbs.view;

import org.eclipse.sirius.components.view.form.FlexDirection;
import org.eclipse.sirius.components.view.form.FormDescription;
import org.eclipse.sirius.components.view.form.FormFactory;
import org.eclipse.sirius.components.view.form.GroupDescription;
import org.eclipse.sirius.components.view.form.PageDescription;
import org.eclipse.sirius.components.view.form.PieChartDescription;
import org.eclipse.syson.sysml.SysmlPackage;
import org.eclipse.syson.util.AQLConstants;
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * Description of the FBStoPBS View diagram using the ViewBuilder API from Sirius Web.
 *
 * @author ebausson
 */
public class FBSToPBSFormView {

    public static final String DESCRIPTION_NAME = "FBS To PBS View";

    public FormDescription createFormDescription() {
        String domainType = SysMLMetamodelHelper.buildQualifiedName(SysmlPackage.eINSTANCE.getNamespace());

        var formDescription = FormFactory.eINSTANCE.createFormDescription();
        formDescription.setDomainType(domainType);
        formDescription.setName(DESCRIPTION_NAME);
        formDescription.setTitleExpression(DESCRIPTION_NAME);

        this.createAllocatedFunctionsPage(formDescription);

        // this.createAllocatedPartPage(formDescription);

        return formDescription;
    }

    private void createAllocatedFunctionsPage(FormDescription formDescription) {
        createPage("Function", "Function");
    }

    private void createAllocatedPartPage(FormDescription formDescription) {
        createPage("Part", "Part");
    }

    private PageDescription createPage(String name, String labelExpression) {
        // Create page
        PageDescription pageDescription = FormFactory.eINSTANCE.createPageDescription();
        pageDescription.setName(name + "Page");
        pageDescription.setLabelExpression(labelExpression);
        pageDescription.setSemanticCandidatesExpression(AQLConstants.AQL_SELF);

        pageDescription.getGroups().add(createPieChartSection(name));

        return pageDescription;
    }

    private GroupDescription createPieChartSection(String name) {
        var groupDescription = FormFactory.eINSTANCE.createGroupDescription();

        var flexboxContainer = FormFactory.eINSTANCE.createFlexboxContainerDescription();
        flexboxContainer.setName(name + "Container");
        flexboxContainer.setLabelExpression("");
        flexboxContainer.setFlexDirection(FlexDirection.ROW);
        groupDescription.getChildren().add(flexboxContainer);
        // TODO
        PieChartDescription pieChartDescription = FormFactory.eINSTANCE.createPieChartDescription();
        pieChartDescription.setKeysExpression("aql:Sequence{'a', 'b', 'c'}");
        pieChartDescription.setValuesExpression("aql:Sequence{24.0, 18.2, 3.7}");
        pieChartDescription.setLabelExpression("test47");
        flexboxContainer.getChildren().add(pieChartDescription);

        // flexboxContainer.getChildren().add(new ViewFormDescriptionConverterSwitch().casePieChartDescription(null));

        return groupDescription;
    }

}

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
package org.eclipse.syson.easymod.diagram.form.methodology.views;

import java.util.List;

import org.eclipse.sirius.components.view.ViewFactory;
import org.eclipse.sirius.components.view.form.FlexDirection;
import org.eclipse.sirius.components.view.form.FormDescription;
import org.eclipse.sirius.components.view.form.FormFactory;
import org.eclipse.sirius.components.view.form.GroupDescription;
import org.eclipse.sirius.components.view.form.ImageDescription;
import org.eclipse.sirius.components.view.form.ListDescription;
import org.eclipse.sirius.components.view.form.PageDescription;
import org.eclipse.syson.sysml.SysmlPackage;
import org.eclipse.syson.util.AQLConstants;
import org.eclipse.syson.util.AQLUtils;
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * Create the methodology form guiding new users.
 *
 * @author ebausson
 */
public class MethodologyFormView {
    public static final String REP_NAME = "Methodology";

    private static final String FUNCTION_PAGE_LABEL = "Functional flow";

    private static final String PRODUCT_PAGE_LABEL = "Logical flow";

    private static final String ALLOCATION_PAGE_LABEL = "Allocation cockpit";

    private static final String FUNCTION_DESCRIPTION = "Define the functions of your system and their interaction.";

    private static final String PRODUCT_DESCRIPTION = "Define the logical architecture of your system.";

    private static final String ALLOCATION_DESCRIPTION = "Define the allocation process.";

    private static final String FUNCTION_LONG_DESCRIPTION = "The Functional Flow Diagram allows users to visually model and organize the functions and their interactions. Users can create, connect, and manage functional blocks to represent the functional architecture of the system.";

    private static final String PRODUCT_LONG_DESCRIPTION = "The Logical Flow Diagram enables users to define and visualize the system's logical architecture, detailing the interactions between subsystems. Users can create, allocate, and connect logical components, illustrating how different parts of the system work together to fulfill the system's functional requirements. This diagram helps in refining system structure, understanding component dependencies, and ensuring the logical integrity of the overall architecture.";

    private static final String ALLOCATION_LONG_DESCRIPTION = " In the Allocation Cockpit, users can visualize the progress of the allocation process through interactive charts and detailed tables. This feature provides real-time insights into how functions are being assigned to components, helping users track and manage the allocation process efficiently. The charts offer a graphical representation of allocation progress, while the tables provide precise data for a more granular view, ensuring users can easily monitor their system's development and allocation completion.";

    private static final String FUNCTION_IMAGE_PATH = "/diagram-images/diagram.svg";

    private static final String PRODUCT_IMAGE_PATH = "/diagram-images/diagram.svg";

    private static final String ALLOCATION_IMAGE_PATH = "/diagram-images/diagram.svg";

    private static final String FUNCTION_DIAGRAM_NAME = "FBSViewDiagram";

    private static final String PRODUCT_DIAGRAM_NAME = "PBSViewDiagram";

    private static final String ALLOCATION_DIAGRAM_NAME = "AllocationViewDiagram";

    private static final String FUNCTION_DIAGRAM_ID = "588b42a6-5652-34d6-884d-70511e2b5e58";

    private static final String PRODUCT_DIAGRAM_ID = "c4bb502e-03d9-353e-aea2-de3128fc68aa";

    private static final String ALLOCATION_DIAGRAM_ID = "5fb6a51c-bacd-395b-a021-ecf81e8ffa41";

    private static final String FUNCTION_DIAGRAM_ELEMENT_ID = "42c7d51d-ddc0-3f80-ba8f-d41bd05bb069";

    private static final String PRODUCT_DIAGRAM_ELEMENT_ID = "ae994350-3a1e-392b-94f7-cf2377699ca5";

    private static final String ALLOCATION_DIAGRAM_ELEMENT_ID = "c56356be-fdca-3129-a999-9bf6cfd3c0b8";

    public FormDescription createFormDescription() {
        String domainType = SysMLMetamodelHelper.buildQualifiedName(SysmlPackage.eINSTANCE.getNamespace());

        var formDescription = FormFactory.eINSTANCE.createFormDescription();
        formDescription.setDomainType(domainType);
        formDescription.setName(REP_NAME);
        formDescription.setTitleExpression(REP_NAME);

        this.createFunctionPage(formDescription);

        this.createProductPage(formDescription);

        this.createAllocationPage(formDescription);

        return formDescription;
    }

    private void createFunctionPage(FormDescription formDescription) {
        PageDescription functionBreakdownStructurePageDescription = createPage("FunctionBreakdownStructure", MethodologyFormView.FUNCTION_PAGE_LABEL,
                MethodologyFormView.FUNCTION_DESCRIPTION, MethodologyFormView.FUNCTION_LONG_DESCRIPTION, FUNCTION_IMAGE_PATH, "create FFD",
                MethodologyFormView.FUNCTION_DIAGRAM_NAME, MethodologyFormView.FUNCTION_DIAGRAM_ID, MethodologyFormView.FUNCTION_DIAGRAM_ELEMENT_ID);
        formDescription.getPages().add(functionBreakdownStructurePageDescription);
    }

    private void createProductPage(FormDescription formDescription) {
        PageDescription productBreakdownStructurePageDescription = createPage("ProductBreakdownStructure", MethodologyFormView.PRODUCT_PAGE_LABEL,
                MethodologyFormView.PRODUCT_DESCRIPTION, MethodologyFormView.PRODUCT_LONG_DESCRIPTION, PRODUCT_IMAGE_PATH, "create LFD",
                MethodologyFormView.PRODUCT_DIAGRAM_NAME, MethodologyFormView.PRODUCT_DIAGRAM_ID, MethodologyFormView.PRODUCT_DIAGRAM_ELEMENT_ID);
        formDescription.getPages().add(productBreakdownStructurePageDescription);
    }

    private void createAllocationPage(FormDescription formDescription) {
        PageDescription fbsToPbsAllocationPageDescription = createPage("FbsToPbsAllocation", MethodologyFormView.ALLOCATION_PAGE_LABEL,
                MethodologyFormView.ALLOCATION_DESCRIPTION, MethodologyFormView.ALLOCATION_LONG_DESCRIPTION, ALLOCATION_IMAGE_PATH, "create AC",
                MethodologyFormView.ALLOCATION_DIAGRAM_NAME, MethodologyFormView.ALLOCATION_DIAGRAM_ID, MethodologyFormView.ALLOCATION_DIAGRAM_ELEMENT_ID);
        formDescription.getPages().add(fbsToPbsAllocationPageDescription);
    }

    // CHECKSTYLE:OFF
    private PageDescription createPage(
            String name,
            String labelExpression,
            String description,
            String longDescription,
            String imagePath,
            String buttonLabel,
            String diagramName,
            String diagramID,
            String diagramDescriptionID) {
        // CHECKSTYLE:ON
        // Create page
        PageDescription pageDescription = FormFactory.eINSTANCE.createPageDescription();
        pageDescription.setName(name + "Page");
        pageDescription.setLabelExpression(labelExpression);
        pageDescription.setSemanticCandidatesExpression(AQLConstants.AQL_SELF);

        // Header
        pageDescription.getGroups().add(createHeaderBlock(name, description, longDescription));

        // Interactive section
        pageDescription.getGroups().add(createInteractiveSection(name, imagePath, buttonLabel, diagramID, diagramDescriptionID));

        return pageDescription;

    }

    private GroupDescription createHeaderBlock(String name, String description, String longDescription) {
        GroupDescription descGroupDescription = FormFactory.eINSTANCE.createGroupDescription();
        descGroupDescription.setName(name + "DescGroup");
        descGroupDescription.setLabelExpression(description);
        descGroupDescription.setSemanticCandidatesExpression(AQLConstants.AQL_SELF);
        var descLabel = FormFactory.eINSTANCE.createLabelDescription();
        descLabel.setName(name + "LongDescGroup");
        descLabel.setLabelExpression("");
        descLabel.setValueExpression(longDescription);
        descGroupDescription.getChildren().add(descLabel);

        descGroupDescription.getChildren().add(FormFactory.eINSTANCE.createLabelDescription());

        descGroupDescription.getChildren().add(FormFactory.eINSTANCE.createLabelDescription());

        return descGroupDescription;
    }

    private GroupDescription createInteractiveSection(String name, String imagePath, String buttonLabel, String diagramID, String diagramDescriptionID) {
        var groupDescription = FormFactory.eINSTANCE.createGroupDescription();

        var flexboxContainer = FormFactory.eINSTANCE.createFlexboxContainerDescription();
        flexboxContainer.setName(name + "Container");
        flexboxContainer.setLabelExpression("");
        flexboxContainer.setFlexDirection(FlexDirection.ROW);
        groupDescription.getChildren().add(flexboxContainer);

        // Padding to move the image close to the create button
        flexboxContainer.getChildren().add(FormFactory.eINSTANCE.createFlexboxContainerDescription());

        // Image
        ImageDescription image = FormFactory.eINSTANCE.createImageDescription();
        image.setName(name + "Image");
        image.setLabelExpression("");
        image.setMaxWidthExpression("40");
        image.setUrlExpression(imagePath);
        flexboxContainer.getChildren().add(image);

        // Button to create representation
        var button = FormFactory.eINSTANCE.createButtonDescription();
        button.setName(name + "Button");
        button.setLabelExpression("");
        button.setButtonLabelExpression(buttonLabel);
        var createOperation = ViewFactory.eINSTANCE.createChangeContext();
        String createRepresentationRequestURL = "'siriusComponents://representationDescription"
                + "?kind=diagramDescription"
                + "&sourceKind=view"
                + "&sourceId=" + diagramID
                + "&sourceElementId=" + diagramDescriptionID
                + "'";
        createOperation.setExpression(AQLUtils.getSelfServiceCallExpression("createRepresentation", List.of(createRepresentationRequestURL, "editingContext")));
        button.getBody().add(createOperation);
        flexboxContainer.getChildren().add(button);

        // List of existings Representations
        ListDescription representationsViewer = FormFactory.eINSTANCE.createListDescription();
        representationsViewer.setName(name + "List");
        representationsViewer.setLabelExpression("Representations Viewer");
        representationsViewer.setValueExpression(AQLUtils.getSelfServiceCallExpression("getAllRepresentations", "Sequence{'" + diagramDescriptionID + "'}, editingContext)"));
        representationsViewer.setDisplayExpression(AQLUtils.getServiceCallExpression("candidate", "getLabel"));
        representationsViewer.setIsDeletableExpression("aql:false");
        flexboxContainer.getChildren().add(representationsViewer);

        return groupDescription;
    }

}

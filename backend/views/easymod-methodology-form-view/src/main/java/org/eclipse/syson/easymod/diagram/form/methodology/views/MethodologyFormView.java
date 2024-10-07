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
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * Create the methodology form guiding new users.
 *
 * @author ebausson
 */
public class MethodologyFormView {
    public static final String REP_NAME = "Methodology";

    private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
            + "Aenean dictum laoreet egestas. Etiam nec velit placerat, eleifend mauris a, placerat eros. "
            + "Morbi id lorem sit amet ligula lacinia dapibus. Nullam efficitur bibendum est eget fermentum. Morbi ut orci sit amet neque eleifend gravida. "
            + "Nam sed commodo orci. Phasellus ullamcorper velit eget euismod fermentum. Curabitur aliquet est quis pellentesque sagittis. "
            + "Curabitur vitae ipsum feugiat, pellentesque elit id, auctor mauris. Sed lacinia quam at turpis euismod, vitae aliquet diam pulvinar. "
            + "In arcu lorem, sagittis ut ligula vel, molestie gravida odio.";

    private static final String FUNCTION_DESCRIPTION = "Define the functions and subfunctions of your system.";

    private static final String PRODUCT_DESCRIPTION = "Define the product and its components.";

    private static final String ALLOCATION_DESCRIPTION = "Define which function is allocated or not.";

    private static final String FUNCTION_LONG_DESCRIPTION = LOREM_IPSUM;

    private static final String PRODUCT_LONG_DESCRIPTION = LOREM_IPSUM;

    private static final String ALLOCATION_LONG_DESCRIPTION = LOREM_IPSUM;

    private static final String FUNCTION_IMAGE_PATH = "/diagram-images/diagram.svg";

    private static final String PRODUCT_IMAGE_PATH = "/diagram-images/diagram.svg";

    private static final String ALLOCATION_IMAGE_PATH = "/diagram-images/diagram.svg";

    private static final String FUNCTION_DIAGRAM_NAME = "FBSViewDiagram";

    private static final String PRODUCT_DIAGRAM_NAME = "PBSViewDiagram";

    private static final String ALLOCATION_DIAGRAM_NAME = "AllocationViewDiagram";

    private static final String FUNCTION_DIAGRAM_ID = "588b42a6-5652-34d6-884d-70511e2b5e58";

    private static final String PRODUCT_DIAGRAM_ID = "c4bb502e-03d9-353e-aea2-de3128fc68aa";

    private static final String ALLOCATION_DIAGRAM_ID = "5fb6a51c-bacd-395b-a021-ecf81e8ffa41";

    private static final String FUNCTION_DIAGRAM_ELEMENT_ID = "7da9f558-0a5f-3c4c-b1c2-db38114b767f";

    private static final String PRODUCT_DIAGRAM_ELEMENT_ID = "eab2d525-254e-3414-8116-49a8f4d64aad";

    private static final String ALLOCATION_DIAGRAM_ELEMENT_ID = "290a3846-1c81-3c6a-8eb0-a7def302fa11";

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
        PageDescription functionBreakdownStructurePageDescription = createPage("FunctionBreakdownStructure", "Function", MethodologyFormView.FUNCTION_DESCRIPTION,
                MethodologyFormView.FUNCTION_LONG_DESCRIPTION, FUNCTION_IMAGE_PATH, "create FBS",
                MethodologyFormView.FUNCTION_DIAGRAM_NAME, MethodologyFormView.FUNCTION_DIAGRAM_ID, MethodologyFormView.FUNCTION_DIAGRAM_ELEMENT_ID);
        formDescription.getPages().add(functionBreakdownStructurePageDescription);
    }

    private void createProductPage(FormDescription formDescription) {
        PageDescription productBreakdownStructurePageDescription = createPage("ProductBreakdownStructure", "Product", MethodologyFormView.PRODUCT_DESCRIPTION,
                MethodologyFormView.PRODUCT_LONG_DESCRIPTION, PRODUCT_IMAGE_PATH, "create PBS",
                MethodologyFormView.PRODUCT_DIAGRAM_NAME, MethodologyFormView.PRODUCT_DIAGRAM_ID, MethodologyFormView.PRODUCT_DIAGRAM_ELEMENT_ID);
        formDescription.getPages().add(productBreakdownStructurePageDescription);
    }

    private void createAllocationPage(FormDescription formDescription) {
        PageDescription fbsToPbsAllocationPageDescription = createPage("FbsToPbsAllocation", "Allocation", MethodologyFormView.ALLOCATION_DESCRIPTION,
                MethodologyFormView.ALLOCATION_LONG_DESCRIPTION, ALLOCATION_IMAGE_PATH, "create FBS to PBS",
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
        createOperation.setExpression("aql:self.createRepresentation('siriusComponents://representationDescription?kind=diagramDescription&sourceKind=view&sourceId=" + diagramID + "&sourceElementId="
                + diagramDescriptionID + "', editingContext)");
        button.getBody().add(createOperation);
        flexboxContainer.getChildren().add(button);

        // List of existings Representations
        ListDescription representationsViewer = FormFactory.eINSTANCE.createListDescription();
        representationsViewer.setName(name + "List");
        representationsViewer.setLabelExpression("Representations Viewer");
        representationsViewer.setValueExpression("aql:self.getAllRepresentations(Sequence{'" + diagramDescriptionID + "'}, editingContext)");
        representationsViewer.setDisplayExpression("aql:candidate.getLabel()");
        representationsViewer.setIsDeletableExpression("aql:false");
        flexboxContainer.getChildren().add(representationsViewer);

        return groupDescription;
    }

}

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
package org.eclipse.easymod.diagram.fbs;

import java.util.List;

import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.diagram.ConditionalNodeStyle;
import org.eclipse.sirius.components.view.diagram.ImageNodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.diagram.NodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import org.eclipse.syson.diagram.common.view.nodes.AbstractNodeDescriptionProvider;
import org.eclipse.syson.sysml.SysmlPackage;
import org.eclipse.syson.util.AQLConstants;
import org.eclipse.syson.util.AQLUtils;
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * FunctionPort node description provider.
 * 
 * @author ebausson
 */
public class FunctionPortNodeDescriptionProvider extends AbstractNodeDescriptionProvider {

    public static final String NODE_NAME = "FunctionPort";

    public static final String UNSET_PORT_IMAGE_PATH = "/images/FunctionPortUnsetArrow.svg";

    public static final String INOUT_PORT_IMAGE_PATH = "/images/FunctionPortInOutArrow.svg";

    public static final String IN_PORT_IMAGE_PATH = "/images/FunctionPortInArrow.svg";

    public static final String OUT_PORT_IMAGE_PATH = "/images/FunctionPortOutArrow.svg";

    public FunctionPortNodeDescriptionProvider(IColorProvider colorProvider) {
        super(colorProvider);
    }

    @Override
    public NodeDescription create() {
        String domainType = SysMLMetamodelHelper.buildQualifiedName(SysmlPackage.eINSTANCE.getPortUsage());
        return this.diagramBuilderHelper.newNodeDescription()
                .domainType(domainType)
                .name(this.getName())
                .semanticCandidatesExpression(AQLConstants.AQL_SELF + ".nestedPort")
                .insideLabel(null)
                .defaultHeightExpression("10")
                .defaultWidthExpression("10")
                .userResizable(UserResizableDirection.NONE)
                .palette(diagramBuilderHelper.newNodePalette().build())
                .style(this.createPortNodeStyleDescription(UNSET_PORT_IMAGE_PATH))
                .conditionalStyles(this.createFunctionPortConditionalNodeStyles().toArray(ConditionalNodeStyle[]::new))
                .synchronizationPolicy(SynchronizationPolicy.SYNCHRONIZED)
                .build();
    }

    protected String getName() {
        return NODE_NAME;
    }

    protected NodeStyleDescription createNodeStyle(String color, String borderColor) {
        return this.diagramBuilderHelper
                .newRectangularNodeStyleDescription()
                .background(colorProvider.getColor(color))
                .borderColor(colorProvider.getColor(borderColor))
                .build();
    }

    protected ImageNodeStyleDescription createPortNodeStyleDescription(String shape) {
        return this.diagramBuilderHelper.newImageNodeStyleDescription()
                .borderSize(0)
                .positionDependentRotation(true)
                .shape(shape)
                .build();
    }

    private List<ConditionalNodeStyle> createFunctionPortConditionalNodeStyles() {
        return List.of(
                this.diagramBuilderHelper.newConditionalNodeStyle()
                        .condition(AQLUtils.getSelfServiceCallExpression("isInFeature"))
                        .style(this.createPortNodeStyleDescription(IN_PORT_IMAGE_PATH))
                        .build(),
                this.diagramBuilderHelper.newConditionalNodeStyle()
                        .condition(AQLUtils.getSelfServiceCallExpression("isOutFeature"))
                        .style(this.createPortNodeStyleDescription(OUT_PORT_IMAGE_PATH))
                        .build(),
                this.diagramBuilderHelper.newConditionalNodeStyle()
                        .condition(AQLUtils.getSelfServiceCallExpression("isInOutFeature"))
                        .style(this.createPortNodeStyleDescription(INOUT_PORT_IMAGE_PATH))
                        .build());
    }

}

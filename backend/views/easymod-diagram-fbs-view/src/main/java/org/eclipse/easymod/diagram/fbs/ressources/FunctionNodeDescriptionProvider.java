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
package org.eclipse.easymod.diagram.fbs.ressources;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.eclipse.easymod.diagram.utils.EasyModColorService;
import org.eclipse.easymod.diagram.view.AbstractEasymodNodeDescriptionProvider;
import org.eclipse.sirius.components.view.builder.IViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.diagram.InsideLabelDescriptionBuilder;
import org.eclipse.sirius.components.view.builder.generated.diagram.InsideLabelStyleBuilder;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.EdgeTool;
import org.eclipse.sirius.components.view.diagram.InsideLabelDescription;
import org.eclipse.sirius.components.view.diagram.InsideLabelPosition;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.diagram.NodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import org.eclipse.syson.util.AQLUtils;

/**
 * Function node description provider.
 * 
 * @author ebausson
 */
public class FunctionNodeDescriptionProvider extends AbstractEasymodNodeDescriptionProvider {

    public static final String NODE_NAME = "Function";

    private final DiagramBuilders diagramBuilderHelper = new DiagramBuilders();

    public FunctionNodeDescriptionProvider(IColorProvider colorProvider) {

    }

    @Override
    public NodeDescription create() {

        return this.diagramBuilderHelper.newNodeDescription()
                .domainType("SEIM::Function")
                .name(this.getName())
                .semanticCandidatesExpression("aql:self.getAllReachableActions(editingContext)")
                .insideLabel(this.generateDefaultLabel("aql:self.name"))
                .defaultHeightExpression("70")
                .defaultWidthExpression("150")
                .userResizable(UserResizableDirection.BOTH)
                .keepAspectRatio(true)
                .palette(diagramBuilderHelper.newNodePalette().build())
                .style(this.createNodeStyle(this.getColor(), this.getBorderColor()))
                .synchronizationPolicy(SynchronizationPolicy.SYNCHRONIZED)
                // .conditionalStyles(createConditionalAssumptionNodeStyle())
                .build();
    }

    protected String getSemanticCandidatesExpression() {
        return AQLUtils.getSelfServiceCallExpression("getEasyModFunctions");
    }

    protected InsideLabelDescription generateDefaultLabel(String expression) {
        return new InsideLabelDescriptionBuilder()
                .labelExpression(expression)
                .style(new InsideLabelStyleBuilder().build())
                .position(InsideLabelPosition.TOP_CENTER)
                .build();
    }

    protected NodeStyleDescription createNodeStyle(String color, String borderColor) {
        return this.diagramBuilderHelper
                .newRectangularNodeStyleDescription()
                .background(EasyModColorService.getInstance().getColor(color))
                .borderColor(EasyModColorService.getInstance().getColor(borderColor))
                .build();
    }

    @Override
    public void link(DiagramDescription diagramDescription, IViewDiagramElementFinder cache) {
        cache.getNodeDescription(this.getName()).ifPresent(nodeDescription -> {
            diagramDescription.getNodeDescriptions().add(nodeDescription);
            nodeDescription.getPalette().getEdgeTools().addAll(generateEdgeTools(cache));
        });
    }

    protected Collection<EdgeTool> generateEdgeTools(IViewDiagramElementFinder cache) {
        return cache.getNodeDescriptions().stream()
                .map(this::generateEdgeTools)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .toList();
    }

    protected Collection<EdgeTool> generateEdgeTools(NodeDescription targetNodeDescription) {
        return List.of();
    }

    protected String getName() {
        return NODE_NAME;
    }

}

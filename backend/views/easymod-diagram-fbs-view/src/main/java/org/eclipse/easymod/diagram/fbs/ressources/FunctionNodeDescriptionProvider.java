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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.eclipse.easymod.diagram.utils.EasyModColorService;
import org.eclipse.easymod.diagram.view.AbstractEasymodNodeDescriptionProvider;
import org.eclipse.sirius.components.view.ChangeContext;
import org.eclipse.sirius.components.view.ViewFactory;
import org.eclipse.sirius.components.view.builder.IViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.diagram.NodeToolSectionBuilder;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.DiagramFactory;
import org.eclipse.sirius.components.view.diagram.DropNodeTool;
import org.eclipse.sirius.components.view.diagram.EdgeTool;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.diagram.NodePalette;
import org.eclipse.sirius.components.view.diagram.NodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.NodeTool;
import org.eclipse.sirius.components.view.diagram.NodeToolSection;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import org.eclipse.sirius.components.view.diagram.provider.DefaultToolsFactory;
import org.eclipse.syson.sysml.SysmlPackage;
import org.eclipse.syson.util.AQLUtils;
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * Function node description provider.
 * 
 * @author ebausson
 */
public class FunctionNodeDescriptionProvider extends AbstractEasymodNodeDescriptionProvider {

    protected final ViewBuilders viewBuilderHelper = new ViewBuilders();

    private final DiagramBuilders diagramBuilderHelper = new DiagramBuilders();

    protected final DefaultToolsFactory defaultToolsFactory = new DefaultToolsFactory();

    protected final IColorProvider colorProvider;

    public static final String NODE_NAME = "Function";

    public FunctionNodeDescriptionProvider(IColorProvider colorProvider) {
        this.colorProvider = Objects.requireNonNull(colorProvider);
    }

    @Override
    public NodeDescription create() {
        String domainType = SysMLMetamodelHelper.buildQualifiedName(SysmlPackage.eINSTANCE.getActionUsage());
        return this.diagramBuilderHelper.newNodeDescription()
                .domainType(domainType)
                .name(this.getName())
                .semanticCandidatesExpression("aql:self.getFunctions(editingContext)")
                .insideLabel(this.generateDefaultLabel("aql:self.name"))
                .defaultHeightExpression("70")
                .defaultWidthExpression("150")
                .userResizable(UserResizableDirection.BOTH)
                .keepAspectRatio(true)
                .palette(diagramBuilderHelper.newNodePalette().build())
                .style(this.createNodeStyle(EasyModColorService.GREEN_BACKGROUND_COLOR, EasyModColorService.BORDER_COLOR))
                .synchronizationPolicy(SynchronizationPolicy.SYNCHRONIZED)
                // .conditionalStyles(createConditionalAssumptionNodeStyle())
                .build();
    }

    protected NodeStyleDescription createNodeStyle(String color, String borderColor) {
        return this.diagramBuilderHelper
                .newRectangularNodeStyleDescription()
                .background(colorProvider.getColor(color))
                .borderColor(colorProvider.getColor(borderColor))
                .build();
    }

    protected List<NodeDescription> getReusedChildren(IViewDiagramElementFinder cache) {
        var reusedChildren = new ArrayList<NodeDescription>();
        cache.getNodeDescription(this.getName()).ifPresent(reusedChildren::add);
        return reusedChildren;
    }

    @Override
    public void link(DiagramDescription diagramDescription, IViewDiagramElementFinder cache) {
        cache.getNodeDescription(this.getName()).ifPresent(nodeDescription -> {
            diagramDescription.getNodeDescriptions().add(nodeDescription);
            nodeDescription.getReusedChildNodeDescriptions().addAll(this.getReusedChildren(cache));
            nodeDescription.setPalette(this.createNodePalette(nodeDescription, cache));
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

    private NodePalette createNodePalette(NodeDescription nodeDescription, IViewDiagramElementFinder cache) {
        var changeContext = this.viewBuilderHelper.newChangeContext()
                .expression(AQLUtils.getSelfServiceCallExpression("deleteFromModel"));

        var deleteTool = this.diagramBuilderHelper.newDeleteTool()
                .name("Delete from Model")
                .body(changeContext.build());

        var callEditService = this.viewBuilderHelper.newChangeContext()
                .expression(AQLUtils.getSelfServiceCallExpression("directEditEasyModNode", "newLabel"));

        var editTool = this.diagramBuilderHelper.newLabelEditTool()
                .name("Edit")
                .initialDirectEditLabelExpression(AQLUtils.getSelfServiceCallExpression("getDefaultEasyModInitialDirectEditLabel"))
                .body(callEditService.build());

        return this.diagramBuilderHelper.newNodePalette()
                .deleteTool(deleteTool.build())
                .labelEditTool(editTool.build())
                .dropNodeTool(this.createDropFromDiagramTool(cache))
                .toolSections(this.createToolSections(cache))
                .build();
    }

    private DropNodeTool createDropFromDiagramTool(IViewDiagramElementFinder cache) {
        var dropElementFromDiagram = this.viewBuilderHelper.newChangeContext()
                .expression("aql:droppedElement.dropElementFromDiagram(droppedNode, targetElement, targetNode, editingContext, diagramContext, convertedNodes)");

        return this.diagramBuilderHelper.newDropNodeTool()
                .name("Drop from Diagram")
                .acceptedNodeTypes(this.getDroppableNodes(cache).toArray(NodeDescription[]::new))
                .body(dropElementFromDiagram.build())
                .build();
    }

    protected List<NodeDescription> getDroppableNodes(IViewDiagramElementFinder cache) {
        var droppableNodes = new ArrayList<NodeDescription>();
        cache.getNodeDescription(this.getName()).ifPresent(droppableNodes::add);

        return droppableNodes;
    }

    private NodeToolSection[] createToolSections(IViewDiagramElementFinder cache) {
        var sections = new ArrayList<NodeToolSection>();

        NodeToolSectionBuilder sectionBuilder = this.diagramBuilderHelper.newNodeToolSection()
                .name("MainTool")
                .nodeTools(this.createFunctionTool());
        sections.add(sectionBuilder.build());
        sections.add(this.defaultToolsFactory.createDefaultHideRevealNodeToolSection());

        return sections.toArray(NodeToolSection[]::new);
    }

    private NodeTool createFunctionTool() {
        NodeTool nodeTool = DiagramFactory.eINSTANCE.createNodeTool();
        nodeTool.setName(FunctionNodeDescriptionProvider.NODE_NAME);
        ChangeContext createElement = ViewFactory.eINSTANCE.createChangeContext();
        createElement.setExpression("aql:self.createFunction(editingContext)");
        nodeTool.getBody().add(createElement);
        return nodeTool;
    }
}

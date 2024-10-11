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
package org.eclipse.syson.easymod.diagram.fbs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.diagrams.description.EdgeDescription;
import org.eclipse.sirius.components.view.ChangeContext;
import org.eclipse.sirius.components.view.ViewFactory;
import org.eclipse.sirius.components.view.builder.IViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.generated.diagram.InsideLabelDescriptionBuilder;
import org.eclipse.sirius.components.view.builder.generated.diagram.InsideLabelStyleBuilder;
import org.eclipse.sirius.components.view.builder.generated.diagram.NodeToolSectionBuilder;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.diagram.ConditionalNodeStyle;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.DiagramFactory;
import org.eclipse.sirius.components.view.diagram.DropNodeTool;
import org.eclipse.sirius.components.view.diagram.EdgeTool;
import org.eclipse.sirius.components.view.diagram.InsideLabelDescription;
import org.eclipse.sirius.components.view.diagram.InsideLabelPosition;
import org.eclipse.sirius.components.view.diagram.InsideLabelStyle;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.diagram.NodePalette;
import org.eclipse.sirius.components.view.diagram.NodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.NodeTool;
import org.eclipse.sirius.components.view.diagram.NodeToolSection;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import org.eclipse.syson.diagram.common.view.nodes.AbstractNodeDescriptionProvider;
import org.eclipse.syson.easymod.diagram.utils.EasyModColorConstants;
import org.eclipse.syson.sysml.SysmlPackage;
import org.eclipse.syson.util.AQLConstants;
import org.eclipse.syson.util.AQLUtils;
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * Function node description provider.
 * 
 * @author ebausson
 */
public class FunctionNodeDescriptionProvider extends AbstractNodeDescriptionProvider {

    public static final String NODE_NAME = "Function";

    public FunctionNodeDescriptionProvider(IColorProvider colorProvider) {
        super(colorProvider);
    }

    @Override
    public NodeDescription create() {
        String domainType = SysMLMetamodelHelper.buildQualifiedName(SysmlPackage.eINSTANCE.getActionUsage());
        return this.diagramBuilderHelper.newNodeDescription()
                .domainType(domainType)
                .name(this.getName())
                .semanticCandidatesExpression(AQLUtils.getSelfServiceCallExpression("getFunctions", IEditingContext.EDITING_CONTEXT))
                .insideLabel(this.generateDefaultLabel(AQLConstants.AQL_SELF + ".name"))
                .defaultHeightExpression("70")
                .defaultWidthExpression("150")
                .userResizable(UserResizableDirection.BOTH)
                .keepAspectRatio(false)
                .style(this.createNodeStyle(EasyModColorConstants.FUNCTION_NODE_UNALLOCATED_BACKGROUND_COLOR, EasyModColorConstants.FUNCTION_NODE_BORDER_COLOR))
                .conditionalStyles(createConditionalNodeStyle())
                .synchronizationPolicy(SynchronizationPolicy.SYNCHRONIZED)
                .build();
    }

    private ConditionalNodeStyle[] createConditionalNodeStyle() {
        var styles = new ArrayList<ConditionalNodeStyle>();
        styles.add(this.generateConditionalNodeSytle(
                EasyModColorConstants.FUNCTION_NODE_ALLOCATED_BACKGROUND_COLOR,
                EasyModColorConstants.FUNCTION_NODE_BORDER_COLOR,
                AQLUtils.getSelfServiceCallExpression("isSEIMFunctionAllocated")));
        return styles.toArray(new ConditionalNodeStyle[0]);
    }

    protected ConditionalNodeStyle generateConditionalNodeSytle(String color, String borderColor, String condition) {
        return this.diagramBuilderHelper
                .newConditionalNodeStyle()
                .condition(condition)
                .style(this.createNodeStyle(color, borderColor))
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

            var borderNodeDescription = cache.getNodeDescription(FunctionPortNodeDescriptionProvider.NODE_NAME);
            nodeDescription.getBorderNodesDescriptions().add(borderNodeDescription.get());
        });

    }

    private EdgeTool createFlowConnectionUsageEdgeTool(NodeDescription targetNodeDescription) {
        var builder = this.diagramBuilderHelper.newEdgeTool();

        var body = this.viewBuilderHelper.newChangeContext()
                .expression(AQLUtils.getServiceCallExpression(EdgeDescription.SEMANTIC_EDGE_SOURCE, "createFunctionalFlow", EdgeDescription.SEMANTIC_EDGE_TARGET));

        return builder
                .name("FunctionalFlowCreation")
                .iconURLsExpression("/icons/full/obj16/" + SysmlPackage.eINSTANCE.getFlowConnectionUsage().getName() + ".svg")
                .body(body.build())
                .targetElementDescriptions(List.of(targetNodeDescription).toArray(NodeDescription[]::new))
                .build();
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

        var edgeTools = new ArrayList<EdgeTool>();
        edgeTools.add(createFlowConnectionUsageEdgeTool(nodeDescription));

        return this.diagramBuilderHelper.newNodePalette()
                .deleteTool(deleteTool.build())
                .labelEditTool(editTool.build())
                .edgeTools(edgeTools.toArray(EdgeTool[]::new))
                .dropNodeTool(this.createDropFromDiagramTool(cache))
                .toolSections(this.createToolSections(cache))
                .build();
    }

    protected Collection<EdgeTool> generateEdgeTools(NodeDescription targetNodeDescription) {
        return List.of(createFlowConnectionUsageEdgeTool(targetNodeDescription));
    }

    private DropNodeTool createDropFromDiagramTool(IViewDiagramElementFinder cache) {
        String dropFromDiagramChangeContextExpression = AQLUtils.getServiceCallExpression("droppedElement", "dropElementFromDiagram",
                List.of("droppedNode", "targetElement", "targetNode", "editingContext", "diagramContext", "convertedNodes"));
        var dropElementFromDiagram = this.viewBuilderHelper.newChangeContext().expression(dropFromDiagramChangeContextExpression);

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
        createElement.setExpression(AQLUtils.getSelfServiceCallExpression("createFunction", List.of("editingContext")));

        nodeTool.getBody().add(createElement);
        return nodeTool;
    }

    protected InsideLabelDescription generateDefaultLabel(String expression) {
        InsideLabelStyle insideLabelStyle = new InsideLabelStyleBuilder().build();
        insideLabelStyle.setBorderSize(0);
        return new InsideLabelDescriptionBuilder()
                .labelExpression(expression)
                .style(insideLabelStyle)
                .position(InsideLabelPosition.TOP_CENTER)
                .build();
    }

}

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
package org.eclipse.syson.easymod.diagram.pbs.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sirius.components.view.ChangeContext;
import org.eclipse.sirius.components.view.RepresentationDescription;
import org.eclipse.sirius.components.view.ViewFactory;
import org.eclipse.sirius.components.view.builder.IViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramToolSectionBuilder;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.builder.providers.IDiagramElementDescriptionProvider;
import org.eclipse.sirius.components.view.builder.providers.IRepresentationDescriptionProvider;
import org.eclipse.sirius.components.view.diagram.DiagramElementDescription;
import org.eclipse.sirius.components.view.diagram.DiagramFactory;
import org.eclipse.sirius.components.view.diagram.DiagramPalette;
import org.eclipse.sirius.components.view.diagram.DiagramToolSection;
import org.eclipse.sirius.components.view.diagram.DropNodeTool;
import org.eclipse.sirius.components.view.diagram.DropTool;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.diagram.NodeTool;
import org.eclipse.syson.diagram.common.view.ViewDiagramElementFinder;
import org.eclipse.syson.diagram.common.view.services.description.ToolDescriptionService;
import org.eclipse.syson.easymod.diagram.pbs.FunctionNodeDescriptionProvider;
import org.eclipse.syson.easymod.diagram.pbs.LogicalConstituentNodeDescriptionProvider;
import org.eclipse.syson.easymod.diagram.pbs.LogicalConstituentPortNodeDescriptionProvider;
import org.eclipse.syson.easymod.diagram.pbs.LogicalFlowEdgeDescriptionProvider;
import org.eclipse.syson.easymod.diagram.view.AbstractEasyModDiagramDescriptionProvider;
import org.eclipse.syson.easymod.diagram.view.EasyModDescriptionNameGenerator;
import org.eclipse.syson.util.AQLUtils;

/**
 * Description of the FBS View diagram using the ViewBuilder API from Sirius Web.
 *
 * @author ebausson
 */
public class PBSViewsDiagramDescriptionProvider extends AbstractEasyModDiagramDescriptionProvider implements IRepresentationDescriptionProvider {

    public static final String DIAGRAM_NAME = "Logical flow diagram";

    public static final String DIAGRAM_LABEL = "Logical flow diagram";

    private static final String TOOL_SECTION_NAME = "MainTools";

    private final DiagramBuilders diagramBuilderHelper = new DiagramBuilders();

    private final ToolDescriptionService toolDescriptionService = new ToolDescriptionService(new EasyModDescriptionNameGenerator());

    private ViewBuilders viewBuilderHelper = new ViewBuilders();

    @Override
    public RepresentationDescription create(IColorProvider colorProvider) {

        var cache = new ViewDiagramElementFinder();

        var diagramDescriptionBuilder = generateDiagramDescription();

        var diagramDescription = diagramDescriptionBuilder.build();

        var diagramElementDescriptionProviders = this.getDiagramElements(colorProvider);

        diagramElementDescriptionProviders.stream().map(IDiagramElementDescriptionProvider::create).forEach(cache::put);

        diagramElementDescriptionProviders.forEach(diagramElementDescriptionProvider -> diagramElementDescriptionProvider.link(diagramDescription, cache));

        var palette = this.createDiagramPalette(cache);
        diagramDescription.setPalette(palette);

        return diagramDescription;
    }

    @Override
    protected List<IDiagramElementDescriptionProvider<? extends DiagramElementDescription>> getDiagramElements(IColorProvider easyModColorProvider) {
        LogicalConstituentNodeDescriptionProvider lcNodeDescriptionProvider = new LogicalConstituentNodeDescriptionProvider(easyModColorProvider);
        LogicalConstituentPortNodeDescriptionProvider lcPortNodeDescriptionProvider = new LogicalConstituentPortNodeDescriptionProvider(easyModColorProvider);
        LogicalFlowEdgeDescriptionProvider logicalFlowEdgeDescriptionProvider = new LogicalFlowEdgeDescriptionProvider(easyModColorProvider);
        FunctionNodeDescriptionProvider functionNodeDescriptionProvider = new FunctionNodeDescriptionProvider(easyModColorProvider);
        return List.of(lcNodeDescriptionProvider, lcPortNodeDescriptionProvider, logicalFlowEdgeDescriptionProvider, functionNodeDescriptionProvider);
    }

    @Override
    protected String getName() {
        return DIAGRAM_NAME;
    }

    @Override
    protected String getLabel() {
        return DIAGRAM_LABEL;
    }

    protected DiagramPalette createDiagramPalette(IViewDiagramElementFinder cache) {
        return this.diagramBuilderHelper.newDiagramPalette()
                .dropNodeTool(this.createDropFromDiagramTool(cache))
                .dropTool(this.createDropFromExplorerTool(cache))
                .toolSections(this.createToolSections(cache))
                .build();
    }

    protected DiagramToolSection[] createToolSections(IViewDiagramElementFinder cache) {
        var sections = new ArrayList<DiagramToolSection>();

        DiagramToolSectionBuilder sectionBuilder = this.diagramBuilderHelper.newDiagramToolSection()
                .name(TOOL_SECTION_NAME)
                .nodeTools(this.createLogicalConsituentTool());
        sections.add(sectionBuilder.build());

        return sections.toArray(DiagramToolSection[]::new);
    }

    private NodeTool createLogicalConsituentTool() {
        NodeTool nodeTool = DiagramFactory.eINSTANCE.createNodeTool();
        nodeTool.setName(LogicalConstituentNodeDescriptionProvider.NODE_NAME);
        ChangeContext createElement = ViewFactory.eINSTANCE.createChangeContext();
        createElement.setExpression("aql:self.createLogicalConstituent(editingContext)");
        nodeTool.getBody().add(createElement);
        return nodeTool;
    }

    /**
     * Builds a {@link DropNodeTool} which will be added to the {@link NodeDescription} palette. May be implemented to
     * extend default behavior which is to return null.
     *
     * @param cache
     *            The cache
     * @return The created {@link DropNodeTool}
     */
    private DropNodeTool createDropFromDiagramTool(IViewDiagramElementFinder cache) {
        var dropElementFromDiagram = this.viewBuilderHelper.newChangeContext()
                .expression(AQLUtils.getServiceCallExpression("droppedElement", "dropElementFromDiagram",
                        List.of("droppedNode", "targetElement", "targetNode", "editingContext", "diagramContext", "convertedNodes")));

        var optLCNodeDescription = cache.getNodeDescription(LogicalConstituentNodeDescriptionProvider.NODE_NAME);
        if (optLCNodeDescription.isPresent()) {
            return this.diagramBuilderHelper.newDropNodeTool()
                    .name("Drop from Diagram")
                    .acceptedNodeTypes(cache.getNodeDescription(LogicalConstituentNodeDescriptionProvider.NODE_NAME).get())
                    .body(dropElementFromDiagram.build())
                    .build();
        }
        return null;
    }

    private DropTool createDropFromExplorerTool(IViewDiagramElementFinder cache) {
        var dropElementFromExplorer = this.viewBuilderHelper.newChangeContext()
                .expression(AQLUtils.getSelfServiceCallExpression("dropFunctionOnPartFromExplorer",
                        List.of("editingContext", "diagramContext", "selectedNode")));

        return this.diagramBuilderHelper.newDropTool()
                .name("Drop from Explorer")
                .body(dropElementFromExplorer.build())
                .build();
    }

    protected List<NodeDescription> getDroppableFromExplorerNodes(IViewDiagramElementFinder cache) {
        var droppableNodes = new ArrayList<NodeDescription>();
        cache.getNodeDescription(FunctionNodeDescriptionProvider.NODE_NAME).ifPresent(droppableNodes::add);
        return droppableNodes;
    }
}

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
package org.eclipse.easymod.diagram.fbs.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.easymod.diagram.fbs.ressources.FunctionNodeDescriptionProvider;
import org.eclipse.easymod.diagram.view.AbstractEasyModDiagramDescriptionProvider;
import org.eclipse.sirius.components.view.ChangeContext;
import org.eclipse.sirius.components.view.RepresentationDescription;
import org.eclipse.sirius.components.view.ViewFactory;
import org.eclipse.sirius.components.view.builder.IViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramToolSectionBuilder;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.builder.providers.IDiagramElementDescriptionProvider;
import org.eclipse.sirius.components.view.builder.providers.IRepresentationDescriptionProvider;
import org.eclipse.sirius.components.view.diagram.DiagramElementDescription;
import org.eclipse.sirius.components.view.diagram.DiagramFactory;
import org.eclipse.sirius.components.view.diagram.DiagramPalette;
import org.eclipse.sirius.components.view.diagram.DiagramToolSection;
import org.eclipse.sirius.components.view.diagram.NodeTool;
import org.eclipse.syson.diagram.common.view.ViewDiagramElementFinder;
import org.eclipse.syson.diagram.common.view.services.description.ToolDescriptionService;

/**
 * Description of the FBS View diagram using the ViewBuilder API from Sirius Web.
 *
 * @author ebausson
 */
public class FBSViewsDiagramDescriptionProvider extends AbstractEasyModDiagramDescriptionProvider implements IRepresentationDescriptionProvider {

    public static final String DIAGRAM_NAME = "FBSView";

    public static final String DIAGRAM_LABEL = "Function Breakdown Structure View";

    private static final String TOOL_SECTION_NAME = "MainTools";

    private final DiagramBuilders diagramBuilderHelper = new DiagramBuilders();

    private final ToolDescriptionService toolDescriptionService = new ToolDescriptionService(new EasyModDescriptionNameGenerator());

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
        FunctionNodeDescriptionProvider functionNodeDescriptionProvider = new FunctionNodeDescriptionProvider(easyModColorProvider);
        return List.of(functionNodeDescriptionProvider);
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
                // .dropNodeTool(this.createDropFromDiagramTool(cache))
                .toolSections(this.createToolSections(cache))
                .build();
    }

    protected DiagramToolSection[] createToolSections(IViewDiagramElementFinder cache) {
        var sections = new ArrayList<DiagramToolSection>();

        DiagramToolSectionBuilder sectionBuilder = this.diagramBuilderHelper.newDiagramToolSection()
                .name(TOOL_SECTION_NAME)
                .nodeTools(this.createFunctionTool());
        sections.add(sectionBuilder.build());

        // add extra section for existing elements
        sections.add(this.toolDescriptionService.addElementsDiagramToolSection());

        return sections.toArray(DiagramToolSection[]::new);
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

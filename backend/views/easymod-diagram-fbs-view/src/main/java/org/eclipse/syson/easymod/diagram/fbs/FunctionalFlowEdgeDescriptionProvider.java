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

import java.util.Objects;

import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.view.builder.IViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.builder.providers.IEdgeDescriptionProvider;
import org.eclipse.sirius.components.view.diagram.ArrowStyle;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.EdgeDescription;
import org.eclipse.sirius.components.view.diagram.EdgePalette;
import org.eclipse.sirius.components.view.diagram.EdgeStyle;
import org.eclipse.sirius.components.view.diagram.LineStyle;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.provider.DefaultToolsFactory;
import org.eclipse.syson.easymod.diagram.utils.EasyModColorConstants;
import org.eclipse.syson.sysml.SysmlPackage;
import org.eclipse.syson.util.AQLConstants;
import org.eclipse.syson.util.AQLUtils;
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * FunctionalFlow edge description provider.
 * 
 * @author ebausson
 */
public class FunctionalFlowEdgeDescriptionProvider implements IEdgeDescriptionProvider {

    private static final String EDGE_NAME = "FunctionalFlow";

    protected final IColorProvider colorProvider;

    private final ViewBuilders viewBuilderHelper = new ViewBuilders();

    private final DefaultToolsFactory defaultToolsFactory = new DefaultToolsFactory();

    private final DiagramBuilders diagramBuilderHelper = new DiagramBuilders();

    public FunctionalFlowEdgeDescriptionProvider(IColorProvider colorProvider) {
        this.colorProvider = Objects.requireNonNull(colorProvider);
    }

    @Override
    public EdgeDescription create() {
        String domainType = SysMLMetamodelHelper.buildQualifiedName(SysmlPackage.eINSTANCE.getInterfaceUsage());
        return this.diagramBuilderHelper.newEdgeDescription()
                .domainType(domainType)
                .name(EDGE_NAME)
                .semanticCandidatesExpression(AQLUtils.getSelfServiceCallExpression("getFunctionalFlows", IEditingContext.EDITING_CONTEXT))
                .sourceNodesExpression(AQLConstants.AQL_SELF + ".source")
                .targetNodesExpression(AQLConstants.AQL_SELF + ".target")
                .synchronizationPolicy(SynchronizationPolicy.SYNCHRONIZED)
                .centerLabelExpression("aql:self.name")
                .isDomainBasedEdge(true)
                .style(this.generateEdgeSytle())
                .build();
    }

    private EdgePalette createEdgePalette() {
        var changeContext = this.viewBuilderHelper.newChangeContext()
                .expression("aql:self.deleteFlow()");

        var deleteTool = this.diagramBuilderHelper.newDeleteTool()
                .name("Delete from Model")
                .body(changeContext.build());

        var callEditService = this.viewBuilderHelper.newChangeContext()
                .expression(AQLUtils.getSelfServiceCallExpression("directEditEasyModNode", "newLabel"));

        var editTool = this.diagramBuilderHelper.newLabelEditTool()
                .name("Edit")
                .initialDirectEditLabelExpression(AQLUtils.getSelfServiceCallExpression("getDefaultEasyModInitialDirectEditLabel"))
                .body(callEditService.build());

        return this.diagramBuilderHelper
                .newEdgePalette()
                .deleteTool(deleteTool.build())
                .centerLabelEditTool(editTool.build())
                .toolSections(this.defaultToolsFactory.createDefaultHideRevealEdgeToolSection())
                .build();
    }

    @Override
    public void link(DiagramDescription diagramDescription, IViewDiagramElementFinder cache) {
        var optEdgeDescription = cache.getEdgeDescription(EDGE_NAME);
        var optPortUsageBorderNodeDescription = cache.getNodeDescription(FunctionPortNodeDescriptionProvider.NODE_NAME);

        if (optEdgeDescription.isPresent() && optPortUsageBorderNodeDescription.isPresent()) {
            EdgeDescription edgeDescription = optEdgeDescription.get();
            diagramDescription.getEdgeDescriptions().add(edgeDescription);
            edgeDescription.getSourceNodeDescriptions().add(optPortUsageBorderNodeDescription.get());
            edgeDescription.getTargetNodeDescriptions().add(optPortUsageBorderNodeDescription.get());
            edgeDescription.setPalette(this.createEdgePalette());
        }
    }

    private EdgeStyle generateEdgeSytle() {
        return this.diagramBuilderHelper
                .newEdgeStyle()
                .edgeWidth(1)
                .borderSize(0)
                .lineStyle(LineStyle.SOLID)
                .color(colorProvider.getColor(EasyModColorConstants.FUNCTION_EDGE_COLOR))
                .sourceArrowStyle(ArrowStyle.NONE)
                .targetArrowStyle(ArrowStyle.NONE)
                .build();
    }

}

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
package org.eclipse.easymod.diagram.view;

import java.util.List;

import org.eclipse.sirius.components.view.ViewFactory;
import org.eclipse.sirius.components.view.builder.IViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramDescriptionBuilder;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.builder.providers.IDiagramElementDescriptionProvider;
import org.eclipse.sirius.components.view.builder.providers.IRepresentationDescriptionProvider;
import org.eclipse.sirius.components.view.diagram.ArrangeLayoutDirection;
import org.eclipse.sirius.components.view.diagram.DiagramElementDescription;
import org.eclipse.sirius.components.view.diagram.DiagramFactory;
import org.eclipse.sirius.components.view.diagram.DiagramPalette;
import org.eclipse.sirius.components.view.diagram.DiagramToolSection;
import org.eclipse.sirius.components.view.diagram.DropTool;
import org.eclipse.syson.sysml.SysmlPackage;
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * Abstract DiagramDescriptionProvider to regroup Easymod project shared functionalities.
 *
 * @author ebausson
 */

public abstract class AbstractEasyModDiagramDescriptionProvider implements IRepresentationDescriptionProvider {

    private final DiagramBuilders diagramBuilderHelper = new DiagramBuilders();

    protected DiagramDescriptionBuilder generateDiagramDescription() {
        var diagramDescriptionBuilder = this.diagramBuilderHelper.newDiagramDescription();
        diagramDescriptionBuilder
                .arrangeLayoutDirection(ArrangeLayoutDirection.DOWN)
                .autoLayout(false)
                .domainType(getDomainType())
                .preconditionExpression(getDiagramPreconditionExpression())
                .name(getName())
                .titleExpression(getLabel());
        return diagramDescriptionBuilder;
    }

    protected DiagramPalette createDiagramPalette(IViewDiagramElementFinder cache) {
        return this.diagramBuilderHelper.newDiagramPalette()
                .dropTool(this.createDropTool())
                .toolSections(this.createToolSections(cache))
                .build();
    }

    protected DropTool createDropTool() {
        var dropTool = DiagramFactory.eINSTANCE.createDropTool();
        var changeContext = ViewFactory.eINSTANCE.createChangeContext();
        dropTool.setName("Drop Tool");
        changeContext.setExpression("aql:self.drop(selectedNode, diagramContext, convertedNodes)");
        dropTool.getBody().add(changeContext);
        return dropTool;
    }

    protected String getDiagramPreconditionExpression() {
        return "aql:true"; // TODO?
    }

    protected String getDomainType() {
        return SysMLMetamodelHelper.buildQualifiedName(SysmlPackage.eINSTANCE.getNamespace());
    }

    protected abstract String getName();

    protected abstract String getLabel();

    protected abstract List<IDiagramElementDescriptionProvider<? extends DiagramElementDescription>> getDiagramElements(IColorProvider easyModColorProvider);

    protected abstract DiagramToolSection[] createToolSections(IViewDiagramElementFinder cache);

}

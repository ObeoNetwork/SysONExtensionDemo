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

import java.util.List;

import org.eclipse.easymod.diagram.fbs.ressources.FunctionNodeDescriptionProvider;
import org.eclipse.easymod.diagram.view.AbstractEasyModDiagramDescriptionProvider;
import org.eclipse.sirius.components.view.RepresentationDescription;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.builder.providers.IRepresentationDescriptionProvider;
import org.eclipse.sirius.components.view.diagram.DiagramElementDescription;
import org.eclipse.syson.diagram.common.view.ViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.providers.IDiagramElementDescriptionProvider;

/**
 * Description of the FBS View diagram using the ViewBuilder API from Sirius Web.
 *
 * @author ebausson
 */
public class FBSViewsDiagramDescriptionProvider extends AbstractEasyModDiagramDescriptionProvider implements IRepresentationDescriptionProvider {

    public static final String DIAGRAM_NAME = "FBSView";
    public static final String DIAGRAM_LABEL = "Function Breakdown Structure View";

    @Override
    public RepresentationDescription create(IColorProvider colorProvider) {
        var cache = new ViewDiagramElementFinder();

        var diagramDescriptionBuilder = generateDiagramDescription();

        var diagramDescription = diagramDescriptionBuilder.build();

        var diagramElementDescriptionProviders = this.getDiagramElements();

        diagramElementDescriptionProviders.stream().map(IDiagramElementDescriptionProvider::create).forEach(cache::put);

        diagramElementDescriptionProviders.forEach(diagramElementDescriptionProvider -> diagramElementDescriptionProvider.link(diagramDescription, cache));
        
        return diagramDescription;
    }

    @Override
    protected List<IDiagramElementDescriptionProvider<? extends DiagramElementDescription>> getDiagramElements() {
//        new ChildUsageNodeDescriptionProvider(SysmlPackage.eINSTANCE.getPartUsage(), colorProvider, this.getDescriptionNameGenerator()),
        return List.of(
                new FunctionNodeDescriptionProvider(this.getColorProvider())
        );
    }

    @Override
    protected String getName() {
        return DIAGRAM_NAME;
    }

    @Override
    protected String getLabel() {
        return DIAGRAM_LABEL;
    }

}

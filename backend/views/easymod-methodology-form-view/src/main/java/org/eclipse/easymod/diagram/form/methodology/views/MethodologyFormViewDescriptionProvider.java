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
package org.eclipse.easymod.diagram.form.methodology.views;

import org.eclipse.sirius.components.view.RepresentationDescription;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.builder.providers.IRepresentationDescriptionProvider;
import org.eclipse.syson.diagram.common.view.IViewDescriptionProvider;
import org.springframework.stereotype.Service;

/**
 * Allows to register the Methodology View diagram in the application.
 *
 * @author ebausson
 */
@Service
public class MethodologyFormViewDescriptionProvider implements IViewDescriptionProvider, IRepresentationDescriptionProvider {
    
    @Override
    public String getViewDiagramId() {
        return "MethodologyFormViewDiagram";
    }

    @Override
    public IRepresentationDescriptionProvider getRepresentationDescriptionProvider() {
        return this;
    }

    @Override
    public RepresentationDescription create(IColorProvider colorProvider) {
        return new MethodologyFormView().createFormDescription();
    }

}

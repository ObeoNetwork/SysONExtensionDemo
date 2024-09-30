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

import org.eclipse.easymod.diagram.fbs.services.EasymodFBSService;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.emf.IJavaServiceProvider;
import org.eclipse.syson.diagram.common.view.services.ViewToolService;
import org.eclipse.syson.services.DeleteService;
import org.springframework.stereotype.Service;

/**
 * FBS service provider.
 * 
 * @author ebausson
 */
@Service
public class FBSViewJavaServiceProvider implements IJavaServiceProvider {

    @Override
    public List<Class<?>> getServiceClasses(View view) {
        var descriptions = view.getDescriptions();
        var optDescription = descriptions.stream()
                .filter(desc -> FBSViewsDiagramDescriptionProvider.DIAGRAM_NAME.equals(desc.getName()))
                .findFirst();
        if (optDescription.isPresent()) {
            return List.of(DeleteService.class,
                    EasymodFBSService.class,
                    ViewToolService.class);
        }
        return List.of();
    }
}

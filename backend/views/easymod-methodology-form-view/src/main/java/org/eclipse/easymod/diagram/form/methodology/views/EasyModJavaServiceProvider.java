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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.sirius.components.view.RepresentationDescription;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.emf.IJavaServiceProvider;
import org.springframework.context.annotation.Configuration;

/**
 * Registers all the Representation Service classes for EasyMod representations.
 *
 * @author ebausson
 */
@Configuration
public class EasyModJavaServiceProvider implements IJavaServiceProvider {

    @Override
    public List<Class<?>> getServiceClasses(View view) {
        // @formatter:off
        return view.getDescriptions().stream()
                .flatMap(representationDescription -> this.getRepresentationServicesClass(representationDescription).stream())
                .collect(Collectors.toList());
        // @formatter:on
    }

    private List<Class<?>> getRepresentationServicesClass(RepresentationDescription representationDescription) {
        List<Class<?>> services = new ArrayList<>();
        String repName = representationDescription.getName();
        if (repName != null) {
            if (representationDescription instanceof org.eclipse.sirius.components.view.form.FormDescription) {
                services.add(MethodologyFormServices.class);
            }
            return services;
        }
        return services;
    }

}

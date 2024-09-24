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
package org.eclipse.easymod.services.diagram;

import org.eclipse.sirius.components.core.RepresentationMetadata;
import org.eclipse.sirius.components.core.api.IIdentityServiceDelegate;
import org.springframework.stereotype.Service;

/**
 * A service to resolve RepresentationMetadata IDs and Kind.
 *
 * @author ebausson
 */
@Service
public class RepresentationMetadataIdentityServiceDelegate implements IIdentityServiceDelegate {

    @Override
    public boolean canHandle(Object object) {
        return object instanceof RepresentationMetadata;
    }

    @Override
    public String getId(Object object) {
        if (object instanceof RepresentationMetadata rm) {
            return rm.getId();
        }
        return null;
    }

    @Override
    public String getKind(Object object) {
        if (object instanceof RepresentationMetadata rm) {
            return rm.getKind();
        }
        return null;
    }

}

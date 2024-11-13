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
package org.eclipse.syson.easymod.diagram.services;

import org.eclipse.sirius.components.core.api.IIdentityServiceDelegate;
import org.eclipse.sirius.web.application.representation.dto.RepresentationMetadataDTO;
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
        return object instanceof RepresentationMetadataDTO;
    }

    @Override
    public String getId(Object object) {
        if (object instanceof RepresentationMetadataDTO rm) {
            return rm.id().toString();
        }
        return null;
    }

    @Override
    public String getKind(Object object) {
        if (object instanceof RepresentationMetadataDTO rm) {
            return rm.kind();
        }
        return null;
    }

}

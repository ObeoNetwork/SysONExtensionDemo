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
package org.eclipse.syson.easymod.form.fbstopbs;

import java.util.List;

import org.eclipse.sirius.components.collaborative.api.IRepresentationDescriptionsProvider;
import org.eclipse.sirius.components.collaborative.api.RepresentationDescriptionMetadata;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.springframework.stereotype.Service;

/**
 * Used to provide the {@link RepresentationDescriptionMetadata}s of a given object from for FBSToPBS form.
 *
 * @author ebausson
 */
@Service
public class EasyModRepresentationDescriptionProvider implements IRepresentationDescriptionsProvider {

    @Override
    public boolean canHandle(IRepresentationDescription representationDescription) {
        return FBSToPBSFormDescriptionProvider.DESCRIPTION_NAME.equals(representationDescription.getLabel());
    }

    @Override
    public List<RepresentationDescriptionMetadata> handle(IEditingContext iEditingContext, Object object, IRepresentationDescription representationDescription) {
        return List.of(new RepresentationDescriptionMetadata(representationDescription.getId(), representationDescription.getLabel(), representationDescription.getLabel()));
    }
}

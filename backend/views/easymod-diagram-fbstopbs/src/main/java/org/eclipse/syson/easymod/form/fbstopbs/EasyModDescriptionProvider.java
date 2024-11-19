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

import java.util.Objects;

import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.springframework.stereotype.Service;

/**
 * EasyModDescriptionProvider.
 * 
 * @author ebausson
 */
@Service
public class EasyModDescriptionProvider implements IEditingContextProcessor {

    private final IObjectService objectService;

    public EasyModDescriptionProvider(IObjectService objectService) {
        this.objectService = Objects.requireNonNull(objectService);
    }

    @Override
    public void preProcess(IEditingContext editingContext) {
        if (editingContext instanceof EditingContext siriusWebEditingContext) {
            var form = new FBSToPBSFormDescriptionProvider().createFormDescription(objectService);
            siriusWebEditingContext.getRepresentationDescriptions().put(form.getId(), form);
        }
    }
}

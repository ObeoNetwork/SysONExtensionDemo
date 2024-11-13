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
package org.eclipse.syson.easymod.diagram.fbstopbs.view;

import java.util.List;
import java.util.Objects;

import org.eclipse.sirius.components.core.api.IEditService;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.view.emf.form.IFormIdProvider;
import org.eclipse.sirius.components.view.emf.form.IWidgetConverterProvider;
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

    private final IEditService editService;

    private final IFormIdProvider formIdProvider;

    private final List<IWidgetConverterProvider> customWidgetConverterProviders;

    private final IFeedbackMessageService feedbackMessageService;

    // private final AQLInterpreter aqlInterpreter;

    public EasyModDescriptionProvider(IObjectService objectService, IEditService editService, IFormIdProvider formIdProvider, List<IWidgetConverterProvider> customWidgetConverterProviders,
            IFeedbackMessageService feedbackMessageService) { // , AQLInterpreter interpreter
        this.objectService = Objects.requireNonNull(objectService);
        this.editService = Objects.requireNonNull(editService);
        this.formIdProvider = Objects.requireNonNull(formIdProvider);
        this.customWidgetConverterProviders = Objects.requireNonNull(customWidgetConverterProviders);
        this.feedbackMessageService = Objects.requireNonNull(feedbackMessageService);
        // this.aqlInterpreter = Objects.requireNonNull(interpreter);
    }

    @Override
    public void preProcess(IEditingContext editingContext) {
        if (editingContext instanceof EditingContext siriusWebEditingContext) {
            siriusWebEditingContext.getRepresentationDescriptions().put(FBSToPBSFormsDiagramDescriptionProvider.FORM_NAME,
                    new FBSToPBSFormsDiagramDescriptionProvider().createFormDescription(objectService, editService, formIdProvider, customWidgetConverterProviders, feedbackMessageService,
                            null));
        }
    }
}

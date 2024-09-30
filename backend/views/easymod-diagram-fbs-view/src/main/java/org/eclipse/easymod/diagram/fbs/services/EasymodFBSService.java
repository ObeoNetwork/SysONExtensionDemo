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
package org.eclipse.easymod.diagram.fbs.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.Element;

/**
 * FBS services.
 * 
 * @author ebausson
 */
public class EasymodFBSService {

    public List<ActionUsage> getAllReachableActions(EObject eObject, IEditingContext editingContext) {
        return extractNotifier(editingContext).stream()
                .filter(notifier -> notifier instanceof ActionUsage)
                .map(ActionUsage.class::cast)
                .toList();

    }

    private List<Notifier> extractNotifier(IEditingContext editingContext) {
        if (editingContext instanceof IEMFEditingContext emfEditingContext) {
            ArrayList<Notifier> notifiersList = new ArrayList<>();
            emfEditingContext.getDomain().getResourceSet().getAllContents().forEachRemaining(notifiersList::add);
            return notifiersList;
        }
        return List.of();
    }

    public boolean isActionUsage(Element element) {
        return element instanceof ActionUsage;
    }

}

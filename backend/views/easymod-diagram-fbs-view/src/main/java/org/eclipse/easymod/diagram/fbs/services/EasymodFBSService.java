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
import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.Element;

/**
 * FBS services.
 * 
 * @author ebausson
 */
public class EasymodFBSService {

    public List<ActionUsage> getAllReachableActions(EObject eObject, IEditingContext editingContext) {
        // eObject.eResource().getAllContents().forEachRemaining(null);
        return extractNotifier(eObject).stream()
                .filter(notifier -> notifier instanceof ActionUsage)
                .map(ActionUsage.class::cast)
                .filter(actionUsage -> actionUsage.getType().stream().anyMatch(t -> t.getQualifiedName().equals("SEIM::Function")))
                .toList();

    }

    private List<Notifier> extractNotifier(EObject eObject) {
        ArrayList<Notifier> notifiersList = new ArrayList<>();
        eObject.eResource().getAllContents().forEachRemaining(notifiersList::add);
        return notifiersList;
    }

    public boolean isActionUsage(Element element) {
        return element instanceof ActionUsage;
    }

}

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
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.syson.services.LabelService;
import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.Element;
import org.eclipse.syson.sysml.OwningMembership;
import org.eclipse.syson.sysml.Package;
import org.eclipse.syson.sysml.SysmlFactory;
import org.eclipse.syson.sysml.Usage;

/**
 * FBS services.
 * 
 * @author ebausson
 */
public class EasymodFBSService {

    public List<ActionUsage> getFunctions(EObject eObject, IEditingContext editingContext) {
        List<ActionUsage> actionUsages = List.of();
        if (eObject instanceof Usage usage) {
            actionUsages = getSubFunctions(usage);
        } else if (eObject instanceof Package pkg) {
            actionUsages = getMainFunctions(pkg);
        }
        // actionUsages = getAllReachableActions(eObject, editingContext);
        // }
        return actionUsages;
    }

    public List<ActionUsage> getMainFunctions(Package pkg) {
        return pkg.getOwnedMember().stream().filter(e -> e instanceof ActionUsage).map(ActionUsage.class::cast)
                .filter(actionUsage -> actionUsage.getType().stream().anyMatch(t -> t.getQualifiedName().equals("SEIM::Function")))
                .toList();

    }

    public List<ActionUsage> getAllReachableActions(EObject eObject, IEditingContext editingContext) {
        // eObject.eResource().getAllContents().forEachRemaining(null);
        return extractNotifier(eObject).stream()
                .filter(notifier -> notifier instanceof ActionUsage)
                .map(ActionUsage.class::cast)
                .filter(actionUsage -> actionUsage.getType().stream().anyMatch(t -> t.getQualifiedName().equals("SEIM::Function")))
                .toList();

    }

    public List<ActionUsage> getSubFunctions(Usage usage) {
        return usage.getNestedAction().stream().filter(actionUsage -> actionUsage.getType().stream().anyMatch(t -> t.getQualifiedName().equals("SEIM::Function")))
                .toList();
    }

    public ActionUsage createFunction(EObject parent, IEditingContext editingContext) {
        ActionUsage newActionUsage = SysmlFactory.eINSTANCE.createActionUsage();
        if (parent instanceof Usage usage) {
            var featureMembership = SysmlFactory.eINSTANCE.createFeatureMembership();
            featureMembership.getOwnedRelatedElement().add(newActionUsage);
            usage.getOwnedRelationship().add(featureMembership);
        } else if (parent instanceof Package pkg) {
            OwningMembership owningMembership = SysmlFactory.eINSTANCE.createOwningMembership();
            pkg.getOwnedRelationship().add(owningMembership);
            owningMembership.getOwnedRelatedElement().add(newActionUsage);
        }
        LabelService labelService = new LabelService(new IFeedbackMessageService.NoOp());
        labelService.directEdit(newActionUsage, "myFunction : SEIM::Function");
        return newActionUsage;
    }

    private List<Notifier> extractNotifier(EObject eObject) {
        ArrayList<Notifier> notifiersList = new ArrayList<>();
        eObject.eResource().getAllContents().forEachRemaining(notifiersList::add);
        return notifiersList;
    }

    public boolean isActionUsage(Element element) {
        return element instanceof ActionUsage;
    }

    public Element directEditEasyModNode(Element element, String newLabel) {
        element.setDeclaredName(newLabel);
        return element;
    }

    public String getDefaultEasyModInitialDirectEditLabel(Element element) {
        return element.getDeclaredName();
    }

}

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.syson.easymod.diagram.utils.EasyModConstants;
import org.eclipse.syson.services.ElementInitializerSwitch;
import org.eclipse.syson.sysml.ActionDefinition;
import org.eclipse.syson.sysml.AttributeUsage;
import org.eclipse.syson.sysml.Definition;
import org.eclipse.syson.sysml.Element;
import org.eclipse.syson.sysml.Feature;
import org.eclipse.syson.sysml.FeatureDirectionKind;
import org.eclipse.syson.sysml.FeatureMembership;
import org.eclipse.syson.sysml.FeatureTyping;
import org.eclipse.syson.sysml.InterfaceDefinition;
import org.eclipse.syson.sysml.InterfaceUsage;
import org.eclipse.syson.sysml.Membership;
import org.eclipse.syson.sysml.Namespace;
import org.eclipse.syson.sysml.Package;
import org.eclipse.syson.sysml.PartDefinition;
import org.eclipse.syson.sysml.PortDefinition;
import org.eclipse.syson.sysml.PortUsage;
import org.eclipse.syson.sysml.SysmlFactory;
import org.eclipse.syson.sysml.Type;
import org.eclipse.syson.sysml.Usage;

/**
 * Services shared by multiple easymod views.
 *
 * @author ebausson
 */
public class EasyModCommonServices {

    protected final ElementInitializerSwitch elementInitializerSwitch = new ElementInitializerSwitch();

    /**
     * Delete the given {@link InterfaceUsage} and its container if it's a {@link Membership}. Also delete related
     * ports.
     *
     * @param interfaceUsage
     *            the {@link InterfaceUsage} to delete.
     * @return the deleted element.
     */
    public EObject deleteFlow(InterfaceUsage interfaceUsage) {
        Set<EObject> elementsToDelete = new HashSet<>();
        elementsToDelete.add(interfaceUsage);
        if (interfaceUsage.eContainer() instanceof Membership membership) {
            elementsToDelete.add(membership);
        }
        EList<Element> sources = interfaceUsage.getSource();
        elementsToDelete.addAll(sources);
        for (Element source : sources) {
            if (source.eContainer() instanceof Membership membership) {
                elementsToDelete.add(membership);
            }
        }
        EList<Element> targets = interfaceUsage.getTarget();
        elementsToDelete.addAll(targets);
        for (Element target : targets) {
            if (target.eContainer() instanceof Membership membership) {
                elementsToDelete.add(membership);
            }
        }
        EcoreUtil.removeAll(elementsToDelete);
        return interfaceUsage;
    }

    /**
     * Direct edit the label of a given element.
     * 
     * @param element
     *            element with the label to edit
     * @param newLabel
     *            the new label of the element
     * @return the element with the new label.
     */
    public Element directEditEasyModNode(Element element, String newLabel) {
        element.setDeclaredName(newLabel);
        return element;
    }

    /**
     * Get the label to display when user activate direct edit.
     * 
     * @param element
     *            element with the label to edit
     * @return the label to display when user activate direct edit.
     */
    public String getDefaultEasyModInitialDirectEditLabel(Element element) {
        return element.getDeclaredName();
    }

    /**
     * Check if {@link Feature} has as a direction and this direction is IN.
     * 
     * @param feature
     *            the port with the direction to check
     * @return {@code true} if the feature has as a direction and this direction is IN, {@code false} otherwise.
     */
    public boolean isInFeature(Feature feature) {
        return FeatureDirectionKind.IN.equals(feature.getDirection());
    }

    /**
     * Check if {@link Feature} has as a direction and this direction is OUT.
     * 
     * @param feature
     *            the port with the direction to check
     * @return {@code true} if the feature has as a direction and this direction is OUT, {@code false} otherwise.
     */
    public boolean isOutFeature(Feature feature) {
        return FeatureDirectionKind.OUT.equals(feature.getDirection());
    }

    /**
     * Check if {@link Feature} has as a direction and this direction is INOUT.
     * 
     * @param feature
     *            the port with the direction to check
     * @return {@code true} if the feature has as a direction and this direction is INOUT, {@code false} otherwise.
     */
    public boolean isInOutFeature(Feature feature) {
        return FeatureDirectionKind.INOUT.equals(feature.getDirection());
    }

    protected Element elementInitializer(Element element) {
        return this.elementInitializerSwitch.doSwitch(element);
    }

    protected Predicate<? super org.eclipse.syson.sysml.Feature> isTypedWith(String qualifiedName) {
        return element -> element.getType().stream().anyMatch(t -> t != null && qualifiedName != null && qualifiedName.equals(t.getQualifiedName()));
    }

    public List<Notifier> extractNotifier(EObject eObject) {
        ArrayList<Notifier> notifiersList = new ArrayList<>();
        eObject.eResource().getAllContents().forEachRemaining(notifiersList::add);
        return notifiersList;
    }

    protected List<Notifier> extractGlobalNotifier(EObject eObject) {
        ArrayList<Notifier> notifiersList = new ArrayList<>();
        eObject.eResource().getResourceSet().getAllContents().forEachRemaining(notifiersList::add);
        return notifiersList;
    }

    protected Namespace getClosestContainingDefinitionOrPackageFrom(Element element) {
        var owner = element.eContainer();
        while (!(owner instanceof Package || owner instanceof Definition) && owner != null) {
            owner = owner.eContainer();
        }
        return (Namespace) owner;
    }

    protected PortUsage createPortUsage(PortDefinition portDefinition, Usage parent, FeatureDirectionKind direction) {
        FeatureMembership borderNodeFeatureMembership = SysmlFactory.eINSTANCE.createFeatureMembership();
        parent.getOwnedRelationship().add(borderNodeFeatureMembership);

        PortUsage portUsage = SysmlFactory.eINSTANCE.createPortUsage();
        this.elementInitializer(portUsage);
        borderNodeFeatureMembership.getOwnedRelatedElement().add(portUsage);
        setType(portDefinition, portUsage);
        portUsage.setDirection(direction);

        return portUsage;
    }

    protected InterfaceUsage createInterfaceUsage(InterfaceDefinition interfaceDefinition, Namespace parent, String declaredName) {
        FeatureMembership newFeatureMembership = SysmlFactory.eINSTANCE.createFeatureMembership();
        parent.getOwnedRelationship().add(newFeatureMembership);
        InterfaceUsage newInterfaceUsage = SysmlFactory.eINSTANCE.createInterfaceUsage();
        newInterfaceUsage.setDeclaredName(declaredName);
        newFeatureMembership.getOwnedRelatedElement().add(newInterfaceUsage);
        setType(interfaceDefinition, newInterfaceUsage);

        return newInterfaceUsage;
    }

    protected void setType(Type seimElementDefinition, Usage newActionUsage) {
        FeatureTyping actionUsageFeatureTyping = SysmlFactory.eINSTANCE.createFeatureTyping();
        this.elementInitializer(actionUsageFeatureTyping);
        actionUsageFeatureTyping.setType(seimElementDefinition);
        newActionUsage.getOwnedRelationship().add(actionUsageFeatureTyping);
    }

    // Element Definition object helpers
    protected Optional<PartDefinition> getOptionalSeimLogicalConstituentDefinition(EObject sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof PartDefinition)
                .map(PartDefinition.class::cast)
                .filter(functionDef -> EasyModConstants.LOGICAL_CONSTITUENT_QUALIFIED_NAME.equals(functionDef.getQualifiedName()))
                .findFirst();
    }

    protected Optional<PortDefinition> getOptionalLogicalConstituentPortDefinition(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof PortDefinition)
                .map(PortDefinition.class::cast)
                .filter(portDef -> EasyModConstants.LOGICAL_CONSTITUENT_PORT_QUALIFIED_NAME.equals(portDef.getQualifiedName()))
                .findFirst();
    }

    protected Optional<InterfaceDefinition> getOptionalLogicalConstituentFlowDefinition(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof InterfaceDefinition)
                .map(InterfaceDefinition.class::cast)
                .filter(portDef -> EasyModConstants.LOGICAL_CONSTITUENT_FLOW_QUALIFIED_NAME.equals(portDef.getQualifiedName()))
                .findFirst();
    }

    protected Optional<ActionDefinition> getOptionalSeimFunctionDefinition(EObject sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof ActionDefinition)
                .map(ActionDefinition.class::cast)
                .filter(functionDef -> EasyModConstants.FUNCTION_QUALIFIED_NAME.equals(functionDef.getQualifiedName()))
                .findFirst();
    }

    protected Optional<PortDefinition> getOptionalSeimFunctionPortDefinition(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof PortDefinition)
                .map(PortDefinition.class::cast)
                .filter(portDef -> EasyModConstants.FUNCTION_PORT_QUALIFIED_NAME.equals(portDef.getQualifiedName()))
                .findFirst();
    }

    protected Optional<InterfaceDefinition> getOptionalSeimFunctionalFlowDefinition(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof InterfaceDefinition)
                .map(InterfaceDefinition.class::cast)
                .filter(portDef -> EasyModConstants.FUNCTION_FLOW_QUALIFIED_NAME.equals(portDef.getQualifiedName()))
                .findFirst();
    }

    protected Optional<AttributeUsage> getOptionalSeimSystemOfInterest(EObject sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof AttributeUsage)
                .map(AttributeUsage.class::cast)
                .filter(functionDef -> EasyModConstants.SEIM_ATTRIBUTE_OF_INTEREST_QUALIFIED_NAME.equals(functionDef.getQualifiedName()))
                .findFirst();
    }
}

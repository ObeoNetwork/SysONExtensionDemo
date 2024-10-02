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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.syson.services.ElementInitializerSwitch;
import org.eclipse.syson.sysml.ActionDefinition;
import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.AllocationUsage;
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
import org.eclipse.syson.sysml.OwningMembership;
import org.eclipse.syson.sysml.Package;
import org.eclipse.syson.sysml.PortDefinition;
import org.eclipse.syson.sysml.PortUsage;
import org.eclipse.syson.sysml.SysmlFactory;
import org.eclipse.syson.sysml.Usage;

/**
 * FBS diagram services.
 * 
 * @author ebausson
 */
public class EasymodFBSService {

    private static final String FUNCTION_QUALIFIED_NAME = "SEIM::Function";

    private static final String FUNCTION_PORT_QUALIFIED_NAME = "SEIM::FunctionPort";

    private static final String FUNCTION_FLOW_QUALIFIED_NAME = "SEIM::FunctionalFlow";

    private static final String SEIM_ALLOCATED_FUNCTION_QUALIFIED_NAME = "SEIM::AllocatedFunction";

    private final ElementInitializerSwitch elementInitializerSwitch = new ElementInitializerSwitch();

    /**
     * Get list of {@link ActionUsage} typed by SEIM::Function contained by a given eObject.
     * 
     * @param eObject
     *            element which contain SEIM::Function
     * @param editingContext
     *            the editing context
     * @return the list of {@link ActionUsage} typed by SEIM::Function
     */
    public List<ActionUsage> getFunctions(EObject eObject, IEditingContext editingContext) {
        List<ActionUsage> actionUsages = List.of();
        if (eObject instanceof Usage usage) {
            actionUsages = getSubFunctions(usage);
        } else if (eObject instanceof Package pkg) {
            actionUsages = getPackageFunctions(pkg);
        }
        return actionUsages;
    }

    /**
     * Get list of {@link InterfaceUsage} typed by SEIM::FunctionalFlow contained by a given eObject.
     * 
     * @param eObject
     *            element which contain SEIM::FunctionalFlow
     * @param editingContext
     *            the editing context
     * @return the list of {@link InterfaceUsage} typed by SEIM::FunctionalFlow
     */
    public List<InterfaceUsage> getFunctionalFlows(EObject eObject, IEditingContext editingContext) {
        return extractNotifier(eObject).stream()
                .filter(notifier -> notifier instanceof InterfaceUsage)
                .map(InterfaceUsage.class::cast)
                .filter(isTypedWith(FUNCTION_FLOW_QUALIFIED_NAME))
                .toList();
    }

    /**
     * Create {@link ActionUsage} typed by SEIM::Function in a given parent.
     * 
     * @param parent
     *            the parent which should contain the new {@link ActionUsage} typed by SEIM::Function
     * @param editingContext
     *            the editingContext
     * @return the new {@link ActionUsage} typed by SEIM::Function.
     */
    public ActionUsage createFunction(EObject parent, IEditingContext editingContext) {
        Optional<ActionDefinition> optSeimFunctionDefinition = getOptionalSiemFunctionDefinition(parent);
        if (parent == null || optSeimFunctionDefinition.isEmpty()) {
            return null;
        }
        ActionUsage newActionUsage = SysmlFactory.eINSTANCE.createActionUsage();
        this.elementInitializer(newActionUsage);
        if (parent instanceof Usage usage) {
            var featureMembership = SysmlFactory.eINSTANCE.createFeatureMembership();
            featureMembership.getOwnedRelatedElement().add(newActionUsage);
            usage.getOwnedRelationship().add(featureMembership);
        } else if (parent instanceof Package pkg) {
            OwningMembership owningMembership = SysmlFactory.eINSTANCE.createOwningMembership();
            pkg.getOwnedRelationship().add(owningMembership);
            owningMembership.getOwnedRelatedElement().add(newActionUsage);
        }
        newActionUsage.setDeclaredName("myFunction");

        FeatureTyping actionUsageFeatureTyping = SysmlFactory.eINSTANCE.createFeatureTyping();
        this.elementInitializer(actionUsageFeatureTyping);
        actionUsageFeatureTyping.setType(optSeimFunctionDefinition.get());
        newActionUsage.getOwnedRelationship().add(actionUsageFeatureTyping);
        return newActionUsage;
    }

    /**
     * Get source {@link PortUsage} of a given functionalFlow.
     * 
     * @param functionalFlow
     *            the functionFlow with the {@link PortUsage} source to extract
     * @return the source {@link PortUsage}
     */
    public PortUsage getSEIMFunctionSourcePort(InterfaceUsage functionalFlow) {
        return getFirstAsPortUsageOrNull(functionalFlow.getSource());
    }

    /**
     * Get target {@link PortUsage} of a given functionalFlow.
     * 
     * @param functionalFlow
     *            the functionFlow with the {@link PortUsage} target to extract
     * @return the target {@link PortUsage}
     */
    public PortUsage getSEIMFunctionTargetPort(InterfaceUsage functionalFlow) {
        return getFirstAsPortUsageOrNull(functionalFlow.getTarget());
    }

    private PortUsage getFirstAsPortUsageOrNull(EList<Element> list) {
        if (list.get(0) instanceof PortUsage) {
            return (PortUsage) list.get(0);
        }
        return null;
    }

    /**
     * Create {@link InterfaceUsage} typed by SEIM::FunctionalFlow in a given parent. Source {@link PortUsage} and
     * target {@link PortUsage} will be automatically created on the given source and target.
     * 
     * @param source
     *            the source which hold the new source {@link PortUsage}, input of functionalFlow
     * @param target
     *            the target which hold the new target {@link PortUsage}, output of functionalFlow
     * @return the new {@link InterfaceUsage} typed by SEIM::FunctionalFlow.
     */
    public InterfaceUsage createFunctionalFlow(ActionUsage source, ActionUsage target) {
        Namespace namespace = this.getClosestContainingDefinitionOrPackageFrom(source);
        Optional<PortDefinition> optSeimFunctionalPortDefinition = getOptionalSiemFunctionPortDefinition(source);
        Optional<InterfaceDefinition> optSeimFunctionFlowDefinition = getOptionalSiemFunctionalFlowDefinition(source);
        if (namespace == null || optSeimFunctionalPortDefinition.isEmpty() || optSeimFunctionFlowDefinition.isEmpty()) {
            return null;
        }

        PortUsage newSourcePortUsage = createPortUsage(optSeimFunctionalPortDefinition.get(), source, FeatureDirectionKind.IN);
        PortUsage newTargetPortUsage = createPortUsage(optSeimFunctionalPortDefinition.get(), target, FeatureDirectionKind.OUT);

        InterfaceUsage newInterfaceUsage = createInterfaceUsage(optSeimFunctionFlowDefinition.get(), namespace);
        newInterfaceUsage.getSource().add(newSourcePortUsage);
        newInterfaceUsage.getTarget().add(newTargetPortUsage);
        return newInterfaceUsage;
    }

    private Optional<ActionDefinition> getOptionalSiemFunctionDefinition(EObject sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof ActionDefinition)
                .map(ActionDefinition.class::cast)
                .filter(functionDef -> FUNCTION_QUALIFIED_NAME.equals(functionDef.getQualifiedName()))
                .findFirst();
    }

    private Optional<PortDefinition> getOptionalSiemFunctionPortDefinition(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof PortDefinition)
                .map(PortDefinition.class::cast)
                .filter(portDef -> FUNCTION_PORT_QUALIFIED_NAME.equals(portDef.getQualifiedName()))
                .findFirst();
    }

    private Optional<InterfaceDefinition> getOptionalSiemFunctionalFlowDefinition(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof InterfaceDefinition)
                .map(InterfaceDefinition.class::cast)
                .filter(portDef -> FUNCTION_FLOW_QUALIFIED_NAME.equals(portDef.getQualifiedName()))
                .findFirst();
    }

    private PortUsage createPortUsage(PortDefinition portDefinition, ActionUsage parent, FeatureDirectionKind direction) {
        FeatureMembership borderNodeFeatureMembership = SysmlFactory.eINSTANCE.createFeatureMembership();
        parent.getOwnedRelationship().add(borderNodeFeatureMembership);

        PortUsage portUsage = SysmlFactory.eINSTANCE.createPortUsage();
        this.elementInitializer(portUsage);
        borderNodeFeatureMembership.getOwnedRelatedElement().add(portUsage);

        FeatureTyping featureTyping = SysmlFactory.eINSTANCE.createFeatureTyping();
        featureTyping.setType(portDefinition);
        portUsage.getOwnedRelationship().add(featureTyping);

        portUsage.setDirection(direction);

        return portUsage;
    }

    private InterfaceUsage createInterfaceUsage(InterfaceDefinition interfaceDefinition, Namespace parent) {
        FeatureMembership featureMembership = SysmlFactory.eINSTANCE.createFeatureMembership();
        parent.getOwnedRelationship().add(featureMembership);

        InterfaceUsage interfaceUsage = SysmlFactory.eINSTANCE.createInterfaceUsage();
        interfaceUsage.setDeclaredName("myFunctionalFlow");
        featureMembership.getOwnedRelatedElement().add(interfaceUsage);

        FeatureTyping interfaceFeatureTyping = SysmlFactory.eINSTANCE.createFeatureTyping();
        this.elementInitializer(interfaceFeatureTyping);
        interfaceFeatureTyping.setType(interfaceDefinition);
        interfaceUsage.getOwnedRelationship().add(interfaceFeatureTyping);

        return interfaceUsage;
    }

    public boolean isActionUsage(Element element) {
        return element instanceof ActionUsage;
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
     * Verify that the SIEM function {@link ActionUsage} is allocated in a @link AllocationUsage} of the right type
     * within the project.
     * 
     * @param seimFunction
     *            the seim Function to check allocation
     * @return {@code true} if the siem function is allocated, {@code false} otherwise
     */
    public boolean isSEIMFunctionAllocated(ActionUsage seimFunction) {

        return extractNotifier(seimFunction).stream()
                .filter(notifier -> notifier instanceof AllocationUsage)
                .map(AllocationUsage.class::cast)
                .filter(isTypedWith(SEIM_ALLOCATED_FUNCTION_QUALIFIED_NAME))
                .anyMatch(allocatedFunction -> allocatedFunction.getSource().contains(seimFunction));
    }

    private Predicate<? super org.eclipse.syson.sysml.Feature> isTypedWith(String qualifiedName) {
        return element -> element.getType().stream().anyMatch(t -> qualifiedName.equals(t.getQualifiedName()));
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

    private List<ActionUsage> getPackageFunctions(Package pkg) {
        return pkg.getOwnedMember().stream().filter(e -> e instanceof ActionUsage).map(ActionUsage.class::cast)
                .filter(isTypedWith(FUNCTION_QUALIFIED_NAME))
                .toList();

    }

    private List<ActionUsage> getSubFunctions(Usage usage) {
        return usage.getNestedAction().stream().filter(isTypedWith(FUNCTION_QUALIFIED_NAME))
                .toList();
    }

    private List<Notifier> extractNotifier(EObject eObject) {
        ArrayList<Notifier> notifiersList = new ArrayList<>();
        eObject.eResource().getAllContents().forEachRemaining(notifiersList::add);
        return notifiersList;
    }

    private List<Notifier> extractGlobalNotifier(EObject eObject) {
        ArrayList<Notifier> notifiersList = new ArrayList<>();
        eObject.eResource().getResourceSet().getAllContents().forEachRemaining(notifiersList::add);
        return notifiersList;
    }

    private Namespace getClosestContainingDefinitionOrPackageFrom(Element element) {
        var owner = element.eContainer();
        while (!(owner instanceof Package || owner instanceof Definition) && owner != null) {
            owner = owner.eContainer();
        }
        return (Namespace) owner;
    }

    private Element elementInitializer(Element element) {
        return this.elementInitializerSwitch.doSwitch(element);
    }

    public boolean isInFeature(Feature feature) {
        return FeatureDirectionKind.IN.equals(feature.getDirection());
    }

    public boolean isOutFeature(Feature feature) {
        return FeatureDirectionKind.OUT.equals(feature.getDirection());
    }

    public boolean isInOutFeature(Feature feature) {
        return FeatureDirectionKind.INOUT.equals(feature.getDirection());
    }

    /**
     * Delete the given {@link Element} and its container if it's a {@link Membership}. Also delete related port.
     *
     * @param element
     *            the {@link Element} to delete.
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

}

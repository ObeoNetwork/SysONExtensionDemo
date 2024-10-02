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
package org.eclipse.easymod.diagram.pbs.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.components.collaborative.diagrams.api.IDiagramContext;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.diagrams.Node;
import org.eclipse.syson.services.ElementInitializerSwitch;
import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.AllocationDefinition;
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
import org.eclipse.syson.sysml.PartDefinition;
import org.eclipse.syson.sysml.PartUsage;
import org.eclipse.syson.sysml.PortDefinition;
import org.eclipse.syson.sysml.PortUsage;
import org.eclipse.syson.sysml.SysmlFactory;
import org.eclipse.syson.sysml.Usage;

/**
 * FBS diagram services.
 * 
 * @author ebausson
 */
public class EasymodPBSService {

    private static final String LOGICAL_CONSTITUENT_QUALIFIED_NAME = "SEIM::LogicalConstituent";

    private static final String LOGICAL_CONSTITUENT_PORT_QUALIFIED_NAME = "SEIM::ConstituentPort";

    private static final String LOGICAL_CONSTITUENT_FLOW_QUALIFIED_NAME = "SEIM::LogicalFlow";

    private static final String SEIM_ALLOCATED_FUNCTION_QUALIFIED_NAME = "SEIM::AllocatedFunction";

    private final ElementInitializerSwitch elementInitializerSwitch = new ElementInitializerSwitch();

    private IObjectService objectService;

    /**
     * Constructor.
     * 
     * @param objectService
     *            objectService used to retrieve semantic element from graphical node
     */
    public EasymodPBSService(IObjectService objectService) {
        this.objectService = Objects.requireNonNull(objectService);
    }

    /**
     * Get list of {@link PartUsage} typed by SEIM::LogicalConstituent contained by a given eObject.
     * 
     * @param eObject
     *            element which contain SEIM::LogicalConstituent
     * @param editingContext
     *            the editing context
     * @return the list of {@link PartUsage} typed by SEIM::LogicalConstituent
     */
    public List<PartUsage> getLogicalConstituents(EObject eObject, IEditingContext editingContext) {
        List<PartUsage> partUsages = List.of();
        if (eObject instanceof Usage usage) {
            partUsages = getSubLogicalConstituents(usage);
        } else if (eObject instanceof Package pkg) {
            partUsages = getMainLogicalConstituents(pkg);
        }
        return partUsages;
    }

    /**
     * Get list of {@link InterfaceUsage} typed by SEIM::LogicalFlow contained by a given eObject.
     * 
     * @param eObject
     *            element which contain SEIM::LogicalFlow
     * @param editingContext
     *            the editing context
     * @return the list of {@link InterfaceUsage} typed by SEIM::LogicalFlow
     */
    public List<InterfaceUsage> getLogicalFlows(EObject eObject, IEditingContext editingContext) {
        return extractNotifier(eObject).stream()
                .filter(notifier -> notifier instanceof InterfaceUsage)
                .map(InterfaceUsage.class::cast)
                .filter(isTypedWith(LOGICAL_CONSTITUENT_FLOW_QUALIFIED_NAME))
                .toList();
    }

    /**
     * Retrieve the list of {@link ActionUsage} typed by SEIM::Function that are allocated to the logiclaConstituent.
     * 
     * @param logicalConstituent
     *            the {@link PartUsage} typed by SEIM::LogicalConstituent to check allocations
     * @return the list of {@link ActionUsage} typed by SEIM::Function that are allocated to the logiclaConstituent.
     */
    public List<ActionUsage> getFunctionsAllocatedOnLogicalConstituent(PartUsage logicalConstituent) {
        return extractNotifier(logicalConstituent).stream()
                .filter(e -> e instanceof AllocationUsage)
                .map(AllocationUsage.class::cast)
                .filter(allocation -> allocation.getTarget().size() == 1)
                .filter(allocation -> allocation.getTarget().get(0).equals(logicalConstituent))
                .filter(allocation -> allocation.getSource().size() == 1)
                .map(allocation -> allocation.getSource().get(0))
                .filter(t -> t instanceof ActionUsage)
                .map(ActionUsage.class::cast)
                .toList();
    }

    /**
     * Create {@link PartUsage} typed by SEIM::LogicalConstituent in a given parent.
     * 
     * @param parent
     *            the parent which should contain the new {@link PartUsage} typed by SEIM::LogicalConstituent
     * @param editingContext
     *            the editingContext
     * @return the new {@link PartUsage} typed by SEIM::LogicalConstituent.
     */
    public PartUsage createLogicalConstituent(EObject parent, IEditingContext editingContext) {
        Optional<PartDefinition> optSeimLogicalConstituentDefinition = getOptionalSiemLogicalConstituentDefinition(parent);
        if (parent == null || optSeimLogicalConstituentDefinition.isEmpty()) {
            return null;
        }
        PartUsage newpartUsage = SysmlFactory.eINSTANCE.createPartUsage();
        this.elementInitializer(newpartUsage);
        if (parent instanceof Usage usage) {
            var featureMembership = SysmlFactory.eINSTANCE.createFeatureMembership();
            featureMembership.getOwnedRelatedElement().add(newpartUsage);
            usage.getOwnedRelationship().add(featureMembership);
        } else if (parent instanceof Package pkg) {
            OwningMembership owningMembership = SysmlFactory.eINSTANCE.createOwningMembership();
            pkg.getOwnedRelationship().add(owningMembership);
            owningMembership.getOwnedRelatedElement().add(newpartUsage);
        }
        newpartUsage.setDeclaredName("myLogicalConstituent");

        FeatureTyping partUsageFeatureTyping = SysmlFactory.eINSTANCE.createFeatureTyping();
        this.elementInitializer(partUsageFeatureTyping);
        partUsageFeatureTyping.setType(optSeimLogicalConstituentDefinition.get());
        newpartUsage.getOwnedRelationship().add(partUsageFeatureTyping);
        return newpartUsage;
    }

    /**
     * Get source {@link PortUsage} of a given logicalConstituentalFlow.
     * 
     * @param logicalConstituentalFlow
     *            the logicalConstituentFlow with the {@link PortUsage} source to extract
     * @return the source {@link PortUsage}
     */
    public PortUsage getSEIMLogicalConstituentSourcePort(InterfaceUsage logicalConstituentalFlow) {
        return getFirstAsPortUsageOrNull(logicalConstituentalFlow.getSource());
    }

    /**
     * Get target {@link PortUsage} of a given logicalConstituentalFlow.
     * 
     * @param logicalConstituentalFlow
     *            the logicalConstituentFlow with the {@link PortUsage} target to extract
     * @return the target {@link PortUsage}
     */
    public PortUsage getSEIMLogicalConstituentTargetPort(InterfaceUsage logicalConstituentalFlow) {
        return getFirstAsPortUsageOrNull(logicalConstituentalFlow.getTarget());
    }

    private PortUsage getFirstAsPortUsageOrNull(EList<Element> list) {
        if (list.get(0) instanceof PortUsage) {
            return (PortUsage) list.get(0);
        }
        return null;
    }

    /**
     * Create {@link InterfaceUsage} typed by SEIM::LogicalFlow in a given parent. Source {@link PortUsage} and target
     * {@link PortUsage} will be automatically created on the given source and target.
     * 
     * @param source
     *            the source which hold the new source {@link PortUsage}, input of logicalConstituentalFlow
     * @param target
     *            the target which hold the new target {@link PortUsage}, output of logicalConstituentalFlow
     * @return the new {@link InterfaceUsage} typed by SEIM::LogicalFlow.
     */
    public InterfaceUsage createLogicalFlow(PartUsage source, PartUsage target) {
        Namespace namespace = this.getClosestContainingDefinitionOrPackageFrom(source);
        Optional<PortDefinition> optSeimLogicalConstituentalPortDefinition = getOptionalLogicalConstituentPortDefinition(source);
        Optional<InterfaceDefinition> optSeimLogicalConstituentFlowDefinition = getOptionalLogicalConstituentFlowDefinition(source);
        if (namespace == null || optSeimLogicalConstituentalPortDefinition.isEmpty() || optSeimLogicalConstituentFlowDefinition.isEmpty()) {
            return null;
        }

        PortUsage newSourcePortUsage = createPortUsage(optSeimLogicalConstituentalPortDefinition.get(), source, FeatureDirectionKind.IN);
        PortUsage newTargetPortUsage = createPortUsage(optSeimLogicalConstituentalPortDefinition.get(), target, FeatureDirectionKind.OUT);

        InterfaceUsage newInterfaceUsage = createInterfaceUsage(optSeimLogicalConstituentFlowDefinition.get(), namespace);
        newInterfaceUsage.getSource().add(newSourcePortUsage);
        newInterfaceUsage.getTarget().add(newTargetPortUsage);
        return newInterfaceUsage;
    }

    private Optional<PartDefinition> getOptionalSiemLogicalConstituentDefinition(EObject sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof PartDefinition)
                .map(PartDefinition.class::cast)
                .filter(functionDef -> LOGICAL_CONSTITUENT_QUALIFIED_NAME.equals(functionDef.getQualifiedName()))
                .findFirst();
    }

    private Optional<PortDefinition> getOptionalLogicalConstituentPortDefinition(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof PortDefinition)
                .map(PortDefinition.class::cast)
                .filter(portDef -> LOGICAL_CONSTITUENT_PORT_QUALIFIED_NAME.equals(portDef.getQualifiedName()))
                .findFirst();
    }

    private Optional<InterfaceDefinition> getOptionalLogicalConstituentFlowDefinition(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof InterfaceDefinition)
                .map(InterfaceDefinition.class::cast)
                .filter(portDef -> LOGICAL_CONSTITUENT_FLOW_QUALIFIED_NAME.equals(portDef.getQualifiedName()))
                .findFirst();
    }

    private PortUsage createPortUsage(PortDefinition portDefinition, PartUsage parent, FeatureDirectionKind direction) {
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
        interfaceUsage.setDeclaredName("LogicalFlow");
        featureMembership.getOwnedRelatedElement().add(interfaceUsage);

        FeatureTyping interfaceFeatureTyping = SysmlFactory.eINSTANCE.createFeatureTyping();
        this.elementInitializer(interfaceFeatureTyping);
        interfaceFeatureTyping.setType(interfaceDefinition);
        interfaceUsage.getOwnedRelationship().add(interfaceFeatureTyping);

        return interfaceUsage;
    }

    /**
     * Allocate {@link ActionUsage} on the semantic element represented by a given node.
     * 
     * @param droppedFunction
     *            {@link ActionUsage} dropped from explorer on Node
     * @param editingContext
     *            the editing context
     * @param diagramContext
     *            the diagram context
     * @param selectedNode
     *            the target node
     * @return allocated {@link ActionUsage} typed by SEIM::Function.
     */
    public ActionUsage dropFunctionOnPartFromExplorer(Element droppedElement, IEditingContext editingContext, IDiagramContext diagramContext, Node selectedNode) {
        Optional<Object> optTargetElement;
        if (selectedNode != null) {
            optTargetElement = this.objectService.getObject(editingContext, selectedNode.getTargetObjectId());
        } else {
            optTargetElement = this.objectService.getObject(editingContext, diagramContext.getDiagram().getTargetObjectId());
        }
        if (optTargetElement.isPresent() && optTargetElement.get() instanceof PartUsage part) {
            if (droppedElement instanceof ActionUsage actionUsage) {
                return createAllocatedFunction(part, actionUsage);
            }
        }
        return null;

    }

    private ActionUsage createAllocatedFunction(PartUsage part, ActionUsage function) {
        Namespace namespace = this.getClosestContainingDefinitionOrPackageFrom(part);
        AllocationDefinition seimAllocationDefinition = getSEIMAllocatedFunction(part);
        if (namespace == null || seimAllocationDefinition == null) {
            return null;
        }
        FeatureMembership featureMembership = SysmlFactory.eINSTANCE.createFeatureMembership();
        namespace.getOwnedRelationship().add(featureMembership);

        AllocationUsage allocationUsage = SysmlFactory.eINSTANCE.createAllocationUsage();
        allocationUsage.setDeclaredName("myAllocatedFunction");
        featureMembership.getOwnedRelatedElement().add(allocationUsage);

        FeatureTyping interfaceFeatureTyping = SysmlFactory.eINSTANCE.createFeatureTyping();
        this.elementInitializer(interfaceFeatureTyping);
        interfaceFeatureTyping.setType(seimAllocationDefinition);
        allocationUsage.getOwnedRelationship().add(interfaceFeatureTyping);

        allocationUsage.getSource().add(function);
        allocationUsage.getTarget().add(part);

        return function;
    }

    private AllocationDefinition getSEIMAllocatedFunction(Element sourceElement) {
        return extractGlobalNotifier(sourceElement).stream()
                .filter(notifier -> notifier instanceof AllocationDefinition)
                .map(AllocationDefinition.class::cast)
                .filter(allocationDefinition -> SEIM_ALLOCATED_FUNCTION_QUALIFIED_NAME.equals(allocationDefinition.getQualifiedName()))
                .findFirst()
                .orElse(null);
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
     * Verify that the {@link PartUsage} is allocated in a @link AllocationUsage} of the right type within the project.
     * 
     * @param SEIMLogicalConstituent
     * @return
     */
    public boolean isSEIMLogicalConstituentAllocated(PartUsage seimLogicalConstituent) {

        return extractNotifier(seimLogicalConstituent).stream()
                .filter(notifier -> notifier instanceof AllocationUsage)
                .map(AllocationUsage.class::cast)
                .filter(isTypedWith(SEIM_ALLOCATED_FUNCTION_QUALIFIED_NAME))
                .anyMatch(allocatedLogicalConstituent -> allocatedLogicalConstituent.getTarget().contains(seimLogicalConstituent));
    }

    private Predicate<? super org.eclipse.syson.sysml.Feature> isTypedWith(String qualifiedName) {
        return element -> element.getType().stream().anyMatch(t -> t != null && qualifiedName != null && qualifiedName.equals(t.getQualifiedName()));
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

    private List<PartUsage> getMainLogicalConstituents(Package pkg) {
        return pkg.getOwnedMember().stream()
                .filter(e -> e instanceof PartUsage)
                .map(PartUsage.class::cast)
                .filter(isTypedWith(LOGICAL_CONSTITUENT_QUALIFIED_NAME))
                .toList();

    }

    private List<PartUsage> getSubLogicalConstituents(Usage usage) {
        return usage.getNestedPart().stream().filter(isTypedWith(LOGICAL_CONSTITUENT_QUALIFIED_NAME))
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

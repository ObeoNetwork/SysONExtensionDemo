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
package org.eclipse.syson.easymod.diagram.fbs.services;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.syson.easymod.diagram.services.EasyModCommonServices;
import org.eclipse.syson.easymod.diagram.utils.EasyModConstants;
import org.eclipse.syson.sysml.ActionDefinition;
import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.AllocationUsage;
import org.eclipse.syson.sysml.Element;
import org.eclipse.syson.sysml.FeatureDirectionKind;
import org.eclipse.syson.sysml.InterfaceDefinition;
import org.eclipse.syson.sysml.InterfaceUsage;
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
public class EasymodFBSService extends EasyModCommonServices {

    /**
     * Returns {@code true} if the diagram can be created on the provided {@code element}.
     *
     * @param element
     *            the element to check
     * @return {@code true} if the diagram can be created on the provided {@code element}
     */
    public boolean canCreateDiagram(Element element) {
        return element instanceof Package || element instanceof ActionUsage;
    }

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
                .filter(isTypedWith(EasyModConstants.FUNCTION_FLOW_QUALIFIED_NAME))
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
        Optional<ActionDefinition> optSeimFunctionDefinition = getOptionalSeimFunctionDefinition(parent);
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

        setType(optSeimFunctionDefinition.get(), newActionUsage);
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
        Optional<PortDefinition> optSeimFunctionalPortDefinition = getOptionalSeimFunctionPortDefinition(source);
        Optional<InterfaceDefinition> optSeimFunctionFlowDefinition = getOptionalSeimFunctionalFlowDefinition(source);
        if (namespace == null || optSeimFunctionalPortDefinition.isEmpty() || optSeimFunctionFlowDefinition.isEmpty()) {
            return null;
        }

        PortUsage newSourcePortUsage = createPortUsage(optSeimFunctionalPortDefinition.get(), source, FeatureDirectionKind.IN);
        PortUsage newTargetPortUsage = createPortUsage(optSeimFunctionalPortDefinition.get(), target, FeatureDirectionKind.OUT);

        InterfaceUsage newInterfaceUsage = createInterfaceUsage(optSeimFunctionFlowDefinition.get(), namespace, "myFunctionalFlow");
        newInterfaceUsage.getSource().add(newSourcePortUsage);
        newInterfaceUsage.getTarget().add(newTargetPortUsage);
        return newInterfaceUsage;
    }

    /**
     * Verify that the SEIM function {@link ActionUsage} is allocated in a @link AllocationUsage} of the right type
     * within the project.
     * 
     * @param seimFunction
     *            the seim Function to check allocation
     * @return {@code true} if the seim function is allocated, {@code false} otherwise
     */
    public boolean isSEIMFunctionAllocated(ActionUsage seimFunction) {
        return extractNotifier(seimFunction).stream()
                .filter(notifier -> notifier instanceof AllocationUsage)
                .map(AllocationUsage.class::cast)
                .filter(isTypedWith(EasyModConstants.SEIM_ALLOCATED_FUNCTION_QUALIFIED_NAME))
                .anyMatch(allocatedFunction -> allocatedFunction.getSource().contains(seimFunction));
    }

    private List<ActionUsage> getPackageFunctions(Package pkg) {
        return pkg.getOwnedMember().stream().filter(e -> e instanceof ActionUsage).map(ActionUsage.class::cast)
                .filter(isTypedWith(EasyModConstants.FUNCTION_QUALIFIED_NAME))
                .toList();

    }

    private List<ActionUsage> getSubFunctions(Usage usage) {
        return usage.getNestedAction().stream().filter(isTypedWith(EasyModConstants.FUNCTION_QUALIFIED_NAME))
                .toList();
    }

}

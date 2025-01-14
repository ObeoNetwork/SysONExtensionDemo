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
package org.eclipse.syson.easymod.form.fbstopbs.services;

import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.syson.easymod.diagram.services.EasyModCommonServices;
import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.AllocationUsage;
import org.eclipse.syson.sysml.Element;
import org.eclipse.syson.sysml.PartUsage;

/**
 * FBS to PBS form services.
 *
 * @author ebausson
 */
public class EasymodFBSToPBSService extends EasyModCommonServices {

    /**
     * Return the PieChart Data.
     * 
     * @param element
     *            the Element the piechart form is part of.
     * @return A list of 2 numbers: the number of allocated functions followed by the number of unallocated functions.
     */
    public List<Number> getAttributionPieChartData(Element element) {
        List<Notifier> notifiers = this.extractNotifier(element);
        List<ActionUsage> allocatedFunctions = this.getAllocatedFunctions(notifiers);
        List<ActionUsage> unallocatedFunctions = this.getUnallocatedFunctions(notifiers);
        return List.of(allocatedFunctions.size(), unallocatedFunctions.size());
    }

    /**
     * Return the PieChart key values.
     * 
     * @param element
     *            the Element the piechart form is part of.
     * @return the list of key value : {Allocated, To allocate}
     */
    public List<String> getPieChartKeyValue(Element element) {
        String toAllocate = "To allocate";
        String allocated = "Allocated";
        List<Notifier> notifiers = this.extractNotifier(element);
        List<ActionUsage> allocatedFunctions = this.getAllocatedFunctions(notifiers);
        List<ActionUsage> unallocatedFunctions = this.getUnallocatedFunctions(notifiers);
        if (allocatedFunctions.size() == 0) {
            allocated = "";
        }
        if (unallocatedFunctions.size() == 0) {
            toAllocate = "";
        }
        return List.of(allocated, toAllocate);
    }

    private List<ActionUsage> getAllocatedFunctions(List<Notifier> notifiers) {
        return notifiers.stream()
                .filter(AllocationUsage.class::isInstance)
                .map(AllocationUsage.class::cast)
                .filter(allocation -> allocation.getTarget().size() == 1)
                .filter(allocation -> allocation.getSource().size() == 1)
                .map(allocation -> allocation.getSource().get(0))
                .filter(ActionUsage.class::isInstance)
                .map(ActionUsage.class::cast)
                .toList();
    }

    private List<ActionUsage> getUnallocatedFunctions(List<Notifier> notifiers) {
        List<ActionUsage> allocatedFunctions = this.getAllocatedFunctions(notifiers);
        return notifiers.stream()
                .filter(ActionUsage.class::isInstance)
                .map(ActionUsage.class::cast)
                .filter(function -> !allocatedFunctions.contains(function))
                .toList();
    }

    public PartUsage getAllocatedProductIfExist(ActionUsage function) {
        return extractNotifier(function).stream()
                .filter(AllocationUsage.class::isInstance)
                .map(AllocationUsage.class::cast)
                .filter(allocation -> allocation.getSource().contains(function))
                .map(AllocationUsage::getTarget)
                .flatMap(List::stream)
                .filter(PartUsage.class::isInstance)
                .map(PartUsage.class::cast)
                .findFirst()
                .orElse(null);
    }

}

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
package org.eclipse.easymod.diagram.view;

import org.eclipse.sirius.components.view.builder.generated.diagram.InsideLabelDescriptionBuilder;
import org.eclipse.sirius.components.view.builder.generated.diagram.InsideLabelStyleBuilder;
import org.eclipse.sirius.components.view.builder.providers.INodeDescriptionProvider;
import org.eclipse.sirius.components.view.diagram.InsideLabelDescription;
import org.eclipse.sirius.components.view.diagram.InsideLabelPosition;
import org.eclipse.sirius.components.view.diagram.InsideLabelStyle;

/**
 * ABstract node description provider.
 * 
 * @author ebausson
 */
public abstract class AbstractEasymodNodeDescriptionProvider implements INodeDescriptionProvider {

    protected abstract String getName();

    protected String getColor() {
        return getName() + "_COLOR";
    }

    protected String getBorderColor() {
        return getName() + "_BORDER_COLOR";
    }

    protected InsideLabelDescription generateDefaultLabel(String expression) {
        InsideLabelStyle insideLabelStyle = new InsideLabelStyleBuilder().build();
        insideLabelStyle.setBorderSize(0);
        return new InsideLabelDescriptionBuilder()
                .labelExpression(expression)
                .style(insideLabelStyle)
                .position(InsideLabelPosition.TOP_CENTER)
                .build();
    }
}

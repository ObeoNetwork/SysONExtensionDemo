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

import org.eclipse.sirius.components.view.builder.providers.INodeDescriptionProvider;

/**
 * Abstract node description provider.
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
}

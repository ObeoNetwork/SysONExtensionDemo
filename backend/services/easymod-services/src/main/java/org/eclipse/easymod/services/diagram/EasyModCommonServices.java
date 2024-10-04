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
package org.eclipse.easymod.services.diagram;

import org.eclipse.syson.services.ElementInitializerSwitch;
import org.eclipse.syson.sysml.Element;

/**
 * Services shared by multiple easymod views.
 *
 * @author ebausson
 */
public class EasyModCommonServices {

    protected final ElementInitializerSwitch elementInitializerSwitch = new ElementInitializerSwitch();

    protected Element elementInitializer(Element element) {
        return this.elementInitializerSwitch.doSwitch(element);
    }
}

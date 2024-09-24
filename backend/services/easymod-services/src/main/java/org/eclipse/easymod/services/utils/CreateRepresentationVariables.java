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
package org.eclipse.easymod.services.utils;

import org.eclipse.sirius.components.collaborative.dto.CreateRepresentationInput;

/**
 * Variables object for the http request allowing to create a representation.
 *
 * @author arichard
 */
public class CreateRepresentationVariables {

    private CreateRepresentationInput input;

    public CreateRepresentationVariables(CreateRepresentationInput input) {
        this.setInput(input);
    }

    public CreateRepresentationInput getInput() {
        return this.input;
    }

    public void setInput(CreateRepresentationInput input) {
        this.input = input;
    }

}

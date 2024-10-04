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
package org.eclipse.easymod.diagram.utils;

import org.springframework.stereotype.Service;

/**
 * Color service.
 * 
 * @author ebausson
 */
@Service
public class EasyModColorService {

    public static final String FUNCTION_NODE_ALLOCATED_BACKGROUND_COLOR = "green 100";

    public static final String FUNCTION_NODE_UNALLOCATED_BACKGROUND_COLOR = "green 400";

    public static final String FUNCTION_NODE_BORDER_COLOR = "green 900";

    public static final String FUNCTION_EDGE_COLOR = "green 900";

    public static final String LOGICAL_CONSTITUENT_NODE_BACKGROUND_COLOR = "blue 100";

    public static final String LOGICAL_CONSTITUENT_NODE_BORDER_COLOR = "blue 900";

    public static final String LOGICAL_CONSTITUENT_EDGE_COLOR = "blue 900";
}

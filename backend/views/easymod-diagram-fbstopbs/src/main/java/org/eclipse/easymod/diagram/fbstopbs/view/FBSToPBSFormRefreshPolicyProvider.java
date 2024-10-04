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
package org.eclipse.easymod.diagram.fbstopbs.view;

import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.api.IRepresentationRefreshPolicy;
import org.eclipse.sirius.components.collaborative.api.IRepresentationRefreshPolicyProvider;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.springframework.stereotype.Service;

/**
 * The representation refresh policy provider for the Methodology representation.
 *
 * @author arichard
 */
@Service
public class FBSToPBSFormRefreshPolicyProvider implements IRepresentationRefreshPolicyProvider {

    @Override
    public boolean canHandle(IRepresentationDescription representationDescription) {
        return "siriusComponents://representationDescription?kind=formDescription&sourceKind=view&sourceId=8c459c3a-6a2a-341b-ad5f-9483cebf821f&sourceElementId=b7d4e7ae-f2bf-33e2-ad37-36045f743e57"
                .equals(representationDescription.getId());
    }

    @Override
    public IRepresentationRefreshPolicy getRepresentationRefreshPolicy(IRepresentationDescription representationDescription) {
        return (changeDescription) -> ChangeKind.SEMANTIC_CHANGE.equals(changeDescription.getKind());
    }

}

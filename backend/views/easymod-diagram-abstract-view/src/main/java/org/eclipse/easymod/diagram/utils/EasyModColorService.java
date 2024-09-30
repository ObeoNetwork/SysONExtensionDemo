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

import java.util.Collection;
import java.util.List;

import org.eclipse.sirius.components.view.ColorPalette;
import org.eclipse.sirius.components.view.FixedColor;
import org.eclipse.sirius.components.view.UserColor;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.ViewFactory;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilder;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.springframework.stereotype.Service;

/**
 * Color service.
 * 
 * @author ebausson
 */
@Service
public final class EasyModColorService implements IColorProvider {

    public static final String BORDER_COLOR = "Border Color";

    public static final String EDGE_COLOR = "Edge Color";

    private static EasyModColorService eINSTANCE;

    private View view;

    private EasyModColorService() {
        view = new ViewBuilder().build();
        view.getColorPalettes().add(createColorPalette());
    }

    public static EasyModColorService getInstance() {
        if (eINSTANCE == null) {
            eINSTANCE = new EasyModColorService();
        }
        return eINSTANCE;
    }

    @Override
    public UserColor getColor(String colorName) {
        return this.view.getColorPalettes()
                .stream()
                .map(ColorPalette::getColors)
                .flatMap(Collection::stream)
                .filter(userColor -> userColor.getName().equals(colorName))
                .findFirst()
                .orElse(null);
    }

    public ColorPalette createColorPalette() {
        ColorPalette colorPalette = ViewFactory.eINSTANCE.createColorPalette();
        List<UserColor> colors = colorPalette.getColors();

        colors.add(this.createFixedColor(BORDER_COLOR, "#282"));
        colors.add(this.createFixedColor(EDGE_COLOR, "#5B5"));

        return colorPalette;
    }

    private FixedColor createFixedColor(String name, String value) {
        var fixedColor = ViewFactory.eINSTANCE.createFixedColor();
        fixedColor.setName(name);
        fixedColor.setValue(value);

        return fixedColor;
    }
}

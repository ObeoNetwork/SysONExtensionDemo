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
package org.eclipse.syson.easymod.form.fbstopbs;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.sirius.components.charts.descriptions.IChartDescription;
import org.eclipse.sirius.components.charts.piechart.PieChartDescription;
import org.eclipse.sirius.components.charts.piechart.components.PieChartStyle;
import org.eclipse.sirius.components.forms.WidgetIdProvider;
import org.eclipse.sirius.components.forms.description.AbstractControlDescription;
import org.eclipse.sirius.components.forms.description.ChartWidgetDescription;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.interpreter.StringValueProvider;
import org.eclipse.sirius.components.representations.VariableManager;

/**
 * Description of the FBStoPBS Form PieChart .
 *
 * @author ebausson
 */
public class EasymodPieChartDescriptionFactory extends AbstractControlDescription {

    private static final String PIECHART_LABEL_EXPRESSION = "FBS to PBS Allocations";

    private static final String PIECHART_KEYS_EXPRESSION = "aql:Sequence{'Allocated', 'To allocate'}";

    private static final String PIECHART_VALUES_EXPRESSION = "aql:self.getAttributionPieChartData()}";

    private static final String PIECHART_LABEL = "Distribution";

    private static final String VARIABLE_MANAGER = "variableManager";

    private static final String FORM_ID = "irt://easymod/pieChart";

    private AQLInterpreter interpreter;

    public EasymodPieChartDescriptionFactory(AQLInterpreter interpreter, Function<VariableManager, String> targetObjectIdProvider) {
        this.interpreter = Objects.requireNonNull(interpreter);
        this.targetObjectIdProvider = Objects.requireNonNull(targetObjectIdProvider);
    }

    public AbstractControlDescription generateDescription() {
        Function<VariableManager, PieChartStyle> styleProvider = getPieChartStyleProvider();

        IChartDescription chartDescription = PieChartDescription.newPieChartDescription(FORM_ID)
                .label(PIECHART_LABEL)
                .targetObjectIdProvider(this.targetObjectIdProvider)
                .keysProvider(this.getMultiValueProvider(PIECHART_KEYS_EXPRESSION, String.class))
                .valuesProvider(this.getMultiValueProvider(PIECHART_VALUES_EXPRESSION, Number.class))
                .styleProvider(styleProvider)
                .build();

        WidgetIdProvider idProvider = new WidgetIdProvider();
        StringValueProvider labelProvider = this.getStringValueProvider(PIECHART_LABEL_EXPRESSION);
        ChartWidgetDescription.Builder builder = ChartWidgetDescription.newChartWidgetDescription(FORM_ID)
                .targetObjectIdProvider(this.targetObjectIdProvider)
                .labelProvider(labelProvider)
                .idProvider(idProvider)
                .chartDescription(chartDescription)
                .diagnosticsProvider(variableManager -> List.of())
                .kindProvider(object -> "")
                .messageProvider(object -> "");
        return builder.build();

    }

    private StringValueProvider getStringValueProvider(String valueExpression) {
        String safeValueExpression = Optional.ofNullable(valueExpression).orElse("");
        return new StringValueProvider(this.interpreter, safeValueExpression);
    }

    private <T> Function<VariableManager, List<T>> getMultiValueProvider(String expression, Class<T> type) {
        String safeExpression = Optional.ofNullable(expression).orElse("");
        return variableManager -> {
            VariableManager childVariableManager = variableManager.createChild();
            childVariableManager.put(VARIABLE_MANAGER, variableManager);
            if (safeExpression.isBlank()) {
                return List.of();
            } else {
                return this.interpreter.evaluateExpression(childVariableManager.getVariables(), safeExpression).asObjects().orElse(List.of()).stream().map(type::cast).toList();
            }
        };
    }

    private Function<VariableManager, PieChartStyle> getPieChartStyleProvider() {
        return variableManager -> {
            return PieChartStyle.newPieChartStyle()
                    .fontSize(16)
                    .build();
        };
    }

}
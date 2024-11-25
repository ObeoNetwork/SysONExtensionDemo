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
package org.eclipse.syson.easymod.form.fbstopbs.descriptions;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.forms.WidgetIdProvider;
import org.eclipse.sirius.components.forms.description.AbstractControlDescription;
import org.eclipse.sirius.components.forms.description.TableWidgetDescription;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.interpreter.StringValueProvider;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.tables.descriptions.CellDescription;
import org.eclipse.sirius.components.tables.descriptions.ColumnDescription;
import org.eclipse.sirius.components.tables.descriptions.LineDescription;
import org.eclipse.sirius.components.tables.descriptions.TableDescription;
import org.eclipse.syson.easymod.form.fbstopbs.services.EasymodFBSToPBSService;
import org.eclipse.syson.sysml.ActionUsage;

/**
 * Description of the FBStoPBS Form Table.
 *
 * @author ebausson
 */
public class EasymodTableDescriptionFactory extends AbstractControlDescription {

    protected static final String FUNCTIONS_COLUMN_LABEL = "Functions";

    protected static final String PRODUCTS_COLUMN_LABEL = "Products";

    private static final String CELL_DESCRIPTION_ID = "irt://easymod/cell";

    private static final String LINE_DESCRIPTION_ID = "irt://easymod/line";

    private static final String FUNCTIONS_COLUMN_ID = "irt://easymod/column/function";

    private static final String PRODUCTS_COLUMN_ID = "irt://easymod/column/product";

    private static final String TABLE_DESCRIPTION_ID = "irt://easymod/table";

    private static final String TABLE_WIDGET_DESCRIPTION_ID = "irt://easymod/widget/table";

    private static final String TABLE_LABEL_EXPRESSION = "FBS to PBS Allocations";

    private final AQLInterpreter interpreter;

    private final IIdentityService identityService;

    private final IObjectService objectService;

    private final EasymodFBSToPBSService easymodFBSToPBSService;

    public EasymodTableDescriptionFactory(AQLInterpreter interpreter, Function<VariableManager, String> targetObjectIdProvider, IObjectService objectService, IIdentityService identityService) {
        this.interpreter = Objects.requireNonNull(interpreter);
        this.targetObjectIdProvider = Objects.requireNonNull(targetObjectIdProvider);
        this.identityService = Objects.requireNonNull(identityService);
        this.objectService = Objects.requireNonNull(objectService);
        this.easymodFBSToPBSService = new EasymodFBSToPBSService();
    }

    public AbstractControlDescription generateDescription() {
        EasymodTableDescriptionBuilderHelper helper = new EasymodTableDescriptionBuilderHelper(easymodFBSToPBSService);
        var labelProvider = this.getStringValueProvider(TABLE_LABEL_EXPRESSION);

        CellDescription cellDescription = CellDescription.newCellDescription(CELL_DESCRIPTION_ID)
                .targetObjectIdProvider(vm -> "")
                .targetObjectKindProvider(vm -> "")
                .cellTypeProvider(helper.getCellTypeProvider())
                .cellValueProvider(helper.getCellValueProvider())
                .cellOptionsIdProvider(helper.getCellOptionsIdProvider(objectService))
                .cellOptionsLabelProvider(helper.getCellOptionsLabelProvider(objectService))
                .cellOptionsProvider(helper.getCellOptionsProvider())
                .newCellValueHandler(helper.getNewCellValueHandler())
                .build();

        LineDescription lineDescription = LineDescription.newLineDescription(UUID.nameUUIDFromBytes(LINE_DESCRIPTION_ID.getBytes()))
                .targetObjectIdProvider(this::getTargetObjectId)
                .targetObjectKindProvider(this::getTargetObjectId)
                .semanticElementsProvider(variableManager -> (List<Object>) this.getLineSemanticElements(variableManager, easymodFBSToPBSService))
                .build();

        ColumnDescription functionColumnDescription = ColumnDescription.newColumnDescription(UUID.nameUUIDFromBytes(FUNCTIONS_COLUMN_ID.getBytes()))
                .labelProvider(variableManager -> FUNCTIONS_COLUMN_LABEL) // column title
                .targetObjectIdProvider(variableManager -> FUNCTIONS_COLUMN_LABEL)
                .targetObjectKindProvider(variableManager -> "")
                .semanticElementsProvider(variableManager -> List.of(FUNCTIONS_COLUMN_LABEL))
                .build();

        ColumnDescription productColumnDescription = ColumnDescription.newColumnDescription(UUID.nameUUIDFromBytes(PRODUCTS_COLUMN_ID.getBytes()))
                .labelProvider(variableManager -> PRODUCTS_COLUMN_LABEL) // column title
                .targetObjectIdProvider(variableManager -> PRODUCTS_COLUMN_LABEL)
                .targetObjectKindProvider(variableManager -> "")
                .semanticElementsProvider(variableManager -> List.of(PRODUCTS_COLUMN_LABEL))
                .build();

        TableDescription tableDescription = TableDescription.newTableDescription(TABLE_DESCRIPTION_ID)
                .labelProvider(labelProvider)
                .lineDescriptions((List<LineDescription>) List.of(lineDescription))
                .columnDescriptions(List.of(functionColumnDescription, productColumnDescription))
                .targetObjectIdProvider(this::getTargetObjectId)
                .targetObjectKindProvider(this::getTargetObjectKind)
                .cellDescription(cellDescription)
                .build();

        return TableWidgetDescription.newTableWidgetDescription(TABLE_WIDGET_DESCRIPTION_ID)
                .targetObjectIdProvider(targetObjectIdProvider)
                .idProvider(new WidgetIdProvider())
                .labelProvider(labelProvider)
                .iconURLProvider(object -> List.of())
                .isReadOnlyProvider(helper.isWidgetReadOnly())
                .diagnosticsProvider(variableManager -> List.of())
                .kindProvider(object -> "")
                .messageProvider(object -> "")
                .tableDescription(tableDescription)
                .build();
    }

    private List<? extends Object> getLineSemanticElements(VariableManager variableManager, EasymodFBSToPBSService service) {
        return variableManager.get(VariableManager.SELF, org.eclipse.syson.sysml.Package.class).stream()
                .map(service::extractNotifier)
                .flatMap(List::stream)
                .filter(ActionUsage.class::isInstance)
                .map(ActionUsage.class::cast)
                .toList();
    }

    private StringValueProvider getStringValueProvider(String valueExpression) {
        String safeValueExpression = Optional.ofNullable(valueExpression).orElse("");
        return new StringValueProvider(this.interpreter, safeValueExpression);
    }

    private String getTargetObjectId(VariableManager variableManager) {
        return variableManager.get(VariableManager.SELF, Object.class)
                .map(this.identityService::getId)
                .orElse(null);
    }

    private String getTargetObjectKind(VariableManager variableManager) {
        return variableManager.get(VariableManager.SELF, Object.class)
                .map(this.identityService::getKind)
                .orElse(null);
    }
}

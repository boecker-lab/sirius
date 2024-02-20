/*
 *  This file is part of the SIRIUS Software for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer, Marvin Meusel and Sebastian Böcker,
 *  Chair of Bioinformatics, Friedrich-Schiller University.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License along with SIRIUS.  If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>
 */

package de.unijena.bioinf.ms.gui.compute;

import de.unijena.bioinf.chemdb.DataSource;
import de.unijena.bioinf.chemdb.DataSources;
import de.unijena.bioinf.chemdb.custom.CustomDataSources;
import de.unijena.bioinf.ms.gui.utils.GuiUtils;
import de.unijena.bioinf.ms.gui.utils.jCheckboxList.JCheckBoxList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBSelectionList extends JCheckBoxList<CustomDataSources.Source> {


    public DBSelectionList() {
        this(true);
    }
    public DBSelectionList( boolean includeCustom) {
        this(null,includeCustom);
    }

    public DBSelectionList(@Nullable String descriptionKey, boolean includeCustom) {
        this(descriptionKey, CustomDataSources.sourcesStream().
                filter(db -> !CustomDataSources.NON_SEARCHABLE_LIST.contains(db.name())).
                filter(db -> includeCustom || !db.isCustomSource()).sorted(Comparator.comparing(CustomDataSources.Source::name)).
                collect(Collectors.toList()));
    }

    protected DBSelectionList(@Nullable String descKey, @NotNull DataSource... values) {
        this(descKey, Stream.of(values).map(DataSource::name).toArray(String[]::new));
    }

    protected DBSelectionList(@Nullable String descKey, @NotNull String... dbName) {
        this(descKey, CustomDataSources.getSourcesFromNames());
    }

    public DBSelectionList(@NotNull List<CustomDataSources.Source> values) {
        this(null, values);
    }

    public DBSelectionList(@Nullable String descKey, @NotNull List<CustomDataSources.Source> values) {
        super(values, (a,b) -> a.name().equals(b.name()));
        if (descKey != null)
            GuiUtils.assignParameterToolTip(this, descKey);
    }

    public List<String> getSelectedFormulaSearchDBStrings() {
        return getCheckedItems().stream().map(db -> {
            if (db.isCustomSource())
                return db.name();
            else
                return DataSources.getSourceFromName(db.name()).map(DataSource::name).orElse(null);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}

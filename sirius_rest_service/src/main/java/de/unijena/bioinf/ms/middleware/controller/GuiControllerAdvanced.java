/*
 *
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer and Sebastian Böcker,
 *  Chair of Bioinformatics, Friedrich-Schilller University.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with SIRIUS. If not, see <https://www.gnu.org/licenses/lgpl-3.0.txt>
 */

package de.unijena.bioinf.ms.middleware.controller;

import de.unijena.bioinf.ms.middleware.model.gui.GuiParameters;
import de.unijena.bioinf.ms.middleware.service.gui.GuiService;
import de.unijena.bioinf.ms.middleware.service.projects.ProjectsProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Advanced GUI control", description = "Open, control and close SIRIUS Graphical User Interface (GUI) on specified projects.")
@ConditionalOnProperty(name = "sirius.middleware.controller.gui.advanced", matchIfMissing = false, havingValue = "true")
public class GuiControllerAdvanced extends GuiController{
    protected GuiControllerAdvanced(ProjectsProvider<?> projectsProvider, GuiService guiService) {
        super(projectsProvider, guiService);
    }

    /**
     * Open GUI instance on specified project-space and bring the GUI window to foreground.
     *
     * @param readOnly  open in read-only mode.
     * @param projectId of project-space the GUI instance will connect to.
     */
    @PostMapping(value = "/api/projects/{projectId}/gui", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void openGui(@PathVariable String projectId, @RequestBody(required = false) GuiParameters guiParameters, @RequestParam(defaultValue = "false") boolean readOnly) {
        guiService.createGuiInstance(projectId, guiParameters);
    }


    /**
     * Apply given changes to the running GUI instance.
     *
     * @param projectId     of project-space the GUI instance is connected to.
     * @param guiParameters parameters that should be applied.
     */
    @PatchMapping(value = "/api/projects/{projectId}/gui", produces = MediaType.APPLICATION_JSON_VALUE)
    public void applyToGui(@PathVariable String projectId, @RequestBody GuiParameters guiParameters) {
        guiService.applyToGuiInstance(projectId, guiParameters);
    }

}

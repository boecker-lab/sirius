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

package de.unijena.bioinf.ms.middleware.compute;

import de.unijena.bioinf.ms.middleware.BaseApiController;
import de.unijena.bioinf.ms.middleware.SiriusContext;
import de.unijena.bioinf.ms.middleware.compute.model.JobSubmission;
import de.unijena.bioinf.ms.rest.model.JobId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "Computations", description = "Start, monitor and cancel compute jobs.")
public class ComputeController extends BaseApiController {
    public ComputeController(SiriusContext context) {
        super(context);
    }

    /**
     * Get job information and its current state and progress (if available).
     *
     * @param projectId project-space to run jobs on
     */

    @Deprecated
    @GetMapping(value = "/projects/{projectId}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JobId> getJobs(@PathVariable String projectId, @RequestParam(required = false, defaultValue = "false") boolean includeState) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "NOT YET IMPLEMENTED");
    }

    /**
     * Get job information and its current state and progress (if available).
     *
     * @param projectId project-space to run jobs on
     * @param jobId of the job to be returned
     */
    @Deprecated
    @GetMapping(value = "/projects/{projectId}/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobId getJob(@PathVariable String projectId, @PathVariable String jobId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "NOT YET IMPLEMENTED");
    }

    /**
     * Start computation for given compounds and with given parameters.
     *
     * @param projectId project-space to run jobs on
     * @param jobSubmission configuration of the job that will be submitted of the job to be returned
     */
    @Deprecated
    @PostMapping(value = "/projects/{projectId}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobId startJob(@PathVariable String projectId, @RequestBody JobSubmission jobSubmission) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "NOT YET IMPLEMENTED");
    }

    /**
     * Delete job. Specify how to behave for running jobs.
     *
     * @param projectId project-space to run jobs on
     * @param jobId of the job to be deleted
     */
    @Deprecated
    @DeleteMapping(value = "/projects/{projectId}/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteJob(@PathVariable String projectId,
                          @PathVariable String jobId,
                          @RequestParam(required = false, defaultValue = "true") boolean cancelIfRunning,
                          @RequestParam(required = false, defaultValue = "true") boolean awaitDeletion) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "NOT YET IMPLEMENTED");
    }

    /**
     * Get with all parameters set to default values.
     */
    @GetMapping(value = "/default-job-parameters", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobSubmission getDefaultJobParameters(@RequestParam(required = false, defaultValue = "false") boolean includeConfigMap) {
        return JobSubmission.createDefaultInstance(includeConfigMap);
    }
}

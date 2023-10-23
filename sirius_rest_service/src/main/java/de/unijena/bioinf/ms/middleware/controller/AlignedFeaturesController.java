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

package de.unijena.bioinf.ms.middleware.controller;

import de.unijena.bioinf.ms.middleware.model.features.AlignedFeature;
import de.unijena.bioinf.ms.middleware.model.features.annotations.*;
import de.unijena.bioinf.ms.middleware.model.spectra.AnnotatedSpectrum;
import de.unijena.bioinf.ms.middleware.service.projects.ProjectsProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;

@RestController
@RequestMapping(value = "/api/projects/{projectId}/aligned-features")
@Tag(name = "Feature based API", description = "Access features (aligned over runs) and there Annotations of " +
        "a specified project-space. This is the entry point to access all raw annotation results an there summaries.")
public class AlignedFeaturesController {


    private final ProjectsProvider<?> projectsProvider;

    @Autowired
    public AlignedFeaturesController(ProjectsProvider<?> projectsProvider) {
        this.projectsProvider = projectsProvider;
    }


    /**
     * Get all available features (aligned over runs) in the given project-space.
     *
     * @param projectId project-space to read from.
     * @param optFields set of optional fields to be included
     * @return AlignedFeatures with additional annotations and MS/MS data (if specified).
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<AlignedFeature> getAlignedFeatures(
            @PathVariable String projectId,
            @ParameterObject Pageable pageable,
            @RequestParam(defaultValue = "") EnumSet<AlignedFeature.OptFields> optFields
    ) {
        return projectsProvider.getProjectOrThrow(projectId).findAlignedFeatures(pageable, optFields);
    }


    /**
     * Get feature (aligned over runs) with the given identifier from the specified project-space.
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId identifier of feature (aligned over runs) to access.
     * @param optFields      set of optional fields to be included
     * @return AlignedFeature with additional annotations and MS/MS data (if specified).
     */
    @GetMapping(value = "/{alignFeatureId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AlignedFeature getAlignedFeatures(
            @PathVariable String projectId, @PathVariable String alignFeatureId,
            @RequestParam(defaultValue = "") EnumSet<AlignedFeature.OptFields> optFields
    ) {
        return projectsProvider.getProjectOrThrow(projectId).findAlignedFeaturesById(alignFeatureId, optFields);
    }

    /**
     * Delete feature (aligned over runs) with the given identifier from the specified project-space.
     *
     * @param projectId      project-space to delete from.
     * @param alignFeatureId identifier of feature (aligned over runs) to delete.
     */
    @DeleteMapping(value = "/{alignFeatureId}")
    public void deleteAlignedFeature(@PathVariable String projectId, @PathVariable String alignFeatureId) {
        projectsProvider.getProjectOrThrow(projectId).deleteAlignedFeaturesById(alignFeatureId);
    }


    /**
     * List of all FormulaResultContainers available for this feature with minimal information.
     * Can be enriched with an optional results overview.
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId feature (aligned over runs) the formula result belongs to.
     * @param optFields      set of optional fields to be included
     * @return All FormulaCandidate of this feature with.
     */
    @GetMapping(value = "/{alignFeatureId}/formulas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<FormulaCandidate> getFormulaCandidates(
            @PathVariable String projectId, @PathVariable String alignFeatureId,
            @ParameterObject Pageable pageable,
            @RequestParam(defaultValue = "") EnumSet<FormulaCandidate.OptFields> optFields
    ) {
        return projectsProvider.getProjectOrThrow(projectId).findFormulaCandidatesByFeatureId(alignFeatureId, pageable, optFields);
        //todo filtering
        //todo ordering
    }

    /**
     * FormulaResultContainers for the given 'formulaId' with minimal information.
     * Can be enriched with an optional results overview and formula candidate information.
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId feature (aligned over runs) the formula result belongs to.
     * @param formulaId      identifier of the requested formula result
     * @param optFields      set of optional fields to be included
     * @return FormulaCandidate of this feature (aligned over runs) with.
     */
    @GetMapping(value = "/{alignFeatureId}/formulas/{formulaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FormulaCandidate getFormulaCandidate(
            @PathVariable String projectId, @PathVariable String alignFeatureId, @PathVariable String formulaId,
            @RequestParam(defaultValue = "") EnumSet<FormulaCandidate.OptFields> optFields

    ) {
        return projectsProvider.getProjectOrThrow(projectId).findFormulaCandidateByFeatureIdAndId(formulaId, alignFeatureId, optFields);
        //todo filtering
        //todo ordering
    }

    /**
     * List of StructureCandidates the given 'formulaId' with minimal information.
     * StructureCandidates can be enriched with molecular fingerprint, structure database links and pubmed ids,
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId feature (aligned over runs) the formula result belongs to.
     * @param formulaId      identifier of the requested formula result
     * @param optFields      set of optional fields to be included
     * @return StructureCandidate of this formula candidate with specified optional fields.
     */
    @GetMapping(value = "/{alignFeatureId}/formulas/{formulaId}/structures", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StructureCandidate> getStructureCandidatesByFormula(
            @PathVariable String projectId, @PathVariable String alignFeatureId, @PathVariable String formulaId,
            @ParameterObject Pageable pageable,
            @RequestParam(defaultValue = "") EnumSet<StructureCandidate.OptFields> optFields
    ) {
        return projectsProvider.getProjectOrThrow(projectId).findStructureCandidatesByFeatureIdAndFormulaId(formulaId, alignFeatureId, pageable, optFields);
    }


    //todo add order by parameter?
    //todo reanable if needed for SIRIUS GUI
//    @Hidden
//    @GetMapping(value = "/{alignFeatureId}/formulas/{formulaId}/sirius-tree", produces = MediaType.APPLICATION_JSON_VALUE)
//    public String getFTree(@PathVariable String projectId, @PathVariable String alignFeatureId, @PathVariable String formulaId) {
//        Instance instance = loadInstance(projectId, alignFeatureId);
//        final FTJsonWriter ftWriter = new FTJsonWriter();
//        return instance.loadFormulaResult(parseFID(instance, formulaId), FTree.class).flatMap(fr -> fr.getAnnotation(FTree.class)).map(ftWriter::treeToJsonString).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FragmentationTree for '" + idString(projectId, alignFeatureId, formulaId) + "' not found!"));
//    }

    /**
     * Returns fragmentation tree (SIRIUS) for the given formula result identifier
     * This tree is used to rank formula candidates (treeScore).
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId feature (aligned over runs) the formula result belongs to.
     * @param formulaId      identifier of the requested formula result
     * @return Fragmentation Tree
     */
    @Hidden
    @GetMapping(value = "/{alignFeatureId}/formulas/{formulaId}/tree", produces = MediaType.APPLICATION_JSON_VALUE)
    public FragmentationTree getFragTree(@PathVariable String projectId, @PathVariable String alignFeatureId, @PathVariable String formulaId) {
        FragmentationTree res = projectsProvider.getProjectOrThrow(projectId)
                .findFormulaCandidateByFeatureIdAndId(alignFeatureId, formulaId, FormulaCandidate.OptFields.fragmentationTree)
                .getFragmentationTree();
        if (res == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FragmentationTree for '" + idString(projectId, alignFeatureId, formulaId) + "' not found!");
        return res;
    }

    /**
     * Returns simulated isotope pattern (SIRIUS) for the given formula result identifier.
     * This simulated isotope pattern is used to rank formula candidates (treeScore).
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId feature (aligned over runs) the formula result belongs to.
     * @param formulaId      identifier of the requested formula result
     * @return Simulated isotope pattern
     */
    @Hidden
    @GetMapping(value = "/{alignFeatureId}/formulas/{formulaId}/isotope-pattern", produces = MediaType.APPLICATION_JSON_VALUE)
    public AnnotatedSpectrum getSimulatedIsotopePattern(@PathVariable String projectId, @PathVariable String alignFeatureId, @PathVariable String formulaId) {
        AnnotatedSpectrum res = projectsProvider.getProjectOrThrow(projectId)
                .findFormulaCandidateByFeatureIdAndId(alignFeatureId, formulaId, FormulaCandidate.OptFields.simulatedIsotopePattern)
                .getSimulatedIsotopePattern();
        if (res == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Isotope Pattern for '" + idString(projectId, alignFeatureId, formulaId) + "' not found!");
        return res;
    }

    /**
     * Returns predicted fingerprint (CSI:FingerID) for the given formula result identifier
     * This fingerprint is used to perform structure database search and predict compound classes.
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId feature (aligned over runs) the formula result belongs to.
     * @param formulaId      identifier of the requested formula result
     * @return probabilistic fingerprint predicted by CSI:FingerID
     */
    @Hidden
    @GetMapping(value = "/{alignFeatureId}/formulas/{formulaId}/fingerprint", produces = MediaType.APPLICATION_JSON_VALUE)
    public double[] getFingerprintPrediction(@PathVariable String projectId, @PathVariable String alignFeatureId, @PathVariable String formulaId) {
        double[] res = projectsProvider.getProjectOrThrow(projectId)
                .findFormulaCandidateByFeatureIdAndId(alignFeatureId, formulaId, FormulaCandidate.OptFields.predictedFingerprint)
                .getPredictedFingerprint();
        if (res == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fingerprint for '" + idString(projectId, alignFeatureId, formulaId) + "' not found!");
        return res;
    }

    /**
     * All predicted compound classes (CANOPUS) from ClassyFire and NPC and their probabilities,
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId feature (aligned over runs) the formula result belongs to.
     * @param formulaId      identifier of the requested formula result
     * @return Predicted compound classes
     */
    @Hidden
    @GetMapping(value = "/{alignFeatureId}/formulas/{formulaId}/canopus-prediction", produces = MediaType.APPLICATION_JSON_VALUE)
    public CanopusPrediction getCanopusPrediction(@PathVariable String projectId, @PathVariable String alignFeatureId, @PathVariable String formulaId) {
        CanopusPrediction res = projectsProvider.getProjectOrThrow(projectId)
                .findFormulaCandidateByFeatureIdAndId(alignFeatureId, formulaId, FormulaCandidate.OptFields.canopusPredictions)
                .getCanopusPrediction();
        if (res == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compound Classes for '" + idString(projectId, alignFeatureId, formulaId) + "' not found!");
        return res;
    }

    /**
     * Best matching compound classes,
     * Set of the highest scoring compound classes (CANOPUS) on each hierarchy level of  the ClassyFire and NPC ontology,
     *
     * @param projectId      project-space to read from.
     * @param alignFeatureId feature (aligned over runs) the formula result belongs to.
     * @param formulaId      identifier of the requested formula result
     * @return Best matching Predicted compound classes
     */
    @Hidden
    @GetMapping(value = "/{alignFeatureId}/formulas/{formulaId}/best-compound-classes", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompoundClasses getBestMatchingCompoundClasses(@PathVariable String projectId, @PathVariable String alignFeatureId, @PathVariable String formulaId) {
        CompoundClasses res = projectsProvider.getProjectOrThrow(projectId)
                .findFormulaCandidateByFeatureIdAndId(alignFeatureId, formulaId, FormulaCandidate.OptFields.compoundClasses)
                .getCompoundClasses();
        if (res == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compound Classes for '" + idString(projectId, alignFeatureId, formulaId) + "' not found!");
        return res;
    }


    protected static String idString(String pid, String cid, String fid) {
        return "'" + pid + "/" + cid + "/" + fid + "'";
    }
}

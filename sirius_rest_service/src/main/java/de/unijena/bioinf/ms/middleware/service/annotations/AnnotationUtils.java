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

package de.unijena.bioinf.ms.middleware.service.annotations;

import de.unijena.bioinf.ms.middleware.model.annotations.*;
import de.unijena.bioinf.ms.middleware.model.features.AlignedFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationUtils {
    private AnnotationUtils() {
        // just to prevent instantiation
    }

    public static ConsensusAnnotationsDeNovo buildConsensusAnnotationsDeNovo(Collection<AlignedFeature> features) {
        //formula based consensus
        Map<String, List<AlignedFeature>> formulaAnnotationAgreement = features.stream()
                .collect(Collectors.groupingBy(f -> Optional.of(f)
                        .map(AlignedFeature::getTopAnnotations)
                        .map(FeatureAnnotations::getFormulaAnnotation)
                        .map(FormulaCandidate::getMolecularFormula)
                        .orElse(""), Collectors.toList()));
        //filter features with no valid formula candidate
        formulaAnnotationAgreement.remove("");

        if (!formulaAnnotationAgreement.isEmpty()) {
            Map.Entry<String, List<AlignedFeature>> max = formulaAnnotationAgreement.entrySet().stream()
                    .max(Comparator.comparing(e -> e.getValue().size())).orElseThrow();

            if (formulaAnnotationAgreement.values().stream().filter(v -> v.size() == max.getValue().size()).count() == 1) {
                return consensusDeNovo(max.getValue(), max.getValue().size() == 1
                        ? ConsensusAnnotationsDeNovo.Criterion.SINGLETON_FORMULA
                        : ConsensusAnnotationsDeNovo.Criterion.MAJORITY_FORMULA);
            }
            return consensusDeNovo(
                    formulaAnnotationAgreement.values().stream().flatMap(List::stream).toList(),
                    ConsensusAnnotationsDeNovo.Criterion.TOP_FORMULA);
        }
        //empty results
        return ConsensusAnnotationsDeNovo.builder().build();
    }

    public static ConsensusAnnotationsCSI buildConsensusAnnotationsCSI(Collection<AlignedFeature> features) {
        {
            Map<String, List<AlignedFeature>> structureAnnotationAgreement = features.stream()
                    .collect(Collectors.groupingBy(f -> Optional.of(f)
                            .map(AlignedFeature::getTopAnnotations)
                            .map(FeatureAnnotations::getStructureAnnotation)
                            .map(StructureCandidateScored::getInchiKey)
                            .orElse(""), Collectors.toList()));

            //filter features with no valid structure candidate
            structureAnnotationAgreement.remove("");

            //structure based consensus
            if (!structureAnnotationAgreement.isEmpty()) {
                Map.Entry<String, List<AlignedFeature>> max = structureAnnotationAgreement.entrySet().stream()
                        .max(Comparator.comparing(e -> e.getValue().size())).orElseThrow();

                if (structureAnnotationAgreement.values().stream().filter(v -> v.size() == max.getValue().size()).count() == 1) {
                    return consensusByStructureCSI(max.getValue(), max.getValue().size() == 1
                            ? ConsensusAnnotationsCSI.Criterion.SINGLETON_STRUCTURE
                            : ConsensusAnnotationsCSI.Criterion.MAJORITY_STRUCTURE);
                }
                return consensusByStructureCSI(
                        structureAnnotationAgreement.values().stream().flatMap(List::stream).toList(),
                        ConsensusAnnotationsCSI.Criterion.CONFIDENCE_STRUCTURE);
            }
        }
        //formula based consensus
        Map<String, List<AlignedFeature>> formulaAnnotationAgreement = features.stream()
                .collect(Collectors.groupingBy(f -> Optional.of(f)
                        .map(AlignedFeature::getTopAnnotations)
                        .map(FeatureAnnotations::getFormulaAnnotation)
                        .map(FormulaCandidate::getMolecularFormula)
                        .orElse(""), Collectors.toList()));
        //filter features with no valid formula candidate
        formulaAnnotationAgreement.remove("");

        if (!formulaAnnotationAgreement.isEmpty()) {
            Map.Entry<String, List<AlignedFeature>> max = formulaAnnotationAgreement.entrySet().stream()
                    .max(Comparator.comparing(e -> e.getValue().size())).orElseThrow();

            if (formulaAnnotationAgreement.values().stream().filter(v -> v.size() == max.getValue().size()).count() == 1) {
                return consensusByFormulaCSI(max.getValue(), max.getValue().size() == 1
                        ? ConsensusAnnotationsCSI.Criterion.SINGLETON_FORMULA
                        : ConsensusAnnotationsCSI.Criterion.MAJORITY_FORMULA);
            }
            return consensusByFormulaCSI(
                    formulaAnnotationAgreement.values().stream().flatMap(List::stream).toList(),
                    ConsensusAnnotationsCSI.Criterion.TOP_FORMULA);
        }
        //empty results
        return ConsensusAnnotationsCSI.builder().build();
    }

    private static ConsensusAnnotationsDeNovo consensusDeNovo(Collection<AlignedFeature> features,
                                                              ConsensusAnnotationsDeNovo.Criterion type) {
        //prefer candidate with compound classes
        AlignedFeature top = features.stream()
                .filter(f -> f.getTopAnnotations().getCompoundClassAnnotation() != null)
                .min(Comparator.comparing(f -> f.getTopAnnotations().getFormulaAnnotation().getSiriusScore()))
                .orElse(null);

        // fallback to non compound class candidate
        if (top == null)
            top = features.stream()
                    .min(Comparator.comparing(f -> f.getTopAnnotations().getFormulaAnnotation().getSiriusScore()))
                    .orElseThrow(() -> new IllegalStateException("No Formula Candidate Found!"));

        return ConsensusAnnotationsDeNovo.builder()
                .selectionCriterion(type)
                .compoundClasses(top.getTopAnnotations().getCompoundClassAnnotation())
                .molecularFormula(top.getTopAnnotations().getFormulaAnnotation().getMolecularFormula())
                .supportingFeatureIds(ConsensusAnnotationsDeNovo.Criterion.TOP_FORMULA == type
                        ? List.of(top.getAlignedFeatureId())
                        : features.stream().map(AlignedFeature::getAlignedFeatureId).toList()
                ).build();
    }


    private static ConsensusAnnotationsCSI consensusByFormulaCSI(Collection<AlignedFeature> features,
                                                                 ConsensusAnnotationsCSI.Criterion type) {

        AlignedFeature top = features.stream()
                .min(Comparator.comparing(f -> f.getTopAnnotations().getFormulaAnnotation().getSiriusScore()))
                .orElseThrow(() -> new IllegalStateException("No Formula Candidate Found!"));


        return ConsensusAnnotationsCSI.builder()
                .selectionCriterion(type)
                .molecularFormula(top.getTopAnnotations().getFormulaAnnotation().getMolecularFormula())
                .compoundClasses(top.getTopAnnotations().getCompoundClassAnnotation())
                .supportingFeatureIds(ConsensusAnnotationsCSI.Criterion.TOP_FORMULA == type
                        ? List.of(top.getAlignedFeatureId())
                        : features.stream().map(AlignedFeature::getAlignedFeatureId).toList()
                ).build();
    }

    private static ConsensusAnnotationsCSI consensusByStructureCSI(Collection<AlignedFeature> features,
                                                                   ConsensusAnnotationsCSI.Criterion type) {
        final boolean mixedStructures = ConsensusAnnotationsCSI.Criterion.CONFIDENCE_STRUCTURE == type;
        //todo use approx confidence if available
        AlignedFeature topConf = features.stream()
                .min(Comparator.comparing(f -> f.getTopAnnotations().getStructureAnnotation().getConfidenceExactMatch()))
                .orElseThrow(() -> new IllegalStateException("No Structure Candidate Found!"));

        Double topConfExact = topConf.getTopAnnotations().getStructureAnnotation().getConfidenceExactMatch();
        Double topConfApprox = mixedStructures
                ? topConf.getTopAnnotations().getStructureAnnotation().getConfidenceApproxMatch()
                : features.stream()
                .map(f -> f.getTopAnnotations().getStructureAnnotation().getConfidenceExactMatch())
                .min(Double::compareTo)
                .orElse(null);

        return ConsensusAnnotationsCSI.builder()
                .selectionCriterion(type)
                .csiFingerIdStructure(topConf.getTopAnnotations().getStructureAnnotation())
                .compoundClasses(topConf.getTopAnnotations().getCompoundClassAnnotation())
                .confidenceExactMatch(topConfExact)
                .confidenceApproxMatch(topConfApprox)
                .molecularFormula(topConf.getTopAnnotations().getFormulaAnnotation().getMolecularFormula())
                .supportingFeatureIds(mixedStructures
                        ? List.of(topConf.getAlignedFeatureId())
                        : features.stream().map(AlignedFeature::getAlignedFeatureId).toList()
                ).build();
    }
}
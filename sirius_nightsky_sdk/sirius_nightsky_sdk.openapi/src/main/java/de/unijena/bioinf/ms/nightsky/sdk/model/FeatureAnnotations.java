/*
 * SIRIUS Nightsky API
 * REST API that provides the full functionality of SIRIUS and its web services as background service. It is intended as entry-point for scripting languages and software integration SDKs.This API is exposed by SIRIUS 6.0.0-SNAPSHOT
 *
 * The version of the OpenAPI document: 2.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.unijena.bioinf.ms.nightsky.sdk.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import de.unijena.bioinf.ms.nightsky.sdk.model.CompoundClasses;
import de.unijena.bioinf.ms.nightsky.sdk.model.FormulaCandidate;
import de.unijena.bioinf.ms.nightsky.sdk.model.Mode;
import de.unijena.bioinf.ms.nightsky.sdk.model.StructureCandidateScored;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Summary of the results of a feature (aligned over runs). Can be added to a AlignedFeature.  The different annotation fields within this summary object are null if the corresponding  feature does not contain the represented results. If fields are non-null  the corresponding result has been computed but might still be empty.
 */
@JsonPropertyOrder({
  FeatureAnnotations.JSON_PROPERTY_FORMULA_ANNOTATION,
  FeatureAnnotations.JSON_PROPERTY_STRUCTURE_ANNOTATION,
  FeatureAnnotations.JSON_PROPERTY_COMPOUND_CLASS_ANNOTATION,
  FeatureAnnotations.JSON_PROPERTY_CONFIDENCE_EXACT_MATCH,
  FeatureAnnotations.JSON_PROPERTY_CONFIDENCE_APPROX_MATCH,
  FeatureAnnotations.JSON_PROPERTY_EXPANSIVE_SEARCH_STATE
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FeatureAnnotations {
  public static final String JSON_PROPERTY_FORMULA_ANNOTATION = "formulaAnnotation";
  private FormulaCandidate formulaAnnotation;

  public static final String JSON_PROPERTY_STRUCTURE_ANNOTATION = "structureAnnotation";
  private StructureCandidateScored structureAnnotation;

  public static final String JSON_PROPERTY_COMPOUND_CLASS_ANNOTATION = "compoundClassAnnotation";
  private CompoundClasses compoundClassAnnotation;

  public static final String JSON_PROPERTY_CONFIDENCE_EXACT_MATCH = "confidenceExactMatch";
  private Double confidenceExactMatch;

  public static final String JSON_PROPERTY_CONFIDENCE_APPROX_MATCH = "confidenceApproxMatch";
  private Double confidenceApproxMatch;

  public static final String JSON_PROPERTY_EXPANSIVE_SEARCH_STATE = "expansiveSearchState";
  private Mode expansiveSearchState;

  public FeatureAnnotations() {
  }

  public FeatureAnnotations formulaAnnotation(FormulaCandidate formulaAnnotation) {
    
    this.formulaAnnotation = formulaAnnotation;
    return this;
  }

   /**
   * Get formulaAnnotation
   * @return formulaAnnotation
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FORMULA_ANNOTATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FormulaCandidate getFormulaAnnotation() {
    return formulaAnnotation;
  }


  @JsonProperty(JSON_PROPERTY_FORMULA_ANNOTATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFormulaAnnotation(FormulaCandidate formulaAnnotation) {
    this.formulaAnnotation = formulaAnnotation;
  }


  public FeatureAnnotations structureAnnotation(StructureCandidateScored structureAnnotation) {
    
    this.structureAnnotation = structureAnnotation;
    return this;
  }

   /**
   * Get structureAnnotation
   * @return structureAnnotation
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_STRUCTURE_ANNOTATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public StructureCandidateScored getStructureAnnotation() {
    return structureAnnotation;
  }


  @JsonProperty(JSON_PROPERTY_STRUCTURE_ANNOTATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setStructureAnnotation(StructureCandidateScored structureAnnotation) {
    this.structureAnnotation = structureAnnotation;
  }


  public FeatureAnnotations compoundClassAnnotation(CompoundClasses compoundClassAnnotation) {
    
    this.compoundClassAnnotation = compoundClassAnnotation;
    return this;
  }

   /**
   * Get compoundClassAnnotation
   * @return compoundClassAnnotation
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_COMPOUND_CLASS_ANNOTATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public CompoundClasses getCompoundClassAnnotation() {
    return compoundClassAnnotation;
  }


  @JsonProperty(JSON_PROPERTY_COMPOUND_CLASS_ANNOTATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCompoundClassAnnotation(CompoundClasses compoundClassAnnotation) {
    this.compoundClassAnnotation = compoundClassAnnotation;
  }


  public FeatureAnnotations confidenceExactMatch(Double confidenceExactMatch) {
    
    this.confidenceExactMatch = confidenceExactMatch;
    return this;
  }

   /**
   * Confidence Score that represents the confidence whether the top hit is correct.
   * @return confidenceExactMatch
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_CONFIDENCE_EXACT_MATCH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getConfidenceExactMatch() {
    return confidenceExactMatch;
  }


  @JsonProperty(JSON_PROPERTY_CONFIDENCE_EXACT_MATCH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setConfidenceExactMatch(Double confidenceExactMatch) {
    this.confidenceExactMatch = confidenceExactMatch;
  }


  public FeatureAnnotations confidenceApproxMatch(Double confidenceApproxMatch) {
    
    this.confidenceApproxMatch = confidenceApproxMatch;
    return this;
  }

   /**
   * Confidence Score that represents the confidence whether the top hit or a very similar hit (estimated by MCES distance) is correct.
   * @return confidenceApproxMatch
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_CONFIDENCE_APPROX_MATCH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getConfidenceApproxMatch() {
    return confidenceApproxMatch;
  }


  @JsonProperty(JSON_PROPERTY_CONFIDENCE_APPROX_MATCH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setConfidenceApproxMatch(Double confidenceApproxMatch) {
    this.confidenceApproxMatch = confidenceApproxMatch;
  }


  public FeatureAnnotations expansiveSearchState(Mode expansiveSearchState) {
    
    this.expansiveSearchState = expansiveSearchState;
    return this;
  }

   /**
   * Get expansiveSearchState
   * @return expansiveSearchState
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_EXPANSIVE_SEARCH_STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Mode getExpansiveSearchState() {
    return expansiveSearchState;
  }


  @JsonProperty(JSON_PROPERTY_EXPANSIVE_SEARCH_STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExpansiveSearchState(Mode expansiveSearchState) {
    this.expansiveSearchState = expansiveSearchState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FeatureAnnotations featureAnnotations = (FeatureAnnotations) o;
    return Objects.equals(this.formulaAnnotation, featureAnnotations.formulaAnnotation) &&
        Objects.equals(this.structureAnnotation, featureAnnotations.structureAnnotation) &&
        Objects.equals(this.compoundClassAnnotation, featureAnnotations.compoundClassAnnotation) &&
        Objects.equals(this.confidenceExactMatch, featureAnnotations.confidenceExactMatch) &&
        Objects.equals(this.confidenceApproxMatch, featureAnnotations.confidenceApproxMatch) &&
        Objects.equals(this.expansiveSearchState, featureAnnotations.expansiveSearchState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formulaAnnotation, structureAnnotation, compoundClassAnnotation, confidenceExactMatch, confidenceApproxMatch, expansiveSearchState);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FeatureAnnotations {\n");
    sb.append("    formulaAnnotation: ").append(toIndentedString(formulaAnnotation)).append("\n");
    sb.append("    structureAnnotation: ").append(toIndentedString(structureAnnotation)).append("\n");
    sb.append("    compoundClassAnnotation: ").append(toIndentedString(compoundClassAnnotation)).append("\n");
    sb.append("    confidenceExactMatch: ").append(toIndentedString(confidenceExactMatch)).append("\n");
    sb.append("    confidenceApproxMatch: ").append(toIndentedString(confidenceApproxMatch)).append("\n");
    sb.append("    expansiveSearchState: ").append(toIndentedString(expansiveSearchState)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

/*
 * SIRIUS Nightsky API
 * REST API that provides the full functionality of SIRIUS and its web services as background service. It is intended as entry-point for scripting languages and software integration SDKs.This API is exposed by SIRIUS 6
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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Link from annotated fragment peak to its parent fragment peak connected by their neutral loss.
 */
@JsonPropertyOrder({
  ParentPeak.JSON_PROPERTY_PARENT_IDX,
  ParentPeak.JSON_PROPERTY_PARENT_FRAGMENT_ID,
  ParentPeak.JSON_PROPERTY_LOSS_FORMULA
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.6.0")
public class ParentPeak {
  public static final String JSON_PROPERTY_PARENT_IDX = "parentIdx";
  private Integer parentIdx;

  public static final String JSON_PROPERTY_PARENT_FRAGMENT_ID = "parentFragmentId";
  private Integer parentFragmentId;

  public static final String JSON_PROPERTY_LOSS_FORMULA = "lossFormula";
  private String lossFormula;

  public ParentPeak() {
  }

  public ParentPeak parentIdx(Integer parentIdx) {
    
    this.parentIdx = parentIdx;
    return this;
  }

   /**
   * Index to the parent peak connected by this loss in this particular spectrum
   * @return parentIdx
  **/
  @jakarta.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_PARENT_IDX)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Integer getParentIdx() {
    return parentIdx;
  }


  @JsonProperty(JSON_PROPERTY_PARENT_IDX)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setParentIdx(Integer parentIdx) {
    this.parentIdx = parentIdx;
  }

  public ParentPeak parentFragmentId(Integer parentFragmentId) {
    
    this.parentFragmentId = parentFragmentId;
    return this;
  }

   /**
   * Identifier of the parent fragment connected via this loss. Can be used to map fragments and peaks  among fragmentation trees and spectra.
   * @return parentFragmentId
  **/
  @jakarta.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_PARENT_FRAGMENT_ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Integer getParentFragmentId() {
    return parentFragmentId;
  }


  @JsonProperty(JSON_PROPERTY_PARENT_FRAGMENT_ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setParentFragmentId(Integer parentFragmentId) {
    this.parentFragmentId = parentFragmentId;
  }

  public ParentPeak lossFormula(String lossFormula) {
    
    this.lossFormula = lossFormula;
    return this;
  }

   /**
   * Molecular formula of the neutral loss that connects these two peaks.
   * @return lossFormula
  **/
  @jakarta.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_LOSS_FORMULA)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getLossFormula() {
    return lossFormula;
  }


  @JsonProperty(JSON_PROPERTY_LOSS_FORMULA)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setLossFormula(String lossFormula) {
    this.lossFormula = lossFormula;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParentPeak parentPeak = (ParentPeak) o;
    return Objects.equals(this.parentIdx, parentPeak.parentIdx) &&
        Objects.equals(this.parentFragmentId, parentPeak.parentFragmentId) &&
        Objects.equals(this.lossFormula, parentPeak.lossFormula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentIdx, parentFragmentId, lossFormula);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParentPeak {\n");
    sb.append("    parentIdx: ").append(toIndentedString(parentIdx)).append("\n");
    sb.append("    parentFragmentId: ").append(toIndentedString(parentFragmentId)).append("\n");
    sb.append("    lossFormula: ").append(toIndentedString(lossFormula)).append("\n");
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


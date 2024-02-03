/*
 * SIRIUS Nightsky API
 * REST API that provides the full functionality of SIRIUS and its web services as background service. It is intended as entry-point for scripting languages and software integration SDKs.This API is exposed by SIRIUS 6.0.0-SNAPSHOT
 *
 * The version of the OpenAPI document: 2.0
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
import de.unijena.bioinf.ms.nightsky.sdk.model.Mode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * User/developer friendly parameter subset for the CSI:FingerID structure db search tool.  Needs results from FingerprintPrediction and Canopus Tool
 */
@JsonPropertyOrder({
  StructureDbSearch.JSON_PROPERTY_ENABLED,
  StructureDbSearch.JSON_PROPERTY_STRUCTURE_SEARCH_D_BS,
  StructureDbSearch.JSON_PROPERTY_TAG_STRUCTURES_WITH_LIPID_CLASS,
  StructureDbSearch.JSON_PROPERTY_EXPANSIVE_SEARCH_CONFIDENCE_MODE
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class StructureDbSearch {
  public static final String JSON_PROPERTY_ENABLED = "enabled";
  private Boolean enabled;

  public static final String JSON_PROPERTY_STRUCTURE_SEARCH_D_BS = "structureSearchDBs";
  private List<String> structureSearchDBs = new ArrayList<>();

  public static final String JSON_PROPERTY_TAG_STRUCTURES_WITH_LIPID_CLASS = "tagStructuresWithLipidClass";
  private Boolean tagStructuresWithLipidClass;

  public static final String JSON_PROPERTY_EXPANSIVE_SEARCH_CONFIDENCE_MODE = "expansiveSearchConfidenceMode";
  private Mode expansiveSearchConfidenceMode;

  public StructureDbSearch() {
  }

  public StructureDbSearch enabled(Boolean enabled) {
    
    this.enabled = enabled;
    return this;
  }

   /**
   * tags whether the tool is enabled
   * @return enabled
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ENABLED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean isEnabled() {
    return enabled;
  }


  @JsonProperty(JSON_PROPERTY_ENABLED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }


  public StructureDbSearch structureSearchDBs(List<String> structureSearchDBs) {
    
    this.structureSearchDBs = structureSearchDBs;
    return this;
  }

  public StructureDbSearch addStructureSearchDBsItem(String structureSearchDBsItem) {
    if (this.structureSearchDBs == null) {
      this.structureSearchDBs = new ArrayList<>();
    }
    this.structureSearchDBs.add(structureSearchDBsItem);
    return this;
  }

   /**
   * Structure databases to search in, If expansive search is enabled this DB selection will be expanded to PubChem  if not high confidence hit was found in the selected databases.   Defaults to BIO + Custom Databases. Possible values are available to Database API.
   * @return structureSearchDBs
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_STRUCTURE_SEARCH_D_BS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<String> getStructureSearchDBs() {
    return structureSearchDBs;
  }


  @JsonProperty(JSON_PROPERTY_STRUCTURE_SEARCH_D_BS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setStructureSearchDBs(List<String> structureSearchDBs) {
    this.structureSearchDBs = structureSearchDBs;
  }


  public StructureDbSearch tagStructuresWithLipidClass(Boolean tagStructuresWithLipidClass) {
    
    this.tagStructuresWithLipidClass = tagStructuresWithLipidClass;
    return this;
  }

   /**
   * Candidates matching the lipid class estimated by El Gordo will be tagged.  The lipid class will only be available if El Gordo predicts that the MS/MS is a lipid spectrum.  If this parameter is set to &#39;false&#39; El Gordo will still be executed and e.g. improve the fragmentation  tree, but the matching structure candidates will not be tagged if they match lipid class.
   * @return tagStructuresWithLipidClass
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_TAG_STRUCTURES_WITH_LIPID_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean isTagStructuresWithLipidClass() {
    return tagStructuresWithLipidClass;
  }


  @JsonProperty(JSON_PROPERTY_TAG_STRUCTURES_WITH_LIPID_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTagStructuresWithLipidClass(Boolean tagStructuresWithLipidClass) {
    this.tagStructuresWithLipidClass = tagStructuresWithLipidClass;
  }


  public StructureDbSearch expansiveSearchConfidenceMode(Mode expansiveSearchConfidenceMode) {
    
    this.expansiveSearchConfidenceMode = expansiveSearchConfidenceMode;
    return this;
  }

   /**
   * Get expansiveSearchConfidenceMode
   * @return expansiveSearchConfidenceMode
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_EXPANSIVE_SEARCH_CONFIDENCE_MODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Mode getExpansiveSearchConfidenceMode() {
    return expansiveSearchConfidenceMode;
  }


  @JsonProperty(JSON_PROPERTY_EXPANSIVE_SEARCH_CONFIDENCE_MODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExpansiveSearchConfidenceMode(Mode expansiveSearchConfidenceMode) {
    this.expansiveSearchConfidenceMode = expansiveSearchConfidenceMode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StructureDbSearch structureDbSearch = (StructureDbSearch) o;
    return Objects.equals(this.enabled, structureDbSearch.enabled) &&
        Objects.equals(this.structureSearchDBs, structureDbSearch.structureSearchDBs) &&
        Objects.equals(this.tagStructuresWithLipidClass, structureDbSearch.tagStructuresWithLipidClass) &&
        Objects.equals(this.expansiveSearchConfidenceMode, structureDbSearch.expansiveSearchConfidenceMode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(enabled, structureSearchDBs, tagStructuresWithLipidClass, expansiveSearchConfidenceMode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StructureDbSearch {\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    structureSearchDBs: ").append(toIndentedString(structureSearchDBs)).append("\n");
    sb.append("    tagStructuresWithLipidClass: ").append(toIndentedString(tagStructuresWithLipidClass)).append("\n");
    sb.append("    expansiveSearchConfidenceMode: ").append(toIndentedString(expansiveSearchConfidenceMode)).append("\n");
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


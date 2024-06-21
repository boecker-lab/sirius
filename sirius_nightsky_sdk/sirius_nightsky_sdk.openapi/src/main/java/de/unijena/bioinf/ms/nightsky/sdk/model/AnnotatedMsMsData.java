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
import de.unijena.bioinf.ms.nightsky.sdk.model.AnnotatedSpectrum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * AnnotatedMsMsData
 */
@JsonPropertyOrder({
  AnnotatedMsMsData.JSON_PROPERTY_MERGED_MS2,
  AnnotatedMsMsData.JSON_PROPERTY_MS2_SPECTRA
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.6.0")
public class AnnotatedMsMsData {
  public static final String JSON_PROPERTY_MERGED_MS2 = "mergedMs2";
  private AnnotatedSpectrum mergedMs2;

  public static final String JSON_PROPERTY_MS2_SPECTRA = "ms2Spectra";
  private List<AnnotatedSpectrum> ms2Spectra = new ArrayList<>();

  public AnnotatedMsMsData() {
  }

  public AnnotatedMsMsData mergedMs2(AnnotatedSpectrum mergedMs2) {
    
    this.mergedMs2 = mergedMs2;
    return this;
  }

   /**
   * Get mergedMs2
   * @return mergedMs2
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_MERGED_MS2)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public AnnotatedSpectrum getMergedMs2() {
    return mergedMs2;
  }


  @JsonProperty(JSON_PROPERTY_MERGED_MS2)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setMergedMs2(AnnotatedSpectrum mergedMs2) {
    this.mergedMs2 = mergedMs2;
  }

  public AnnotatedMsMsData ms2Spectra(List<AnnotatedSpectrum> ms2Spectra) {
    
    this.ms2Spectra = ms2Spectra;
    return this;
  }

  public AnnotatedMsMsData addMs2SpectraItem(AnnotatedSpectrum ms2SpectraItem) {
    if (this.ms2Spectra == null) {
      this.ms2Spectra = new ArrayList<>();
    }
    this.ms2Spectra.add(ms2SpectraItem);
    return this;
  }

   /**
   * Get ms2Spectra
   * @return ms2Spectra
  **/
  @jakarta.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_MS2_SPECTRA)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<AnnotatedSpectrum> getMs2Spectra() {
    return ms2Spectra;
  }


  @JsonProperty(JSON_PROPERTY_MS2_SPECTRA)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setMs2Spectra(List<AnnotatedSpectrum> ms2Spectra) {
    this.ms2Spectra = ms2Spectra;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnnotatedMsMsData annotatedMsMsData = (AnnotatedMsMsData) o;
    return Objects.equals(this.mergedMs2, annotatedMsMsData.mergedMs2) &&
        Objects.equals(this.ms2Spectra, annotatedMsMsData.ms2Spectra);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mergedMs2, ms2Spectra);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnnotatedMsMsData {\n");
    sb.append("    mergedMs2: ").append(toIndentedString(mergedMs2)).append("\n");
    sb.append("    ms2Spectra: ").append(toIndentedString(ms2Spectra)).append("\n");
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


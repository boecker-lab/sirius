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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Info
 */
@JsonPropertyOrder({
  Info.JSON_PROPERTY_NIGHT_SKY_API_VERSION,
  Info.JSON_PROPERTY_SIRIUS_VERSION,
  Info.JSON_PROPERTY_SIRIUS_LIB_VERSION,
  Info.JSON_PROPERTY_FINGER_ID_LIB_VERSION,
  Info.JSON_PROPERTY_CHEM_DB_VERSION,
  Info.JSON_PROPERTY_FINGER_ID_MODEL_VERSION,
  Info.JSON_PROPERTY_FINGERPRINT_ID,
  Info.JSON_PROPERTY_AVAILABLE_I_L_P_SOLVERS,
  Info.JSON_PROPERTY_SUPPORTED_I_L_P_SOLVERS
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class Info {
  public static final String JSON_PROPERTY_NIGHT_SKY_API_VERSION = "nightSkyApiVersion";
  private String nightSkyApiVersion;

  public static final String JSON_PROPERTY_SIRIUS_VERSION = "siriusVersion";
  private String siriusVersion;

  public static final String JSON_PROPERTY_SIRIUS_LIB_VERSION = "siriusLibVersion";
  private String siriusLibVersion;

  public static final String JSON_PROPERTY_FINGER_ID_LIB_VERSION = "fingerIdLibVersion";
  private String fingerIdLibVersion;

  public static final String JSON_PROPERTY_CHEM_DB_VERSION = "chemDbVersion";
  private String chemDbVersion;

  public static final String JSON_PROPERTY_FINGER_ID_MODEL_VERSION = "fingerIdModelVersion";
  private String fingerIdModelVersion;

  public static final String JSON_PROPERTY_FINGERPRINT_ID = "fingerprintId";
  private String fingerprintId;

  /**
   * Gets or Sets availableILPSolvers
   */
  public enum AvailableILPSolversEnum {
    GUROBI("GUROBI"),
    
    CPLEX("CPLEX"),
    
    GLPK("GLPK"),
    
    CLP("CLP");

    private String value;

    AvailableILPSolversEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AvailableILPSolversEnum fromValue(String value) {
      for (AvailableILPSolversEnum b : AvailableILPSolversEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String JSON_PROPERTY_AVAILABLE_I_L_P_SOLVERS = "availableILPSolvers";
  private List<AvailableILPSolversEnum> availableILPSolvers = new ArrayList<>();

  public static final String JSON_PROPERTY_SUPPORTED_I_L_P_SOLVERS = "supportedILPSolvers";
  private Map<String, String> supportedILPSolvers = new HashMap<>();

  public Info() {
  }

  public Info nightSkyApiVersion(String nightSkyApiVersion) {
    
    this.nightSkyApiVersion = nightSkyApiVersion;
    return this;
  }

   /**
   * API version of the SIRIUS Nightsky API
   * @return nightSkyApiVersion
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_NIGHT_SKY_API_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getNightSkyApiVersion() {
    return nightSkyApiVersion;
  }


  @JsonProperty(JSON_PROPERTY_NIGHT_SKY_API_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setNightSkyApiVersion(String nightSkyApiVersion) {
    this.nightSkyApiVersion = nightSkyApiVersion;
  }


  public Info siriusVersion(String siriusVersion) {
    
    this.siriusVersion = siriusVersion;
    return this;
  }

   /**
   * Version of the SIRIUS application
   * @return siriusVersion
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SIRIUS_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSiriusVersion() {
    return siriusVersion;
  }


  @JsonProperty(JSON_PROPERTY_SIRIUS_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSiriusVersion(String siriusVersion) {
    this.siriusVersion = siriusVersion;
  }


  public Info siriusLibVersion(String siriusLibVersion) {
    
    this.siriusLibVersion = siriusLibVersion;
    return this;
  }

   /**
   * Version of the SIRIUS libraries
   * @return siriusLibVersion
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SIRIUS_LIB_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSiriusLibVersion() {
    return siriusLibVersion;
  }


  @JsonProperty(JSON_PROPERTY_SIRIUS_LIB_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSiriusLibVersion(String siriusLibVersion) {
    this.siriusLibVersion = siriusLibVersion;
  }


  public Info fingerIdLibVersion(String fingerIdLibVersion) {
    
    this.fingerIdLibVersion = fingerIdLibVersion;
    return this;
  }

   /**
   * Version of the CSI:FingerID libraries
   * @return fingerIdLibVersion
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FINGER_ID_LIB_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFingerIdLibVersion() {
    return fingerIdLibVersion;
  }


  @JsonProperty(JSON_PROPERTY_FINGER_ID_LIB_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFingerIdLibVersion(String fingerIdLibVersion) {
    this.fingerIdLibVersion = fingerIdLibVersion;
  }


  public Info chemDbVersion(String chemDbVersion) {
    
    this.chemDbVersion = chemDbVersion;
    return this;
  }

   /**
   * Version of the Chemical Database available via SIRIUS web services
   * @return chemDbVersion
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_CHEM_DB_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getChemDbVersion() {
    return chemDbVersion;
  }


  @JsonProperty(JSON_PROPERTY_CHEM_DB_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setChemDbVersion(String chemDbVersion) {
    this.chemDbVersion = chemDbVersion;
  }


  public Info fingerIdModelVersion(String fingerIdModelVersion) {
    
    this.fingerIdModelVersion = fingerIdModelVersion;
    return this;
  }

   /**
   * Version of the Machine learning models used for Fingerprint, Compound Class and Structure Prediction  Not available if web service is not reachable.
   * @return fingerIdModelVersion
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FINGER_ID_MODEL_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFingerIdModelVersion() {
    return fingerIdModelVersion;
  }


  @JsonProperty(JSON_PROPERTY_FINGER_ID_MODEL_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFingerIdModelVersion(String fingerIdModelVersion) {
    this.fingerIdModelVersion = fingerIdModelVersion;
  }


  public Info fingerprintId(String fingerprintId) {
    
    this.fingerprintId = fingerprintId;
    return this;
  }

   /**
   * Version of the Molecular Fingerprint used by SIRIUS
   * @return fingerprintId
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FINGERPRINT_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFingerprintId() {
    return fingerprintId;
  }


  @JsonProperty(JSON_PROPERTY_FINGERPRINT_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFingerprintId(String fingerprintId) {
    this.fingerprintId = fingerprintId;
  }


  public Info availableILPSolvers(List<AvailableILPSolversEnum> availableILPSolvers) {
    
    this.availableILPSolvers = availableILPSolvers;
    return this;
  }

  public Info addAvailableILPSolversItem(AvailableILPSolversEnum availableILPSolversItem) {
    if (this.availableILPSolvers == null) {
      this.availableILPSolvers = new ArrayList<>();
    }
    this.availableILPSolvers.add(availableILPSolversItem);
    return this;
  }

   /**
   * Set of solvers that are configured correctly and can be loaded
   * @return availableILPSolvers
  **/
  @jakarta.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_AVAILABLE_I_L_P_SOLVERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<AvailableILPSolversEnum> getAvailableILPSolvers() {
    return availableILPSolvers;
  }


  @JsonProperty(JSON_PROPERTY_AVAILABLE_I_L_P_SOLVERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setAvailableILPSolvers(List<AvailableILPSolversEnum> availableILPSolvers) {
    this.availableILPSolvers = availableILPSolvers;
  }


  public Info supportedILPSolvers(Map<String, String> supportedILPSolvers) {
    
    this.supportedILPSolvers = supportedILPSolvers;
    return this;
  }

  public Info putSupportedILPSolversItem(String key, String supportedILPSolversItem) {
    this.supportedILPSolvers.put(key, supportedILPSolversItem);
    return this;
  }

   /**
   * Set of ILP Solvers that are Supported and their version information
   * @return supportedILPSolvers
  **/
  @jakarta.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_SUPPORTED_I_L_P_SOLVERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, String> getSupportedILPSolvers() {
    return supportedILPSolvers;
  }


  @JsonProperty(JSON_PROPERTY_SUPPORTED_I_L_P_SOLVERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSupportedILPSolvers(Map<String, String> supportedILPSolvers) {
    this.supportedILPSolvers = supportedILPSolvers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Info info = (Info) o;
    return Objects.equals(this.nightSkyApiVersion, info.nightSkyApiVersion) &&
        Objects.equals(this.siriusVersion, info.siriusVersion) &&
        Objects.equals(this.siriusLibVersion, info.siriusLibVersion) &&
        Objects.equals(this.fingerIdLibVersion, info.fingerIdLibVersion) &&
        Objects.equals(this.chemDbVersion, info.chemDbVersion) &&
        Objects.equals(this.fingerIdModelVersion, info.fingerIdModelVersion) &&
        Objects.equals(this.fingerprintId, info.fingerprintId) &&
        Objects.equals(this.availableILPSolvers, info.availableILPSolvers) &&
        Objects.equals(this.supportedILPSolvers, info.supportedILPSolvers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nightSkyApiVersion, siriusVersion, siriusLibVersion, fingerIdLibVersion, chemDbVersion, fingerIdModelVersion, fingerprintId, availableILPSolvers, supportedILPSolvers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Info {\n");
    sb.append("    nightSkyApiVersion: ").append(toIndentedString(nightSkyApiVersion)).append("\n");
    sb.append("    siriusVersion: ").append(toIndentedString(siriusVersion)).append("\n");
    sb.append("    siriusLibVersion: ").append(toIndentedString(siriusLibVersion)).append("\n");
    sb.append("    fingerIdLibVersion: ").append(toIndentedString(fingerIdLibVersion)).append("\n");
    sb.append("    chemDbVersion: ").append(toIndentedString(chemDbVersion)).append("\n");
    sb.append("    fingerIdModelVersion: ").append(toIndentedString(fingerIdModelVersion)).append("\n");
    sb.append("    fingerprintId: ").append(toIndentedString(fingerprintId)).append("\n");
    sb.append("    availableILPSolvers: ").append(toIndentedString(availableILPSolvers)).append("\n");
    sb.append("    supportedILPSolvers: ").append(toIndentedString(supportedILPSolvers)).append("\n");
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

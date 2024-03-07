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
import de.unijena.bioinf.ms.nightsky.sdk.model.BinaryFingerprint;
import de.unijena.bioinf.ms.nightsky.sdk.model.DBLink;
import de.unijena.bioinf.ms.nightsky.sdk.model.SpectralLibraryMatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * StructureCandidateScored
 */
@JsonPropertyOrder({
  StructureCandidateScored.JSON_PROPERTY_INCHI_KEY,
  StructureCandidateScored.JSON_PROPERTY_SMILES,
  StructureCandidateScored.JSON_PROPERTY_STRUCTURE_NAME,
  StructureCandidateScored.JSON_PROPERTY_XLOG_P,
  StructureCandidateScored.JSON_PROPERTY_DB_LINKS,
  StructureCandidateScored.JSON_PROPERTY_SPECTRAL_LIBRARY_MATCHES,
  StructureCandidateScored.JSON_PROPERTY_CSI_SCORE,
  StructureCandidateScored.JSON_PROPERTY_TANIMOTO_SIMILARITY,
  StructureCandidateScored.JSON_PROPERTY_CONFIDENCE_EXACT_MATCH,
  StructureCandidateScored.JSON_PROPERTY_CONFIDENCE_APPROX_MATCH,
  StructureCandidateScored.JSON_PROPERTY_FINGERPRINT
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class StructureCandidateScored {
  public static final String JSON_PROPERTY_INCHI_KEY = "inchiKey";
  private String inchiKey;

  public static final String JSON_PROPERTY_SMILES = "smiles";
  private String smiles;

  public static final String JSON_PROPERTY_STRUCTURE_NAME = "structureName";
  private String structureName;

  public static final String JSON_PROPERTY_XLOG_P = "xlogP";
  private Double xlogP;

  public static final String JSON_PROPERTY_DB_LINKS = "dbLinks";
  private List<DBLink> dbLinks;

  public static final String JSON_PROPERTY_SPECTRAL_LIBRARY_MATCHES = "spectralLibraryMatches";
  private List<SpectralLibraryMatch> spectralLibraryMatches;

  public static final String JSON_PROPERTY_CSI_SCORE = "csiScore";
  private Double csiScore;

  public static final String JSON_PROPERTY_TANIMOTO_SIMILARITY = "tanimotoSimilarity";
  private Double tanimotoSimilarity;

  public static final String JSON_PROPERTY_CONFIDENCE_EXACT_MATCH = "confidenceExactMatch";
  private Double confidenceExactMatch;

  public static final String JSON_PROPERTY_CONFIDENCE_APPROX_MATCH = "confidenceApproxMatch";
  private Double confidenceApproxMatch;

  public static final String JSON_PROPERTY_FINGERPRINT = "fingerprint";
  private BinaryFingerprint fingerprint;

  public StructureCandidateScored() {
  }

  public StructureCandidateScored inchiKey(String inchiKey) {
    
    this.inchiKey = inchiKey;
    return this;
  }

   /**
   * Get inchiKey
   * @return inchiKey
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_INCHI_KEY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getInchiKey() {
    return inchiKey;
  }


  @JsonProperty(JSON_PROPERTY_INCHI_KEY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInchiKey(String inchiKey) {
    this.inchiKey = inchiKey;
  }


  public StructureCandidateScored smiles(String smiles) {
    
    this.smiles = smiles;
    return this;
  }

   /**
   * Get smiles
   * @return smiles
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SMILES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSmiles() {
    return smiles;
  }


  @JsonProperty(JSON_PROPERTY_SMILES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSmiles(String smiles) {
    this.smiles = smiles;
  }


  public StructureCandidateScored structureName(String structureName) {
    
    this.structureName = structureName;
    return this;
  }

   /**
   * Get structureName
   * @return structureName
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_STRUCTURE_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getStructureName() {
    return structureName;
  }


  @JsonProperty(JSON_PROPERTY_STRUCTURE_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setStructureName(String structureName) {
    this.structureName = structureName;
  }


  public StructureCandidateScored xlogP(Double xlogP) {
    
    this.xlogP = xlogP;
    return this;
  }

   /**
   * Get xlogP
   * @return xlogP
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_XLOG_P)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getXlogP() {
    return xlogP;
  }


  @JsonProperty(JSON_PROPERTY_XLOG_P)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setXlogP(Double xlogP) {
    this.xlogP = xlogP;
  }


  public StructureCandidateScored dbLinks(List<DBLink> dbLinks) {
    
    this.dbLinks = dbLinks;
    return this;
  }

  public StructureCandidateScored addDbLinksItem(DBLink dbLinksItem) {
    if (this.dbLinks == null) {
      this.dbLinks = new ArrayList<>();
    }
    this.dbLinks.add(dbLinksItem);
    return this;
  }

   /**
   * List of structure database links belonging to this structure candidate  OPTIONAL: needs to be added by parameter
   * @return dbLinks
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_DB_LINKS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<DBLink> getDbLinks() {
    return dbLinks;
  }


  @JsonProperty(JSON_PROPERTY_DB_LINKS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDbLinks(List<DBLink> dbLinks) {
    this.dbLinks = dbLinks;
  }


  public StructureCandidateScored spectralLibraryMatches(List<SpectralLibraryMatch> spectralLibraryMatches) {
    
    this.spectralLibraryMatches = spectralLibraryMatches;
    return this;
  }

  public StructureCandidateScored addSpectralLibraryMatchesItem(SpectralLibraryMatch spectralLibraryMatchesItem) {
    if (this.spectralLibraryMatches == null) {
      this.spectralLibraryMatches = new ArrayList<>();
    }
    this.spectralLibraryMatches.add(spectralLibraryMatchesItem);
    return this;
  }

   /**
   * List of spectral library matches belonging to this structure candidate  OPTIONAL: needs to be added by parameter
   * @return spectralLibraryMatches
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SPECTRAL_LIBRARY_MATCHES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<SpectralLibraryMatch> getSpectralLibraryMatches() {
    return spectralLibraryMatches;
  }


  @JsonProperty(JSON_PROPERTY_SPECTRAL_LIBRARY_MATCHES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSpectralLibraryMatches(List<SpectralLibraryMatch> spectralLibraryMatches) {
    this.spectralLibraryMatches = spectralLibraryMatches;
  }


  public StructureCandidateScored csiScore(Double csiScore) {
    
    this.csiScore = csiScore;
    return this;
  }

   /**
   * Get csiScore
   * @return csiScore
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_CSI_SCORE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getCsiScore() {
    return csiScore;
  }


  @JsonProperty(JSON_PROPERTY_CSI_SCORE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCsiScore(Double csiScore) {
    this.csiScore = csiScore;
  }


  public StructureCandidateScored tanimotoSimilarity(Double tanimotoSimilarity) {
    
    this.tanimotoSimilarity = tanimotoSimilarity;
    return this;
  }

   /**
   * Get tanimotoSimilarity
   * @return tanimotoSimilarity
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_TANIMOTO_SIMILARITY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getTanimotoSimilarity() {
    return tanimotoSimilarity;
  }


  @JsonProperty(JSON_PROPERTY_TANIMOTO_SIMILARITY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTanimotoSimilarity(Double tanimotoSimilarity) {
    this.tanimotoSimilarity = tanimotoSimilarity;
  }


  public StructureCandidateScored confidenceExactMatch(Double confidenceExactMatch) {
    
    this.confidenceExactMatch = confidenceExactMatch;
    return this;
  }

   /**
   * Get confidenceExactMatch
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


  public StructureCandidateScored confidenceApproxMatch(Double confidenceApproxMatch) {
    
    this.confidenceApproxMatch = confidenceApproxMatch;
    return this;
  }

   /**
   * Get confidenceApproxMatch
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


  public StructureCandidateScored fingerprint(BinaryFingerprint fingerprint) {
    
    this.fingerprint = fingerprint;
    return this;
  }

   /**
   * Get fingerprint
   * @return fingerprint
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FINGERPRINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public BinaryFingerprint getFingerprint() {
    return fingerprint;
  }


  @JsonProperty(JSON_PROPERTY_FINGERPRINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFingerprint(BinaryFingerprint fingerprint) {
    this.fingerprint = fingerprint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StructureCandidateScored structureCandidateScored = (StructureCandidateScored) o;
    return Objects.equals(this.inchiKey, structureCandidateScored.inchiKey) &&
        Objects.equals(this.smiles, structureCandidateScored.smiles) &&
        Objects.equals(this.structureName, structureCandidateScored.structureName) &&
        Objects.equals(this.xlogP, structureCandidateScored.xlogP) &&
        Objects.equals(this.dbLinks, structureCandidateScored.dbLinks) &&
        Objects.equals(this.spectralLibraryMatches, structureCandidateScored.spectralLibraryMatches) &&
        Objects.equals(this.csiScore, structureCandidateScored.csiScore) &&
        Objects.equals(this.tanimotoSimilarity, structureCandidateScored.tanimotoSimilarity) &&
        Objects.equals(this.confidenceExactMatch, structureCandidateScored.confidenceExactMatch) &&
        Objects.equals(this.confidenceApproxMatch, structureCandidateScored.confidenceApproxMatch) &&
        Objects.equals(this.fingerprint, structureCandidateScored.fingerprint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inchiKey, smiles, structureName, xlogP, dbLinks, spectralLibraryMatches, csiScore, tanimotoSimilarity, confidenceExactMatch, confidenceApproxMatch, fingerprint);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StructureCandidateScored {\n");
    sb.append("    inchiKey: ").append(toIndentedString(inchiKey)).append("\n");
    sb.append("    smiles: ").append(toIndentedString(smiles)).append("\n");
    sb.append("    structureName: ").append(toIndentedString(structureName)).append("\n");
    sb.append("    xlogP: ").append(toIndentedString(xlogP)).append("\n");
    sb.append("    dbLinks: ").append(toIndentedString(dbLinks)).append("\n");
    sb.append("    spectralLibraryMatches: ").append(toIndentedString(spectralLibraryMatches)).append("\n");
    sb.append("    csiScore: ").append(toIndentedString(csiScore)).append("\n");
    sb.append("    tanimotoSimilarity: ").append(toIndentedString(tanimotoSimilarity)).append("\n");
    sb.append("    confidenceExactMatch: ").append(toIndentedString(confidenceExactMatch)).append("\n");
    sb.append("    confidenceApproxMatch: ").append(toIndentedString(confidenceApproxMatch)).append("\n");
    sb.append("    fingerprint: ").append(toIndentedString(fingerprint)).append("\n");
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


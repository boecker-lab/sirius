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
import de.unijena.bioinf.ms.nightsky.sdk.model.ParentPeak;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * 
 */
@JsonPropertyOrder({
  PeakAnnotation.JSON_PROPERTY_FRAGMENT_ID,
  PeakAnnotation.JSON_PROPERTY_MOLECULAR_FORMULA,
  PeakAnnotation.JSON_PROPERTY_IONIZATION,
  PeakAnnotation.JSON_PROPERTY_EXACT_MASS,
  PeakAnnotation.JSON_PROPERTY_MASS_DEVIATION_MZ,
  PeakAnnotation.JSON_PROPERTY_MASS_DEVIATION_PPM,
  PeakAnnotation.JSON_PROPERTY_RECALIBRATED_MASS_DEVIATION_MZ,
  PeakAnnotation.JSON_PROPERTY_RECALIBRATED_MASS_DEVIATION_PPM,
  PeakAnnotation.JSON_PROPERTY_PARENT_PEAK,
  PeakAnnotation.JSON_PROPERTY_SUBSTRUCTURE_ATOMS,
  PeakAnnotation.JSON_PROPERTY_SUBSTRUCTURE_BONDS,
  PeakAnnotation.JSON_PROPERTY_SUBSTRUCTURE_BONDS_CUT,
  PeakAnnotation.JSON_PROPERTY_SUBSTRUCTURE_SCORE,
  PeakAnnotation.JSON_PROPERTY_HYDROGEN_REARRANGEMENTS
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PeakAnnotation {
  public static final String JSON_PROPERTY_FRAGMENT_ID = "fragmentId";
  private Integer fragmentId;

  public static final String JSON_PROPERTY_MOLECULAR_FORMULA = "molecularFormula";
  private String molecularFormula;

  public static final String JSON_PROPERTY_IONIZATION = "ionization";
  private String ionization;

  public static final String JSON_PROPERTY_EXACT_MASS = "exactMass";
  private Double exactMass;

  public static final String JSON_PROPERTY_MASS_DEVIATION_MZ = "massDeviationMz";
  private Double massDeviationMz;

  public static final String JSON_PROPERTY_MASS_DEVIATION_PPM = "massDeviationPpm";
  private Double massDeviationPpm;

  public static final String JSON_PROPERTY_RECALIBRATED_MASS_DEVIATION_MZ = "recalibratedMassDeviationMz";
  private Double recalibratedMassDeviationMz;

  public static final String JSON_PROPERTY_RECALIBRATED_MASS_DEVIATION_PPM = "recalibratedMassDeviationPpm";
  private Double recalibratedMassDeviationPpm;

  public static final String JSON_PROPERTY_PARENT_PEAK = "parentPeak";
  private ParentPeak parentPeak;

  public static final String JSON_PROPERTY_SUBSTRUCTURE_ATOMS = "substructureAtoms";
  private List<Integer> substructureAtoms;

  public static final String JSON_PROPERTY_SUBSTRUCTURE_BONDS = "substructureBonds";
  private List<Integer> substructureBonds;

  public static final String JSON_PROPERTY_SUBSTRUCTURE_BONDS_CUT = "substructureBondsCut";
  private List<Integer> substructureBondsCut;

  public static final String JSON_PROPERTY_SUBSTRUCTURE_SCORE = "substructureScore";
  private Float substructureScore;

  public static final String JSON_PROPERTY_HYDROGEN_REARRANGEMENTS = "hydrogenRearrangements";
  private Integer hydrogenRearrangements;

  public PeakAnnotation() {
  }

  public PeakAnnotation fragmentId(Integer fragmentId) {
    
    this.fragmentId = fragmentId;
    return this;
  }

   /**
   * Identifier of the peak/fragment. Can be used to map fragments and peaks  among fragmentation trees and spectra.
   * @return fragmentId
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_FRAGMENT_ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Integer getFragmentId() {
    return fragmentId;
  }


  @JsonProperty(JSON_PROPERTY_FRAGMENT_ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setFragmentId(Integer fragmentId) {
    this.fragmentId = fragmentId;
  }


  public PeakAnnotation molecularFormula(String molecularFormula) {
    
    this.molecularFormula = molecularFormula;
    return this;
  }

   /**
   * Molecular formula that has been annotated to this peak
   * @return molecularFormula
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_MOLECULAR_FORMULA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getMolecularFormula() {
    return molecularFormula;
  }


  @JsonProperty(JSON_PROPERTY_MOLECULAR_FORMULA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMolecularFormula(String molecularFormula) {
    this.molecularFormula = molecularFormula;
  }


  public PeakAnnotation ionization(String ionization) {
    
    this.ionization = ionization;
    return this;
  }

   /**
   * Ionization that has been annotated to this peak
   * @return ionization
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_IONIZATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getIonization() {
    return ionization;
  }


  @JsonProperty(JSON_PROPERTY_IONIZATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIonization(String ionization) {
    this.ionization = ionization;
  }


  public PeakAnnotation exactMass(Double exactMass) {
    
    this.exactMass = exactMass;
    return this;
  }

   /**
   * Exact mass of the annotated molecular formula and ionization
   * @return exactMass
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_EXACT_MASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getExactMass() {
    return exactMass;
  }


  @JsonProperty(JSON_PROPERTY_EXACT_MASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExactMass(Double exactMass) {
    this.exactMass = exactMass;
  }


  public PeakAnnotation massDeviationMz(Double massDeviationMz) {
    
    this.massDeviationMz = massDeviationMz;
    return this;
  }

   /**
   * Absolute mass deviation of the exact mass to the measured peak mass in mDa
   * @return massDeviationMz
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_MASS_DEVIATION_MZ)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getMassDeviationMz() {
    return massDeviationMz;
  }


  @JsonProperty(JSON_PROPERTY_MASS_DEVIATION_MZ)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMassDeviationMz(Double massDeviationMz) {
    this.massDeviationMz = massDeviationMz;
  }


  public PeakAnnotation massDeviationPpm(Double massDeviationPpm) {
    
    this.massDeviationPpm = massDeviationPpm;
    return this;
  }

   /**
   * Relative mass deviation of the exact mass to the measured peak mass in ppm
   * @return massDeviationPpm
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_MASS_DEVIATION_PPM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getMassDeviationPpm() {
    return massDeviationPpm;
  }


  @JsonProperty(JSON_PROPERTY_MASS_DEVIATION_PPM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMassDeviationPpm(Double massDeviationPpm) {
    this.massDeviationPpm = massDeviationPpm;
  }


  public PeakAnnotation recalibratedMassDeviationMz(Double recalibratedMassDeviationMz) {
    
    this.recalibratedMassDeviationMz = recalibratedMassDeviationMz;
    return this;
  }

   /**
   * Absolute mass deviation of the exact mass to the recalibrated peak mass in mDa
   * @return recalibratedMassDeviationMz
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_RECALIBRATED_MASS_DEVIATION_MZ)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getRecalibratedMassDeviationMz() {
    return recalibratedMassDeviationMz;
  }


  @JsonProperty(JSON_PROPERTY_RECALIBRATED_MASS_DEVIATION_MZ)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRecalibratedMassDeviationMz(Double recalibratedMassDeviationMz) {
    this.recalibratedMassDeviationMz = recalibratedMassDeviationMz;
  }


  public PeakAnnotation recalibratedMassDeviationPpm(Double recalibratedMassDeviationPpm) {
    
    this.recalibratedMassDeviationPpm = recalibratedMassDeviationPpm;
    return this;
  }

   /**
   * Relative mass deviation of the exact mass to the recalibrated peak mass in ppm
   * @return recalibratedMassDeviationPpm
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_RECALIBRATED_MASS_DEVIATION_PPM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Double getRecalibratedMassDeviationPpm() {
    return recalibratedMassDeviationPpm;
  }


  @JsonProperty(JSON_PROPERTY_RECALIBRATED_MASS_DEVIATION_PPM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRecalibratedMassDeviationPpm(Double recalibratedMassDeviationPpm) {
    this.recalibratedMassDeviationPpm = recalibratedMassDeviationPpm;
  }


  public PeakAnnotation parentPeak(ParentPeak parentPeak) {
    
    this.parentPeak = parentPeak;
    return this;
  }

   /**
   * Get parentPeak
   * @return parentPeak
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_PARENT_PEAK)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ParentPeak getParentPeak() {
    return parentPeak;
  }


  @JsonProperty(JSON_PROPERTY_PARENT_PEAK)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setParentPeak(ParentPeak parentPeak) {
    this.parentPeak = parentPeak;
  }


  public PeakAnnotation substructureAtoms(List<Integer> substructureAtoms) {
    
    this.substructureAtoms = substructureAtoms;
    return this;
  }

  public PeakAnnotation addSubstructureAtomsItem(Integer substructureAtomsItem) {
    if (this.substructureAtoms == null) {
      this.substructureAtoms = new ArrayList<>();
    }
    this.substructureAtoms.add(substructureAtomsItem);
    return this;
  }

   /**
   * Array/List of indices of the atoms of the structure candidate that are part of this fragments substructure  (highlighted atoms)
   * @return substructureAtoms
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SUBSTRUCTURE_ATOMS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<Integer> getSubstructureAtoms() {
    return substructureAtoms;
  }


  @JsonProperty(JSON_PROPERTY_SUBSTRUCTURE_ATOMS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSubstructureAtoms(List<Integer> substructureAtoms) {
    this.substructureAtoms = substructureAtoms;
  }


  public PeakAnnotation substructureBonds(List<Integer> substructureBonds) {
    
    this.substructureBonds = substructureBonds;
    return this;
  }

  public PeakAnnotation addSubstructureBondsItem(Integer substructureBondsItem) {
    if (this.substructureBonds == null) {
      this.substructureBonds = new ArrayList<>();
    }
    this.substructureBonds.add(substructureBondsItem);
    return this;
  }

   /**
   * Array/List of indices of the bonds of the structure candidate that are part of this fragments substructure  (highlighted bonds)   Null if substructure annotation not available or not requested.
   * @return substructureBonds
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SUBSTRUCTURE_BONDS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<Integer> getSubstructureBonds() {
    return substructureBonds;
  }


  @JsonProperty(JSON_PROPERTY_SUBSTRUCTURE_BONDS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSubstructureBonds(List<Integer> substructureBonds) {
    this.substructureBonds = substructureBonds;
  }


  public PeakAnnotation substructureBondsCut(List<Integer> substructureBondsCut) {
    
    this.substructureBondsCut = substructureBondsCut;
    return this;
  }

  public PeakAnnotation addSubstructureBondsCutItem(Integer substructureBondsCutItem) {
    if (this.substructureBondsCut == null) {
      this.substructureBondsCut = new ArrayList<>();
    }
    this.substructureBondsCut.add(substructureBondsCutItem);
    return this;
  }

   /**
   * Array/List of indices of the bonds of the structure candidate that need to be cut to produce this fragments  substructure (highlighted cutted bonds).   Null if substructure annotation not available or not requested.
   * @return substructureBondsCut
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SUBSTRUCTURE_BONDS_CUT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<Integer> getSubstructureBondsCut() {
    return substructureBondsCut;
  }


  @JsonProperty(JSON_PROPERTY_SUBSTRUCTURE_BONDS_CUT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSubstructureBondsCut(List<Integer> substructureBondsCut) {
    this.substructureBondsCut = substructureBondsCut;
  }


  public PeakAnnotation substructureScore(Float substructureScore) {
    
    this.substructureScore = substructureScore;
    return this;
  }

   /**
   * This score roughly reflects the probability of this fragment forming.   This is the score of the path from root to this node which has the maximal score or \&quot;profit\&quot;.  The score of a path is equal to the sum of scores of its contained fragments and edges.  Note: Refers to &#39;totalScore&#39; in CombinatorialNode   Null if substructure annotation not available or not requested.
   * @return substructureScore
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SUBSTRUCTURE_SCORE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Float getSubstructureScore() {
    return substructureScore;
  }


  @JsonProperty(JSON_PROPERTY_SUBSTRUCTURE_SCORE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSubstructureScore(Float substructureScore) {
    this.substructureScore = substructureScore;
  }


  public PeakAnnotation hydrogenRearrangements(Integer hydrogenRearrangements) {
    
    this.hydrogenRearrangements = hydrogenRearrangements;
    return this;
  }

   /**
   * Number of hydrogens rearrangements needed to match the substructure to the fragment formula.   Null if substructure annotation not available or not requested.
   * @return hydrogenRearrangements
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_HYDROGEN_REARRANGEMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getHydrogenRearrangements() {
    return hydrogenRearrangements;
  }


  @JsonProperty(JSON_PROPERTY_HYDROGEN_REARRANGEMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setHydrogenRearrangements(Integer hydrogenRearrangements) {
    this.hydrogenRearrangements = hydrogenRearrangements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PeakAnnotation peakAnnotation = (PeakAnnotation) o;
    return Objects.equals(this.fragmentId, peakAnnotation.fragmentId) &&
        Objects.equals(this.molecularFormula, peakAnnotation.molecularFormula) &&
        Objects.equals(this.ionization, peakAnnotation.ionization) &&
        Objects.equals(this.exactMass, peakAnnotation.exactMass) &&
        Objects.equals(this.massDeviationMz, peakAnnotation.massDeviationMz) &&
        Objects.equals(this.massDeviationPpm, peakAnnotation.massDeviationPpm) &&
        Objects.equals(this.recalibratedMassDeviationMz, peakAnnotation.recalibratedMassDeviationMz) &&
        Objects.equals(this.recalibratedMassDeviationPpm, peakAnnotation.recalibratedMassDeviationPpm) &&
        Objects.equals(this.parentPeak, peakAnnotation.parentPeak) &&
        Objects.equals(this.substructureAtoms, peakAnnotation.substructureAtoms) &&
        Objects.equals(this.substructureBonds, peakAnnotation.substructureBonds) &&
        Objects.equals(this.substructureBondsCut, peakAnnotation.substructureBondsCut) &&
        Objects.equals(this.substructureScore, peakAnnotation.substructureScore) &&
        Objects.equals(this.hydrogenRearrangements, peakAnnotation.hydrogenRearrangements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fragmentId, molecularFormula, ionization, exactMass, massDeviationMz, massDeviationPpm, recalibratedMassDeviationMz, recalibratedMassDeviationPpm, parentPeak, substructureAtoms, substructureBonds, substructureBondsCut, substructureScore, hydrogenRearrangements);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PeakAnnotation {\n");
    sb.append("    fragmentId: ").append(toIndentedString(fragmentId)).append("\n");
    sb.append("    molecularFormula: ").append(toIndentedString(molecularFormula)).append("\n");
    sb.append("    ionization: ").append(toIndentedString(ionization)).append("\n");
    sb.append("    exactMass: ").append(toIndentedString(exactMass)).append("\n");
    sb.append("    massDeviationMz: ").append(toIndentedString(massDeviationMz)).append("\n");
    sb.append("    massDeviationPpm: ").append(toIndentedString(massDeviationPpm)).append("\n");
    sb.append("    recalibratedMassDeviationMz: ").append(toIndentedString(recalibratedMassDeviationMz)).append("\n");
    sb.append("    recalibratedMassDeviationPpm: ").append(toIndentedString(recalibratedMassDeviationPpm)).append("\n");
    sb.append("    parentPeak: ").append(toIndentedString(parentPeak)).append("\n");
    sb.append("    substructureAtoms: ").append(toIndentedString(substructureAtoms)).append("\n");
    sb.append("    substructureBonds: ").append(toIndentedString(substructureBonds)).append("\n");
    sb.append("    substructureBondsCut: ").append(toIndentedString(substructureBondsCut)).append("\n");
    sb.append("    substructureScore: ").append(toIndentedString(substructureScore)).append("\n");
    sb.append("    hydrogenRearrangements: ").append(toIndentedString(hydrogenRearrangements)).append("\n");
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


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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets SpectralLibraryMatchOptField
 */
public enum SpectralLibraryMatchOptField {
  
  NONE("none"),
  
  REFERENCESPECTRUM("referenceSpectrum");

  private String value;

  SpectralLibraryMatchOptField(String value) {
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
  public static SpectralLibraryMatchOptField fromValue(String value) {
    for (SpectralLibraryMatchOptField b : SpectralLibraryMatchOptField.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    return null;
  }
}


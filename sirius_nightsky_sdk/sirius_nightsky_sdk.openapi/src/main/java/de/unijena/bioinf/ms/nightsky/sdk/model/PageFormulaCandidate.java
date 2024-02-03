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
import de.unijena.bioinf.ms.nightsky.sdk.model.FormulaCandidate;
import de.unijena.bioinf.ms.nightsky.sdk.model.PageableObject;
import de.unijena.bioinf.ms.nightsky.sdk.model.SortObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * PageFormulaCandidate
 */
@JsonPropertyOrder({
  PageFormulaCandidate.JSON_PROPERTY_TOTAL_PAGES,
  PageFormulaCandidate.JSON_PROPERTY_TOTAL_ELEMENTS,
  PageFormulaCandidate.JSON_PROPERTY_FIRST,
  PageFormulaCandidate.JSON_PROPERTY_LAST,
  PageFormulaCandidate.JSON_PROPERTY_SORT,
  PageFormulaCandidate.JSON_PROPERTY_SIZE,
  PageFormulaCandidate.JSON_PROPERTY_CONTENT,
  PageFormulaCandidate.JSON_PROPERTY_NUMBER,
  PageFormulaCandidate.JSON_PROPERTY_NUMBER_OF_ELEMENTS,
  PageFormulaCandidate.JSON_PROPERTY_PAGEABLE,
  PageFormulaCandidate.JSON_PROPERTY_EMPTY
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PageFormulaCandidate {
  public static final String JSON_PROPERTY_TOTAL_PAGES = "totalPages";
  private Integer totalPages;

  public static final String JSON_PROPERTY_TOTAL_ELEMENTS = "totalElements";
  private Long totalElements;

  public static final String JSON_PROPERTY_FIRST = "first";
  private Boolean first;

  public static final String JSON_PROPERTY_LAST = "last";
  private Boolean last;

  public static final String JSON_PROPERTY_SORT = "sort";
  private SortObject sort;

  public static final String JSON_PROPERTY_SIZE = "size";
  private Integer size;

  public static final String JSON_PROPERTY_CONTENT = "content";
  private List<FormulaCandidate> content;

  public static final String JSON_PROPERTY_NUMBER = "number";
  private Integer number;

  public static final String JSON_PROPERTY_NUMBER_OF_ELEMENTS = "numberOfElements";
  private Integer numberOfElements;

  public static final String JSON_PROPERTY_PAGEABLE = "pageable";
  private PageableObject pageable;

  public static final String JSON_PROPERTY_EMPTY = "empty";
  private Boolean empty;

  public PageFormulaCandidate() {
  }

  public PageFormulaCandidate totalPages(Integer totalPages) {
    
    this.totalPages = totalPages;
    return this;
  }

   /**
   * Get totalPages
   * @return totalPages
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_TOTAL_PAGES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getTotalPages() {
    return totalPages;
  }


  @JsonProperty(JSON_PROPERTY_TOTAL_PAGES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }


  public PageFormulaCandidate totalElements(Long totalElements) {
    
    this.totalElements = totalElements;
    return this;
  }

   /**
   * Get totalElements
   * @return totalElements
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_TOTAL_ELEMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Long getTotalElements() {
    return totalElements;
  }


  @JsonProperty(JSON_PROPERTY_TOTAL_ELEMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }


  public PageFormulaCandidate first(Boolean first) {
    
    this.first = first;
    return this;
  }

   /**
   * Get first
   * @return first
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FIRST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean isFirst() {
    return first;
  }


  @JsonProperty(JSON_PROPERTY_FIRST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFirst(Boolean first) {
    this.first = first;
  }


  public PageFormulaCandidate last(Boolean last) {
    
    this.last = last;
    return this;
  }

   /**
   * Get last
   * @return last
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_LAST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean isLast() {
    return last;
  }


  @JsonProperty(JSON_PROPERTY_LAST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLast(Boolean last) {
    this.last = last;
  }


  public PageFormulaCandidate sort(SortObject sort) {
    
    this.sort = sort;
    return this;
  }

   /**
   * Get sort
   * @return sort
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SORT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SortObject getSort() {
    return sort;
  }


  @JsonProperty(JSON_PROPERTY_SORT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSort(SortObject sort) {
    this.sort = sort;
  }


  public PageFormulaCandidate size(Integer size) {
    
    this.size = size;
    return this;
  }

   /**
   * Get size
   * @return size
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getSize() {
    return size;
  }


  @JsonProperty(JSON_PROPERTY_SIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSize(Integer size) {
    this.size = size;
  }


  public PageFormulaCandidate content(List<FormulaCandidate> content) {
    
    this.content = content;
    return this;
  }

  public PageFormulaCandidate addContentItem(FormulaCandidate contentItem) {
    if (this.content == null) {
      this.content = new ArrayList<>();
    }
    this.content.add(contentItem);
    return this;
  }

   /**
   * Get content
   * @return content
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_CONTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<FormulaCandidate> getContent() {
    return content;
  }


  @JsonProperty(JSON_PROPERTY_CONTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setContent(List<FormulaCandidate> content) {
    this.content = content;
  }


  public PageFormulaCandidate number(Integer number) {
    
    this.number = number;
    return this;
  }

   /**
   * Get number
   * @return number
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_NUMBER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getNumber() {
    return number;
  }


  @JsonProperty(JSON_PROPERTY_NUMBER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setNumber(Integer number) {
    this.number = number;
  }


  public PageFormulaCandidate numberOfElements(Integer numberOfElements) {
    
    this.numberOfElements = numberOfElements;
    return this;
  }

   /**
   * Get numberOfElements
   * @return numberOfElements
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_NUMBER_OF_ELEMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getNumberOfElements() {
    return numberOfElements;
  }


  @JsonProperty(JSON_PROPERTY_NUMBER_OF_ELEMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setNumberOfElements(Integer numberOfElements) {
    this.numberOfElements = numberOfElements;
  }


  public PageFormulaCandidate pageable(PageableObject pageable) {
    
    this.pageable = pageable;
    return this;
  }

   /**
   * Get pageable
   * @return pageable
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_PAGEABLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public PageableObject getPageable() {
    return pageable;
  }


  @JsonProperty(JSON_PROPERTY_PAGEABLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPageable(PageableObject pageable) {
    this.pageable = pageable;
  }


  public PageFormulaCandidate empty(Boolean empty) {
    
    this.empty = empty;
    return this;
  }

   /**
   * Get empty
   * @return empty
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_EMPTY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean isEmpty() {
    return empty;
  }


  @JsonProperty(JSON_PROPERTY_EMPTY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEmpty(Boolean empty) {
    this.empty = empty;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageFormulaCandidate pageFormulaCandidate = (PageFormulaCandidate) o;
    return Objects.equals(this.totalPages, pageFormulaCandidate.totalPages) &&
        Objects.equals(this.totalElements, pageFormulaCandidate.totalElements) &&
        Objects.equals(this.first, pageFormulaCandidate.first) &&
        Objects.equals(this.last, pageFormulaCandidate.last) &&
        Objects.equals(this.sort, pageFormulaCandidate.sort) &&
        Objects.equals(this.size, pageFormulaCandidate.size) &&
        Objects.equals(this.content, pageFormulaCandidate.content) &&
        Objects.equals(this.number, pageFormulaCandidate.number) &&
        Objects.equals(this.numberOfElements, pageFormulaCandidate.numberOfElements) &&
        Objects.equals(this.pageable, pageFormulaCandidate.pageable) &&
        Objects.equals(this.empty, pageFormulaCandidate.empty);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalPages, totalElements, first, last, sort, size, content, number, numberOfElements, pageable, empty);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageFormulaCandidate {\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    first: ").append(toIndentedString(first)).append("\n");
    sb.append("    last: ").append(toIndentedString(last)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    numberOfElements: ").append(toIndentedString(numberOfElements)).append("\n");
    sb.append("    pageable: ").append(toIndentedString(pageable)).append("\n");
    sb.append("    empty: ").append(toIndentedString(empty)).append("\n");
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


/**
 * 현대IT&E 3차 프로젝트 API 명세서
 * 현대IT&E 3차 프로젝트에 사용되는 API 명세서
 *
 * OpenAPI spec version: v1
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package com.hoxy.hlivv.data.models


/**
 *
 * @param optionName
 * @param originalPrice
 * @param discountPrice
 */
data class ProductOptionDto(

    val optionName: String? = null,
    val originalPrice: Long? = null,
    val discountPrice: Long? = null
)
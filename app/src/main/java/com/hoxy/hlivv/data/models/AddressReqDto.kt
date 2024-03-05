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
 * @param streetAddress
 * @param detailedAddress
 * @param zipCode
 * @param telephoneNumber
 * @param mobilePhoneNumber
 * @param requestMsg
 * @param defaultYn
 */
data class AddressReqDto(

    val streetAddress: String? = null,
    val detailedAddress: String? = null,
    val zipCode: String? = null,
    val telephoneNumber: String? = null,
    val mobilePhoneNumber: String? = null,
    val requestMsg: String? = null,
    val defaultYn: Boolean? = null
)
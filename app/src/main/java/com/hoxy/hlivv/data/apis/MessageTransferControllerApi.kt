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
package com.hoxy.hlivv.data.apis

import com.hoxy.hlivv.data.infrastructure.ApiClient
import com.hoxy.hlivv.data.infrastructure.ClientError
import com.hoxy.hlivv.data.infrastructure.ClientException
import com.hoxy.hlivv.data.infrastructure.MultiValueMap
import com.hoxy.hlivv.data.infrastructure.RequestConfig
import com.hoxy.hlivv.data.infrastructure.RequestMethod
import com.hoxy.hlivv.data.infrastructure.ResponseType
import com.hoxy.hlivv.data.infrastructure.ServerError
import com.hoxy.hlivv.data.infrastructure.ServerException
/**
 * @author 반정현
 */
class MessageTransferControllerApi(basePath: String = "https://hlivv.com") :
    ApiClient(basePath) {

    /**
     *
     *
     * @param toNumber
     * @param contents
     * @return void
     */
    fun transferRestoreMessage(toNumber: String, contents: String): Unit {
        val localVariableQuery: MultiValueMap =
            mutableMapOf<String, List<String>>().apply {
                put("toNumber", listOf(toNumber.toString()))
                put("contents", listOf(contents.toString()))
            }
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/messageTransfer", query = localVariableQuery
        )
        val response = request<Any?>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException(
                (response as ClientError<*>).body as? String ?: "Client error"
            )

            ResponseType.ServerError -> throw ServerException(
                (response as ServerError<*>).message ?: "Server error"
            )
        }
    }
}

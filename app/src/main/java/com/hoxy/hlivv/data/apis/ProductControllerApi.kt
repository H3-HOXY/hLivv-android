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
import com.hoxy.hlivv.data.infrastructure.Success
import com.hoxy.hlivv.data.models.ProductDto
import com.hoxy.hlivv.data.models.ProductIdReviewBody
import com.hoxy.hlivv.data.models.Request
import com.hoxy.hlivv.data.models.Response
import com.hoxy.hlivv.data.models.ReviewDto

class ProductControllerApi(basePath: String = "http://10.0.2.2:8080") : ApiClient(basePath) {

    /**
     *
     *
     * @param body
     * @return ProductDto
     */
    @Suppress("UNCHECKED_CAST")
    fun createProduct(body: ProductDto): ProductDto {
        val localVariableBody: Any = body
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/product"
        )
        val response = request<ProductDto>(
            localVariableConfig, localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as ProductDto
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

    /**
     *
     *
     * @param pageNo  (optional, default to 1)
     * @param pageSize  (optional, default to 20)
     * @param sortCriteria  (optional, default to PRICE_DESC)
     * @return kotlin.Array<ProductDto>
     */
    @Suppress("UNCHECKED_CAST")
    fun getProduct(
        pageNo: Int? = null,
        pageSize: Int? = null,
        sortCriteria: String? = null
    ): Array<ProductDto> {
        val localVariableQuery: MultiValueMap =
            mutableMapOf<String, List<String>>().apply {
                if (pageNo != null) {
                    put("pageNo", listOf(pageNo.toString()))
                }
                if (pageSize != null) {
                    put("pageSize", listOf(pageSize.toString()))
                }
                if (sortCriteria != null) {
                    put("sortCriteria", listOf(sortCriteria.toString()))
                }
            }
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/api/product", query = localVariableQuery
        )
        val response = request<Array<ProductDto>>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as Array<ProductDto>
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

    /**
     *
     *
     * @param productId
     * @return ProductDto
     */
    @Suppress("UNCHECKED_CAST")
    fun getProduct1(productId: Long): ProductDto {
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/api/product/{productId}".replace("{" + "productId" + "}", "$productId")
        )
        val response = request<ProductDto>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as ProductDto
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

    /**
     *
     *
     * @param productId
     * @return kotlin.Array<ReviewDto>
     */
    @Suppress("UNCHECKED_CAST")
    fun getReviewsByProductId(productId: Long): Array<ReviewDto> {
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/api/product/{productId}/review".replace("{" + "productId" + "}", "$productId")
        )
        val response = request<Array<ReviewDto>>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as Array<ReviewDto>
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

    /**
     *
     *
     * @param body
     * @param productId
     * @return ProductDto
     */
    @Suppress("UNCHECKED_CAST")
    fun updateProduct(body: ProductDto, productId: Long): ProductDto {
        val localVariableBody: Any = body
        val localVariableConfig = RequestConfig(
            RequestMethod.PUT,
            "/api/product/{productId}".replace("{" + "productId" + "}", "$productId")
        )
        val response = request<ProductDto>(
            localVariableConfig, localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as ProductDto
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

    /**
     *
     *
     * @param writeReviewRequest
     * @param productId
     * @param body  (optional)
     * @return Response
     */
    @Suppress("UNCHECKED_CAST")
    fun writeReviewToProduct(
        writeReviewRequest: Request,
        productId: Long,
        body: ProductIdReviewBody? = null
    ): Response {
        val localVariableBody: Any? = body
        val localVariableQuery: MultiValueMap =
            mutableMapOf<String, List<String>>().apply {
                put("writeReviewRequest", listOf(writeReviewRequest.toString()))
            }
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/product/{productId}/review".replace("{" + "productId" + "}", "$productId"),
            query = localVariableQuery
        )
        val response = request<Response>(
            localVariableConfig, localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as Response
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
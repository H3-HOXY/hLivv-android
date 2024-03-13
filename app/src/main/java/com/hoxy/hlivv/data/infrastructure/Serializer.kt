package io.swagger.client.infrastructure

import com.hoxy.hlivv.data.infrastructure.LocalDateAdapter
import com.hoxy.hlivv.data.infrastructure.LocalDateTimeAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Date

/**
 * @author 반정현
 */
object Serializer {
    @JvmStatic
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .add(LocalDateTimeAdapter())
        .add(LocalDateAdapter())
        .build()
}
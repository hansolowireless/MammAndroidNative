package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName

data class LocatorResponse(
    val status: Int,  // era "statusCode" pero en JSON es "status"
    val message: String,
    val data: EndpointData  // correcto
)

data class EndpointData(
    val operator: String? = null,

    @SerializedName("endpoint_static")
    val endpointStatic: String? = null,

    @SerializedName("endpoint_idm")
    val endpointIdm: String? = null,

    @SerializedName("endpoint_search")
    val endpointSearch: String? = null,

    @SerializedName("endpoint_manager")
    val endpointManager: String? = null,

    @SerializedName("endpoint_drm")
    val endpointDrm: String? = null,

    @SerializedName("endpoint_qos")
    val endpointQos: String? = null,

    @SerializedName("endpoint_proxybuyer")
    val endpointProxybuyer: String? = null
)
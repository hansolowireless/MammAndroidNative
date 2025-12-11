package com.mamm.mammapps.data.model.login

import com.google.gson.annotations.SerializedName

data class LocatorResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: EndpointData
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
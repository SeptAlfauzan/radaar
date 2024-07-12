package com.kudos.radaar.core.data.raw

import com.google.gson.annotations.SerializedName

data class RadarRawResponse(

	@field:SerializedName("distance")
	val distance: Double? = null,

	@field:SerializedName("degree")
	val degree: Int? = null
)

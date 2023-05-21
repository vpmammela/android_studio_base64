package com.example.base64images.datatypes.base64image

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("id"    ) var id    : Int?    = null,
  @SerializedName("image" ) var image : String? = null

)
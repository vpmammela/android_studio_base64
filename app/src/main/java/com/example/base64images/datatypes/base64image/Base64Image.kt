package com.example.base64images.datatypes.base64image

import com.google.gson.annotations.SerializedName


data class Base64Image (

  @SerializedName("data" ) var data : Data? = Data()

)
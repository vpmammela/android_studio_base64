package com.example.base64images

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.base64images.databinding.FragmentGetImageBinding
import com.example.base64images.datatypes.base64image.Base64Image
import com.google.gson.GsonBuilder

class GetImageFragment : Fragment() {
    private var _binding: FragmentGetImageBinding? = null

    // This property is only valid between onCreateView and // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGetImageBinding.inflate(inflater, container, false)

        val root: View = binding.root

        binding.buttonGetImage.setOnClickListener {
            getImages()
        }

        return root
    }
    fun getImages() {

        var images : List<Base64Image> = emptyList()
        val gson = GsonBuilder().setPrettyPrinting().create()
        // this is the url where we want to get our data
        // Note: if using a local server, use http://10.0.2.2 for localhost. this is a virtual address for Android emulators, since
        // localhost refers to the Android device instead of your computer
        val JSON_URL = "http://10.0.2.2:8055/items/Base64/6?access_token=L_LruBFG3SiZWRFwKTA17XLyC9WnFQAL"

        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable

                var item : Base64Image = gson.fromJson(response, Base64Image::class.java)

                val base64Image = item.data?.image
                val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                binding.imageViewBase64.setImageBitmap(bitmap)

            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("TESTI", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                // basic headers for the data
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        // if using this in an activity, use "this" instead of "context"
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

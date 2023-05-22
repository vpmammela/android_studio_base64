package com.example.base64images
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.base64images.databinding.FragmentPostImageBinding
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException

class PostImageFragment : Fragment() {
    private var _binding: FragmentPostImageBinding? = null
    private val binding get() = _binding!!

    private val PICK_IMAGE_REQUEST = 1
    private var isImageSelected = false
    private var base64ImageToPost = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostImageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (!isImageSelected) {
            binding.imageViewSelectedImage.visibility = View.GONE
            binding.buttonPostImage.visibility = View.GONE
        }
        binding.buttonChoosePhoto.setOnClickListener {
            openGallery()
        }

        binding.buttonPostImage.setOnClickListener {
            postImage(base64ImageToPost)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                val bitmap = decodeImageUri(selectedImageUri)
                if (bitmap !== null) {
                    isImageSelected = true
                    binding.imageViewSelectedImage.visibility = View.VISIBLE
                    binding.buttonPostImage.visibility = View.VISIBLE
                    binding.imageViewSelectedImage.setImageBitmap(bitmap)
                }
                base64ImageToPost = convertBitmapToBase64(bitmap)
                Log.d("TESTI", base64ImageToPost)

                // Use the base64Image string for your further processing
            }
        }
    }

    private fun decodeImageUri(uri: Uri): Bitmap? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        inputStream?.let {
            val options = BitmapFactory.Options().apply {
                inSampleSize = 8  // Adjust the inSampleSize as per your requirement to optimize memory usage
            }
            return BitmapFactory.decodeStream(inputStream, null, options)
        }
        return null
    }

    private fun convertBitmapToBase64(bitmap: Bitmap?): String {
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val imageBytes = stream.toByteArray()
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }
        return ""
    }

    fun postImage(base64: String) {
        val JSON_URL = "http://10.0.2.2:8055/items/Base64/?access_token=L_LruBFG3SiZWRFwKTA17XLyC9WnFQAL"

        val jsonObject = JSONObject()
        jsonObject.put("image", base64)
        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, JSON_URL,
            Response.Listener { response ->
                // usually APIs return the added new data back
                // when sending new data
                // therefore the response here should contain the JSON version
                // of the data you just sent below
                Log.d("TESTI", response)
                showToast("Image posted successfully")
            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("TESTI", it.toString())
                showToast("Error posting image")
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // we have to specify a proper header, otherwise the API might block our queries!
                // define that we are after JSON data!
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }

            // let's build the new data here
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                // this function is only needed when sending data
                var body = ByteArray(0)
                try {
                    body = jsonObject.toString().toByteArray(Charsets.UTF_8)
                } catch (e: UnsupportedEncodingException) {
                    // problems with converting our data into UTF-8 bytes
                }
                return body
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        // if using this in an activity, use "this" instead of "context"
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
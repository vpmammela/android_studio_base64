package com.example.base64images

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.base64images.databinding.FragmentGetImageBinding
import com.example.base64images.databinding.FragmentPostImageBinding


class PostImageFragment : Fragment() {
    private var _binding: FragmentPostImageBinding? = null

    // This property is only valid between onCreateView and // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostImageBinding.inflate(inflater, container, false)

        val root: View = binding.root



        return root
    }
}
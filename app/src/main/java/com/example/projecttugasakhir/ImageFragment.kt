package com.example.projecttugasakhir

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projecttugasakhir.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    private lateinit var binding:FragmentImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_gallery, container, false)
        binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            arguments?.let {
                val bundle_img = ImageFragmentArgs.fromBundle(requireArguments()).img
                val image = bundle_img.getParcelable<Bitmap>("bitmap_img")
                binding.imgSelect.setImageBitmap(image)
            }
        }
    }
}
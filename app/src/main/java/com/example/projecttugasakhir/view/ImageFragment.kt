package com.example.projecttugasakhir.view

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projecttugasakhir.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    private lateinit var binding:FragmentImageBinding
    private var image:Bitmap? = null

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
            val bundle_img = ImageFragmentArgs.fromBundle(requireArguments()).img
            image = bundle_img.getParcelable(HomeFragment.BITMAP_IMG_KEY)!!
            binding.imgSelect.setImageBitmap(image)
        }
    }
}
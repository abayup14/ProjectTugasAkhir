package com.example.projecttugasakhir.view

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.projecttugasakhir.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    private lateinit var binding:FragmentImageBinding
    private lateinit var bitmap_img:Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val bundle_img = ImageFragmentArgs.fromBundle(requireArguments()).img
            bitmap_img = bundle_img.getParcelable(HomeFragment.BITMAP_IMG_KEY)!!
            binding.imgSelect.setImageBitmap(bitmap_img)
        }

        binding.btnSelect.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            bundleAndNavigateToResultFragment(bitmap_img, HomeFragment.BITMAP_IMG_KEY, requireView())
        }
    }

    private fun bundleAndNavigateToResultFragment(bitmap_img:Bitmap, key_name:String, view:View) {
        val bundle = Bundle()
        bundle.putParcelable(key_name, bitmap_img)
        val action = ImageFragmentDirections.actionResult(bundle)
        Navigation.findNavController(view).navigate(action)
    }
}
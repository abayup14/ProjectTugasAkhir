package com.example.projecttugasakhir.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.projecttugasakhir.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var bitmap_img:Bitmap
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_LOAD_IMAGE = 2
    private val REQUEST_PERMISSION_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun takePictureFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_LOAD_IMAGE)
    }

    private fun takePictureFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGaleri.setOnClickListener {
            takePictureFromGallery()
        }

        binding.btnKamera.setOnClickListener {
            takePictureFromCamera()
        }
    }

    private fun checkCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            return false
        }
    }

    private fun bundleAndNavigateToImageFragment(bitmap_img:Bitmap, key_name:String, view:View) {
        val bundle = Bundle()
        bundle.putParcelable(key_name, bitmap_img)
        val action = HomeFragmentDirections.actionImage(bundle)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            bitmap_img = extras?.get("data") as Bitmap
            bundleAndNavigateToImageFragment(bitmap_img, BITMAP_IMG_KEY, requireView())
        } else if (requestCode == REQUEST_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImage:Uri? = data?.data
            selectedImage?.let {
                bitmap_img = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                bundleAndNavigateToImageFragment(bitmap_img, BITMAP_IMG_KEY, requireView())
            }
        }
    }

    companion object {
        val BITMAP_IMG_KEY = "bitmap_img"
    }
}
package com.example.projecttugasakhir

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
    private val REQUEST_READ_EXTERNAL = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        }

        if (!checkStoragePermission()) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    private fun checkStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            return false
        }
    }

    private fun navigateToImageFragment(bitmap_img:Bitmap, view:View) {
        val action = HomeFragmentDirections.actionImage(bitmap_img)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val extras = data?.extras
                bitmap_img = extras?.get("data") as Bitmap
                navigateToImageFragment(bitmap_img, requireView())
            } else if (requestCode == REQUEST_LOAD_IMAGE) {
                val selectedImage:Uri? = data?.data
                selectedImage?.let {
                    bitmap_img = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                    navigateToImageFragment(bitmap_img, requireView())
                }
            }
        }
    }
}
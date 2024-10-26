package com.example.projecttugasakhir

import android.app.AlertDialog
import android.content.DialogInterface
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
            bitmap_img = ImageFragmentArgs.fromBundle(requireArguments()).img
            binding.imgSelect.setImageBitmap(bitmap_img)
        }

        binding.btnSelect.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Konfirmasi Foto")
            alert.setMessage("Apakah ini foto yang ingin digunakan untuk prediksi?")
            alert.setPositiveButton("GUNAKAN", DialogInterface.OnClickListener { dialog, which ->
                navigateToResultFragment(bitmap_img, requireView())
            })
            alert.setNegativeButton("TIDAK", DialogInterface.OnClickListener { dialog, which ->
                null
            })

            alert.create().show()
        }
    }

    private fun navigateToResultFragment(bitmap_img:Bitmap, view:View) {
        val action = ImageFragmentDirections.actionResult(bitmap_img)
        Navigation.findNavController(view).navigate(action)
    }
}
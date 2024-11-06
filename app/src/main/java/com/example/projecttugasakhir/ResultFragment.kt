package com.example.projecttugasakhir

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.projecttugasakhir.databinding.FragmentResultBinding
import com.example.projecttugasakhir.ml.ModelPadang
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ResultFragment : Fragment() {
    private lateinit var binding:FragmentResultBinding
    private lateinit var bitmap_img: Bitmap
    private lateinit var labels: List<PadangFood>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        if (arguments != null) {
            bitmap_img = ResultFragmentArgs.fromBundle(requireArguments()).img
        }

        val array_prediction = classifyImage(bitmap_img)
        val maxIndex = array_prediction.indices.maxByOrNull { array_prediction[it] } ?: -1
        if (maxIndex >= 0) {
            val predictedLabel = labels[maxIndex]
            binding.txtNamaMakanan.text = predictedLabel.nama
            val drawableId = resources.getIdentifier(predictedLabel.foto,
                "drawable",
                requireActivity().application.packageName)
            Picasso.get()
                .load(drawableId)
                .into(binding.imgFood)
            val bahan = "Bahan:\n" + predictedLabel.bahan
            val resep = "Resep:\n" + predictedLabel.resep
            binding.txtBahanMakanan.text = bahan
            binding.txtResepMakanan.text = resep
            Log.d("ResultFragment", "Predicted label: ${predictedLabel.nama}")
        } else {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Informasi")
            alert.setMessage("Tidak dapat melakukan prediksi. Coba ambil foto dari kamera atau galeri.")
            alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                val action = ResultFragmentDirections.actionHome()
                Navigation.findNavController(requireView()).navigate(action)
            })
        }

        binding.btnThreeBest.setOnClickListener {
            val sortedArray = sortAndTake3Best(array_prediction)
            var msg = ""
            for ((index, value) in sortedArray) {
                val nama = labels[index].nama
                val conf = value * 100.0
                msg += "$nama - ${String.format("%.2f", conf)}%\n"
            }
            val alertPrediksi = AlertDialog.Builder(requireContext())
            alertPrediksi.setTitle("3 Prediksi Tertinggi")
            alertPrediksi.setMessage(msg)
            alertPrediksi.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                null
            })

            alertPrediksi.create().show()
        }
    }

    private fun sortAndTake3Best(array:FloatArray): List<Pair<Int, Float>> {
        val sortedArray = array
            .mapIndexed { index, fl -> index to fl }
            .sortedByDescending { it.second }
            .take(3)

        return sortedArray
    }

    private fun loadData() {
        val gson = Gson()
        val inputStream = requireContext().assets.open("padang.json")
        val reader = InputStreamReader(inputStream)
        val labelListType = object : TypeToken<List<PadangFood>>() {}.type
        labels = gson.fromJson(reader, labelListType)
        reader.close()

        Log.d("ResultFragment", "Loaded labels: $labels")
    }

    private fun classifyImage(bitmap_img:Bitmap): FloatArray {
        val model = ModelPadang.newInstance(requireContext())

        val byteBuffer = convertBitmapToByteBuffer(bitmap_img)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val outputArray = outputFeature0.floatArray
        Log.d("ResultFragment", "Model output: ${outputArray.contentToString()}")

        model.close()

        return outputArray
    }

    private fun convertBitmapToByteBuffer(bitmap_img: Bitmap): ByteBuffer {
        val inputSize = 224

        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)

        byteBuffer.order(ByteOrder.nativeOrder())

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap_img, inputSize, inputSize, true)

        for (y in 0 until resizedBitmap.height) {
            for ( x in 0 until resizedBitmap.width) {
                val pixel = resizedBitmap.getPixel(x, y)
                val r = ((pixel shr 16 and 0xFF) / 255.0f)
                val g = ((pixel shr 8 and 0xFF) / 255.0f)
                val b = ((pixel and 0xFF) / 255.0f)
                byteBuffer.putFloat(r)
                byteBuffer.putFloat(g)
                byteBuffer.putFloat(b)
            }
        }

        return byteBuffer
    }
}
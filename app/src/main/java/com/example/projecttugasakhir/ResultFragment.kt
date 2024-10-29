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
import androidx.navigation.Navigation
import com.example.projecttugasakhir.databinding.FragmentResultBinding
import com.example.projecttugasakhir.ml.ModelPadang
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
            binding.imgFood.setImageBitmap(bitmap_img)
//            Log.d("ResultFragment", "Bitmap image received: ${bitmap_img.width}x${bitmap_img.height}")
        }

        val array_prediction = classifyImage(bitmap_img)

        val maxIndex = array_prediction.indices.maxByOrNull { array_prediction[it] } ?: -1
        if (maxIndex >= 0) {
            val predictedLabel = labels[maxIndex]
            binding.txtNamaMakanan.text = predictedLabel.nama
            binding.txtBahanMakanan.text = predictedLabel.bahan
            binding.txtResepMakanan.text = predictedLabel.resep
            Log.d("ResultFragment", "Predicted label: ${predictedLabel.nama}")
        }

        binding.btnThreeBest.setOnClickListener {
            val alertPrediksi = AlertDialog.Builder(requireContext())
            alertPrediksi.setTitle("3 Prediksi Tertinggi")
            alertPrediksi.setMessage("")
            alertPrediksi.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                null
            })

            alertPrediksi.create().show()
        }
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
        Log.d("ResultFragment", "ByteBuffer size: ${byteBuffer.capacity()}")

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val outputArray = outputFeature0.floatArray
        Log.d("ResultFragment", "Model output: ${outputArray.contentToString()}")

//        val labelIndex = outputFeature0.getIntValue(0)
//        val predictedLabel = labels[labelIndex]

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

                if (x < 5 && y < 5) {
                    Log.d("ResultFragment", "Pixel[$x,$y] - R: $r, G: $g, B: $b")
                }
            }
        }

        return byteBuffer
    }
}
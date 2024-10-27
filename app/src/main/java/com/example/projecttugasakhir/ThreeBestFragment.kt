package com.example.projecttugasakhir

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projecttugasakhir.databinding.FragmentThreeBestBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class ThreeBestFragment : Fragment() {
    private lateinit var binding:FragmentThreeBestBinding
    private lateinit var array_predict:FloatArray
    private lateinit var labels: List<PadangFood>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThreeBestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        if (arguments != null) {
            array_predict = ThreeBestFragmentArgs.fromBundle(requireArguments()).arrayPredict
        }

        val sorted_array = sortPredictAndTake3(array_predict)
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

    private fun sortPredictAndTake3(array_predict:FloatArray): List<Pair<Int, Float>> {
        val sorted_array = array_predict
            .mapIndexed { index, fl -> index to fl }
            .sortedByDescending { it.second }
            .take(3)

        return sorted_array
    }
}
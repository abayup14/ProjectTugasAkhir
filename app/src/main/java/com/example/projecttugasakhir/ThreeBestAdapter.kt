package com.example.projecttugasakhir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projecttugasakhir.databinding.CardItemBinding

class ThreeBestAdapter(val array_predict: FloatArray): RecyclerView.Adapter<ThreeBestAdapter.ThreeBestHolder>() {
    class ThreeBestHolder(var view:CardItemBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreeBestHolder {
        var view = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThreeBestHolder(view)
    }

    override fun getItemCount(): Int {
        return array_predict.size
    }

    override fun onBindViewHolder(holder: ThreeBestHolder, position: Int) {

    }
}
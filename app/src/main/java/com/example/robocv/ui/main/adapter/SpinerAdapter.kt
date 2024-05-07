package com.example.robocv.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.robocv.R
import com.example.robocv.databinding.CardGarbageBinding
import com.example.robocv.databinding.CustomSpinerBinding
import com.example.robocv.domain.model.StoragePlaceRoboCV

class SpinerAdapter(
    context: Context,
    resource: Int,
    objects: List<StoragePlaceRoboCV>
) : ArrayAdapter<StoragePlaceRoboCV>(context, resource, objects) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = CustomSpinerBinding.inflate(LayoutInflater.from(context), parent, false)
        val storagePlace = getItem(position)
        binding.customTextViewForSpiner.text = storagePlace?.name
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}

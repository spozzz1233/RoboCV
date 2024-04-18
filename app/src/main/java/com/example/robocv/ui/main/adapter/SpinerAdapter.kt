package com.example.robocv.ui.main.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.robocv.domain.model.StoragePlaceRoboCV

class SpinerAdapter(context: Context, resource: Int, objects: List<StoragePlaceRoboCV>) :
    ArrayAdapter<StoragePlaceRoboCV>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val storagePlace = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = storagePlace?.name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

}

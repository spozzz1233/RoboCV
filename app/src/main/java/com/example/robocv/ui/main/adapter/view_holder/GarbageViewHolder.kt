package com.example.robocv.ui.main.adapter.view_holder

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.robocv.R
import com.example.robocv.domain.model.Garbage
import java.text.SimpleDateFormat
import java.util.Locale

class GarbageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewStorageId: TextView = itemView.findViewById(R.id.StorageId)
    val textViewStorageNumber: TextView = itemView.findViewById(R.id.StorageNumber)
    val textViewName: TextView = itemView.findViewById(R.id.name)
    val buttonDelete : ImageView = itemView.findViewById(R.id.item_delete_button)

    fun bind(item: Garbage) {
        textViewStorageId.text = item.StoragePlace.toString()
        textViewStorageNumber.text = item.StoragePlaceName
        textViewName.text = item.name
    }
}

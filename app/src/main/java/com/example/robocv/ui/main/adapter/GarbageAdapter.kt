package com.example.robocv.ui.main.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.robocv.databinding.ActivityMainBinding
import com.example.robocv.databinding.CardGarbageBinding
import com.example.robocv.domain.model.Garbage
import com.example.robocv.ui.main.adapter.view_holder.GarbageViewHolder

class GarbageAdapter(
    private val clickListener: GarbagesClick,
) : RecyclerView.Adapter<GarbageViewHolder>() {

    private var garbages: List<Garbage> = ArrayList()
    private lateinit var binding: CardGarbageBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GarbageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = CardGarbageBinding.inflate(inflater, parent, false)
        return GarbageViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: GarbageViewHolder, position: Int) {
        holder.bind(garbages[position])
        val item = garbages[position]
        holder.buttonDelete.setOnClickListener {
            clickListener.onClick(item, position)
        }
    }

    override fun getItemCount(): Int = garbages.size

    fun setItems(items: ArrayList<Garbage>) {
        garbages = items
        notifyDataSetChanged()
    }
    fun interface GarbagesClick {
        fun onClick(garbages: Garbage, position: Int)
    }
}

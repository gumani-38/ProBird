package com.example.probird

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.probird.models.BirdTaxonomyItem

class BirdAdapter(private val birdList: List<BirdTaxonomyItem>) : RecyclerView.Adapter<BirdAdapter.BirdViewHolder>() {

    class BirdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val birdImage: ImageView = itemView.findViewById(R.id.photo)
        val birdName: TextView = itemView.findViewById(R.id.edtName)
        val birdDescription: TextView = itemView.findViewById(R.id.edtDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bird_item, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birdList[position]
        holder.birdName.text = bird.comName
        holder.birdDescription.text = "Scientific Name: ${bird.sciName}"

        // TODO: Load image dynamically if available. For now, using placeholder image.
        holder.birdImage.setImageResource(R.drawable.cardbird)
    }

    override fun getItemCount(): Int {
        return birdList.size
    }

}

package com.alisiyararslan.artbooknavigationkotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.alisiyararslan.artbooknavigationkotlin.databinding.RecyclerRowBinding
import com.alisiyararslan.artbooknavigationkotlin.model.Art
import com.alisiyararslan.artbooknavigationkotlin.view.ListFragmentDirections


class ArtAdapter(val artlist:List<Art>): RecyclerView.Adapter<ArtAdapter.ArtHolder>(){
    class ArtHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.recylerViewText.text=artlist.get(position).artName.toString()
        holder.itemView.setOnClickListener{
            val action=ListFragmentDirections.actionListFragmentToDetailFragment("old",artlist.get(position).id)
            Navigation.findNavController(it).navigate(action)

        }
    }

    override fun getItemCount(): Int {
        return artlist.size
    }
}
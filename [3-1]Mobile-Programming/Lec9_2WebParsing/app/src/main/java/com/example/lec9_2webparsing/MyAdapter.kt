package com.example.lec9_2webparsing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter (val items: ArrayList<MyData>)
    :RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    interface OnItemClickListener{
        fun OnItemClick(holder : MyViewHolder , view : View, data : MyData, position : Int)
    }

    var itemClickListener : OnItemClickListener ?= null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var newsTitle:TextView = itemView.findViewById(R.id.newsTitle)

        init{
            itemView.setOnClickListener{
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition )
            }
        }
    }

    fun changeVisiblity(view : View){
        if(view.visibility == View.VISIBLE){
            view.visibility = View.GONE
        }else{
            view.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.newsTitle.text = items[position].newsTitle
    }
}



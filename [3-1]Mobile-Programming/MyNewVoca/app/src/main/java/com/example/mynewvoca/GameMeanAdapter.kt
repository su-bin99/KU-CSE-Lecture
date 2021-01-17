package com.example.mynewvoca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_gamemeaning.view.*
import java.util.*
import kotlin.collections.ArrayList

class GameMeanAdapter (val items:ArrayList<String>, val words : Map<String, String>, val answerIndex : Int)
    : RecyclerView.Adapter<GameMeanAdapter.MyViewHolder>(){
    val random = Random()
    var answerViewIndex : Int = random.nextInt(5)
    var array = arrayOf(0, 1, 2, 3, 4)

    interface OnItemClickListener{
        fun OnItemClick(holder : MyViewHolder , view : View, data : String, position : Int)
    }

    var itemClickListener : OnItemClickListener ?= null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.meanText

        init {
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_gamemeaning, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when(position){
            answerViewIndex ->{
                holder.textView.text= words.get(items[answerIndex])
                array.set(answerViewIndex,answerIndex )
            }
            else -> {
                var a : Int

                overlapCheck@ while(true){
                    a = random.nextInt(items.size)
                    if(a== answerIndex) continue@overlapCheck
                    arrayCheck@ for( i in 0..position-1){
                        if(array.get(i) == a) continue@overlapCheck
                    }
                    array.set(position, a)
                    break@overlapCheck
                }

                holder.textView.text= words.get(items[a])
            }
        }
    }
}
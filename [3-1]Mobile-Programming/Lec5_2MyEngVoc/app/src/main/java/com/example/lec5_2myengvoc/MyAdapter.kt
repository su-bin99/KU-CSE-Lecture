package com.example.lec5_2myengvoc

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val items:ArrayList<MyData>, val words : Map<String, String>)
    : RecyclerView.Adapter<MyAdapter.MyViewHolder>()
{
    interface OnItemClickListener{
        fun OnItemClick(holder : MyViewHolder , view : View, data : String, position : Int)
    }

    var itemClickListener : OnItemClickListener ?= null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView:TextView = itemView.findViewById(R.id.textView)
        var meanView:TextView = itemView.findViewById(R.id.meaningTextView)

        init{
            itemView.setOnClickListener{
                itemClickListener?.OnItemClick(this, it, items[adapterPosition].word, adapterPosition )
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

        holder.textView.text = items[position].word
        holder.meanView.text = words.get(items[position].word)
    }

    fun moveItem(oldPos : Int, newPos : Int){
        //drag&drop되었을때 기능

        val item = items.get(oldPos)
        items.removeAt(oldPos)
        items.add(newPos, item)
        //데이터에 해당하는 위치정보가 바뀐것!
        //-> 데이터가 이동됐음을 알려야 adapter에 해당하는 내용이 갱신되고 화면에 보여짐
        notifyItemMoved(oldPos, newPos)
    }

    fun removeItem(pos:Int){
        //스와이프 되었을때의 기능
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

}




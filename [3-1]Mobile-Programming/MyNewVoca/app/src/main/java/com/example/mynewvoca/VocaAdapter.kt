package com.example.mynewvoca

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.FileNotFoundException
import java.io.PrintStream
import java.util.*


class VocaAdapter (val items:ArrayList<String>, val words : Map<String, String>)
    : RecyclerView.Adapter<VocaAdapter.MyViewHolder>(){

    val mywordFile = "mywords4.txt"
    var context : Context ?= null
    interface OnItemClickListener{
        fun OnItemClick(holder : MyViewHolder , view : View, data : String, position : Int)
    }

    var itemClickListener : OnItemClickListener ?= null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView:TextView = itemView.findViewById(R.id.textView)
        var meanView:TextView = itemView.findViewById(R.id.meaningTextView)

        init {
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_voca, parent, false)
        context = parent.getContext()
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = items[position]
        holder.meanView.text = words.get(items[position])
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

    fun readFileScan(scan : Scanner, word:String) : String?{
        var str = ""
        var flag = false
        while(scan.hasNextLine()){
            var temp = scan.nextLine()
            if(temp == word){
                if(scan.hasNextLine()) scan.nextLine()
                flag = true
            }else{
                str+= temp
                if(scan.hasNextLine()) str+="\n"
            }
        }
        scan.close()
        if(flag == true) return str
        else  return null
    }

    fun readFiletoDelete(pos : Int) :String?{
        var wordtoDelete = items[pos]
        var readAll :String ?= null
        try{
            val scan2 = Scanner(context?.openFileInput(mywordFile))
            readAll = readFileScan(scan2, wordtoDelete)
        }catch(e: FileNotFoundException){
            null
        }

        if(readAll!=null){//첫번째 파일에 해당 단어가 있을 경우
            val output = PrintStream(context?.getApplicationContext()?.openFileOutput(mywordFile, Context.MODE_PRIVATE))
            output.print(readAll)
            removeItem(pos)
            return wordtoDelete
        }else{
            android.widget.Toast.makeText(context,"기본제공단어는 삭제할 수 업습니다. ", android.widget.Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
            return null
        }
    }
}


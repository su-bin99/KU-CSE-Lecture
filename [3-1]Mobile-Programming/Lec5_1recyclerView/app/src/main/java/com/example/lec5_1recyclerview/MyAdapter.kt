package com.example.lec5_1recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val items:ArrayList<MyData>)
    : RecyclerView.Adapter<MyAdapter.MyViewHolder>()
{
    //Adapter클래스 : RecyclerView에 표시될 View를 공급하는 클래스
    //RecyclerView.ViewHolder 클래스 : Recycler에 배치되는 View의 설정
        // onCreateViewHolder에 의해 생성됨

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView:TextView = itemView.findViewById(R.id.textView)
        //R.id.textView : raw.xml파일에 있는 textView위젯의 아이디
        //뷰의 layout정보를 저장해둔 xml파일에 따라서 객체화 해둔 view를 저장하는 클래스
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //뷰홀더( RecyclerView에 배치되는 VIew의 설정.. layout정보포함? ) 생성
        //inflate : raw.xml에 쓰여져있는 view이 정의로 실제 view객체로 만드는 역할
        val v = LayoutInflater.from(parent.context).inflate(R.layout.raw, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        //아이텡 갯수 반환
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //뷰홀더에 값을 설정
        holder.textView.text = items[position].name
        holder.textView.textSize = items[position].pt.toFloat()
    }

}




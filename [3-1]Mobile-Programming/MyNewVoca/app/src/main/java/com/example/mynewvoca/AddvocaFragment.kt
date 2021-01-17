package com.example.mynewvoca

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_addvoca.*
import kotlinx.android.synthetic.main.fragment_addvoca.view.*
import java.io.PrintStream

/**
 * A simple [Fragment] subclass.
 */
class AddvocaFragment : Fragment() {
    private var root: View? = null
    var dataArray : ArrayList<MyData> = ArrayList()
    val mywordFile = "mywords4.txt"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_addvoca, container, false)
        init()
        return root
    }

    private fun init(){

        root!!.addBtn.setOnClickListener {
            val word = root!!.vocEdit.text.toString()
            val mean = root!!.meanEdit.text.toString()
            if(word == "" && mean == ""){
                Toast.makeText(requireActivity(),"단어와 뜻이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }else if( word == "") {
                Toast.makeText(requireActivity(), "단어가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }else if(mean == ""){
                Toast.makeText(requireActivity(), "뜻이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }else{
                writeFile(word, mean)
                vocEdit.setText(null)
                meanEdit.setText(null)
            }
        }
    }
    fun writeFile(word: String, mean: String) {
        val output = PrintStream(getActivity()?.getApplicationContext()?.openFileOutput(mywordFile, Context.MODE_APPEND))
        output.println(word)
        output.println(mean)
        dataArray.add(MyData(word, mean))
        output.close()
        Toast.makeText(requireActivity(), word+"가 입력되었습니다.", Toast.LENGTH_SHORT).show()
    }






}

package com.example.lec8_1staticfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_text.*

/**
 * A simple [Fragment] subclass.
 */
class TextFragment : Fragment() {
    var imgNum = 1
    val data = arrayListOf<String>("ImageData 1", "ImageData 2", "ImageData 3")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text, container, false)
    }

    fun setActiveImage(img : Int ){
        textFragmentView.text = data[img-1]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val intent = requireActivity().intent
        if(intent!=null){
            val img = intent.getIntExtra("imgNum", -1)
            if(img != -1)
                setActiveImage(img)
            else
                setActiveImage(imgNum)
        }

    }

}

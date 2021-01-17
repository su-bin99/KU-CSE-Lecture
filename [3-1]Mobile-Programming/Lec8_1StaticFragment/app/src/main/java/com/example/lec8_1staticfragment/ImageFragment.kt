package com.example.lec8_1staticfragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.*

/**
 * A simple [Fragment] subclass.
 */
class ImageFragment : Fragment() {
    var imgNum = 1 //초기상태
    override fun onCreateView( //얘에 의해 view가 만들어짐
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false)
        //false니까 container에 붙지 않는 것
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imageView.setOnClickListener{
            //세로모드일때, 가로모드일때 두가지의 경우가 있음
            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
               //세로일때
                val i = Intent(activity, SecondActivity::class.java)
                i.putExtra("imgNum", imgNum)
                startActivity(i)
            }else if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                //가로일때 (이미 프래그 먼트가 있으니까 intent를 넘길 필요 없음 )
                val txtFrag = requireActivity().textFragment as TextFragment
                //requireActivity()로 액티비티를 접근하라 -> 멤버 접근가능
                txtFrag.setActiveImage(imgNum)
            }
        }

        fun checkOrientation(){
            if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //가로일때 사용하기 위한 함수
                val txtFrag = requireActivity().textFragment as TextFragment
                //requireActivity()로 액티비티를 접근하라 -> 멤버 접근가능
                txtFrag.setActiveImage(imgNum)
            }
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radioBtn1->{
                    imageView.setImageResource(R.drawable.img1)
                    imgNum = 1
                }
                R.id.radioBtn2->{
                    imageView.setImageResource(R.drawable.img2)
                    imgNum = 2
                }
                R.id.radioBtn3->{
                    imageView.setImageResource(R.drawable.img3)
                    imgNum = 3
                }
            }
            checkOrientation()
        }
    }

}

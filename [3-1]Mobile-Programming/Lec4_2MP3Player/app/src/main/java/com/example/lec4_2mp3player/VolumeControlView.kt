package com.example.lec4_2mp3player

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import kotlin.math.PI
import kotlin.math.atan2

@SuppressLint("AppCompatCustomView")
class VolumeControlView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {
    //터치를 하면 볼륨이미지가 돌아가도록
    //이 View관련 클래스에서 기능적으로 볼륨을 조절하는건X
    //그 기능은 인터페이스를 활용함

    var mx = 0.0f //이미지의 중점에서 터치한 지점까지의 x축거리
    var my = 0.0f
    var tx = 0.0f //터치한 x좌표
    var ty = 0.0f
    var angle = 180.0f  //터치한 지점의 각도


    //VolumeListener : 볼륨을 조정하는 인터페이스 !!
    var listener : VolumeListener ?= null

    public interface VolumeListener{
        public fun onChanged(angle:Float):Unit
    }

    public fun setVolumeListener(listener:VolumeListener):Unit{
        this.listener = listener
    }

    //볼륨조정과 관계 없이 View에관련한 코드
    fun getAngle(x1:Float, y1 :Float):Float{
        mx = x1-(width/ 2.0f)  //width는 이미지의 폭길이
        my = (height/2.0f) - y1
        return (atan2(mx, my) * 180.0f / PI).toFloat()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event!= null){
            tx = event.getX(0)
            ty = event.getY(0)
            angle = getAngle(tx, ty)
            invalidate() //onDraw함수를 재호출해서 그림을 다시그리도록
            listener?.onChanged(angle)
            return true
        }
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        //부모view에서 draw하기 전에 이미지를 돌려놓으면 됨
        canvas?.rotate(angle,width/2.0f, height/2.0f)
        super.onDraw(canvas)
    }
}
package com.example.lec4_2mp3player

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null
    var vol = 0.5f //현재상태의 볼륨이 맥스 , 1이고 0-1에서 조정가능
    var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init() {

        ButtonView.setVolumeListener(object : VolumeControlView.VolumeListener {
            override fun onChanged(angle: Float) {
                //angle : -180 ~ +180
                vol = if (angle > 0) {
                    angle / 360
                } else {
                    (360 + angle) / 360
                }
                mediaPlayer?.setVolume(vol, vol)
            }

        })

        playBtn.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.song)
                mediaPlayer?.setVolume(vol, vol)
            }
            mediaPlayer?.start()
            flag = true
        }

        pauseBtn.setOnClickListener {
            mediaPlayer?.pause()
            flag = false
        }

        stopBtn.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            //음악파일을 메모리에 가져와서 재생하는건데 stop했으니까 그 메모리를 해제시켜주는것
            mediaPlayer = null
            flag = false
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        Toast.makeText(this, "앱이 Pause됨 ", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        if (flag) {
            mediaPlayer?.start()
        }
        Toast.makeText(this, "앱이 실행됨 ", Toast.LENGTH_SHORT).show()
    }

}




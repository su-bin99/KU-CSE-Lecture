package com.example.lec7_3notification_pendingintent

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mytimepicker_dialog.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //5초 후에 알림이 발생하도록 하며, 알림을 선택하면 해당 액티비티로 화면 전환

    // < pendingIntent 사용 방법
//    채널 생성 -> 빌더로 notification 빌더객체 생성
//    ->빌더객체에 pendingIntent정보 등록
//    ->매니저객체가 notification채널 생성
//    ->빌더가 빌드한 notification으로 manager가 notify함

    var mymemo = ""
    var myhour = 0
    var mymin = 0
    var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //펜딩인텐트로 인텐트가 호출되었을 때
        val str = intent.getStringExtra("time")
        if(str != null)
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        init()
    }

    fun init(){
        calendarView.setOnDateChangeListener{view, year, month, dayOfMonth->
            val dialog = layoutInflater.inflate(R.layout.mytimepicker_dialog, null)
            val dlgTime = dialog.findViewById<TimePicker>(R.id.timePicker)
            val dlgMemo = dialog.findViewById<EditText>(R.id.editText)
            val dlgBuilder = AlertDialog.Builder(this)
            dlgBuilder.setView(dialog)
                .setPositiveButton("추가"){
                    _,_->
                    //추가 버튼을 누르면사용자가 입력한 정보들로 string
                    //2초뒤에 st
                    mymemo = dlgMemo.text.toString()
                    myhour = dlgTime.hour
                    mymin = dlgTime.minute
                    message = myhour.toString() +"시" + mymin.toString() + "분 : " + mymemo
                    val timerTask = object:TimerTask(){
                        override fun run() {
                            makeNotification()
                        }
                    }
                    val timer = Timer()
                    timer.schedule(timerTask, 2000) //2초 후에 timerTask실행
                    Toast.makeText(this, "알림이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("취소"){
                    _,_->
                }
                .show()
        }
    }

    fun makeNotification(){
        val channelId = "MyChannel"
        val channelName = "TimeCheckChannel"
        val notificationChannel = NotificationChannel(channelId, channelName,
            NotificationManager.IMPORTANCE_DEFAULT) //사운드O 헤더X
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor= Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("일정알림")
            .setContentText(message)
            .setAutoCancel(true)

        //화면전환
       val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("time", message)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(this,
            1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
        val notification = builder.build()
        manager.notify(10, notification)
    }
}

package com.example.lec9_1webview

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.CheckedInputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        button.setOnClickListener {
            val url = URL(editText.text.toString())
            val myTask = MyAsyncTask(this)
            myTask.execute(url) //Task동작시킴
        }
    }

    class MyAsyncTask(context:MainActivity): AsyncTask<URL, Unit, String>(){
        //메인액티비티의 멤버 접근 가능하도록 context정보도 인자로 가져옴
        private val activityReference = WeakReference(context) //이 액티비티를 약한 참조!

        override fun onPreExecute() {
            super.onPreExecute()
            val activity = activityReference.get()
            activity?.progressBar?.visibility = View.VISIBLE
        }
        override fun doInBackground(vararg params: URL?): String {
            var result = ""
            val connect = params[0]?.openConnection() as HttpURLConnection
            connect.connectTimeout = 4000
            connect.readTimeout = 4000
            connect.requestMethod = "GET"
            connect.connect()
            val responseCode = connect.responseCode
            if(responseCode == 200){ //정상적으로 로드되면 200반환
                result = streamToString(connect.inputStream)
                    //연결이 되었고 그 객체로부터 데이터를 읽어올 수 있음
                    //읽어오는 inputStream을 넘겨 주고 streamToString에서 읽어오도록
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if(activity == null || activity.isFinishing)
                return
            activity.textView.text = result
            activity.progressBar.visibility = View.GONE
        }

        fun streamToString(inputStream: InputStream):String{
            val bufferReader = BufferedReader(InputStreamReader(inputStream))
                //connertion객체가 InputStream으로 들어옴
                //바이트 코드들이 들어오니까 InputStreamReader로 우리가 읽을 수 객체로 변환
                //-> 버퍼에 담음
            var line : String
            var result = ""
            try{
                do{
                    line = bufferReader.readLine()
                    if(line != null){
                        result += line
                    }
                }while(line!= null)
                inputStream.close()
            }catch (ex:Exception){
               Log.e("error", "읽기 실패")
            }
            return result
        }
    }
}

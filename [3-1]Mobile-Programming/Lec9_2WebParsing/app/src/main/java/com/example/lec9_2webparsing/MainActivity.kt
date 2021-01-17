package com.example.lec9_2webparsing

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.lang.ref.WeakReference
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var adapter :MyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        startTask()
        //startXMLTask()
        //startJSONTask()
    }

    fun startTask(){
        val task = MyAsyncTesk(this)
        //task.execute(URL("https://www.daum.net"))
        task.execute(URL("https://kupis.konkuk.ac.kr/sugang/acd/cour/aply/CourInwonInqTime.jsp?ltYy=2020&ltShtm=B01014&sbjtId=8014"))

    }

    fun startXMLTask(){
        val task = MyAsyncTesk(this)
        task.execute(URL("http://fs.jtbc.joins.com//RSS/culture.xml"))
    }

    fun startJSONTask(){
        val task = MyAsyncTesk(this)
        task.execute(URL("http://api.icndb.com/jokes/random"))
    }

    private fun init(){
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing= true
            startTask()
            //startXMLTask()
            //startJSONTask()
        }

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        //구분선을 줌
        adapter = MyAdapter(ArrayList<MyData>())
        adapter.itemClickListener = object:MyAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: MyAdapter.MyViewHolder,
                view: View,
                data: MyData,
                position: Int
            ) {
                //url => intent
                val intent = Intent(ACTION_VIEW, Uri.parse(adapter.items[position].url))
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter //adapter 달아줌

    }

    class MyAsyncTesk(context:MainActivity) : AsyncTask<URL, Unit, Unit>(){
        private val activityReference = WeakReference(context)

        override fun doInBackground(vararg params: URL?): Unit {
            val activity = activityReference.get()
            activity?.adapter?.items?.clear()

            //<ul class = "list_txt">
            //   <li>
            //      < a href = "dlfksld" class = "link_txt @1-1"> 21대 국회 </a>

            //<TABLE CLASS = "table_bg">
            //     <tr>

            // 다음뉴스에서 http를 파싱할때 읽어오는 코드
            val doc = Jsoup.connect(params[0].toString()).get()
           // val headlines = doc.select("ul.list_txt>li>a")
            val headlines = doc.select("td.table_bg_white")
//            for(news in headlines){
//                activity?.adapter?.items?.add(MyData(news.text(), news.absUrl("href")))
//            }
            //activity?.adapter?.items?.add(MyData(headlines[1].text(), headlines[1].absUrl("href")))


            //JTBC뉴스에서 xml을 파싱할때 읽어오는 코드
//            val doc = Jsoup.connect(params[0].toString())
//                .parser(Parser.xmlParser()).get()
//            //디폴트가 html파서라서 xml파서라고 지정해줘야함
//            val headlines = doc.select("item")
//            for(news in headlines ){
//                activity?.adapter?.items?.add(MyData(news.select("title").text(),
//                    news.select("link").text()))
//            }

//            val doc = Jsoup.connect(params[0].toString()).ignoreContentType(true).get()
//                //ignore : JSON오브젝트 타입도 받아올 수 있음
//            Log.i("JSON : " ,doc.text())
//            val json = JSONObject(doc.text())
//            val joke = json.getJSONObject("value")
//            val jokeStr = joke.getString("joke")
//            Log.i("JSON Joke : ", jokeStr)


        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if(activity==null || activity.isFinishing)
                return
            activity.adapter.notifyDataSetChanged()
            activity.swipeRefresh.isRefreshing = false
        }


    }

}



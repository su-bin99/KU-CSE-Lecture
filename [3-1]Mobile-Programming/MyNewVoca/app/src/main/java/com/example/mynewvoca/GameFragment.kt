package com.example.mynewvoca

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_gamescore.*
import kotlinx.android.synthetic.main.dialog_gamescore.view.*
import kotlinx.android.synthetic.main.dialog_inputname.view.*
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import kotlinx.android.synthetic.main.fragment_game.view.recyclerView
import kotlinx.android.synthetic.main.fragment_voca.view.*
import java.io.FileNotFoundException
import java.io.PrintStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {
    private var root: View? = null
    val mywordFile = "mywords4.txt"
    val myscoreFile = "score6.txt"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_game, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        readFile()
        init()
    }

    val random = Random()
    var words = mutableMapOf<String, String>()
    var array = ArrayList<String>()
    var score : Int = 0
    var wordNum : Int = 0
    var answerIndex : Int = 0
    var scoreArray : MutableList<ScoreData> = mutableListOf()

    lateinit var adapter : GameMeanAdapter


    fun init(){
        wordNum = array.size
        root!!.wordnumText.text = wordNum.toString()
        root!!.scoreText.text = score.toString()
        newWord() //여기서 정답의 인덱스 초기화

        root!!.recyclerView.layoutManager = LinearLayoutManager(requireActivity(),
            LinearLayoutManager.VERTICAL, false)
        adapter = GameMeanAdapter(array, words, answerIndex)

        adapter.itemClickListener = object:GameMeanAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: GameMeanAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                when(position){
                    adapter.answerViewIndex ->{
                        score += 10
                        Toast.makeText (requireActivity(), "정답입니다.", Toast.LENGTH_SHORT).show()
                        init()
                    }
                    else ->{
                        Toast.makeText(requireActivity(), "GAME OVER", Toast.LENGTH_SHORT).show()
                        gameEnd()

                    }
                }
            }
        }
        root!!.recyclerView.adapter = adapter
        root!!.restartbtn.setOnClickListener {
            gameEnd()
        }
        root!!.scoreBoard?.setOnClickListener {
            val dialog = layoutInflater.inflate(R.layout.dialog_gamescore, null)
            val layoutArray = arrayListOf<LinearLayout>(dialog.layout_first,
                dialog.layout_second, dialog.layout_third, dialog.layout_fourth, dialog.layout_fifth)
            val scoreviewArray = arrayListOf(dialog.score1, dialog.score2, dialog.score3, dialog.score4, dialog.score5)
            val nameviewArray = arrayListOf(dialog.name1, dialog.name2, dialog.name3, dialog.name4, dialog.name5)

            dialog.button.setOnClickListener {
                dialog.noneScore.visibility = View.VISIBLE
                for( i in 0 until scoreArray.size) {
                    if(i<5){
                        layoutArray[i].visibility = View.GONE
                    }
                }
                scoreArray.clear()
                val output = PrintStream(context?.getApplicationContext()?.openFileOutput(myscoreFile, Context.MODE_PRIVATE))
                output.print("")
                output.close()

            }
            scoreArray = readscoreFile()

            var tempscoreArray = scoreArray.sortedByDescending { it.score.toInt() }
            if(tempscoreArray.isNotEmpty()){
                dialog.noneScore.visibility = View.GONE
                for( i in 0 until tempscoreArray.size) {
                    if(i<5){
                        layoutArray[i].visibility = View.VISIBLE
                        scoreviewArray[i].text = tempscoreArray[i].score
                        nameviewArray[i].text = tempscoreArray[i].name
                    }
                }
            }
            val dlgBuilder = AlertDialog.Builder(context!!)
            dlgBuilder.setView(dialog)
                .setPositiveButton("확인"){
                        _,_->
                }
                .show()
        }
    }

    fun newWord(){
        answerIndex = random.nextInt(array.size)
        root!!.wordText.text = array[answerIndex]
    }

    fun readFileScan(scan : Scanner){
        while(scan.hasNextLine()){
            val word = scan.nextLine()
            var meaning = ""
            if(scan.hasNextLine())
                meaning = scan.nextLine()
            words[word] = meaning
            array.add(word)
        }
        scan.close()
    }

    fun readFile(){
        array.clear()
        try{
            val scan2 = Scanner(getActivity()?.getApplicationContext()?.openFileInput(mywordFile))
            readFileScan(scan2)
        }catch(e: FileNotFoundException){
            null
        }
        val scan = Scanner(resources.openRawResource(R.raw.words))
        readFileScan(scan)
    }

    fun readscoreFile(): MutableList<ScoreData>  {
        val myscoreArray : MutableList<ScoreData> = mutableListOf()
        try{
            val scan = Scanner(getActivity()?.getApplicationContext()?.openFileInput(myscoreFile))
            while(scan.hasNextLine()){
                val strLine = scan.nextLine()
                val nameAndscore= strLine.split(" ")
                val readname = nameAndscore[0]
                val readscore = nameAndscore[1]
                myscoreArray.add(ScoreData(readname, readscore))
            }
            scan.close()
        }catch(e: FileNotFoundException){
            null
        }
        return myscoreArray
    }

    fun gameEnd(){
        val dialog2 = layoutInflater.inflate(R.layout.dialog_inputname, null)
        dialog2.textview_score2.text = score.toString()

        val dlgBuilder2 = AlertDialog.Builder(context!!)

        dlgBuilder2.setView(dialog2)
            .setPositiveButton("추가"){
                    _,_->
                var nametoAdd = dialog2.EditText_name.text.toString()
                val scoretoAdd = dialog2.textview_score2.text.toString()
                if(nametoAdd != ""){
                    scoreArray.add(ScoreData(nametoAdd, scoretoAdd))
                    val output = PrintStream(getActivity()?.getApplicationContext()?.openFileOutput(myscoreFile, Context.MODE_APPEND))
                    output.println(nametoAdd+" "+ scoretoAdd)
                    output.close()
                }else{
                    Toast.makeText(requireActivity(), "이름이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소"){
                    _,_->
            }
            .show()



        score = 0
        init()
    }

}

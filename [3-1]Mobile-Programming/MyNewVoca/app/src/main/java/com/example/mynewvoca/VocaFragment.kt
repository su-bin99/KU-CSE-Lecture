package com.example.mynewvoca

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_voca.view.*
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class VocaFragment : Fragment() {

    val mywordFile = "mywords4.txt"
    private var root: View? = null
    var words = mutableMapOf<String, String>()
    var array = ArrayList<String>()
    lateinit var adapter : VocaAdapter
    lateinit var tts : TextToSpeech
    var isTtsReady = false
    var addvocaNum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_voca, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()

        readFile()
        init()
    }

    private fun init(){
        tts = TextToSpeech(requireActivity(), TextToSpeech.OnInitListener {
            isTtsReady = true
            tts.language = Locale.UK
        })
        root!!.recyclerView.layoutManager = LinearLayoutManager(requireActivity(),
            LinearLayoutManager.VERTICAL, false)
        adapter = VocaAdapter(array, words)
        adapter.itemClickListener = object:VocaAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: VocaAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                var meanView: TextView = view.findViewById(R.id.meaningTextView)
                adapter.changeVisiblity(meanView)

                if(isTtsReady) {
                    tts.speak(data, TextToSpeech.QUEUE_ADD, null, null)
                    Toast.makeText(requireActivity(), words[data], Toast.LENGTH_SHORT).show()
                }
            }
        }
        root!!.recyclerView.adapter = adapter

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.DOWN or ItemTouchHelper.UP
            , ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){   //익명클래스로 만들기

            //simpleCallback객체를 달아줘야함!!!! (중요)
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos :Int = viewHolder.adapterPosition
                when(direction){

                    ItemTouchHelper.RIGHT->{
                        Toast.makeText(requireActivity(), "네이버 영어사전으로 \n이동합니다.", Toast.LENGTH_SHORT).show()
                        val pageUrl = "https://en.dict.naver.com/#/search?range=all&query="+adapter.items[pos]
                        val webpage = Uri.parse(pageUrl)
                        val webIntent = Intent(Intent.ACTION_VIEW, webpage)
                        startActivity(webIntent)
                    }

                    ItemTouchHelper.LEFT->{
                        val word = adapter.readFiletoDelete(pos)
                        if( word != null){
                            words.remove(word)
                        }
                    }
                }
            }


            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val deleteIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_black_24dp)
                val internetIcon  = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_public_black_24dp)

                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val inHeight :Int = deleteIcon!!.intrinsicHeight
                val inWidth :Int = deleteIcon!!.intrinsicWidth
                val Rightbackground = ColorDrawable()
                val Leftbackground = ColorDrawable()

                // Draw the red delete background
                Rightbackground.color = Color.RED
                Rightbackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                Rightbackground.draw(canvas)

                Leftbackground.color = Color.BLUE
                Leftbackground.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt(),
                    itemView.bottom
                )
                Leftbackground.draw(canvas)

                // Calculate position of delete icon
                val iconTop = itemView.top + (itemHeight - inHeight) / 2
                val iconMargin = (itemHeight - inHeight) / 2
                val RighticonLeft = itemView.right - iconMargin - inWidth
                val RighticonRight = itemView.right - iconMargin
                val iconBottom = iconTop + inHeight

                val LefticonLeft = itemView.left + iconMargin
                val LefticonRight = itemView.left + iconMargin + inWidth

                // Draw the delete icon
                deleteIcon.setBounds(RighticonLeft, iconTop, RighticonRight, iconBottom)
                deleteIcon.draw(canvas)

                internetIcon?.setBounds(LefticonLeft, iconTop, LefticonRight, iconBottom)
                internetIcon?.draw(canvas)

            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(root!!.recyclerView)

        root!!.searchbtn.setOnClickListener {
            val searchWord = root!!.search_edit.text.toString()
            if(searchWord != ""){
                if(!words.contains(searchWord)){
                    root!!.searchLayout.visibility = View.GONE
                    Toast.makeText (requireActivity(), "단어장에 존재하지 않는 단어입니다.", Toast.LENGTH_SHORT).show()
                }else{
                    root!!.search_edit.setText("")
                    root!!.searchLayout.visibility = View.VISIBLE
                    root!!.englishText.text = searchWord
                    root!!.koreanText.text = words.get(searchWord)
                }
            }else{
                Toast.makeText(requireActivity(), "단어가 입력되지 않았습니다. ", Toast.LENGTH_SHORT).show()
                root!!.searchLayout.visibility = View.GONE

            }
        }
    }



    fun readFileScan(scan : Scanner) : Int{
        var num = 0
        while(scan.hasNextLine()){
            val word = scan.nextLine()
            var meaning = " "
            if(scan.hasNextLine())
                meaning = scan.nextLine()
            words[word] = meaning
            array.add(word)
            num+=1
        }
        scan.close()
        return num
    }

    fun readFile(){
        array.clear()
        try{
            val scan2 = Scanner(getActivity()?.getApplicationContext()?.openFileInput(mywordFile))
            addvocaNum = readFileScan(scan2)
        }catch(e: FileNotFoundException){
            null
        }
        val scan = Scanner(resources.openRawResource(R.raw.words))
        readFileScan(scan)
    }

}



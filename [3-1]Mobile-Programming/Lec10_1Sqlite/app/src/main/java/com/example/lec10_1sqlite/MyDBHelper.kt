package com.example.lec10_1sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text

class MyDBHelper(val context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    //클래스 내부에서 디비 버전, 디비 이름, 등을 정해두고 사용함
    companion object{
        val DB_VERSION = 1
        val DB_NAME = "mydb.db"
        val TABLE_NAME = "Products"

        //DB에서의 속성 정보들
        val PID = "pid"
        val PNAME = "pname"
        val PQUANTITY = "pquantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
    //테이블 정보 만들어주면 됨
        val create_table = "create table if not exists " + TABLE_NAME+"("+
                PID + " integer primary key autoincrement, "+
                PNAME + " text, "+
                PQUANTITY + " integer )"
            //autoincrement 옵션 -> 자동으로 하나씩 증가시켜줌 ( id자동 보유 )

        db?.execSQL(create_table)
            //sql 실행시켜라 ( insert/delete/update도 이 구문으로 가능 )
        }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    //버전 정보 바뀌었을때 해야되는 것
        val drop_table = "drop table if exists " + TABLE_NAME
        db?.execSQL(drop_table) // 저 질의 구문을 실행시켜주는 함수 !
        onCreate(db)
    }

    fun insertProduct(product:Product): Boolean{
    //잘 삽입이 되면 true, 아니면 false
        val values = ContentValues()
        values.put(PNAME, product.pName)
        values.put(PQUANTITY, product.pQuantity)
        val db = this.writableDatabase
        if(db.insert(TABLE_NAME, null, values) > 0){
            //insert실패하면 -1반환함
            db.close()
            deleteEditText()
            return true
        }
        else{
            db.close()
            return false
        }
    }

    fun updateProduct(product:Product): Boolean{
        //id값을 가진 객체를 찾음
        val strsql = "select * from "+ TABLE_NAME + " where "+
                PID + " = \'" + product.pId + "\'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)

        //update시켜줌
        if(cursor.moveToFirst()){
            val values = ContentValues()
            values.put(PNAME , product.pName)
            values.put(PQUANTITY , product.pQuantity)
            db.update(TABLE_NAME, values, PID+"=?", arrayOf(product.pId.toString()))

            cursor.close()
            db.close()
            deleteEditText()
            return true
        }
        cursor.close()
        db.close()
        return false
    }

    fun deleteProduct(pId : String) : Boolean{
        val strsql = "select * from "+ TABLE_NAME + " where "+
                PID + " = \'" + pId + "\'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)
        if( cursor.moveToFirst()){
            db.delete(TABLE_NAME, PID+"=?", arrayOf(pId))
            cursor.close()
            db.close()
            deleteEditText()
            return true
        }
        cursor.close()
        db.close()
        return false
    }

    fun findProduct(name:String):Boolean{
        //select * from products where pname = '새우깡'
        // ' 이거 꼭 있어야됨
        val strsql = "select * from "+ TABLE_NAME + " where "+
                PNAME + " = \'" + name + "\'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.count != 0){
            showRecord(cursor)
            cursor.close()
            db.close()
            deleteEditText()
            return true
        }
        cursor.close()
        db.close()
        return false
    }

    fun findProduct2(name:String):Boolean{
        //select * from products where pname like '김%'
        // ' 이거 꼭 있어야됨
        val strsql = "select * from "+ TABLE_NAME + " where "+
                PNAME + " LIKE \'" + name + "%\'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.count != 0){
            showRecord(cursor)
            cursor.close()
            db.close()
            deleteEditText()
            return true
        }
        cursor.close()
        db.close()
        return false
    }

    fun getAllRecord(){
        val strsql = "select * from "+ TABLE_NAME
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
            //데이터를 찾거나 비어도 커서가 넘어옴
        if(cursor.count >=0 ){//0 이 아니면 뭔가를 가져온것!
            //테이블 레이아웃에 동적으로 생성해서 넣어줌!
            showRecord(cursor)
        }
        cursor.close()
        db.close()
    }

    fun showRecord(cursor : Cursor){
        //받아오는 커서정보로 출력해줌 ( 데이터는 커서를 통해서만 접근 가능 )
        cursor.moveToFirst()//처음위치로 이동
        val count = cursor.columnCount //colume을 몇개 가지고있느냐
        val rowcount = cursor.count // 가져온 데이터가 몇개냐

        //액티비티에 행을 만들어서 테이블레이아웃에 넣어줄것
        //생성시 context정보 -> 메인액티비티 접근 가능 -> 메인액티비티에서
        // -> 테이블 레이아웃 접근 가능 -> 테이블 레이아웃에서 행을 만들어줄것
        val activity = context as MainActivity //메인액티비티 접근방법
        context.tableLayout.removeAllViewsInLayout() //테이블 레이아웃 내의 모든 view제거

        /////////////////////////////////////////////////////////
        //컬럼 타이틀 만들기

        val tablerow = TableRow(activity)
        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT, count.toFloat())
        tablerow.layoutParams = rowParam // 행을 하나 구성한 것
        val viewParam = TableRow.LayoutParams(0, 100, 1f)
        for( i in 0 until count){
            val textView = TextView(activity)
            textView.layoutParams = viewParam
            textView.text = cursor.getColumnName(i)
            textView.setBackgroundColor(Color.LTGRAY)
            textView.textSize = 15.0f
            textView.gravity = Gravity.CENTER
            tablerow.addView(textView)
        }
        activity.tableLayout.addView(tablerow)

        /////////////////////////////////////////////////////////////
        //실제 레코드 읽어오기
        if( rowcount > 0){
            do{
                val row = TableRow(activity)
                row.layoutParams = rowParam
                row.setOnClickListener {
                    for(i in 0 until count){
                        val txtView = row.getChildAt(i) as TextView
                        //row의 child객체를 가져옴
                        when(txtView.tag){
                            0->activity.pIdEdit.setText(txtView.text)
                            1->activity.pNameEdit.setText(txtView.text)
                            2->activity.pQuantityEdit.setText(txtView.text)
                        }
                    }
                }
                for( i in 0 until count){
                    val textView = TextView(activity)
                    textView.layoutParams = viewParam
                    textView.text = cursor.getString(i)
                    textView.textSize = 13.0f
                    textView.setTag(i) //태그로 구분할 수 있도록
                    row.addView(textView)
                }
                activity.tableLayout.addView(row)
            }while(cursor.moveToNext())

        }
    }

    fun deleteEditText(){
        val activity = context as MainActivity
        activity.pIdEdit.setText(null)
        activity.pNameEdit.setText(null)
        activity.pQuantityEdit.setText(null)
    }
}
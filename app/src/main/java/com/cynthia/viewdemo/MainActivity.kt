package com.cynthia.viewdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cynthia.viewdemo.widget.slidecards.SlideInterface
import com.cynthia.viewdemo.widget.slidecards.SlideViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_card.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scvs.setHelper(object : SlideInterface {
            override fun initLayout(parent: ViewGroup?): SlideViewHolder {
                val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.item_card, parent, false)
                return SlideViewHolder(view)
            }

            override fun bindData(holder: SlideViewHolder): View {
                holder.itemView.text1.text = Random().nextInt(20).toString()
                return holder.itemView
            }

        })
//        cet_demo.registerListener {
//            it.setText("")
//        }

//        bcv_data.startAnim()
    }

//    override fun onPause() {
//        super.onPause()
//        bcv_data.stopAnim()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        bcv_data.stopAnim()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        bcv_data.stopAnim()
//    }
}

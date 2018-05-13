package com.example.shuzo.androidthingskhr3hv

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView

open class ServoCardRecyclerAdapter(val servoIDs: List<Int>) : RecyclerView.Adapter<ServoCardRecyclerAdapter.ServoCardVH>() {

    private lateinit var view: View

    class ServoCardVH(view: View) : RecyclerView.ViewHolder(view) {
        val seekBar: SeekBar = view.findViewById(R.id.degreeSeekbar)
        val textId: TextView = view.findViewById(R.id.textID)
        val textResult: TextView = view.findViewById(R.id.resultText)
    }

    protected open var onSeekBarProgressChange = { _: SeekBar?, _: Int, _: Boolean, _: Int, _: TextView -> }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ServoCardVH {
        view = View.inflate(parent!!.context, R.layout.layout_card, null)
        return ServoCardVH(view)
    }

    // リストのデータとlayoutを紐付ける
    override fun onBindViewHolder(holder: ServoCardVH, position: Int) {
        holder.textId.text = servoIDs[position].toString()
        holder.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onSeekBarProgressChange(seekBar, progress, fromUser, servoIDs[holder.adapterPosition], holder.textResult)
            }
        })
    }

    override fun getItemCount(): Int = servoIDs.size

}
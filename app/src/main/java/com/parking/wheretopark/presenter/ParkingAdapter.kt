package com.parking.wheretopark.presenter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.parking.wheretopark.R
import com.parking.wheretopark.model.Sample
import com.squareup.picasso.Picasso
import java.util.ArrayList

/*
* This is Adapter class used for populate view with informaton collected from server, such us title, description, image..
* */

class ParkingAdapter(val context: Context, var itemList: ArrayList<Sample>):
        RecyclerView.Adapter<ParkingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.items, parent , false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = itemList[position].title
        holder.txtDescription.text = itemList[position].description
        holder.bindItems(itemList[position])
        var imageUri = itemList[position].image
        imageUri = imageUri!!.replace("\\s".toRegex(), "")
        Picasso.with(context).load(imageUri).into(holder.image)
        holder.constrainLayout.setOnClickListener {
            listener!!.onClick(position)
        }    }


    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setList(newList: ArrayList<Sample>) {
        this.itemList = newList
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<Sample> = itemList

    override fun getItemCount() = itemList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.imageView)!!
        val txtTitle = itemView.findViewById<TextView>(R.id.textView)!!
        val txtDescription = itemView.findViewById<TextView>(R.id.textVievDescription)!!
        val constrainLayout = itemView.findViewById<ConstraintLayout>(R.id.main)!!

        fun bindItems(user: Sample) {
            val textViewName = itemView.findViewById<TextView>(R.id.textView)
            textViewName.text = user.title
           }

    }

}
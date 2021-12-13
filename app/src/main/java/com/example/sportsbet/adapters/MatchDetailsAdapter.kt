package com.example.sportsbet.adapters

import android.app.Application
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbet.MainActivity
import com.example.sportsbet.MyApplication
import com.example.sportsbet.R
import com.example.sportsbet.interfaces.Communication
import com.example.sportsbet.models.MatchDetailsModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.recycle_view_list_row.view.*

class MatchDetailsAdapter(private val modelList:  MutableList<MatchDetailsModel>,private val responder: Communication) : RecyclerView.Adapter<MatchDetailsAdapter.ModelViewHolder>(){



    class ModelViewHolder(view: View,communication: Communication) : RecyclerView.ViewHolder(view){
        private var myApp : MyApplication = MyApplication()
        private var firebaseAnalytics: FirebaseAnalytics = myApp.getFirebase()
        private val response: Communication = communication
        val teams = view.teams
        val underPoint = view.tvUnder
        val overPoint = view.tvOver
        val underPrice = view.btnUnder
        val overPrice = view.btnOver

        fun bindItems(item : MatchDetailsModel){
            teams.setText(item.home_team + "-" + item.away_team)
            underPoint.setText(item.points + " UNDER")
            overPoint.setText(item.points + " OVER")
            underPrice.setText(item.first_price)
            overPrice.setText(item.second_price)
            underPrice.isSelected = item.checkedUnder
            overPrice.isSelected = item.checkedOver


            underPrice.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    underPrice.isSelected = !underPrice.isSelected
                    if(overPrice.isSelected) overPrice.isSelected = false
                    item.checkedUnder = !item.checkedUnder
                    item.checkedOver = false
                    if(underPrice.isSelected){
                        var bundle =  Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,item.id)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,item.home_team+"-"+item.away_team)
                        bundle.putString(FirebaseAnalytics.Param.PRICE,item.first_price)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,item.points+" under")
                      firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART,bundle)
                    } else {
                        var bundle =  Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,item.id)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,item.home_team+"-"+item.away_team)
                        bundle.putString(FirebaseAnalytics.Param.PRICE,item.first_price)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,item.points+" under")
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART,bundle)
                    }

                    sendItem(item,underPrice.isSelected)
                    returnChanges(item)

                }
            })
            overPrice.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    overPrice.isSelected = !overPrice.isSelected
                    if(underPrice.isSelected) underPrice.isSelected = false
                    item.checkedOver = !item.checkedOver
                    item.checkedUnder = false
                    if(overPrice.isSelected){
                        var bundle =  Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,item.id)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,item.home_team+"-"+item.away_team)
                        bundle.putString(FirebaseAnalytics.Param.PRICE,item.second_price)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,item.points+" over")
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART,bundle)
                    } else {
                        var bundle =  Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,item.id)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,item.home_team+"-"+item.away_team)
                        bundle.putString(FirebaseAnalytics.Param.PRICE,item.second_price)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,item.points+" over")
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART,bundle)
                    }

                    sendItem(item,overPrice.isSelected)
                    returnChanges(item)

                }
            })
        }

        fun sendItem(item: MatchDetailsModel,check: Boolean){
            response.addToList(item,check)
        }
        fun returnChanges(item:MatchDetailsModel){
            response.changeItems(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_list_row,parent,false)
        return ModelViewHolder(view,responder)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bindItems(modelList.get(position))
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}
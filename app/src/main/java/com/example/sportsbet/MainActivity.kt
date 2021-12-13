package com.example.sportsbet

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.sportsbet.adapters.MatchDetailsAdapter
import com.example.sportsbet.interfaces.Communication
import com.example.sportsbet.models.MatchDetailsModel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.math.RoundingMode

class MainActivity : AppCompatActivity(),Communication {

    private var models: ArrayList<MatchDetailsModel> = arrayListOf<MatchDetailsModel>()
    private var total: ArrayList<MatchDetailsModel> = arrayListOf<MatchDetailsModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSports()
        val editText: EditText = findViewById(R.id.editText)
        editText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                Log.d(TAG, "afterTextChanged: " +p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "beforeTextChanged: ")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filter(p0.toString())
            }
        })
    }

    override fun onResume() {
        super.onResume()

    }

    override fun addToList(match: MatchDetailsModel, check: Boolean) {

        if(check){
            if(total.size > 0){
                for (i in 0 until total.size){
                    if(total[i].id.equals(match.id)) {
                        total.removeAt(i)
                        break
                    }
                }
                total.add(match)
            }else total.add(match)
        }else {
            if(total.size > 0){
                for (i in 0 until total.size){
                    if(total[i].id.equals(match.id)) {
                        total.removeAt(i)
                        break
                    }
                }
            }
        }

        if(total.size > 0){
            var odds: Double= 1.0
            for (i in 0 until total.size){
                if(total[i].checkedUnder) odds = odds * (total[i].first_price).toDouble()
                if(total[i].checkedOver) odds = odds * (total[i].second_price).toDouble()
            }
            odds = odds.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            updateUI(total.size,odds)

        } else updateUI(0,1.00)
    }

    override fun changeItems(match: MatchDetailsModel) {
        for (i in 0 until models.size){
            if(models[i].id.equals(match.id)) {
                models.removeAt(i)
                break
            }
        }
        models.add(match)
    }

    fun updateUI(totalMatch: Int, totalOdds: Double){
        val textView: TextView = findViewById(R.id.total)
        textView.text = "$totalMatch matches: $totalOdds"

    }
    fun getSports(){
        val queue = Volley.newRequestQueue(this)
        val url = getString(R.string.get_sports) + "?regions=eu&markets=totals&" + "apiKey=" + getString(R.string.api_key)

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String>{response ->
                    initRecyclerView(response)
                },
                Response.ErrorListener{ Log.d(TAG, "getSports: error")})

        queue.add(stringRequest)

    }

    fun initRecyclerView(response: String?){
        val jsonResponse = "{\"data\":" +response+"}"
        val jsonObject = JSONObject(jsonResponse)
        val jsonArray = jsonObject.optJSONArray("data")

        for(i in 0 until jsonArray.length()){

            val jsonObject = jsonArray.getJSONObject(i)
            val id = jsonObject.optString("id")
            val home_team = jsonObject.optString("home_team")
            val away_team = jsonObject.optString("away_team")
            val jsonArrayBookmarkers = jsonObject.optJSONArray("bookmakers")
            val jsonArrayFirstBookmarker = jsonArrayBookmarkers.getJSONObject(0)
            val jsonArrayMarkets = jsonArrayFirstBookmarker.optJSONArray("markets")
            val jsonArrayFirstMarket = jsonArrayMarkets.getJSONObject(0)
            val jsonArrayOutcomes = jsonArrayFirstMarket.optJSONArray("outcomes")
            val points = jsonArrayOutcomes.getJSONObject(0).optString("point")
            val firstPrice = jsonArrayOutcomes.getJSONObject(0).optString("price")
            val secondPrice = jsonArrayOutcomes.getJSONObject(1).optString("price")

            models.add(MatchDetailsModel(id,home_team,away_team,points,firstPrice,secondPrice,false,false))


        }

        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter = MatchDetailsAdapter(models,this)

    }

    fun filter(text: String){
        val searchModel: ArrayList<MatchDetailsModel> = arrayListOf<MatchDetailsModel>()

        for(i in 0 until models.size){
            if(text.equals("")){
                searchModel.add(models[i])
            }else if(models[i].home_team.lowercase().contains(text) || models[i].away_team.lowercase().contains(text)){
                searchModel.add(models[i])
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter = MatchDetailsAdapter(searchModel,this)

    }

}
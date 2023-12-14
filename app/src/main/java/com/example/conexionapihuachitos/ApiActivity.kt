package com.example.conexionapihuachitos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import org.json.JSONException
import org.json.JSONObject

class ApiActivity : AppCompatActivity(),ApiCallback {
    private lateinit var listDataFromJson : ListView
    private lateinit var getRequestButton : Button
    private lateinit var adapter : ArrayAdapter<String>
    private lateinit var listData : MutableList<String>
    private  var URL : String = "https://huachitos.cl/api/animales"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)

        listDataFromJson = findViewById(R.id.listViewDataFromJson)
        getRequestButton = findViewById(R.id.getRequest)

        listData = mutableListOf(
            getString(R.string.api_instruction)
        )

        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, // Built-in layout for simple items
            listData
        )
        listDataFromJson.adapter = adapter

        getRequestButton.setOnClickListener{
            val apiRequestTask = ApiTask(this)
            apiRequestTask.execute(URL)
        }
    }

    override fun onRequestComplete(result: String) {
        //Update GUI
        listData = processingData(result)
        Log.i("APIRestActivity",listData.toString())
        adapter.clear()
        adapter.addAll(listData)
        adapter.notifyDataSetChanged()

    }

    fun processingData(result: String) : MutableList<String> {
        var list : MutableList<String> = mutableListOf()
        try {
            // Parse the JSON string into a JSONObject
            val jsonObject = JSONObject(result)

            // Access the "data" array
            val dataArray = jsonObject.getJSONArray("data")

            // Iterate through the array and access individual objects
            for (i in 0 until dataArray.length()) {
                val dataObject = dataArray.getJSONObject(i)

                val firstName = dataObject.getString("nombre")
                Log.i("APIRestActivity",firstName.toString())
                list.add(firstName)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            list.add(getString(R.string.api_error))
        }
        return list.toMutableList()
    }
}
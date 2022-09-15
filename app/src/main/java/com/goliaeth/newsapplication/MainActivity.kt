package com.goliaeth.newsapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.goliaeth.newsapplication.databinding.ActivityMainBinding
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private var pageNumber = 1
    private var list = mutableListOf<Data>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.searchButton.setOnClickListener {
            list = mutableListOf()
            sendRequest()
        }

        binding.loadMoreButton.setOnClickListener {
            pageNumber += 1
            sendRequest()
        }

    }

    private fun getUrl(): String {
        val word = binding.searchEditText.text
        val apiKey = "701f35c8-af4a-4131-9906-f97e9fd848c9"
        val pageSize = 10
        return "https://content.guardianapis.com/$word?page=$pageNumber&page-size=$pageSize&api-key=$apiKey"
    }

    private fun extractJSON(response: String) {
        val jsonObject = JSONObject(response)
        val jsonResponseBody = jsonObject.getJSONObject("response")
        val results = jsonResponseBody.getJSONArray("results")

        for (i in 0..9) {
            val item = results.getJSONObject(i)
            val webTitle = item.getString("webTitle")
            val webUrl = item.getString("webUrl")
            val data = Data(webTitle, webUrl)
            list.add(data)
        }

        val adapter = NewsAdapter(list)
        binding.listView.adapter = adapter

    }

    private fun sendRequest() {
        val url = getUrl()

        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    extractJSON(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            })

        queue.add(stringRequest)
    }

}
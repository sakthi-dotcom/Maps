package com.example.hepto.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hepto.R
import com.example.hepto.adapter.CommentAdapter
import com.example.hepto.model.Comment
import com.example.hepto.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fetchComments()
    }

    private fun fetchComments() {
        val apiService = RetrofitInstance.create()
        apiService.getComments().enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (response.isSuccessful) {
                    val comments = response.body() ?: emptyList()
                    adapter = CommentAdapter(comments)
                    recyclerView.adapter = adapter
                } else {
                    Log.d("Something went wrong in API ", response.message())
                }
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {

                Toast.makeText(context, "Failed to load comments", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

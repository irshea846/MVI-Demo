package com.example.mvi_architecture.uis.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvi_architecture.data.api.ApiHelperImpl
import com.example.mvi_architecture.data.api.RetrofitBuilder
import com.example.mvi_architecture.data.model.User
import com.example.mvi_architecture.util.ViewModelFactory
import com.example.mvi_architecture.uis.adapter.MainAdapter
import com.example.mvi_architecture.uis.intent.DataIntent
import com.example.mvi_architecture.uis.viewmodel.DataViewModel
import com.example.mvi_architecture.uis.viewstate.DataState
import com.example.mvi_architecture.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var dataViewModel: DataViewModel
    private var mainAdapter: MainAdapter = MainAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setupUI()
        setupViewModel()
        observeViewModel()
        setupClicks()
    }

    private fun setupClicks() {
        mainBinding.buttonShowUsers.setOnClickListener {
            lifecycleScope.launch {
                dataViewModel.dataIntent.send(DataIntent.FetchData)
            }
        }
    }


    private fun setupUI() {
        mainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.run {
            addItemDecoration(
                DividerItemDecoration(
                    mainBinding.recyclerView.context,
                    (mainBinding.recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
            adapter = mainAdapter
        }
    }


    private fun setupViewModel() {
        dataViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(
                    RetrofitBuilder.apiService
                )
            )
        )[DataViewModel::class.java]
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            dataViewModel.dataState.collect {
                when (it) {
                    is DataState.Inactive -> {
                        Log.d("Inactive","Inactive State")
                    }
                    is DataState.Loading -> {
                        mainBinding.buttonShowUsers.visibility = View.GONE
                        mainBinding.progressBar.visibility = View.VISIBLE
                    }

                    is DataState.ResponseData -> {
                        mainBinding.progressBar.visibility = View.GONE
                        mainBinding.buttonShowUsers.visibility = View.GONE
                        renderList(it.data.data)
                    }
                    is DataState.Error -> {
                        mainBinding.progressBar.visibility = View.GONE
                        mainBinding.buttonShowUsers.visibility = View.VISIBLE
                        Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun renderList(users: List<User>) {
        mainBinding.recyclerView.apply {
            mainAdapter.addData(users)
            visibility = View.VISIBLE
            //users.let { listOfUsers -> listOfUsers.let { adapter.addData(it) } }
            //adapter.notifyDataSetChanged()
        }
    }
}

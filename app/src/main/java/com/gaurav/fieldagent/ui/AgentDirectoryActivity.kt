package com.gaurav.fieldagent.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gaurav.fieldagent.R
import com.gaurav.fieldagent.data.local.AppDatabase
import com.gaurav.fieldagent.data.remote.ApiService
import com.gaurav.fieldagent.data.repository.UserRepositoryImpl
import com.gaurav.fieldagent.domain.usecase.GetUsersUseCase
import com.gaurav.fieldagent.domain.usecase.SearchUsersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AgentDirectoryActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var userAdapter: UserAdapter
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agent_directory)
        val mainLayout = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userRecyclerView = findViewById(R.id.user_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        searchView = findViewById(R.id.search_view)

        setupViewModel()
        setupRecyclerView()
        getUsers()
        setupSwipeToRefresh()
        setupSearchView()
    }

    private fun setupViewModel() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val appDatabase = AppDatabase.getDatabase(this)
        val userRepository = UserRepositoryImpl(apiService, appDatabase)
        val getUsersUseCase = GetUsersUseCase(userRepository)
        val searchUsersUseCase = SearchUsersUseCase(userRepository)
        val viewModelFactory = ViewModelFactory(getUsersUseCase, searchUsersUseCase)

        viewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            val intent = Intent(this, AgentProfileActivity::class.java).apply {
                putExtra("USER_EXTRA", user)
            }
            startActivity(intent)
        }
        userRecyclerView.adapter = userAdapter

        userAdapter.addLoadStateListener { loadState ->
            progressBar.visibility = if (loadState.refresh is LoadState.Loading) View.VISIBLE else View.GONE
            swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading
        }
    }

    private fun getUsers() {
        lifecycleScope.launch {
            viewModel.getUsers().collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    private fun searchUsers(query: String) {
        lifecycleScope.launch {
            viewModel.searchUsers(query).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    if (!newText.isNullOrBlank()) {
                        searchUsers(newText)
                    } else {
                        getUsers()
                    }
                }
                return true
            }
        })
    }

    private fun setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            userAdapter.refresh()
        }
    }
}
package com.gaurav.fieldagent.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.facebook.shimmer.ShimmerFrameLayout
import com.gaurav.fieldagent.R
import com.gaurav.fieldagent.data.local.AppDatabase
import com.gaurav.fieldagent.data.remote.ApiService
import com.gaurav.fieldagent.data.repository.UserRepositoryImpl
import com.gaurav.fieldagent.domain.usecase.GetUsersUseCase
import com.gaurav.fieldagent.domain.usecase.SearchUsersUseCase
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.gaurav.fieldagent.utils.NetworkStatus
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach

class AgentDirectoryActivity : AppCompatActivity() {


    private lateinit var viewModel: UserViewModel
    private lateinit var userAdapter: UserAdapter
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private lateinit var shimmerViewContainer: ShimmerFrameLayout
    private var searchJob: Job? = null
    private lateinit var toolbar: MaterialToolbar
    private lateinit var networkStatusTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_agent_directory)
        val mainLayout = findViewById<View>(R.id.main)
//        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        userRecyclerView = findViewById(R.id.user_recycler_view)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        searchView = findViewById(R.id.search_view)
        shimmerViewContainer = findViewById(R.id.shimmer_view_container)
        networkStatusTextView = findViewById(R.id.network_status_text_view)

        setupViewModel()
        setupRecyclerView()
        getUsers()
        setupSwipeToRefresh()
        setupSearchView()
        observeNetworkStatus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.directory_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        val networkStatus = NetworkStatus(this)
        val viewModelFactory = ViewModelFactory(getUsersUseCase, searchUsersUseCase, networkStatus)

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
            if (loadState.refresh is LoadState.Loading) {
                shimmerViewContainer.startShimmer()
                shimmerViewContainer.visibility = View.VISIBLE
                userRecyclerView.visibility = View.GONE
            } else {
                shimmerViewContainer.stopShimmer()
                shimmerViewContainer.visibility = View.GONE
                userRecyclerView.visibility = View.VISIBLE

                // Check for empty result from local search
                if (loadState.refresh is LoadState.NotLoading && userAdapter.itemCount == 0 && searchView.query.toString().isNotBlank()) {
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(500) // Debounce for remote search
                        viewModel.searchRemoteUsers(searchView.query.toString()).collectLatest {
                            userAdapter.submitData(it)
                        }
                    }
                }
            }
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

    private fun search(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchLocalUsers(query).collectLatest {
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
                if (!newText.isNullOrBlank()) {
                    search(newText)
                } else {
                    getUsers()
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

    private fun observeNetworkStatus() {
        lifecycleScope.launch {
            viewModel.isOnline.collect { isOnline ->
                if (isOnline) {
                    networkStatusTextView.text = "Back Online"
                    networkStatusTextView.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark))
                    lifecycleScope.launch {
                        delay(2000)
                        networkStatusTextView.visibility = View.GONE
                    }
                } else {
                    networkStatusTextView.text = "You are offline"
                    networkStatusTextView.setBackgroundColor(resources.getColor(android.R.color.black))
                    networkStatusTextView.visibility = View.VISIBLE
                }
            }
        }
    }
}
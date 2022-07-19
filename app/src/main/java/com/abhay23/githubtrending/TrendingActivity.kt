package com.abhay23.githubtrending

import RepositoryResponse
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.abhay23.githubtrending.network.GithubService
import com.google.android.material.appbar.MaterialToolbar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


class TrendingActivity : AppCompatActivity() {
    private lateinit var successView: ViewGroup
    private lateinit var loadingView: View
    private lateinit var errorView: ViewGroup
    private lateinit var flContainer: ViewGroup
    private lateinit var repositoriesAdapter: RepositoriesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trending)
        flContainer = findViewById<FrameLayout>(R.id.fl_layout_container)
        loadingView = createLoadingView()
        successView = createSuccessView()
        errorView = createErrorView()
        setupRetryButton(errorView)
        flContainer.addView(loadingView)
        setupSuccessRecyclerView(successView)
        setupSuccessSwipeRefreshLayout(successView)
        setupToolbar()
        getRepositories()
    }

    private fun setupRetryButton(errorView: ViewGroup) {
        errorView.findViewById<Button>(R.id.btn_retry).setOnClickListener {
            flContainer.removeView(errorView)
            flContainer.addView(loadingView)
            getRepositories()
        }
    }

    private fun setupToolbar() {
        val mtToolbar = findViewById<MaterialToolbar>(R.id.mt_toolbar)
        mtToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sort_by_name -> {
                    repositoriesAdapter.sortRepositoriesByName()
                    true
                }
                R.id.action_sort_by_stars -> {
                    repositoriesAdapter.sortRepositoriesByStars()
                    true
                }
                else -> false
            }
        }
    }

    private fun getRepositories() {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
        val retrofit = Retrofit.Builder()
            .client(okHttpClient.build())
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val githubService = retrofit.create(GithubService::class.java)
        val repositoriesForOrganization: Call<List<RepositoryResponse>> =
            githubService.getRepositoriesForOrganization("octokit")

        swipeRefreshLayout.isRefreshing = true
        repositoriesForOrganization.enqueue(object : Callback<List<RepositoryResponse>> {
            override fun onResponse(
                call: Call<List<RepositoryResponse>>,
                response: Response<List<RepositoryResponse>>
            ) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val repositories = response.body()?.map {
                        it.toRepository()
                    }
                    repositoriesAdapter.updateData(repositories!!)
                    flContainer.removeView(loadingView)
                    when {
                        successView.parent != null -> {
                            (successView.parent as ViewGroup).removeView(successView)
                        }
                    }
                    flContainer.addView(successView)
                } else {
                    showErrorView()
                }
            }

            override fun onFailure(call: Call<List<RepositoryResponse>>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                showErrorView()
            }
        })
    }

    private fun showErrorView() {
        flContainer.removeView(loadingView)
        flContainer.removeView(successView)
        flContainer.addView(errorView)
    }

    private fun createErrorView(): ViewGroup {
        return layoutInflater.inflate(R.layout.trending_error, flContainer, false) as ViewGroup
    }

    private fun createSuccessView(): ViewGroup {
        return layoutInflater.inflate(R.layout.trending_success, flContainer, false) as ViewGroup
    }

    private fun createLoadingView(): View {
        return layoutInflater.inflate(R.layout.trending_loading, flContainer, false)
    }


    private fun setupSuccessRecyclerView(successView: ViewGroup) {
        val recyclerView = successView.findViewById<RecyclerView>(R.id.rv_trending)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        repositoriesAdapter = RepositoriesAdapter()
        recyclerView.adapter = repositoriesAdapter
    }

    private fun setupSuccessSwipeRefreshLayout(successView: ViewGroup) {
        swipeRefreshLayout =
            successView.findViewById(R.id.swipe_trending_success_layout)
        swipeRefreshLayout.setOnRefreshListener {
            getRepositories()
        }
    }
}

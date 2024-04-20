package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.map.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private fun refreshData() {
        recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_settings_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        viewModel.getSession().observe(this@MainActivity) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                finish()
            } else {

                getDataStory(user.token)

            }
        }
        setupView()
        setupAction()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    //
    private fun getDataStory(token: String?) {
        showLoading(true)

        lifecycleScope.launch {

            if (isNetworkAvailable(this@MainActivity)) {
                try {
                    val adapter = ListAdapter(this@MainActivity)
                    binding.recyclerView.adapter = adapter
                    viewModel.story(token).observe(this@MainActivity, { pagingData ->
                        adapter.submitData(lifecycle, pagingData)

                    })
                    delay(300)
                    swipeRefreshLayout.isRefreshing = false
                    showLoading(false)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "gagal", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }


    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected == true
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupAction() {
        binding.imageBtnLogout.setOnClickListener {
            viewModel.logout()
        }
        binding.imageBtnAdd.setOnClickListener {
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity)

            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent, optionsCompat.toBundle())
        }
        binding.imageBtnMap.setOnClickListener {
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity)

            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent, optionsCompat.toBundle())
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
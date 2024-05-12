package com.example.myapplication

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Constants.PERMISSIONS_REQUEST_READ_CONTACTS
import com.example.myapplication.adapter.ArticleAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), ItemListListener {

    private var countriesAdapter: ArticleAdapter = ArticleAdapter(this, arrayListOf())
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.activity_main,
                null,
                false
            )

        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.articleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countriesAdapter
        }

        if (ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermission()
        } else {
            observeViewModel()
        }
    }

    /**requesting permission for notifications*/
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                POST_NOTIFICATIONS
            )
        ) {
            // Display a dialog explaining why the permission is needed
            AlertDialog.Builder(this)
                .setTitle("Notifications Permission")
                .setMessage("This app needs access to post notifications to function properly.")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(POST_NOTIFICATIONS),
                        PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create().show()
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(
                this, arrayOf(POST_NOTIFICATIONS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                observeViewModel()
            } else {
                // Permission denied
                this.finishAffinity()
            }
        }
    }

    private fun observeViewModel() {
        mainViewModel.articles.observe(this) { articles ->
            articles?.let {
                countriesAdapter.updateArticles(it)
                binding.progressBar.visibility = View.GONE
                binding.errorText.visibility = View.GONE
                binding.articleList.visibility = View.VISIBLE
            }
        }
        mainViewModel.error.observe(this) { isError ->
            isError?.let {
                binding.errorText.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    binding.progressBar.visibility = View.GONE
                    binding.articleList.visibility = View.GONE
                }
            }
        }

        mainViewModel.loading.observe(this) { isLoading ->
            isLoading?.let {
                binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    binding.errorText.visibility = View.GONE
                    binding.articleList.visibility = View.GONE
                }
            }
        }
    }

    /**called when a particular article is clicked*/
    override fun onClick(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
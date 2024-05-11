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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Constants.BASE_URL
import com.example.myapplication.Constants.PERMISSIONS_REQUEST_READ_CONTACTS
import com.example.myapplication.adapter.ArticleAdapter
import com.example.myapplication.data.Articles
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.network.asyncGetHttpRequest


class MainActivity : AppCompatActivity(), ItemListListener {

    private var articleList = mutableListOf<Articles>()
    private var countriesAdapter: ArticleAdapter = ArticleAdapter(this, arrayListOf())
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(this),
                com.example.myapplication.R.layout.activity_main,
                null,
                false
            )

        setContentView(binding.root)
        binding.articleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countriesAdapter
        }

        //
        if (ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermission()
        } else {
            fetchArticles()
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
                        arrayOf<String>(android.Manifest.permission.POST_NOTIFICATIONS),
                        PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create().show()
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(android.Manifest.permission.POST_NOTIFICATIONS),
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
                fetchArticles()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission to read contacts was denied.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**fetching the articleslist*/
    private fun fetchArticles() {
        asyncGetHttpRequest(
            endpoint = BASE_URL,
            onSuccess = {
                articleList = it.response.articles
                countriesAdapter.updateArticles(articleList)
                binding.progressBar.visibility = View.GONE
                binding.errorText.visibility = View.GONE
                binding.articleList.visibility = View.VISIBLE
            },
            onError = {
                binding.progressBar.visibility = View.GONE
                binding.articleList.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }
        )
    }

    /**called when a particular article is clicked*/
    override fun onClick(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}



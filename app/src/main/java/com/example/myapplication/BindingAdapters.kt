package com.example.myapplication

import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.example.myapplication.extensions.downloadBitmap
import kotlin.concurrent.thread

/*displays image in the imageView*/
@BindingAdapter("loadImageFromUrl")
fun ImageView.bindImage(imgUrl: String?) {
    if (this.drawable == null) {
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build().toString()
            val uiHandler = Handler(Looper.getMainLooper())
            thread(start = true) {
                val bitmap = downloadBitmap(imgUri)
                uiHandler.post {
                    this.setImageBitmap(bitmap)
                }
            }
        }
    }

}
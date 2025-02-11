package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = getImage()
            withContext(Dispatchers.Main) {
                showImage(bitmap)
            }
        }*/
        CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            val firstDeffered = async(Dispatchers.IO) { getImage() }
            val bitmap = firstDeffered.await()
            showImage(bitmap)
            val secondDeffered = async {
                //delay(10000)
                applyFilter(bitmap)
            }
            val bitmap2 = secondDeffered.await()
            showImage2(bitmap2)
        }

    }


    private fun getImage() = URL(IMAGE_URL).openStream().use { BitmapFactory.decodeStream(it) }
    private fun showImage(bitmap: Bitmap) {
        findViewById<ImageView>(R.id.imageView).apply{
            setImageBitmap(bitmap)
            visibility = View.VISIBLE
        }

    }
    private fun showImage2(bitmap: Bitmap) {
        findViewById<ImageView>(R.id.imageView2).apply{
            setImageBitmap(bitmap)
            visibility = View.VISIBLE
        }
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
    }

    private fun applyFilter(bitmap: Bitmap) = bitmap.toGrayscale()

    object Constants{
        val grayPaint = android.graphics.Paint()
        init {
            val cm = ColorMatrix()
            cm.setSaturation(0f)
            val f = ColorMatrixColorFilter(cm)
            grayPaint.colorFilter = f
        }
    }

    fun Bitmap.toGrayscale(): Bitmap {
        val height: Int = this.height
        val width: Int = this.width
        val bmpGrayscale: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmpGrayscale)
        c.drawBitmap(this, 0f, 0f, Constants.grayPaint)
        return bmpGrayscale
    }

    companion object {
        const val IMAGE_URL = "https://img.freepik.com/free-psd/earth-planet-transparent-background_84443-27083.jpg"
    }
}
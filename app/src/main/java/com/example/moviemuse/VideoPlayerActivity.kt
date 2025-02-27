package com.example.moviemuse

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VideoPlayerActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoKey = intent.getStringExtra("VIDEO_KEY") ?: "71juUqkBGY4"


        val webView: WebView = findViewById(R.id.web_view)

        webView.layoutParams = webView.layoutParams.apply {
            height = (resources.displayMetrics.widthPixels * 9) / 16
        }

        val videoHtml = """
            <html>
            <body style="margin:0;padding:0;">
                <iframe width="100%" height="100%"
                    src="https://www.youtube.com/embed/$videoKey?autoplay=1&rel=0&fs=1"
                    frameborder="0"
                    allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture; fullscreen"
                    allowfullscreen>
                </iframe>
            </body>
            </html>
        """.trimIndent()

        webView.loadData(videoHtml, "text/html", "utf-8")
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()


    }
}
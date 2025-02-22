package Components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun YouTubeWebView(videoId: String) {
    val context = LocalContext.current

    val htmlData = remember(videoId) {
        """
        <html>
            <body style="margin:0;padding:0;">
                <iframe width="100%" height="100%" 
                    src="https://www.youtube.com/embed/$videoId?rel=0&autoplay=0" 
                    frameborder="0" allowfullscreen>
                </iframe>
            </body>
        </html>
        """.trimIndent()
    }

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
    }

    DisposableEffect(videoId) {
        webView.loadDataWithBaseURL(
            "https://www.youtube.com",
            htmlData,
            "text/html",
            "utf-8",
            null
        )
        onDispose {
            // Stop loading and clear content
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.clearHistory()
            webView.removeAllViews()
            // Then destroy the WebView
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
package com.example.webviewinjection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.webkit.WebResourceResponse;
import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.ResponseBody;

class LoggingInterceptor implements Interceptor {
    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        Log.d("request headers", request.headers().toString());
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Log.d("response headers", response.headers().toString());
        return response;
    }
}

class ChangeResponse implements Interceptor {
    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String responseString = originalResponse.body().string();
        Document doc = Jsoup.parse(responseString);
        doc.getElementById("myIframe").removeAttr("sandbox");
        MediaType contentType = originalResponse.body().contentType();
        ResponseBody body = ResponseBody.create(doc.toString(), contentType);

        return originalResponse.newBuilder()
                .body(body)
                .removeHeader("Content-Security-Policy")
                .header("X-XSS-Protection", "0")
                .build();
    }
};


public class MainActivity extends AppCompatActivity {
    @NonNull
    private WebResourceResponse handleRequestViaOkHttp(@NonNull String url) {
        try {
            final OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor())
                    .addInterceptor(new ChangeResponse())
                    .build();

            final Call call = client.newCall(new Request.Builder()
                    .url(url)
                    .build()
            );

            final Response response = call.execute();
            return new WebResourceResponse("text/html", "utf-8",
                    response.body().byteStream()
            );
        } catch (Exception e) {
            return null; // return response for bad request
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView mywebview = (WebView) findViewById(R.id.webView);
        mywebview.loadUrl("http://192.168.1.34:31337/home");
        mywebview.clearCache(true);
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView Console Error: ", consoleMessage.message());
                return true;
            }});

        mywebview.setWebViewClient(new WebViewClient(){
            @SuppressWarnings("deprecation") // From API 21 we should use another overload
            @Override
            public WebResourceResponse shouldInterceptRequest(@NonNull WebView view, @NonNull String url) {
                return handleRequestViaOkHttp(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mywebview.loadUrl("javascript:var button = document.createElement(\"button\"); button.innerHTML = \"Access\"; var body = document.getElementsByTagName(\"body\")[0]; body.appendChild(button); button.addEventListener(\"click\", function(){ alert(document.getElementById('myIframe').contentDocument.getElementById('data').innerText); }, false);");
            }
        });
    }
}

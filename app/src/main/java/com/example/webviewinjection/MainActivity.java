package com.example.webviewinjection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WebView mywebview = (WebView) findViewById(R.id.webView);
        mywebview.clearCache(true);
        mywebview.loadUrl("http://192.168.1.36:31337/home?name=<body onload=\"f()\"><script type=\"text/javascript\">function f(){var button=document.getElementsByName(\"submit\")[0];button.addEventListener(\"click\", function(){ alert(\"injected 1\"); return false; },false);}</script>");
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView Console Error: ", consoleMessage.message());
                return true;
            }});
        mywebview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mywebview.loadUrl(
                        "javascript:var button = document.getElementsByName(\"submit\")[0];button.addEventListener(\"click\", function(){ alert(\"injected 1\"); },false);"
                );
            }
        });
    }
}
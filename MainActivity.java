package com.ghosi.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;

public class MainActivity extends Activity {
  private WebView web;
  private ValueCallback<Uri[]> fileCallback;
  private static final int FILE_REQUEST = 42;

  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    web = new WebView(this);
    setContentView(web);
    WebSettings settings = web.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setMediaPlaybackRequiresUserGesture(false);
    web.setWebViewClient(new WebViewClient());
    web.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
        if (fileCallback != null) fileCallback.onReceiveValue(null);
        fileCallback = callback;
        try { startActivityForResult(params.createIntent(), FILE_REQUEST); return true; }
        catch (Exception e) { fileCallback.onReceiveValue(null); fileCallback = null; return true; }
      }
    });
    web.loadUrl("https://ghosi.floot.app");
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FILE_REQUEST && fileCallback != null) {
      fileCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
      fileCallback = null;
    }
  }

  @Override public void onBackPressed() {
    if (web.canGoBack()) web.goBack(); else super.onBackPressed();
  }

  @Override protected void onDestroy() { if (web != null) web.destroy(); super.onDestroy(); }
}

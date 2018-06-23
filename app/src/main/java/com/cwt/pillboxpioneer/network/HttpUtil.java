package com.cwt.pillboxpioneer.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 网络相关
 * Created by 曹吵吵 on 2018/6/15 0015.
 */

public class HttpUtil {
    public static final String defaultCss = "<style type=\"text/css\"> img {" +
            "width:100%;" +//限定图片宽度填充屏幕
            "height:auto;" +//限定图片高度自动
            "}" +
            "body {" +
            "margin-right:5px;" +//限定网页中的文字右边距为5px(可根据实际需要进行行管屏幕适配操作)
            "margin-left:5px;" +//限定网页中的文字左边距为5px(可根据实际需要进行行管屏幕适配操作)
            "margin-top:5px;" +//限定网页中的文字上边距为5px(可根据实际需要进行行管屏幕适配操作)
            //"font-size:40px;" +//限定网页中文字的大小为40px,请务必根据各种屏幕分辨率进行适配更改
            "word-wrap:break-word;"+//允许自动换行(汉字网页应该不需要这一属性,这个用来强制英文单词换行,类似于word/wps中的西文换行)
            "}" +
            "</style>";

    public static String addCssToHtml(String html){
        return "<html><header>" + defaultCss + "</header>" + html + "</html>";
    }

    public static String addCssToHtml(String html,String css){
        return "<html><header>" + css + "</header>" + html + "</html>";
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static String makeUA() {
        final String ua= Build.BRAND + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE;
        Log.e("User-agent", "makeUA: "+ua);
        return ua;
    }

    public static class NoJumpWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }
    }
}

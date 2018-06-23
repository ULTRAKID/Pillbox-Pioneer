package com.cwt.pillboxpioneer.search;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cwt.pillboxpioneer.R;
import com.cwt.pillboxpioneer.network.HttpUtil;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class MedicineManualActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private TextView titleTextView,manualContentText;
    private Button backButton;
    private String manualHtml,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_manual);
        init();
    }

    private void init(){
        initData();
        initView();
    }

    private void initData(){
        Intent intent=getIntent();
        manualHtml=intent.getStringExtra("HtmlCode");
        title=intent.getStringExtra("MedicineName");
    }

    private void initView(){
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        /*webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.setWebViewClient(new HttpUtil.NoJumpWebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress==100){
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
        //manualHtml= HttpUtil.addCssToHtml(manualHtml);
        webView.loadDataWithBaseURL(null,manualHtml, "text/html" , "utf-8", null);*/
        manualContentText=(TextView)findViewById(R.id.manual_content_text);
        manualHtml=manualHtml.replace("【","<br/>【");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
            manualContentText.setText(Html.fromHtml(manualHtml,FROM_HTML_MODE_COMPACT));
        }else {
            manualContentText.setText(Html.fromHtml(manualHtml));
        }
        //manualContentText.setText(manualHtml);
        titleTextView = (TextView)findViewById(R.id.article_title);
        titleTextView.setText(title);
        backButton = (Button)findViewById(R.id.article_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}

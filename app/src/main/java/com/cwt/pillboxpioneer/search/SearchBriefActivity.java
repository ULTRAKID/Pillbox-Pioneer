package com.cwt.pillboxpioneer.search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cwt.pillboxpioneer.R;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

import com.cwt.pillboxpioneer.network.WebCrawler;

public class SearchBriefActivity extends AppCompatActivity {
    private List<MedicineBrief> briefList;
    private Handler handler=null;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private Document searchDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_brief);
        init();
    }

    private void init(){
        initView();
        initData();
    }

    private void initView(){
        recyclerView=(RecyclerView)findViewById(R.id.medicine_brief_recycler_view);
        Button backButton=(Button)findViewById(R.id.search_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData(){
        Intent intent=getIntent();
        final String keyword=intent.getStringExtra("SearchKeyword");
        initHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    searchDoc=WebCrawler.getSearchDoc(keyword);
                    Message message=new Message();
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(SearchBriefActivity.this).setMessage("搜索出错！");
                }
            }
        }).start();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("搜索中");
        //progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void initHandler(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                briefList=WebCrawler.getResultBrief(searchDoc);
                initRecyclerView();
                progressDialog.dismiss();
                return true;
            }
        });
    }

    private void initRecyclerView(){
        ImageView imageView=(ImageView)findViewById(R.id.no_result_img);
        if (briefList.isEmpty()){
            imageView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        imageView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MedicineBriefAdapter adapter=new MedicineBriefAdapter(briefList);
        recyclerView.setAdapter(adapter);
    }

}

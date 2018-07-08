package com.cwt.pillboxpioneer.clock;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cwt.pillboxpioneer.ClockService;
import com.cwt.pillboxpioneer.MainActivity;
import com.cwt.pillboxpioneer.R;
import com.cwt.pillboxpioneer.network.IOTclientFactory;
import com.cwt.pillboxpioneer.network.UdpClient;
import com.cwt.pillboxpioneer.personinfo.Account;
import com.cwt.pillboxpioneer.personinfo.LoginActivity;

import java.io.IOException;
import java.util.List;

public class ClockActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Clock> clockList;
    Button backButton;
    ClockSQLiteHelper clockSQLiteHelper;
    SQLiteDatabase clockDatabase;
    UdpClient udpClient;
    SharedPreferences sharedPreferences;
    AlertDialog alertDialog;
    public static Button loginQuitButton;
    public static FloatingActionButton saveClockFAB;
    public static int changedNum =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        init();
    }

    private void init(){
        initData();
        initView();
    }

    private void initData(){
        clockSQLiteHelper=new ClockSQLiteHelper(this);
        clockDatabase=clockSQLiteHelper.getWritableDatabase();
        clockList=clockSQLiteHelper.queryAllClocks(clockDatabase);
        udpClient=IOTclientFactory.getInstance();
        sharedPreferences=getSharedPreferences(Account.SP_FILE_NAME,0);
    }

    private void initView(){
        saveClockFAB=(FloatingActionButton)findViewById(R.id.save_clock_fab);
        final ClockAdapter adapter=new ClockAdapter(clockList);
        saveClockFAB.setVisibility(View.GONE);
        saveClockFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.loginFlag){
                    Toast.makeText(ClockActivity.this,"请先登录！",Toast.LENGTH_SHORT);
                    changedNum=0;
                    return;
                }
                clockList=adapter.getClockList();
                clockSQLiteHelper.saveClockConfig(clockDatabase,clockList);
                saveClockFAB.setVisibility(View.GONE);
                String pack=Clock.packList2Str(clockList);
                try {
                    udpClient.sendMsg(pack);
                } catch (IOException e) {
                    Log.e("Clock","发送闹钟配置出错");
                    e.printStackTrace();
                }
                new AlertDialog.Builder(ClockActivity.this)
                        .setMessage("保存成功！")
                        .setCancelable(true)
                        .show();
                changedNum =0;
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.clock_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        backButton=(Button)findViewById(R.id.clock_back_button);
        changedNum =0;
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ClockActivity","Config changed "+ changedNum);
                if (changedNum !=0){
                    AlertDialog.Builder dialog=new AlertDialog.Builder(ClockActivity.this);
                    dialog.setMessage("尚未保存修改内容，是否保存？");
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveClockFAB.callOnClick();
                        }
                    });
                    alertDialog=dialog.show();
                    changedNum =0;
                } else {
                    finish();
                }
            }
        });
        loginQuitButton=(Button)findViewById(R.id.login_quit_button);
        if (MainActivity.loginFlag)
            loginQuitButton.setText("注销");
        else
            loginQuitButton.setText("登陆");
        loginQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.loginFlag){
                    Intent intent=new Intent(ClockActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.remove(Account.SP_ID_NAME);
                    editor.remove(Account.SP_PSW_NAME);
                    editor.commit();
                    Toast.makeText(ClockActivity.this,"成功注销",Toast.LENGTH_SHORT).show();
                    MainActivity.loginFlag=false;
                    //IOTclientFactory.stopReceiving();
                    IOTclientFactory.setAccount(null);
                    Intent serviceIntent=new Intent(ClockActivity.this,ClockService.class);
                    stopService(serviceIntent);
                    loginQuitButton.setText("登陆");
                    saveClockFAB.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backButton.callOnClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
    }
}

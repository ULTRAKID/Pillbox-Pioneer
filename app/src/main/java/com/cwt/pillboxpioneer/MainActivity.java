package com.cwt.pillboxpioneer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.cwt.pillboxpioneer.clock.Clock;
import com.cwt.pillboxpioneer.clock.ClockActivity;
import com.cwt.pillboxpioneer.easteregg.EasterEggActivity;
import com.cwt.pillboxpioneer.network.HttpUtil;
import com.cwt.pillboxpioneer.network.IOTclientFactory;
import com.cwt.pillboxpioneer.network.UdpClient;
import com.cwt.pillboxpioneer.personinfo.Account;
import com.cwt.pillboxpioneer.personinfo.LoginActivity;
import com.cwt.pillboxpioneer.search.SearchBriefActivity;

import java.io.IOException;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    SearchView searchView;
    FloatingActionButton clockFAB;
    Handler handler=null;
    NotificationManager notificationManager;
    UdpClient udpClient;
    Notification notification;
    SharedPreferences sharedPreferences;
    private Account userAccount;
    public static boolean loginFlag=false;
    public static Context context;
    private String warningWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        init();
    }

    private void init(){
        initData();
        initView();
    }

    private void initView(){
        clockFAB=(FloatingActionButton)findViewById(R.id.clock_fab);
        searchView=(SearchView)findViewById(R.id.search_view);
        searchView.setSubmitButtonEnabled(true);
        searchView.setFocusable(false);
        searchView.clearFocus();
        int id=searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView=(TextView)searchView.findViewById(id);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setHintTextColor(Color.argb(100,00,00,00));
        try {
            Field mCursorDrawableRes=TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(textView, R.drawable.cursor_color);
        } catch (Exception e){
            e.printStackTrace();
        }

        clockFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ClockActivity.class);
                startActivity(intent);
            }
        });
        clockFAB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent=new Intent(context, EasterEggActivity.class);
                startActivity(intent);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (HttpUtil.isNetworkConnected(context)){
                    Intent intent=new Intent(context,SearchBriefActivity.class);
                    intent.putExtra("SearchKeyword",s);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("错误")
                            .setMessage("请检查网络连接！")
                            .show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void initData(){
        getUserAccount();
        Intent intent = new Intent(MainActivity.this, ClockActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bitmapL= BitmapFactory.decodeResource(MainActivity.this.getResources(),R.drawable.notification_large);
        notification=new NotificationCompat.Builder(MainActivity.this)
                .setLargeIcon(bitmapL)// 设置大图标
                //.setSmallIcon(android.R.drawable.sym_def_app_icon)// 设置小图标
                .setSmallIcon(R.drawable.notification_small)
                .setContentInfo("Pillbox Pioneer")// 设置小图标旁的文本信息
                .setTicker("该吃药了！")// 设置状态栏文本标题
                .setContentTitle("提醒")// 设置标题
                .setContentIntent(pIntent)
                .setContentText("记得按时吃药！")// 设置内容
                .setSound(RingtoneManager.getActualDefaultRingtoneUri(
                        getBaseContext(), RingtoneManager.TYPE_NOTIFICATION))// 设置Notification提示铃声为系统默认铃声
                .setVibrate(new long[]{0,1000,1000,1000,1000,1000})//震动
                .setLights(Color.RED,800,800)//前置灯光
                .setPriority(NotificationCompat.PRIORITY_MAX)//优先级
                .setAutoCancel(true).build();// 点击Notification的时候使它自动消失
        notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        udpClient= IOTclientFactory.getInstance();
        initHandler();
        IOTclientFactory.setHandler(handler);
        if (loginFlag){
            //IOTclientFactory.startReceiving(userAccount);
            IOTclientFactory.setAccount(userAccount);
            Intent serviceIntent=new Intent(this,ClockService.class);
            stopService(serviceIntent);
            startService(serviceIntent);
            //udpClient.login(userAccount);
        }
    }

    private boolean getUserAccount(){
        sharedPreferences=getSharedPreferences(Account.SP_FILE_NAME,0);
        String id=sharedPreferences.getString(Account.SP_ID_NAME,Account.NO_SAVED_ID);
        String psw=sharedPreferences.getString(Account.SP_PSW_NAME,Account.NO_SAVED_PSW);
        warningWord=sharedPreferences.getString(Account.SP_WARNING_NAME,Account.DEFAULT_WARNING_CMD);
        userAccount=new Account(id,psw);
        loginFlag=userAccount.isLegal();
        return loginFlag;
    }

    private void initHandler(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                String msg=(String)message.obj;
                Log.e("Handler","receive a msg:"+msg);
                if (msg.equals(UdpClient.LOGIN_SUCCESS_INFO)){
                    IOTclientFactory.isOnline=true;
                } else if (msg.equals(warningWord)){
                    Log.e("Handler","NEED_REMIND_TAKING_PILLS");
                    notificationManager.notify(0,notification);
                }
                return true;
            }
        });
    }


}

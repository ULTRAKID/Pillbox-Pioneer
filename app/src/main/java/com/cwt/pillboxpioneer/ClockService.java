package com.cwt.pillboxpioneer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cwt.pillboxpioneer.clock.ClockActivity;
import com.cwt.pillboxpioneer.network.IOTclientFactory;
import com.cwt.pillboxpioneer.network.UdpClient;
import com.cwt.pillboxpioneer.personinfo.Account;

public class ClockService extends Service {
    private static Account userAccount;
    Notification notification;
    NotificationManager notificationManager;
    Handler handler=null;
    String warningWord;

    public ClockService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getUserAccount();
        IOTclientFactory.setHandler(handler);
        IOTclientFactory.setAccount(userAccount);
        IOTclientFactory.startReceiving();
        Log.e("ClockService","service start command receiving");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IOTclientFactory.stopReceiving();
        Log.e("ClockService","service stop receiving");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, ClockActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bitmapL= BitmapFactory.decodeResource(this.getResources(),R.drawable.notification_large);
        notification=new NotificationCompat.Builder(this)
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
        initHandler();
        notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.e("ClockService","service start receiving");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private boolean getUserAccount(){
        SharedPreferences sharedPreferences=getSharedPreferences(Account.SP_FILE_NAME,0);
        String id=sharedPreferences.getString(Account.SP_ID_NAME,Account.NO_SAVED_ID);
        String psw=sharedPreferences.getString(Account.SP_PSW_NAME,Account.NO_SAVED_PSW);
        warningWord=sharedPreferences.getString(Account.SP_WARNING_NAME,Account.DEFAULT_WARNING_CMD);
        userAccount=new Account(id,psw);
        boolean loginFlag=userAccount.isLegal();
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

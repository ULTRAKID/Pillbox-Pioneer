package com.cwt.pillboxpioneer.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cwt.pillboxpioneer.personinfo.Account;

import java.io.IOException;

/**
 * Created by 曹吵吵 on 2018/6/21 0021.
 */

public class IOTclientFactory {
    private static UdpClient client=null;
    private static boolean receivingFlag=true;
    private static Handler handler=null;
    public static boolean isOnline=false;
    public static Account userAccount=null;

    public static UdpClient getInstance(){
        if (client==null)
            client=new UdpClient(UdpClient.IOT_IP,UdpClient.IOT_PORT);
        return client;
    }

    public static void setAccount(Account account){
        userAccount=account;
    }

    public static void setHandler(Handler nhandler){
        handler=nhandler;
    }

    public static boolean startReceiving(){
        if (userAccount==null)  {Log.e("IOTclientFactory","account null");return false;}
        if (client==null)   {Log.e("IOTclientFactory","client null");client=getInstance();}
        new Thread(new Runnable() {
            @Override
            public void run() {
                receivingFlag=true;
                Log.e("IOTclientFactory","thread start");
                while (receivingFlag){
                    String msg= null;
                    try {
                        if (!isOnline)
                            client.login(userAccount);
                        msg = client.getMsg();
                        if (msg.equals(UdpClient.NEED_LOGIN_INFO)){
                            isOnline=false;
                            client.login(userAccount);
                        } else {
                            isOnline=true;
                        }
                        Message message=new Message();
                        message.obj=msg;
                        //Log.e("IOTclientFactory","receive a msg:"+msg);
                        if (handler!=null)
                            handler.sendMessage(message);
                    } catch (IOException e) {
                        //Log.e("IOTclientFactory","receiving thread error");
                        e.printStackTrace();
                    }
                }
                Log.e("IOTclientFactory","Thread for receiving is dead.");
            }
        }).start();
        return true;
    }

    public static void stopReceiving(){
        receivingFlag=false;
    }

}

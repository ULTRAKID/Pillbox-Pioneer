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

    public static UdpClient getInstance(){
        if (client==null)
            client=new UdpClient(UdpClient.IOT_IP,UdpClient.IOT_PORT);
        return client;
    }

    public static void setHandler(Handler nhandler){
        handler=nhandler;
    }

    public static boolean startReceiving(final Account account){
        if (client==null)   return false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                receivingFlag=true;
                while (receivingFlag){
                    String msg= null;
                    try {
                        if (!isOnline)
                            client.login(account);
                        msg = client.getMsg();
                        if (msg.equals(UdpClient.NEED_LOGIN_INFO)){
                            isOnline=false;
                            client.login(account);
                        } else {
                            isOnline=true;
                        }
                        Message message=new Message();
                        message.obj=msg;
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

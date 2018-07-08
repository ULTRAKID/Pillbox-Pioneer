package com.cwt.pillboxpioneer.network;

import android.util.Log;

import com.cwt.pillboxpioneer.personinfo.Account;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * udp通信
 * 与硬件设备进行沟通
 * Created by 曹吵吵 on 2018/4/23 0023.
 */

public class UdpClient {
    public final static String IOT_IP="115.29.240.46";
    public final static int IOT_PORT=6000;
    public final static String DEVICE_ID="R13DRCJYXHUK5H1A";
    public final static String DEVICE_PASSWORD="123517";
    public final static String LOGIN_PACKAGE ="ep=R13DRCJYXHUK5H1A&pw=123517";
    public final static String NEED_LOGIN_INFO="[iotxx:send-reg-first]";
    public final static String LOGIN_SUCCESS_INFO="[iotxx:ok]";
    //public final static String NEED_REMIND_TAKING_PILLS="warning";

    private DatagramSocket socket;
    private DatagramPacket datagramPacket;
    private boolean canSendBySocket;
    private InetAddress address;
    private int port;

    public UdpClient(String ip,int port)  {
        try {
            this.port=port;
            socket=new DatagramSocket(0);
            if (socket.equals(null))
                Log.e("Udp","socket is null");
            else
                Log.e("Udp","socket is not null");
            address=InetAddress.getByName(ip);
            //login();
            canSendBySocket=true;
        } catch (Exception e) {
            e.printStackTrace();
            canSendBySocket=false;
        }
    }

    public boolean login(Account account) throws IOException {
        String pack=account.getLoginPackage();
        byte[] buf= pack.getBytes();
        Log.e("UdpClient",pack);
        datagramPacket=new DatagramPacket(buf,buf.length,address,port);
        socket.send(datagramPacket);
        return true;
    }

    public boolean sendMsg(String msg) throws IOException {
        if (!canSendBySocket)
            return false;
        byte[] buf=msg.getBytes();
        datagramPacket=new DatagramPacket(buf,buf.length,address,port);
        socket.send(datagramPacket);
        return true;
    }

    //阻塞式，1s
    public String getMsg() throws IOException {
        byte[] buf=new byte[1024];
        datagramPacket=new DatagramPacket(buf,buf.length);
        socket.setSoTimeout(1000);
        socket.receive(datagramPacket);
        String str=new String(datagramPacket.getData(),0,datagramPacket.getLength());
        return str;
    }

}

package com.cwt.pillboxpioneer.clock;

import java.util.List;

/**
 * Clock类
 * Created by 曹吵吵 on 2018/6/20 0020.
 */

public class Clock {
    public int id;
    public int state;  //状态，1为开，0为关
    public int hour;   //24小时制
    public int minute;

    public String getTime(){
        String time="";
        if (hour<10)
            time+="0";
        time+=hour+":";
        if (minute<10)
            time+="0";
        time+=minute;
        return time;
    }

    @Override
    public String toString(){
        return "id:"+id+" state:"+state+" time:"+getTime();
    }

    public String packStr(){
        return id+" "+state+" "+hour+" "+minute+"\n";
    }

    public static String packList2Str(List<Clock> list){
        String pack="";
        pack+=list.size()+"\n";
        for (int i=0;i<list.size();i++){
            pack+=list.get(i).packStr();
        }
        return pack;
    }
}

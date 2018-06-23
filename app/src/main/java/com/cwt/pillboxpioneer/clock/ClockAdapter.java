package com.cwt.pillboxpioneer.clock;

import android.app.TimePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.cwt.pillboxpioneer.R;

import java.util.List;

/**
 * Created by 曹吵吵 on 2018/6/20 0020.
 */

public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ViewHolder> {
    private List<Clock> clockList;
    private View view;
    private static final String[] STATE_STR={"已关闭","已打开"};
    private static final String[] ID_CH_STR={"一","二","三","四","五","六","七","八","九","十"};
    private static final float OFF_ALPHA=0.5f,ON_ALPHA=1.0f;
    private FloatingActionButton saveClockFAB;

    public ClockAdapter(List<Clock> clockList, FloatingActionButton FAB){
        this.clockList=clockList;
        saveClockFAB=FAB;
    }

    public ClockAdapter(List<Clock> clockList){
        this.clockList=clockList;
        saveClockFAB=ClockActivity.saveClockFAB;
    }

    public List<Clock> getClockList(){
        return clockList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.clock_item,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.clockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int p=viewHolder.getAdapterPosition();
                Float alpha=ON_ALPHA;
                if (!b) alpha=OFF_ALPHA;
                viewHolder.clockSwitch.setAlpha(alpha);
                viewHolder.clockState.setAlpha(alpha);
                viewHolder.clockTime.setAlpha(alpha);
                clockList.get(p).state=b?1:0;
                Clock clock=clockList.get(p);
                int id=clock.id,state=clock.state;
                String str="闹钟"+ID_CH_STR[id]+"\t"+STATE_STR[state];
                viewHolder.clockState.setText(str);
                saveClockFAB.setVisibility(View.VISIBLE);
                ClockActivity.changedNum++;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int p=viewHolder.getAdapterPosition();
                int nhour=clockList.get(p).hour;
                int nminute=clockList.get(p).minute;
                TimePickerDialog dialog=new TimePickerDialog(view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                clockList.get(p).hour=hour;
                                clockList.get(p).minute=minute;
                                clockList.get(p).state=1;
                                notifyItemChanged(p);
                                saveClockFAB.setVisibility(View.VISIBLE);
                                ClockActivity.changedNum++;
                            }
                        },nhour,nminute,true);
                dialog.show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Clock clock=clockList.get(position);
        holder.clockTime.setText(clock.getTime());
        int id=clock.id,state=clock.state;
        String str="闹钟"+ID_CH_STR[id]+"\t"+STATE_STR[state];
        holder.clockState.setText(str);
        if (state==1){
            holder.clockSwitch.setChecked(true);
            ClockActivity.changedNum--;
            holder.clockSwitch.setAlpha(ON_ALPHA);
            holder.clockState.setAlpha(ON_ALPHA);
            holder.clockTime.setAlpha(ON_ALPHA);
        } else {
            holder.clockSwitch.setChecked(false);
            holder.clockSwitch.setAlpha(OFF_ALPHA);
            holder.clockState.setAlpha(OFF_ALPHA);
            holder.clockTime.setAlpha(OFF_ALPHA);
        }
    }

    @Override
    public int getItemCount() {
        return clockList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView clockTime,clockState;
        ToggleButton clockSwitch;
        View clockView;

        public ViewHolder(View itemView) {
            super(itemView);
            clockView=itemView;
            clockState=(TextView)itemView.findViewById(R.id.clock_state_text);
            clockTime=(TextView)itemView.findViewById(R.id.clock_time_text);
            clockSwitch=(ToggleButton)itemView.findViewById(R.id.clock_switch_button);
        }
    }

    /*private String int2Time(int hour,int minute){
        String time="";
        if (hour<10)
            time+="0";
        time+=hour+":";
        if (minute<10)
            time+="0";
        time+=minute;
        return time;
    }*/
}

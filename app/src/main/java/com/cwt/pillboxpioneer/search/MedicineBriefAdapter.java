package com.cwt.pillboxpioneer.search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cwt.pillboxpioneer.R;
import com.cwt.pillboxpioneer.network.WebCrawler;

import java.io.IOException;
import java.util.List;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

/**
 * Created by 曹吵吵 on 2018/6/16 0016.
 */

public class MedicineBriefAdapter extends RecyclerView.Adapter<MedicineBriefAdapter.ViewHolder> {
    private List<MedicineBrief> briefList;
    private View view;
    private Handler handler=null;
    private ProgressDialog progressDialog;
    private String manualHtml,manualName;

    public MedicineBriefAdapter(List<MedicineBrief> list){
        briefList=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.brief_result_item,parent,false);
        initHandler();
        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.medicineBriefView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=viewHolder.getAdapterPosition();
                MedicineBrief brief=briefList.get(position);
                manualName=brief.name;
                getManual(brief.detailsLink);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MedicineBrief brief=briefList.get(position);
        String url=brief.picLink;
        if (url==""){
            Glide.with(view.getContext()).load(R.drawable.no_pic).into(holder.medicinePic);
        } else {
            Glide.with(view.getContext()).load(url).into(holder.medicinePic);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
            holder.medicineName.setText(Html.fromHtml("<b>"+brief.name+"</b>",FROM_HTML_MODE_COMPACT));
            holder.medicineBrief.setText(Html.fromHtml("<p>&nbsp;&nbsp;"+brief.brief+"</p>",FROM_HTML_MODE_COMPACT));
        }else {
            holder.medicineName.setText(Html.fromHtml("<b>"+brief.name+"</b>"));
            holder.medicineBrief.setText(Html.fromHtml("<p>&nbsp;&nbsp;"+brief.brief+"</p>"));
        }
    }

    @Override
    public int getItemCount() {
        return briefList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView medicinePic;
        TextView medicineName,medicineBrief;
        View medicineBriefView;

        public ViewHolder(View itemView) {
            super(itemView);
            medicineBriefView=itemView;
            medicinePic=(ImageView)itemView.findViewById(R.id.medicine_pic_img);
            medicineName=(TextView)itemView.findViewById(R.id.medicine_name_text);
            medicineBrief=(TextView)itemView.findViewById(R.id.medicine_brief_text);
        }
    }

    private void getManual(final String manualUrl){
        progressDialog=new ProgressDialog(view.getContext());
        progressDialog.setMessage("加载中");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    manualHtml= WebCrawler.getManualHtml(manualUrl);
                    Message message=new Message();
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(view.getContext()).setMessage("加载出错！").show();
                }
            }
        }).start();
    }

    private void initHandler(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                progressDialog.dismiss();
                Intent intent=new Intent(view.getContext(),MedicineManualActivity.class);
                intent.putExtra("HtmlCode",manualHtml);
                intent.putExtra("MedicineName",manualName);
                view.getContext().startActivity(intent);
                return true;
            }
        });
    }
}

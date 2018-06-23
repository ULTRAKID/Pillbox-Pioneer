package com.cwt.pillboxpioneer.easteregg;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwt.pillboxpioneer.R;
import com.cwt.pillboxpioneer.autoscroll.AutoScrollView;

public class EasterEggActivity extends AppCompatActivity {
    ImageView background;
    EditText editText;
    Button enterButton;
    //VerticalScrollTextView subtitleView;
    AutoScrollView scrollView;
    TextView subText;
    Handler bonusHandler;
    private static final String magicStr="0517200209081008";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg);
        initHandler();
        initView();
    }

    private void initView(){
        background=(ImageView)findViewById(R.id.easter_bg_img);
        editText=(EditText)findViewById(R.id.easter_input_edit);
        enterButton=(Button)findViewById(R.id.easter_enter_button);
        //subtitleView=(VerticalScrollTextView)findViewById(R.id.subtitle_thanks_view);
        scrollView=(AutoScrollView)findViewById(R.id.sub_scroll);
        subText=(TextView)findViewById(R.id.sub_text);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=editText.getText().toString();
                if (input.equals(magicStr)){
                    Toast.makeText(EasterEggActivity.this,"Bonus!",Toast.LENGTH_SHORT).show();
                    background.setImageResource(R.drawable.easter_egg_bg_key);
                    editText.setVisibility(View.GONE);
                    enterButton.setVisibility(View.GONE);
                    editText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // 隐藏软键盘
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    /*subtitleView.setSpead(6f);
                    subtitleView.setVisibility(View.VISIBLE);*/
                    scrollView.setVisibility(View.VISIBLE);
                    scrollView.setDuration(0);
                    scrollView.setSpeed(15);
                    scrollView.setPeriod(5000);
                    scrollView.setExtraFun(bonusHandler);
                    scrollView.setScrolled(true);
                } else {
                    Toast.makeText(EasterEggActivity.this,"Wrong! But I will give u an extra life.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initHandler(){
        bonusHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                final String msg=getResources().getString(R.string.magic_str);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.setScrolled(false);
                        TextPaint tp=subText.getPaint();
                        tp.setFakeBoldText(true);
                        subText.setText(msg);
                        subText.setTextColor(Color.BLACK);
                    }
                });
                return true;
            }
        });
    }
}

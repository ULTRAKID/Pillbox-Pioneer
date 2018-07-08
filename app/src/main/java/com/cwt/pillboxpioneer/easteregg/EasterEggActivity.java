package com.cwt.pillboxpioneer.easteregg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwt.pillboxpioneer.R;
import com.cwt.pillboxpioneer.autoscroll.AutoScrollView;
import com.cwt.pillboxpioneer.personinfo.Account;

public class EasterEggActivity extends AppCompatActivity {
    ImageView background;
    EditText editText;
    Button enterButton;
    //VerticalScrollTextView subtitleView;
    AutoScrollView scrollView;
    TextView subText;
    Handler bonusHandler;
    private static final String MAGIC_STR ="0517200209081008";
    private static final String SETTING_STR="SET ";

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
                if (input.equals(MAGIC_STR)){
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
                } else if(isSettingCmd(input)){
                    String word=input.substring(SETTING_STR.length());
                    boolean flag=setWarningWord(word);
                    Toast.makeText(EasterEggActivity.this,"设置成功,重启后生效。",Toast.LENGTH_SHORT).show();
                    Log.e("EasterEggSettingWarning","setting:"+flag);
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

    public static boolean isSettingCmd(String cmd){
        if (cmd.length()<=SETTING_STR.length())
            return false;
        String firstStr=cmd.substring(0,SETTING_STR.length());
        return firstStr.equals(SETTING_STR);
    }

    private boolean setWarningWord(String word){
        SharedPreferences sharedPreferences=getSharedPreferences(Account.SP_FILE_NAME,0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Account.SP_WARNING_NAME,word);
        return editor.commit();
    }
}

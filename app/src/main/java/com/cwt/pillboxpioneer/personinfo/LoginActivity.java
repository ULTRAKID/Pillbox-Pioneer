package com.cwt.pillboxpioneer.personinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cwt.pillboxpioneer.ClockService;
import com.cwt.pillboxpioneer.MainActivity;
import com.cwt.pillboxpioneer.R;
import com.cwt.pillboxpioneer.clock.ClockActivity;
import com.cwt.pillboxpioneer.network.IOTclientFactory;
import com.cwt.pillboxpioneer.network.UdpClient;

public class LoginActivity extends AppCompatActivity {
    EditText idEditText,pswEditText;
    CheckBox remeberCheckBox;
    Button loginButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        initView();
        initData();
    }

    private void initView(){
        idEditText=(EditText)findViewById(R.id.id_edit_text);
        pswEditText=(EditText)findViewById(R.id.password_edit_text);
        remeberCheckBox=(CheckBox)findViewById(R.id.remeber_psw_checkbox);
        loginButton=(Button)findViewById(R.id.login_button);
    }

    private void initData(){

        sharedPreferences=getSharedPreferences(Account.SP_FILE_NAME,0);
        String id=sharedPreferences.getString(Account.SP_ID_NAME,Account.NO_SAVED_ID);
        String psw=sharedPreferences.getString(Account.SP_PSW_NAME,Account.NO_SAVED_PSW);
        Account userAccount=new Account(id,psw);
        if (userAccount.isLegal()){
            idEditText.setText(userAccount.getId());
            pswEditText.setText(userAccount.getPsw());
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=idEditText.getText().toString();
                String psw=pswEditText.getText().toString();
                Account userAccount=new Account(id,psw);
                if (userAccount.getLoginPackage().equals(UdpClient.LOGIN_PACKAGE)){
                    if(remeberCheckBox.isChecked()){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString(Account.SP_ID_NAME,id);
                        editor.putString(Account.SP_PSW_NAME,psw);
                        editor.commit();
                    }
                    MainActivity.loginFlag=true;
                    //IOTclientFactory.startReceiving(userAccount);
                    IOTclientFactory.setAccount(userAccount);
                    Intent serviceIntent=new Intent(LoginActivity.this,ClockService.class);
                    stopService(serviceIntent);
                    startService(serviceIntent);
                    ClockActivity.loginQuitButton.setText("注销");
                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,"账号密码错误",Toast.LENGTH_SHORT);
                }
            }
        });
    }
}

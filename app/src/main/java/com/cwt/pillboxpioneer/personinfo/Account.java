package com.cwt.pillboxpioneer.personinfo;

/**
 * 账户密码类
 * Created by 曹吵吵 on 2018/6/22 0022.
 */

public class Account {
    public final static String SP_ID_NAME="ACCOUNT_NUMBER";
    public final static String SP_PSW_NAME="ACCOUNT_PSW";
    public final static String SP_FILE_NAME="USER_ACCOUNT";
    public final static String SP_WARNING_NAME="WARNING_NAME";
    public final static String DEFAULT_WARNING_CMD="warning";
    public final static String NO_SAVED_ID="NO_SUCH_ID";
    public final static String NO_SAVED_PSW="NO_SAVED_PSW";
    private String id;  //账号
    private String psw;  //密码

    public Account(String id,String psw){
        this.id=id;this.psw=psw;
    }

    public String getLoginPackage(){    //获取登陆包字符
        String packageStr="ep=";
        packageStr+=id;
        packageStr+="&pw=";
        packageStr+=psw;
        return packageStr;
    }

    public boolean isLegal(){       //判断合法性
        if (id.equals(Account.NO_SAVED_ID)||psw.equals(Account.NO_SAVED_PSW))
            return false;
        return true;
    }

    public String getId() {
        return id;
    }

    public String getPsw() {
        return psw;
    }
}

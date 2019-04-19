package com.six.landing.common;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component("stateCode")
public  class  StateCode {
    //成功
    private  final String success="1";
    //失败
    private  final String error="-1";
    //参数为空
    private  final String nullData="0";
   //查询为空
    private  final String nulls="-2";
    //系统异常
    private final String systemError="-3";
    //数据库连接错误
    private final String sqlError="-102";

    public String getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getNulls() {
        return nulls;
    }

    public String getNullData() {
        return nullData;
    }

    public String getSystemError() {
        return systemError;
    }

    public String getSqlError() {
        return sqlError;
    }
}

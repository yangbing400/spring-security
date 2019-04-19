package com.six.landing.common;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * 工具类
 */
@Component(value = "utils")
public class Utils {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat formatForNo = new SimpleDateFormat("yyyyMMdd");
    /**
     * 获取UUID
     * @return Strint
     */
   public String uuid(){
       UUID uuid=UUID.randomUUID();
       return uuid.toString();
   }
    // 获取今日24点
    public Date getTodayEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date end = calendar.getTime();
        return end;
    }
    /*
     *SpringSecurity 加密
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        return passwordEncoder;
    }

}

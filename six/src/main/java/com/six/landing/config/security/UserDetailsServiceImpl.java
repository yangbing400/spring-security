package com.six.landing.config.security;

import com.alibaba.fastjson.JSON;
import com.six.landing.common.SpringUtils;
import com.six.landing.config.security.data.ResponseBody;
import com.six.landing.dao.UsersDao;
import com.six.landing.domian.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static java.util.Collections.emptyList;

/**
 * @author zhaoxinguo on 2017/9/13.
 */
@Component(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private UsersDao usersDao;

    ResponseBody responseBody=new ResponseBody();

    /**
     * 通过构造器注入UserRepository
     * @param
     */
    @Transactional
    public User getAllUser(String username){
        return usersDao.findByUserName(username);
    }

    @Autowired
    private HttpServletResponse response;

    public UserDetailsServiceImpl(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(usersDao==null){
            usersDao=(UsersDao) SpringUtils.getBean("userDao");
        }
        User user = usersDao.findByUserName(username);
        if(user == null){
                try {
                    responseBody.setStatus("201");
                    responseBody.setMsg("登陆失败,用户不存在");
                    response.setContentType("text/html;charset=utf-8");
                    response.getWriter().write(JSON.toJSONString(responseBody));
                } catch (IOException e) {

                    e.printStackTrace();
                }

            return null;
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), emptyList());
    }
}

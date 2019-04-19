package com.six.landing.config.security.filter;

import com.alibaba.fastjson.JSON;
import com.six.landing.common.SpringUtils;
import com.six.landing.config.security.SelfUserDetails;
import com.six.landing.config.security.UserDetailsServiceImpl;
import com.six.landing.config.security.data.ResponseBody;
import com.six.landing.dao.UsersDao;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sun.plugin.viewer.IExplorerPluginObject;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.beans.Transient;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * 自定义身份认证验证组件
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

     @Autowired
     private UsersDao usersDao;

    //返回状态信息
    ResponseBody responseBody=new ResponseBody();

    @Autowired
    private HttpServletResponse response;

    public CustomAuthenticationProvider( HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserDetails user=userDetailsService().loadUserByUsername(name);
        if(user==null){
            Authentication auth = new UsernamePasswordAuthenticationToken(null, null, null);
            return auth;
        }
        SelfUserDetails userInfo = new SelfUserDetails();
        userInfo.setUsername(name); // 任意用户名登录
        userInfo.setPassword(user.getPassword());

        if (!passwordEncoder.matches(password,user.getPassword())) {
            responseBody.setStatus("202");
            responseBody.setMsg("登陆失败,用户密码不正确");
            try {
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(JSON.toJSONString(responseBody));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Authentication auth = new UsernamePasswordAuthenticationToken(null, null, null);
            return auth;
            //throw new BadCredentialsException("用户名密码不正确，请重新登陆！");
        }
        // 认证逻辑
       // UserDetails userDetails = userDetailsService.loadUserByUsername(name);
        if (null != user) {
                // 这里设置权限和角色
                ArrayList<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add( new GrantedAuthorityImpl("ROLE_ADMIN"));
                //authorities.add( new GrantedAuthorityImpl("AUTH_WRITE"));
                // 生成令牌 这里令牌里面存入了:name,password,authorities, 当然你也可以放其他内容
                Authentication auth = new UsernamePasswordAuthenticationToken(name, password, authorities);
                return auth;
        } else {
            Authentication auth = new UsernamePasswordAuthenticationToken(null, null, null);
            return auth;
            //throw new UsernameNotFoundException("用户不存在");
        }
    }

    /**
     * 是否可以提供输入类型的认证服务
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public UserDetailsServiceImpl userDetailsService(){
        return new UserDetailsServiceImpl(response);
    }


}

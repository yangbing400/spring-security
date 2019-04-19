package com.six.landing.dao;

import com.six.landing.domian.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
@Mapper
@Component("userDao")
public interface UsersDao {

    User findByUserName(String name);

    int insert(User user);
}

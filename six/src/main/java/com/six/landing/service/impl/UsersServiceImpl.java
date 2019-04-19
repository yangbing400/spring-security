package com.six.landing.service.impl;

import com.six.landing.dao.UsersDao;
import com.six.landing.domian.User;
import com.six.landing.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersDao usersDao;

    @Override
    public User getByUserName(String name) {

        return usersDao.findByUserName(name);
    }
}

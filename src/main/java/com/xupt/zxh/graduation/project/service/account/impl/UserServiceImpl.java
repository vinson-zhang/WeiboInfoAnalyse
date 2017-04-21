package com.xupt.zxh.graduation.project.service.account.impl;

import com.xupt.zxh.graduation.project.bean.account.User;
import com.xupt.zxh.graduation.project.dao.account.UserDao;
import com.xupt.zxh.graduation.project.service.account.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 张涛 on 2017/4/17.
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    @Override
    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email);
        return user;
    }

    @Override
    public boolean editPassword(String oldPassword, String newPassword) {
        //临时暂定
        String email = "563375002@qq.com";
        User user = userDao.getUserByEmail(email);
        if(user.getPassword().equals(oldPassword)){
            user.setPassword(newPassword);
            userDao.editPassword(user);
            return true;
        }
        return false;
    }
}

package com.techpulseIt.workflowengine.service;



import com.techpulseIt.workflowengine.entity.User;

import java.util.List;

public interface IUserService {
    User registerUser(User user);
    List<User> getUsers();
    void  deleteUser(String email);

    User getUser(String email);
}
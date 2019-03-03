package com.example.web_socket.model;

import java.security.Principal;

/**
 * @ClassName  MyPrincipal
 * @Description
 * @author  lixueyun
 * @Date  2019/3/1 17:45
 */
public class MyPrincipal implements Principal {

    private User user;

    public MyPrincipal(User user){
        this.user = user;
    }

    @Override
    public String getName() {
        return this.user.getName();
    }
}

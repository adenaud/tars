package com.rcon4games.tars.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsersController {

    @RequestMapping("/users")
    public String users(){
        return "user/users";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String loginForm(Model model){
        return "user/login";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(@RequestParam(name = "username", required = true) String username, @RequestParam(name = "password", required = true) String password, Model model){
        boolean authOK = false;
        if(authOK){
            return "redirect:home";
        }else{
            return "user/login";
        }
    }

    @RequestMapping(value = "/user-create",method = RequestMethod.GET)
    public String createUserForm(Model model){
        return "user/create";
    }

    @RequestMapping(value = "/user-create",method = RequestMethod.POST)
    public String createUser(@RequestParam(name = "username", required = true) String username,
                             @RequestParam(name = "email", required = true) String email,
                             @RequestParam(name = "password", required = true) String password,
                             @RequestParam(name = "cpassword", required = true) String cpassword,
                             Model model){
        return "user/create";
    }
}

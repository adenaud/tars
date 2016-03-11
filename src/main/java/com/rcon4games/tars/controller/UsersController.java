package com.rcon4games.tars.controller;

import com.rcon4games.tars.dao.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsersController {

    @Autowired
    private UserDao userDao;

    @RequestMapping("/users")
    public String users() {
        return "user/users";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(Model model) {
        return "user/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(name = "username", required = true) String username, @RequestParam(name = "password", required = true) String password, Model model) {
        boolean authOK = false;
        if (authOK) {
            return "redirect:home";
        } else {
            return "user/login";
        }
    }

    @RequestMapping(value = "/user-create", method = RequestMethod.GET)
    public String createUserForm(Model model) {
        return "user/create";
    }

    @RequestMapping(value = "/user-create", method = RequestMethod.POST)
    public String createUser(@RequestParam(name = "username", required = true) String username,
                             @RequestParam(name = "email", required = true) String email,
                             @RequestParam(name = "password", required = true) String password,
                             @RequestParam(name = "cpassword", required = true) String cpassword,
                             Model model) {

        boolean error = false;
        if (!StringUtils.isEmpty(username)) {
                //TODO check if username exist
        }else{
            error = true;
            model.addAttribute("usernameError","true");
        }
        if (StringUtils.isEmpty(email)) {
            error = true;
            model.addAttribute("emailError","true");
        }
        if (StringUtils.isEmpty(password)) {
            error = true;
            model.addAttribute("passwordError","true");
        }
        if (StringUtils.isEmpty(cpassword)) {
            error = true;
            model.addAttribute("cPasswordError","true");
        }

        if(StringUtils.isNoneEmpty(password,cpassword) && !cpassword.equals(password)){
            error = true;
            model.addAttribute("cPasswordError","true");
        }
        if(!error){
            userDao.create(username,password,email);
            model.addAttribute("success","");
        }
        return "user/create";
    }
}

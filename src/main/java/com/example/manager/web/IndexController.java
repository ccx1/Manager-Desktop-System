package com.example.manager.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/success")
    public String index() {
        System.out.println("调用");
        return "index";
    }

    @GetMapping("/")
    public String login() {
        return "login";
    }


}

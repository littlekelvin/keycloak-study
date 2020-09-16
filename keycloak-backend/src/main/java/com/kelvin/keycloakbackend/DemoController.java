package com.kelvin.keycloakbackend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin()
public class DemoController {

    @RequestMapping("/")
    public String index() {
        return "index everyone can see";
    }

    @RequestMapping("/users/user")
    public String customer() {
        return "only commonuser can see";
    }

    @RequestMapping("/admins/admin")
    public String admin() {
        return "only superadmin cas see";
    }
}

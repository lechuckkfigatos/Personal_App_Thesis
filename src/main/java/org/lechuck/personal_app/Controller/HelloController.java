package org.lechuck.personal_app.Controller;

import org.lechuck.personal_app.Service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HelloController {

    private final UserService userService;

    public HelloController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping()
//    public User getUserByIds(@RequestBody() RequestDTO requestDTO){
//        return userService.findUserByIds(requestDTO.getId());
//    }
}

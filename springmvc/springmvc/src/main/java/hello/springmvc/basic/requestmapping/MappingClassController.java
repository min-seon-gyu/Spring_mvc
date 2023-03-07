package hello.springmvc.basic.requestmapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mapping/users")
public class MappingClassController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    public String user(){
        return "get users";
    }

    @PostMapping
    public String addUser(){
        return "post users";
    }

    @GetMapping("/{userId}")
    public String findUser(@PathVariable("userId") String data){
        return "get userId = "+ data;
    }

    @PatchMapping("/{userId}")
    public String updateUser(@PathVariable("userId") String data){
        return "update userId = "+ data;
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable("userId") String data){
        return "delete userId = "+ data;
    }




}

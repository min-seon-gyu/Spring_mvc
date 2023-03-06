package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LogTestController {

    @RequestMapping("/log-test")
    public String logTest(){
        String name = "Spring";

        log.info("info log={}", name);
        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.error("error log={}", name);
        log.warn("warn log={}", name);
        return "ok";
    }
}

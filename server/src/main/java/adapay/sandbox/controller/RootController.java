package adapay.sandbox.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author bin.zhang
 */
@Controller
public class RootController {


    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String index() {
        return "hello sandbox";
    }

}

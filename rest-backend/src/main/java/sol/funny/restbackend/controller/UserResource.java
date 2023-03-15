package sol.funny.restbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sol.funny.restbackend.security.JwtTokenUtil;

@RestController
public class UserResource {

    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @GetMapping("/abc")
    public String home() {
        return "0k";
    }

    @GetMapping("/genToken")
    public String genToken() {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("funnySol", "funnySol");
        return jwtTokenUtil.createToken(authenticationToken,false);
    }

    @GetMapping("/cbd")
    public String cbd() {
        return "0k";
    }

}
package sol.funny.restbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sol.funny.commonutils.sercurity.PasswordEncoder;
import sol.funny.datacore.beans.ResultData;
import sol.funny.datacore.entity.domain.Client;
import sol.funny.datacore.repository.ClientRepository;
import sol.funny.restbackend.model.SignInBean;
import sol.funny.restbackend.model.SignUpBean;
import sol.funny.restbackend.security.JwtTokenUtil;

import java.util.Date;

@RestController
public class UserResource {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("/ping")
    public String home() {
        return "0k";
    }

    @PostMapping("/signUp")
    public ResponseEntity<ResultData> signUp(@RequestBody SignUpBean request){
        if(!request.getConfirmPassword().equals(request.getPassword())) {
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("error! Password don't match!");
            resultData.setErrorCode("E501");
            return ResponseEntity.ok().body(resultData);
        }

        if(clientRepository.existsByUserNameAndStatus(request.getUserName(), "A")){
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("error! User name is exists!");
            resultData.setErrorCode("E501");
            return ResponseEntity.ok().body(resultData);
        }

        String encodePassword = PasswordEncoder.encode(request.getPassword());

        Client newClient = new Client();
        newClient.setLastUpdateDate(new Date());
        newClient.setUserName(request.getUserName());
        newClient.setPassword(encodePassword);
        newClient.setStatus("A");

        clientRepository.save(newClient);

        ResultData resultData  = ResultData.builder().errorMessage("ok")
                .errorCode("00")
                .data(newClient).build();
        return ResponseEntity.ok(resultData);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ResultData> signIn(@RequestBody SignInBean request) {
        Client client = clientRepository.getClientByUserNameAndStatus(request.getUserName(), "A");
        if (client == null) {
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("error! Client don't exists!");
            resultData.setErrorCode("E502");
            return ResponseEntity.ok().body(resultData);
        }

        if (PasswordEncoder.matches(request.getPassword(), client.getPassword())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(client.getUserName(), client.getPassword());
            String jwt = jwtTokenUtil.createToken(authenticationToken, false);
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("ok");
            resultData.setErrorCode("00");
            resultData.setData(jwt);
            return ResponseEntity.ok(resultData);
        } else {
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("error! Password isn't correct!");
            resultData.setErrorCode("E503");
            return ResponseEntity.ok().body(resultData);
        }
    }

    @GetMapping("/genToken")
    public String genToken() {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("funnySol", "funnySol");
        return jwtTokenUtil.createToken(authenticationToken, false);
    }

    @GetMapping("/cbd")
    public String cbd() {
        return "0k";
    }

}
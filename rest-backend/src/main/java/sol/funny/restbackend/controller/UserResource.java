package sol.funny.restbackend.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sol.funny.commonutils.common.CommonKeys;
import sol.funny.commonutils.common.ErrorCode;
import sol.funny.commonutils.sercurity.PasswordEncoder;
import sol.funny.datacore.beans.ResultData;
import sol.funny.datacore.entity.domain.Client;
import sol.funny.datacore.repository.ClientRepository;
import sol.funny.restbackend.model.SignInBean;
import sol.funny.restbackend.model.SignUpBean;
import sol.funny.restbackend.security.JwtTokenUtil;

import java.util.Date;
import java.util.Locale;

@RestController
public class UserResource {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MessageSource messageSource;

    @GetMapping("/ping")
    public String home() {
        return "0k";
    }

    @PostMapping("/signUp")
    public ResponseEntity<ResultData> signUp(@RequestBody SignUpBean request) {
        if (!request.getConfirmPassword().equals(request.getPassword())) {
            ResultData resultData = new ResultData();
            resultData.setErrorCode(ErrorCode.EC_PASSWORD_DO_NOT_MATCH);
            resultData.setErrorMessage(
                    messageSource.getMessage(ErrorCode.EC_PASSWORD_DO_NOT_MATCH, null, getLocale(request.getLanguageCode())));
            return ResponseEntity.ok().body(resultData);
        }

        if (clientRepository.existsByUserNameAndStatus(request.getUserName(), "A")) {
            ResultData resultData = new ResultData();
            resultData.setErrorCode(ErrorCode.EC_USER_NAME_IS_EXISTS);
            resultData.setErrorMessage(
                    messageSource.getMessage(ErrorCode.EC_USER_NAME_IS_EXISTS, null, getLocale(request.getLanguageCode())));

            return ResponseEntity.ok().body(resultData);
        }

        String encodePassword = PasswordEncoder.encode(request.getPassword());

        Client newClient = new Client();
        newClient.setLastUpdateDate(new Date());
        newClient.setUserName(request.getUserName());
        newClient.setPassword(encodePassword);
        newClient.setStatus("A");

        clientRepository.save(newClient);

        ResultData resultData = ResultData.builder().errorMessage("ok")
                .errorCode("00")
                .data(newClient).build();
        return ResponseEntity.ok(resultData);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ResultData> signIn(@RequestBody SignInBean request) {
        Client client = clientRepository.getClientByUserNameAndStatus(request.getUserName(), "A");
        if (client == null) {
            ResultData resultData = new ResultData();
            resultData.setErrorCode(ErrorCode.EC_CLIENT_NOT_EXISTS);
            resultData.setErrorMessage(
                    messageSource.getMessage(ErrorCode.EC_CLIENT_NOT_EXISTS, null, getLocale(request.getLanguageCode())));

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
            resultData.setErrorCode(ErrorCode.EC_PASSWORD_NOT_CORRECT);
            resultData.setErrorMessage(
                    messageSource.getMessage(ErrorCode.EC_PASSWORD_NOT_CORRECT, null, getLocale(request.getLanguageCode())));
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

    public Locale getLocale(String languageCode) {
        languageCode = StringUtils.isEmpty(languageCode) ? CommonKeys.VIETNAMESE_LANGUAGE : languageCode;
        return new Locale(languageCode, "");
    }

}
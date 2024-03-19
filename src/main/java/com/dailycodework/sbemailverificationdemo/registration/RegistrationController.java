package com.dailycodework.sbemailverificationdemo.registration;

import com.dailycodework.sbemailverificationdemo.HttpResponseHandler;
import com.dailycodework.sbemailverificationdemo.DTO.LoginDTO;
import com.dailycodework.sbemailverificationdemo.event.RegistrationCompleteEvent;
import com.dailycodework.sbemailverificationdemo.registration.token.VerificationToken;
import com.dailycodework.sbemailverificationdemo.registration.token.VerificationTokenRepository;
import com.dailycodework.sbemailverificationdemo.response.LoginResponse;
import com.dailycodework.sbemailverificationdemo.user.User;
import com.dailycodework.sbemailverificationdemo.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sampson Alfred
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping(path = "/register")
    public Object registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user = userService.registerUser(registrationRequest);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        String successfulData = "Request successful:  Please, check your email for to complete your registration";
        HttpResponseHandler response = HttpResponseHandler.successResponse(successfulData);
        return response;
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){
        VerificationToken theToken = tokenRepository.findByToken(token);
        if (theToken.getUser().isEnabled()){
            return "This account has already been verified, please, login.";
        }
        String verificationResult = userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")){
            return "Email verified successfully. Now you can login to your account";
        }
        return "Invalid verification token";
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginEmployee(@RequestBody LoginDTO loginDTO)
    {
        LoginResponse loginResponse = userService.loginUser(loginDTO);
        return ResponseEntity.ok(loginResponse);
    }


    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}

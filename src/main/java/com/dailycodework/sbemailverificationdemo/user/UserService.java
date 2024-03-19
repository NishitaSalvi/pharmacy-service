package com.dailycodework.sbemailverificationdemo.user;

import com.dailycodework.sbemailverificationdemo.DTO.LoginDTO;
import com.dailycodework.sbemailverificationdemo.exception.UserAlreadyExistsException;
import com.dailycodework.sbemailverificationdemo.registration.RegistrationRequest;
import com.dailycodework.sbemailverificationdemo.registration.token.VerificationToken;
import com.dailycodework.sbemailverificationdemo.registration.token.VerificationTokenRepository;
import com.dailycodework.sbemailverificationdemo.response.LoginResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * @author Sampson Alfred
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
       Optional<User> user = this.findByEmail(request.email());
       if (user.isPresent()){
           throw new UserAlreadyExistsException(
                   "User with email "+request.email() + " already exists");
       }
       var newUser = new User();
       newUser.setFirstName(request.firstName());
       newUser.setLastName(request.lastName());
       newUser.setEmail(request.email());
       newUser.setPassword(passwordEncoder.encode(request.password()));
       newUser.setRole(request.role());
        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token, theUser);
        tokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = tokenRepository.findByToken(theToken);
        if(token == null){
            return "Invalid verification token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            tokenRepository.delete(token);
            return "Token already expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public LoginResponse loginUser(LoginDTO loginDTO) {
        String msg = "";
        Optional<User> user1 = userRepository.findByEmail(loginDTO.getEmail());
        if (user1 != null) {
            String password = loginDTO.getPassword();
            String encodedPassword = user1.map(User::getPassword).orElse(null);
            Boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<User> user = userRepository.findOneByEmailAndPassword(loginDTO.getEmail(), encodedPassword);
                if (user.isPresent()) {
                    return new LoginResponse("Login Success", true);
                } else {
                    return new LoginResponse("Login Failed", false);
                }
            } else {
                return new LoginResponse("password Not Match", false);
            }
        }else {
            return new LoginResponse("Email not exits", false);
        }
    
    }
}

package com.dailycodework.sbemailverificationdemo.DTO;

public class LoginDTO {

    private String email;
    private String password;

    public LoginDTO() {
    }

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
        
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    @Override
    public String toString(){
        return "LoginDTO{" + "email= " + email + '\'' + ", password= " + password + '\'' + '}'  ;
    }
    
}

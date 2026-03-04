package com.example.securitydemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 註冊數據傳輸對象 (DTO)
 * 負責承接前端註冊表單數據，並透過 Bean Validation 執行初步的數據合法性校驗。
 */
public class RegisterForm {

    @NotBlank(message = "Username 不可為空")
    @Size(min = 3, max = 20, message = "Username 長度需介於 3 到 20 字元")
    private String username;

    @NotBlank(message = "Password 不可為空")
    @Size(min = 8, max = 20, message = "Password 長度需至少為 8 碼")
    private String password;

    @NotBlank(message = "請再次輸入密碼以進行確認")
    private String confirmPassword;

    public RegisterForm() { }

    public String getUsername() { 
        return username; 
    }

    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPassword() { 
        return password; 
    }

    public void setPassword(String password) { 
        this.password = password; 
    }

    public String getConfirmPassword() { 
        return confirmPassword; 
    }

    public void setConfirmPassword(String confirmPassword) { 
        this.confirmPassword = confirmPassword; 
    }
}

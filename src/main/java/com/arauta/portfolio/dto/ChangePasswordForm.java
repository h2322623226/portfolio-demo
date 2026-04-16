package com.arauta.portfolio.dto;

import jakarta.validation.constraints.Size;

public class ChangePasswordForm {

    private String oldPassword;

    @Size(min = 8, max = 20, message = "密碼長度需介於 8 到 20 字元")
    private String newPassword;

    private String confirmPassword;

    public ChangePasswordForm() { }

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}

package com.simplevat.entity;

public enum MailEnum {


    FORGOT_PASSWORD("Forgot your password? Don't worry",
            "<p>Dear ${user},</p><br/><p> Your password is reset successfully, your new password is : <pre>${newPassword}</pre> </p> <br/> Thank you."),
    SIGN_UP_VERIFICATION("Welcome to SimpleVat",
            "<p>Dear ${user},</p><br/><p>Thank you for joining us at Simplevat. </p> <br/> Thank you."),
    INVOICE_PDF("Welcome to SimpleVat",
            "<p>Dear ${user},</p><br/><p>Please pay your bill. </p> <br/> Thank you."),
    NEW_USER_CREATED("Welcome to SimpleVat",
            "<p>Dear ${user},</p><br/><p>Your user account created successfully </p> <br/> Thank you.");

    private String subject;
    private String body;

    MailEnum(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}

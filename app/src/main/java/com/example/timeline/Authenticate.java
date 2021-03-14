package com.example.timeline;

import android.widget.EditText;

public class Authenticate {

    public static boolean isValidAdmin(EditText etUser, EditText etPass){
        boolean vUser = false, vPass = false;
        String user = etUser.getText().toString().toLowerCase().trim();
        String pass = etPass.getText().toString().toLowerCase().trim();

        if (user.length() <= 5 ){
            etUser.setError("نام کاربری باید بیش از 5 رقم باشد.");
            etUser.requestFocus();
        } else { vUser = true;}

        if (pass.length() <= 5){
            etPass.setError("رمز عبور باید بیش از 5 رقم باشد.");
            etPass.requestFocus();
        }

        if (pass.split(" ").length >= 2){
            etPass.setError("رمز عبور نباید شامل کاراکتر فاصله باشد.");
            etPass.requestFocus();
        }

        if (pass.length() > 5 && pass.split(" ").length < 2){
            vPass = true;
        }

        return (vUser && vPass);
    }

    public static boolean isValidStudent(EditText etUser, EditText etPass, EditText etPhone){
        boolean vUser = false, vPass = false, vPhone = false;
        String user = etUser.getText().toString().toLowerCase().trim();
        String pass = etPass.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (user.length() <= 5 ){
            etUser.setError("نام کاربری باید بیش از 5 کاراکتر باشد");
            etUser.requestFocus();
        } else { vUser = true;}

        if (pass.length() < 8){
            etPass.setError("شماره دانشجویی باید بیش از 7 رقم باشد");
            etPass.requestFocus();
        }

        if (pass.split(" ").length >= 2){
            etPass.setError("شماره دانشجویی نباید شامل کاراکتر فاصله باشد");
            etPass.requestFocus();
        }

        if (pass.length() > 7 && pass.split(" ").length < 2){
            vPass = true;
        }

        if (phone.length() != 11){
            etPhone.setError("شماره موبایل باید 11 رقم باشد");
            etPhone.requestFocus();
        }

        if (!phone.startsWith("09")){
            etPhone.setError("شماره موبایل باید 09 شروع شود");
            etPhone.requestFocus();
        }

        if (phone.split(" ").length >= 2){
            etPhone.setError("شماره موبایل نباید شامل فاصله باشد");
            etPhone.requestFocus();
        }

        if (phone.length() == 11 && phone.startsWith("09") && phone.split(" ").length < 2){
            vPhone = true;
        }

        return (vUser && vPass && vPhone);
    }

    public static boolean isValidLogin(EditText etUser, EditText etPass){
        boolean vUser = false, vPass = false;
        String user = etUser.getText().toString().toLowerCase().trim();
        String pass = etPass.getText().toString().trim();

        if (user.length() <= 5 ){
            etUser.setError("نام کاربری باید بیش از 5 کاراکتر باشد");
            etUser.requestFocus();
        } else { vUser = true;}

        if (pass.length() < 8){
            etPass.setError("شماره دانشجویی باید بیش از 7 رقم باشد");
            etPass.requestFocus();
        }

        if (pass.split(" ").length >= 2){
            etPass.setError("شماره دانشجویی نباید شامل کاراکتر فاصله باشد");
            etPass.requestFocus();
        }

        if (pass.length() > 7 && pass.split(" ").length < 2){
            vPass = true;
        }

        return (vUser && vPass);
    }

}

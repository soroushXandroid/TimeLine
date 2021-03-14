package com.example.timeline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timeline.admin.ScrollingActivity;
import com.example.timeline.student.PublicActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements TextWatcher {

    EditText etAdminUser, etAdminPass, etStudentUser, etStudentPass,
            etStNumber, etStUser, etStudentPhone;
    TextView tvStatus;
    Button btnLogin, btnSend;
    RadioGroup rgUsers;
    RadioButton rbAdmin, rbUser;
    Switch swInOn;
    ProgressBar pbLoading;
    ConstraintLayout adminLayout, studentLogin, studentLogon, logInOn;

    public static List<String> adminList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findElements();

        etAdminUser.addTextChangedListener(this);
        etStUser.addTextChangedListener(this);
        etStudentUser.addTextChangedListener(this);

        rgUsers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb_admin){

                    btnLogin.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
                    adminLayout.setVisibility(View.VISIBLE);
                    studentLogin.setVisibility(View.GONE);
                    studentLogon.setVisibility(View.GONE);
                    logInOn.setVisibility(View.GONE);
                    etStudentUser.setText("");
                    etStudentPass.setText("");
                    etStUser.setText("");
                    etStNumber.setText("");
                    etStudentPhone.setText("");
                    swInOn.setChecked(true);
                    tvStatus.setText("");

                } else if (checkedId == R.id.rb_student){

                    adminLayout.setVisibility(View.GONE);
                    studentLogon.setVisibility(View.VISIBLE);
                    logInOn.setVisibility(View.VISIBLE);
                    etAdminUser.setText("");
                    etAdminPass.setText("");
                    tvStatus.setText("");

                } }});


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onStart() {
        super.onStart();
        //btnLogin
    }

    @Override
    protected void onResume() {
        super.onResume();

        getAdmins();
        swInOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int active = rgUsers.getCheckedRadioButtonId();

                if (isChecked){
                    if (active != R.id.rb_admin) {
                        btnLogin.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.GONE);
                        studentLogon.setVisibility(View.VISIBLE);
                        studentLogin.setVisibility(View.GONE);
                        etStudentUser.setText("");
                        etStudentPass.setText("");
                        etStudentPhone.setText("");
                        tvStatus.setText("");
                    }
                } else {
                    btnLogin.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                    studentLogon.setVisibility(View.GONE);
                    studentLogin.setVisibility(View.VISIBLE);
                    etStUser.setText("");
                    etStNumber.setText("");
                    tvStatus.setText("");
                } }});

    }

    private void findElements(){
        etAdminPass = findViewById(R.id.password);
        etAdminUser = findViewById(R.id.username);
        etStudentUser = findViewById(R.id.student_username);
        etStudentPass = findViewById(R.id.student_password);
        etStUser = findViewById(R.id.logon_stuser);
        etStNumber = findViewById(R.id.logon_stnumber);
        etStudentPhone = findViewById(R.id.student_phone);
        tvStatus = findViewById(R.id.tv_status);
        btnLogin = findViewById(R.id.login);
        btnSend = findViewById(R.id.login1);
        rgUsers = findViewById(R.id.radioGroup);
        rbAdmin = findViewById(R.id.rb_admin);
        rbUser = findViewById(R.id.rb_student);
        adminLayout = findViewById(R.id.admin_form);
        studentLogin = findViewById(R.id.student_login_form);
        studentLogon = findViewById(R.id.student_logon_form);
        logInOn = findViewById(R.id.log_in_on_layout);
        swInOn = findViewById(R.id.switch1);
        pbLoading = findViewById(R.id.loading);
    }

    public void login(View v){

        if (v.getId() == R.id.login){

            if (rgUsers.getCheckedRadioButtonId() == R.id.rb_admin && Authenticate.isValidAdmin(etAdminUser, etAdminPass)){

                String username = etAdminUser.getText().toString().trim();
                String password = etAdminPass.getText().toString().trim();
                checkAdmin(username, password);

            } else if (rgUsers.getCheckedRadioButtonId() == R.id.rb_student && swInOn.isChecked()
                    && Authenticate.isValidLogin(etStUser, etStNumber)){

                String username = etStUser.getText().toString().toLowerCase().trim();
                String password = etStNumber.getText().toString().trim();
                checkStudent(username, password);

            } else {
                tvStatus.setText(R.string.invalid_data);
                tvStatus.setTextColor(Color.RED);
            }

        } else if (v.getId() == R.id.login1){

             if (rgUsers.getCheckedRadioButtonId() == R.id.rb_student && !swInOn.isChecked()
                    && Authenticate.isValidStudent(etStudentUser, etStudentPass, etStudentPhone)){

                String username = etStudentUser.getText().toString().trim();
                String password = etStudentPass.getText().toString().trim();
                String phone = etStudentPhone.getText().toString().trim();
                saveNewStudent(username, password, phone);
                nextStep();

            } else {
                 tvStatus.setText(R.string.invalid_data);
                 tvStatus.setTextColor(Color.RED);
             }

        }

    }

    private void saveNewStudent(final String user, final String pass, final String phone){

        pbLoading.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL + App.SAVE_STUDENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tvStatus.setText(response);
                        tvStatus.setTextColor(0xFF69b5b0);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvStatus.setText("اشکال در ارتباط");
                    }})
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("username", user);
                map.put("password", pass);
                map.put("phone", phone);
                return map;

            }};

        App.queue.add(request);
    }

    private void checkAdmin(final String user, final String pass){

        StringRequest request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL + App.CHECK_ADMIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String res = response.trim();
                        if ("1".equals(res)) {

                            tvStatus.setText("اطلاعات وارد شده صحیح است.");
                            tvStatus.setTextColor(0xFF00EA00);
                            adminEntrance(user);

                        }
                        else {

                            tvStatus.setText("کاربری با این اطلاعات وجود ندارد.");
                            tvStatus.setTextColor(Color.RED);

                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "onErrorResponse: ", error);
                    }})
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("username", user);
                map.put("password", pass);
                return map;

            }};

        App.queue.add(request);
    }

    private void checkStudent(final String user, final String pass){

        StringRequest request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL + App.CHECK_STUDENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String res = response.trim();
                        if ("1".equals(res)) {

                            tvStatus.setText("اطلاعات وارد شده صحیح است.");
                            tvStatus.setTextColor(Color.GREEN);
                            studentEntrance(user);

                        }
                        else {

                            tvStatus.setText("کاربری با این اطلاعات وجود ندارد.");
                            tvStatus.setTextColor(Color.RED);

                        }
                    }}, null)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("username", user);
                map.put("password", pass);
                return map;

            }};

        App.queue.add(request);
    }

    private void adminEntrance(final String username){

        pbLoading.setVisibility(View.VISIBLE);
        new CountDownTimer(2000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent i = new Intent(LoginActivity.this, ScrollingActivity.class);
                App.username = username;
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                pbLoading.setVisibility(View.GONE);
                finish();
            }

        }.start();

    }

    private void studentEntrance(final String username){

        pbLoading.setVisibility(View.VISIBLE);
        new CountDownTimer(2000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent i = new Intent(LoginActivity.this, PublicActivity.class);
                App.username = username;
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                pbLoading.setVisibility(View.GONE);
                finish();
            }

        }.start();

    }

    private void nextStep(){
        new CountDownTimer(1000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                pbLoading.setVisibility(View.GONE);
                swInOn.setChecked(true);
                tvStatus.setText("برای ورود نام کاربری و رمز عبور خود را دوباره وارد کنید");
                tvStatus.setTextColor(0xFF69b5b0);
            }

        }.start();
    }

    private void getAdmins(){
        StringRequest request = new StringRequest(
                App.BASE_URL + App.GET_ADMIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LoginActivity.adminList = JsonParser.parseAdmins(response);
                    }}, null);
        App.queue.add(request);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.length() > 0){
            btnLogin.setEnabled(true);
            btnSend.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
            btnSend.setEnabled(false);
        }

    }
    @Override
    public void afterTextChanged(Editable s) {

    }
}


package com.example.timeline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatActivity extends AppCompatActivity {

    FloatingActionButton fab;
    ChatImageUpload retrofitUp;
    boolean isAccess, isDrawerOpen, isPhoneAccess;
    String source, dest, number, phone;
    Dialog dialog;
    EditText etMsgbox, etSearch;
    TextView tvFlowingUsername, tvLogoUser, tvLogoNumber;
    LinearLayout settingLinear, searchLinear, logoutLinear, msgboxLinear;
    ConstraintLayout searchcLayout, logoLayout;
    CoordinatorLayout parentLayout;
    FlowingDrawer fDrawer;
    CircleImageView icSend;
    ImageView ivBack, ivCall;
    RecyclerView recyclerDocs;
    PrivateChatAdapter adapter, searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_flowing_drawer_menu);
        findElements();
        setUploadImage();
        setUploadText();
        configureDrawer();
        isDrawerOpen = false;
        retrofitUp = new ChatImageUpload(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            dest = bundle.getString("destination");
            number = bundle.getString("number");
            phone = bundle.getString("phone");
        } else {
            dest = "نامشخص";
            number = "0000";
            phone = "0";
        }
        source = App.username;
        tvLogoUser.setText(dest);
        tvLogoNumber.setText(number);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isAccess = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            isPhoneAccess = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
        }

        etMsgbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){

                    icSend.setImageResource(R.drawable.icon_send);
                    icSend.setBorderWidth(0);

                } else {

                    icSend.setImageResource(R.drawable.icon_send_active);
                    icSend.setBorderWidth(1);
                    icSend.setBorderColor(getResources().getColor(R.color.active_send_color));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvFlowingUsername.setText(source);
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { prepareCall(); }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL_PRIVATE + App.GET_ALL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        List<ChatModel> list = JsonParser.parseChatDocs(response);
                        adapter = new PrivateChatAdapter(list);
                        recyclerDocs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerDocs.setAdapter(adapter);

                    }}, null)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("source", source);
                params.put("destination", dest);
                return params;
            }
        };
        App.queue.add(request);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerDocs.setAdapter(adapter);
                searchcLayout.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                msgboxLinear.setVisibility(View.VISIBLE);
                logoLayout.setVisibility(View.VISIBLE);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        App.BASE_URL_PRIVATE + App.SEARCH_TEXT_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                List<ChatModel> searchList = JsonParser.parseChatDocs(response);
                                searchAdapter = new PrivateChatAdapter(searchList);
                                recyclerDocs.setAdapter(searchAdapter);

                            }}, null)
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("source", source);
                        params.put("destination", dest);
                        params.put("query", s + "");
                        return params;
                    }
                };
                App.queue.add(request);
            }
            @Override
            public void afterTextChanged(Editable s) {}});

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (App.theme){
            parentLayout.setBackgroundResource(R.drawable.login_bg1);
            msgboxLinear.setBackgroundColor(Color.argb(100, 121, 174, 205));
            findViewById(R.id.chat_app_bar).setBackgroundColor(Color.argb(100, 121, 174, 205));
            etSearch.setTextColor(Color.BLACK);
            etMsgbox.setTextColor(Color.BLACK);
            etSearch.setHintTextColor(Color.rgb(20, 20, 20));
            etMsgbox.setHintTextColor(Color.rgb(20, 20, 20));
            tvLogoUser.setTextColor(Color.BLACK);
            tvLogoNumber.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onBackPressed() {

        if (isDrawerOpen){
            fDrawer.closeMenu(true);
        } else if (searchcLayout.getVisibility() == View.VISIBLE){
            recyclerDocs.setAdapter(adapter);
            searchcLayout.setVisibility(View.GONE);
            logoLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            msgboxLinear.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {

        if (!isAccess) {
            if (requestCode == App.PERMS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isAccess = true;
                Snackbar.make(fab, R.string.storage_access
                        , BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.GREEN).show();
            }

        }

        if (requestCode == App.PERMS_CALL && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isPhoneAccess = true;
            Snackbar.make(fab, "دسترسی به تلفن داده شده"
                    , BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.GREEN).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            initUploadDialog(selectedImage);

        }
    }

    private void findElements(){
        parentLayout = findViewById(R.id.user_chat_parent);
        fab = findViewById(R.id.fab_chat);
        recyclerDocs = findViewById(R.id.chat_document_recycler);
        etMsgbox = findViewById(R.id.chat_et_msgbox);
        icSend = findViewById(R.id.chat_btn_send_message);
        fDrawer = findViewById(R.id.chat_drawer_layout);
        tvFlowingUsername = findViewById(R.id.chat_tv_username_menu);
        searchcLayout = findViewById(R.id.search_layout);
        etSearch = findViewById(R.id.chat_et_search);
        ivBack = findViewById(R.id.chat_iv_back_search);
        ivCall = findViewById(R.id.iv_chat_call);
        msgboxLinear = findViewById(R.id.chat_msgbox_linear);
        logoLayout = findViewById(R.id.chat_logo_layout);
        tvLogoUser = findViewById(R.id.chat_tv_contact_username);
        tvLogoNumber = findViewById(R.id.chat_tv_contact_number);
    }

    private void setUploadImage(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAccess){
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);
                } else {
                    ActivityCompat.requestPermissions(UserChatActivity.this, App.APP_PERMS, App.PERMS_CODE);
                    Snackbar.make(fab, R.string.storage_no_access
                            , BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.RED).show();
                }

            }});
    }

    private void setUploadText(){
        icSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String txt = etMsgbox.getText().toString().trim();
                final String time = getDateTime();
                if (!txt.isEmpty()){

                    adapter.addMessage(source, dest, txt, time);
                    etMsgbox.setText("");

                    StringRequest request = new StringRequest(
                            Request.Method.POST,
                            App.BASE_URL_PRIVATE + App.UPLOAD_MESSAGE_URL,
                            null, null)
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("source", source);
                            map.put("destination", dest);
                            map.put("text", txt);
                            map.put("time", time);
                            return map;
                        }};

                    App.queue.add(request);
                }
            }});
    }

    private void initUploadDialog(final Uri selectedImage){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.desc_dialog);

        final EditText caption = dialog.findViewById(R.id.et_capt_dialog);
        Button btnSend, btnCancel;
        btnCancel = dialog.findViewById(R.id.brn_cancel);
        btnSend = dialog.findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = caption.getText().toString().trim();
                retrofitUp.uploadFile(selectedImage, source, dest, text, getDateTime());
                dialog.dismiss();
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }});

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }});

        dialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    private String getDateTime(){

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date newDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date myDate = null;
        try {
            myDate = df.parse(formatter.format(newDate));
        } catch (ParseException e) { e.printStackTrace(); }
        df.setTimeZone(TimeZone.getTimeZone("GMT+7:30"));

        //CharSequence s  = DateFormat.format("MMMM d, yyyy ", newDate.getTime());

        return df.format(myDate);
    }

    private void configureDrawer(){

        fDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        fDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_OPEN){

                    isDrawerOpen = true;

                    settingLinear = findViewById(R.id.chat_setting_linear);
                    settingLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(UserChatActivity.this, SettingsActivity.class);
                            startActivity(i);
                            fDrawer.closeMenu(false);
                        }});

                    searchLinear = findViewById(R.id.chat_search_linear);
                    searchLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fDrawer.closeMenu(true);
                            searchcLayout.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.INVISIBLE);
                            msgboxLinear.setVisibility(View.INVISIBLE);
                            logoLayout.setVisibility(View.GONE);
                        }});

                    logoutLinear = findViewById(R.id.chat_logout_linear);
                    logoutLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(UserChatActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }});

                }
                else if (newState == ElasticDrawer.STATE_CLOSED){
                    isDrawerOpen = false;
                }

            }@Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {}});

    }

    private void prepareCall(){

        if (!isPhoneAccess){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CALL_PHONE}, App.PERMS_CALL);
            Snackbar.make(fab, "دسترسی به تلفن ندارید"
                    , BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.RED).show();
        }
        else {

            if (phone.equals("0")){
                Snackbar.make(fab, "شماره ی مخاطب ثبت نشده"
                        , BaseTransientBottomBar.LENGTH_LONG).setTextColor(Color.GREEN).show();
            } else {
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + phone));
                startActivity(call);
            }
        }

    }

}

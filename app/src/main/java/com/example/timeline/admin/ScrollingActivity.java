package com.example.timeline.admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.timeline.App;
import com.example.timeline.DocModel;
import com.example.timeline.DocumentAdapter;
import com.example.timeline.JsonParser;
import com.example.timeline.LoginActivity;
import com.example.timeline.SettingsActivity;
import com.example.timeline.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeline.R;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScrollingActivity extends AppCompatActivity {

    FloatingActionButton fab;
    RetrofitFileUpload retrofitUp;
    boolean isAccess, isDrawerOpen;
    Dialog dialog;
    EditText etMsgbox, etSearch;
    TextView tvFlowingUsername, tvContact, tvSetting, tvSearch, tvFav, tvLogout;
    LinearLayout contactLinear, favLinear, settingLinear, searchLinear, logoutLinear, msgboxLinear;
    ConstraintLayout searchcLayout;
    CoordinatorLayout parentLayout;
    CardView menuFrame, contactFrame;
    FlowingDrawer fDrawer;
    CircleImageView icSend;
    ImageView ivBack, ivBackContact;
    RecyclerView recyclerDocs, contactRecycler;
    DocumentAdapter adapter, searchAdapter;
    ContactAdapter cAdapter;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flowing_drawer_menu);
        App.context = getApplicationContext();
        username = App.username;
        findElements();
        setUploadImage();
        setUploadText();
        configureDrawer();
        retrofitUp = new RetrofitFileUpload(this);
        isDrawerOpen = false;
        tvFlowingUsername.setText(username);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            isAccess = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

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
        ivBackContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFrame.setVisibility(View.VISIBLE);
                contactFrame.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i("TIME", "onStart: " + getDateTime());
        super.onStart();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        App.fontType = sharedPref.getString("font-type", "nazanin");
        App.fontSize = sharedPref.getInt("font-size", 18);
        App.theme = sharedPref.getBoolean("theme", false);
        Typeface tf = Typeface.createFromAsset(App.context.getAssets(), "fonts/" + App.fontType + ".ttf");
        tvFlowingUsername.setTypeface(tf);
        tvFav.setTypeface(tf);
        tvContact.setTypeface(tf);
        tvSetting.setTypeface(tf);
        tvSearch.setTypeface(tf);
        tvLogout.setTypeface(tf);

        if (App.theme){
            parentLayout.setBackgroundResource(R.drawable.login_bg1);
            msgboxLinear.setBackgroundColor(Color.argb(100, 121, 174, 205));
            findViewById(R.id.app_bar).setBackgroundColor(Color.argb(100, 121, 174, 205));
            etSearch.setTextColor(Color.BLACK);
            etMsgbox.setTextColor(Color.BLACK);
            etSearch.setHintTextColor(Color.rgb(20, 20, 20));
            etMsgbox.setHintTextColor(Color.rgb(20, 20, 20));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StringRequest request = new StringRequest(
                App.BASE_URL_PUBLIC + App.GET_ALL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        List<DocModel> list = JsonParser.parseDocs(response);
                        adapter = new DocumentAdapter(list);
                        recyclerDocs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerDocs.setAdapter(adapter);

                    }}, null);
        App.queue.add(request);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerDocs.setAdapter(adapter);
                searchcLayout.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                msgboxLinear.setVisibility(View.VISIBLE);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        App.BASE_URL_PUBLIC + App.SEARCH_TEXT_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                List<DocModel> searchList = JsonParser.parseDocs(response);
                                searchAdapter = new DocumentAdapter(searchList);
                                recyclerDocs.setAdapter(searchAdapter);

                            }}, null)
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
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
    public void onBackPressed() {

        if (isDrawerOpen){
            fDrawer.closeMenu(true);
        } else if (searchcLayout.getVisibility() == View.VISIBLE){
            recyclerDocs.setAdapter(adapter);
            searchcLayout.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            msgboxLinear.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == App.PERMS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccess = true;
            Snackbar.make(fab, R.string.storage_access
                    , BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.GREEN).show();

        } else {

            isAccess = false;
            Snackbar.make(fab, R.string.storage_no_access
                    , BaseTransientBottomBar.LENGTH_SHORT).setTextColor(Color.RED).show();

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
        parentLayout = findViewById(R.id.admin_parent_layout);
        fab = findViewById(R.id.fab);
        recyclerDocs = findViewById(R.id.document_recycler);
        etMsgbox = findViewById(R.id.et_msgbox);
        icSend = findViewById(R.id.btn_send_message);
        fDrawer = findViewById(R.id.drawer_layout);
        tvFlowingUsername = findViewById(R.id.tv_username_menu);
        searchcLayout = findViewById(R.id.logo_layout);
        etSearch = findViewById(R.id.et_search);
        ivBack = findViewById(R.id.iv_back_search);
        ivBackContact = findViewById(R.id.iv_back_contact);
        msgboxLinear = findViewById(R.id.msgbox_linear);
        menuFrame = findViewById(R.id.menu_frame);
        contactFrame = findViewById(R.id.contact_frame);
        contactRecycler = findViewById(R.id.contact_recycler);
        tvContact = findViewById(R.id.tv_contact_menu);
        tvSearch = findViewById(R.id.tv_search_menu);
        tvSetting = findViewById(R.id.tv_setting_menu);
        tvFav = findViewById(R.id.tv_fav_menu);
        tvLogout = findViewById(R.id.tv_logout_menu);
    }

    private void setUploadImage(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAccess){
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);
                } else {
                    ActivityCompat.requestPermissions(ScrollingActivity.this, App.APP_PERMS, App.PERMS_CODE);
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

                    if (username.isEmpty()){
                        adapter.addMessage(txt, "", 0, "",  time);
                    } else {
                        adapter.addMessage(txt, username, 0, "",  time);
                    }

                    etMsgbox.setText("");

                    StringRequest request = new StringRequest(
                            Request.Method.POST,
                            App.BASE_URL_PUBLIC + App.UPLOAD_MESSAGE_URL,
                            null, null)
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("user", username);
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
                retrofitUp.uploadFile(selectedImage, username, text, getDateTime());
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
        df.setTimeZone(TimeZone.getTimeZone("GMT+0:0"));
        String formattedDate = df.format(myDate);

        CharSequence s  = DateFormat.format("MMMM d, yyyy ", newDate.getTime());

        return s + "| " + formattedDate;
    }

    private void configureDrawer(){

        fDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        fDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_OPEN){

                    isDrawerOpen = true;

                    contactLinear = findViewById(R.id.contact_linear);
                    contactLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            menuFrame.setVisibility(View.GONE);
                            contactFrame.setVisibility(View.VISIBLE);

                            StringRequest request = new StringRequest(
                                    App.BASE_URL + App.GET_STUDENT_URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            List<UserModel> contactList = JsonParser.parseContacts(response);
                                            cAdapter = new ContactAdapter(contactList);
                                            contactRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                            contactRecycler.setAdapter(cAdapter);

                                        }}, null);
                            App.queue.add(request);

                        }});

                    favLinear = findViewById(R.id.favorite_linear);
                    favLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fDrawer.closeMenu(true);
                        }});

                    settingLinear = findViewById(R.id.setting_linear);
                    settingLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(App.context, SettingsActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            fDrawer.closeMenu(false);
                        }});

                    searchLinear = findViewById(R.id.search_linear);
                    searchLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fDrawer.closeMenu(true);
                            searchcLayout.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.INVISIBLE);
                            msgboxLinear.setVisibility(View.INVISIBLE);
                        }});

                    logoutLinear = findViewById(R.id.logout_linear);
                    logoutLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ScrollingActivity.this, LoginActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            finish();
                        }});

                }
                else if (newState == ElasticDrawer.STATE_CLOSED){
                    isDrawerOpen = false;
                    contactFrame.setVisibility(View.GONE);
                    menuFrame.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {}});

    }

}

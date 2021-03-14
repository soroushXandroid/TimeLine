package com.example.timeline.student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.example.timeline.admin.ContactAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timeline.R;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicActivity extends AppCompatActivity {

    FloatingActionButton fab;
    boolean isDrawerOpen;
    EditText etSearch;
    TextView tvFlowingUsername, tvContact, tvSetting, tvSearch, tvFav, tvLogout;
    LinearLayout contactLinear, favLinear, settingLinear, searchLinear, logoutLinear;
    ConstraintLayout searchcLayout;
    CoordinatorLayout parentLayout;
    CardView menuFrame, contactFrame;
    FlowingDrawer fDrawer;
    ImageView ivBack, ivBackContact;
    RecyclerView recyclerDocs, contactRecycler;
    DocumentAdapter adapter, searchAdapter;
    ContactAdapter cAdapter;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_flowing_drawer_menu);
        App.context = getApplicationContext();
        findElements();
        configureDrawer();
        isDrawerOpen = false;
        username = App.username;
        tvFlowingUsername.setText(username);
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
            findViewById(R.id.public_app_bar).setBackgroundColor(Color.argb(100, 121, 174, 205));
            etSearch.setTextColor(Color.BLACK);
            etSearch.setHintTextColor(Color.rgb(20, 20, 20));
        } else {
            parentLayout.setBackgroundResource(R.drawable.login_bg);
            findViewById(R.id.public_app_bar).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            etSearch.setTextColor(Color.WHITE);
            etSearch.setHintTextColor(getResources().getColor(R.color.silver));
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

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (isDrawerOpen){
                    fDrawer.closeMenu(true);
                } else {
                    fDrawer.openMenu(true);
                }
            }});

    }

    @Override
    public void onBackPressed() {

        if (isDrawerOpen){
            fDrawer.closeMenu(true);
        } else if (searchcLayout.getVisibility() == View.VISIBLE){
            recyclerDocs.setAdapter(adapter);
            searchcLayout.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }

    }

    private void findElements(){
        parentLayout = findViewById(R.id.public_parent);
        fab = findViewById(R.id.public_fab);
        recyclerDocs = findViewById(R.id.public_chat_recycler);
        fDrawer = findViewById(R.id.public_drawer);
        tvFlowingUsername = findViewById(R.id.tv_public_uersname);
        tvContact = findViewById(R.id.tv_public_contact);
        tvSearch = findViewById(R.id.tv_public_search);
        tvSetting = findViewById(R.id.tv_public_setting);
        tvFav = findViewById(R.id.tv_public_fav);
        tvLogout = findViewById(R.id.tv_public_logout);
        searchcLayout = findViewById(R.id.public_search_layout);
        etSearch = findViewById(R.id.et_search);
        ivBack = findViewById(R.id.iv_back_search);
        ivBackContact = findViewById(R.id.iv_public_back_contact);
        menuFrame = findViewById(R.id.public_frame);
        contactFrame = findViewById(R.id.public_contact_frame);
        contactRecycler = findViewById(R.id.public_contact_recycler);
    }

    private void configureDrawer(){

        fDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        fDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_OPEN){

                    isDrawerOpen = true;

                    contactLinear = findViewById(R.id.public_contact_linear);
                    contactLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            menuFrame.setVisibility(View.GONE);
                            contactFrame.setVisibility(View.VISIBLE);

                            StringRequest request = new StringRequest(
                                    App.BASE_URL + App.GET_ADMIN_URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            List<UserModel> contactList = JsonParser.parseAdminContacts(response);
                                            cAdapter = new ContactAdapter(contactList);
                                            contactRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                            contactRecycler.setAdapter(cAdapter);

                                        }}, null);
                            App.queue.add(request);

                        }});

                    favLinear = findViewById(R.id.public_favorite_linear);
                    favLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fDrawer.closeMenu(true);
                        }});

                    settingLinear = findViewById(R.id.public_setting_linear);
                    settingLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(App.context, SettingsActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            fDrawer.closeMenu(false);
                        }});

                    searchLinear = findViewById(R.id.public_search_linear);
                    searchLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fDrawer.closeMenu(true);
                            searchcLayout.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.INVISIBLE);
                        }});

                    logoutLinear = findViewById(R.id.public_logout_linear);
                    logoutLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(PublicActivity.this, LoginActivity.class);
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

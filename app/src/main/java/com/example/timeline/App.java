package com.example.timeline;

import android.Manifest;
import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class App extends Application {

    public static final String BASE_URL = "http://192.168.43.136/TimeLine/";
    public static final String BASE_URL_PUBLIC = "http://192.168.43.136/TimeLine/PublicChat/";
    public static final String BASE_URL_PRIVATE = "http://192.168.43.136/TimeLine/PrivateChat/";
    public static final String SAVE_STUDENT_URL = "InsertStudent.php";
    public static final String CHECK_ADMIN_URL = "CheckAdmin.php";
    public static final String CHECK_STUDENT_URL = "CheckStudent.php";
    public static final String UPLOAD_MESSAGE_URL = "UploadText.php";
    public static final String GET_ALL_URL = "GetAllDocs.php";
    public static final String DELETE_ITEM_URL = "DeleteItem.php";
    public static final String EDIT_ITEM_URL = "EditItem.php";
    public static final String UPDATE_FAV_URL = "UpdateFavorites.php";
    public static final String SEARCH_TEXT_URL = "SearchText.php";
    public static final String GET_FAVS_URL = "GetAllFavs.php";
    public static final String GET_STUDENT_URL = "GetAllStudents.php";
    public static final String GET_ADMIN_URL = "GetAllAdmins.php";

    public static int docID = 99;
    public static int chatDocID = 2;

    public static String[] APP_PERMS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int PERMS_CODE = 1234;
    public static final int PERMS_CALL = 4321;

    public static RequestQueue queue;

    public static String username;
    public static Context context;
    public static boolean isAdmin;
    public static String fontType;
    public static int fontSize;
    public static boolean theme;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
    }

}

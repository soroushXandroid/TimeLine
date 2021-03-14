package com.example.timeline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static List<DocModel> parseDocs(String content){

        List<DocModel> list = new ArrayList<>();

        try {

            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++){

                JSONObject object = array.getJSONObject(i);
                DocModel model = new DocModel();
                model.setId(object.getInt("id"));
                model.setUser(object.getString("username"));
                model.setImage(object.getInt("image"));
                model.setFav(object.getInt("fav"));
                model.setText(object.getString("text"));
                model.setUrl(object.getString("url"));
                model.setTime(object.getString("time"));
                list.add(model);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<UserModel> parseContacts(String content){

        List<UserModel> list = new ArrayList<>();

        try {

            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++){

                JSONObject object = array.getJSONObject(i);
                UserModel model = new UserModel();
                model.setId(object.getInt("id"));
                model.setUser(object.getString("username"));
                model.setPass(object.getString("password"));
                model.setPhone(object.getString("phone"));
                list.add(model);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<String> parseAdmins(String content){

        List<String> list = new ArrayList<>();

        try {

            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++){

                JSONObject object = array.getJSONObject(i);
                list.add(object.getString("username"));


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<ChatModel> parseChatDocs(String content){

        List<ChatModel> list = new ArrayList<>();

        try {

            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++){

                JSONObject object = array.getJSONObject(i);
                ChatModel model = new ChatModel();
                model.setId(object.getInt("id"));
                model.setSource(object.getString("source"));
                model.setDestination(object.getString("destination"));
                model.setImage(object.getInt("image"));
                model.setText(object.getString("text"));
                model.setUrl(object.getString("url"));
                model.setTime(object.getString("time"));
                list.add(model);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<UserModel> parseAdminContacts(String content){

        List<UserModel> list = new ArrayList<>();

        try {

            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++){

                JSONObject object = array.getJSONObject(i);
                UserModel model = new UserModel();
                model.setUser(object.getString("username"));
                model.setPhone(object.getString("phone"));

                list.add(model);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

}

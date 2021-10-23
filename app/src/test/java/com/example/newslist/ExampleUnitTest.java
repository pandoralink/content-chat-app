package com.example.newslist;

import com.example.newslist.data.Constants;
import com.example.newslist.user.User;
import com.example.newslist.utils.Author;
import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void loginTest() {
        Request request = new Request.Builder()
                .url(Constants.LOCAL_LOGIN_BASE_URL + "?user_account=10021111&user_password=12345678")
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void url2Test() {
        Request request = new Request.Builder()
                .url(Constants.BACK_BASE_URL + "getAuthorId?newId=" + 34)
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginUrlTest() {
        Request request = new Request.Builder()
                .url(Constants.LOCAL_LOGIN_BASE_URL + "?user_account=10021111&user_password=12345678")
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            final String body = response.body().string();
            System.out.println(body);
            User user = new Gson().fromJson(body, User.class);
            System.out.println(user.getUser_account());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void utilsTest() {
        String test = "";
    }

    @Test
    public void gsonTest() {
        Author author = new Author();
        author.setUheadUrl("123");
        author.setUname("123");
        author.setUid(123);
        Gson gson = new Gson();
        Author author1 = gson.fromJson(gson.toJson(author),Author.class);
        System.out.println(author1.getUheadUrl());
    }
}
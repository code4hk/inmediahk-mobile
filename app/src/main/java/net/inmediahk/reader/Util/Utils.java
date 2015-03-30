package net.inmediahk.reader.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.inmediahk.reader.Model.FacebookItem;
import net.inmediahk.reader.Model.FeedItem;
import net.inmediahk.reader.R;

import java.util.ArrayList;

public class Utils {

    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, Window win, boolean on) {
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);

        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setTintColor(activity.getResources().getColor(R.color.app_color));
        // set a custom navigation bar resource
//        tintManager.setNavigationBarTintResource(R.drawable.my_tint);
        // set a custom status bar drawable
//        tintManager.setStatusBarTintDrawable(MyDrawable);
    }

    public static ArrayList<FacebookItem> getFacebookProperty(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationInfo().name, Activity.MODE_PRIVATE);
        final String data = sharedPreferences.getString(key, null);
        if (TextUtils.isEmpty(data))
            return null;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray arr = parser.parse(data).getAsJsonArray();
//        FeedItem[] feedItems = new FeedItem[arr.size()];
        ArrayList<FacebookItem> feedItems = new ArrayList<FacebookItem>();
//        int i=0;
        for (JsonElement jsonElement : arr)
            feedItems.add(gson.fromJson(jsonElement, FacebookItem.class));
        return feedItems;
    }

    public static ArrayList<FeedItem> getFeedProperty(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationInfo().name, Activity.MODE_PRIVATE);
        final String data = sharedPreferences.getString(key, null);
        if (TextUtils.isEmpty(data))
            return null;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray arr = parser.parse(data).getAsJsonArray();
//        FeedItem[] feedItems = new FeedItem[arr.size()];
        ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();
//        int i=0;
        for (JsonElement jsonElement : arr)
            feedItems.add(gson.fromJson(jsonElement, FeedItem.class));
        return feedItems;
    }

    public static void setFeedProperty(Context context, String key, ArrayList<FeedItem> feedItem) {
        SharedPreferences mPrefs=context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed=mPrefs.edit();
        Gson gson = new Gson();
        ed.putString(key, gson.toJson(feedItem));
        ed.apply();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }
}
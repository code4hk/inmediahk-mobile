package net.inmediahk.reader.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.inmediahk.reader.R;

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
}
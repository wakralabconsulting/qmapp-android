package com.qatarmuseums.qatarmuseumsapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.R;

public class Util {
    private CustomDialogClass customDialog;

    public boolean checkImageResource(Context ctx, ImageView imageView,
                                      int imageResource) {
        boolean result = false;

        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources()
                        .getDrawable(imageResource, ctx.getTheme())
                        .getConstantState();
            } else {
                constantState = ctx.getResources().getDrawable(imageResource)
                        .getConstantState();
            }

            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }

        return result;
    }

    public void showComingSoonDialog(Activity activity) {
        customDialog = new CustomDialogClass(activity
                , activity.getResources().getString(R.string.coming_soon_txt)
                , activity.getResources().getString(R.string.coming_soon_content));
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();

    }
    public void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected())
            return true;
        else {
            return false;
        }

    }

}

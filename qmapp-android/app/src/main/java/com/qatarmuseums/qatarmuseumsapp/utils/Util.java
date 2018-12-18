package com.qatarmuseums.qatarmuseumsapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    private CustomDialogClass customDialog;
    private String lang;
    private Dialog tokenDialog;
    private LayoutInflater layoutInflater;
    private View closeBtn;
    private EditText mTokenView;

    public long getTimeStamp(String dateVal) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        long datevalue = 0;
        try {
            Date date = format.parse(dateVal);
            datevalue = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datevalue;
    }

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

    public void showComingSoonDialog(Activity activity, int stringId) {
        customDialog = new CustomDialogClass(activity
                , activity.getResources().getString(R.string.coming_soon_txt)
                , activity.getResources().getString(stringId));
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();

    }

    public void showNormalDialog(Activity activity, int stringId) {
        customDialog = new CustomDialogClass(activity
                , ""
                , activity.getResources().getString(stringId));
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();

    }

    public void showAlertDialog(Activity activity) {
        customDialog = new CustomDialogClass(activity
                , activity.getResources().getString(R.string.alert_txt)
                , activity.getResources().getString(R.string.error_content));
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();

    }

    public void showCulturalPassAlertDialog(Activity activity) {
        customDialog = new CustomDialogClass(activity
                , ""
                , activity.getResources().getString(R.string.thankyou_interest));
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!activity.isFinishing())
            customDialog.show();

    }

    public void showLocationAlertDialog(Activity activity) {
        customDialog = new CustomDialogClass(activity
                , activity.getResources().getString(R.string.location_alert_txt)
                , activity.getResources().getString(R.string.location_error_content));
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();

    }

    public void showLocationAlertDialog(Context context) {
        customDialog = new CustomDialogClass(context
                , context.getResources().getString(R.string.location_alert_txt)
                , context.getResources().getString(R.string.location_error_content));
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

    public void setLocale(Context context, int language) {
        if (language == 1)
            lang = "en";
        else
            lang = "ar";
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale(lang));
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public String html2string(String html) {
        String s;
        if (html == null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        s = document.html().replaceAll("\\\\n", "\n");
        s = s.replaceAll("&amp;", "and");
        s = s.replaceAll("&nbsp;", " ");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    public static void setupItem(final View view, final LibraryObject libraryObject, Context mContext) {
        final ImageView img = (ImageView) view.findViewById(R.id.img_item);
        GlideApp.with(mContext)
                .load(libraryObject.getRes())
                .placeholder(R.drawable.placeholder)
                .into(img);
    }

    public static class LibraryObject {

        private String mRes;

        public LibraryObject(final String res) {
            mRes = res;
        }


        public String getRes() {
            return mRes;
        }

        public void setRes(final String res) {
            mRes = res;
        }
    }

    public String convertDegreeToDecimalMeasure(String degreeValue) {

        String value = degreeValue.trim();
        String[] latParts = value.split("°");
        float degree = Float.parseFloat(latParts[0]);
        if (degree == 0.0) {
            return null;
        }
        value = latParts[1].trim();
        latParts = value.split("'");
        float min = Float.parseFloat(latParts[0]);
        value = latParts[1].trim();
        latParts = value.split("\"");
        float sec = Float.parseFloat(latParts[0]);
        String result;
        result = String.valueOf(degree + (min / 60) + (sec / 3600));
        return result;

    }

}

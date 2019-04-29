package com.qatarmuseums.qatarmuseumsapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.education.Events;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Util {
    private CustomDialogClass customDialog;
    private ContentResolver contentResolver;
    private ContentValues contentValues;
    private int MY_PERMISSIONS_REQUEST_CALENDAR = 100;
    private int REQUEST_PERMISSION_SETTING = 110;

    public Util() {
    }

    public long getTimeStamp(String dateVal) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        long dateValue = 0;
        try {
            Date date = format.parse(dateVal);
            dateValue = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateValue;
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
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }

    public void setLocale(Context context, int language) {
        String lang;
        if (language == 1)
            lang = LocaleManager.LANGUAGE_ENGLISH;
        else
            lang = LocaleManager.LANGUAGE_ARABIC;
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
        final ImageView img = view.findViewById(R.id.img_item);
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

    public String formatDateFromDateString(String inputDate) throws ParseException {
        Date mParsedDate;
        String mOutputDateString;
        SimpleDateFormat mInputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat mOutputDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        mParsedDate = mInputDateFormat.parse(inputDate);
        mOutputDateString = mOutputDateFormat.format(mParsedDate);
        return mOutputDateString;

    }

    public void removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        }
    }

    public void removeHtmlTags(ArrayList<Events> models) {
        for (int i = 0; i < models.size(); i++) {
            ArrayList<String> fieldValue = models.get(i).getField();
            fieldValue.set(0, html2string(fieldValue.get(0)));
            models.get(i).setField(fieldValue);
            ArrayList<String> startDateVal = models.get(i).getStartTime();
            startDateVal.set(0, html2string(startDateVal.get(0)));
            models.get(i).setStartTime(startDateVal);
            ArrayList<String> endDateVal = models.get(i).getEndTime();
            endDateVal.set(0, html2string(endDateVal.get(0)));
            models.get(i).setEndTime(endDateVal);
            models.get(i).setProgramType(html2string(models.get(i).getProgramType()));
            models.get(i).setLongDescription(html2string(models.get(i).getLongDescription()));
            models.get(i).setShortDescription(html2string(models.get(i).getShortDescription()));
            models.get(i).setTitle(html2string(models.get(i).getTitle()));
        }
    }

    public void updateTimeStamp(ArrayList<Events> events, long timestamp, String day, String month, String year) {
        for (int i = 0; i < events.size(); i++) {
            // Split time and converting time to timestamp and updating
            String start = events.get(i).getStartTime().get(0);
            String startValue = start.substring(start.lastIndexOf("-") + 1);
            String[] startTimeArray = startValue.trim().split("-");
            String startTime = startTimeArray[0].trim();
            String end = events.get(i).getEndTime().get(0);
            String endValue = end.substring(end.lastIndexOf("-") + 1);
            String[] endTimeArray = endValue.trim().split("-");
            String endTime = endTimeArray[0].trim();
            String str_start_date = day + "-" + month + "-" + year + " " + startTime;
            String str_end_date = day + "-" + month + "-" + year + " " + endTime;
            ArrayList<String> st = new ArrayList<>();
            st.add(0, String.valueOf(convertDate(str_start_date)));
            ArrayList<String> en = new ArrayList<>();
            en.add(0, String.valueOf(convertDate(str_end_date)));
            events.get(i).setStartTime(st);
            events.get(i).setEndTime(en);
            // Setting event date to selected date timestamp
            events.get(i).setDate(String.valueOf(timestamp / 1000));

        }

        // Sort events depends on start time
        Collections.sort(events, (event1, event2) -> event1.getStartTime().get(0).compareTo(event2.getStartTime().get(0)));
    }


    public int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        double height = displayMetrics.heightPixels;
        height = (height) * (0.75);
        return (int) height;
    }

    public long convertDate(String dateValue) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = null;
        try {
            date = formatter.parse(dateValue);
        } catch (ParseException e) {
            Timber.e("convertDate() - ParseException: %s", e.getMessage());
            e.printStackTrace();
        }
        return date != null ? date.getTime() : 0;
    }

    @SuppressLint("MissingPermission")
    public int getCalendarId(Context context) {
        Timber.i("getCalendarId()");
        Cursor cursor;
        ContentResolver contentResolver = context.getContentResolver();
        Uri calendars = CalendarContract.Calendars.CONTENT_URI;

        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
                CalendarContract.Calendars.IS_PRIMARY                     // 4
        };

        int PROJECTION_ID_INDEX = 0;
        int PROJECTION_DISPLAY_NAME_INDEX = 2;
        int PROJECTION_VISIBLE = 4;

        cursor = contentResolver.query(calendars, EVENT_PROJECTION, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String calName;
            long calId = 0;
            String visible;

            do {
                calName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
                calId = cursor.getLong(PROJECTION_ID_INDEX);
                visible = cursor.getString(PROJECTION_VISIBLE);
                if (visible.equals("1")) {
                    return (int) calId;
                }
                Timber.e("Calendar Id : %d  : %s : %s", calId, calName, visible);
            } while (cursor.moveToNext());
            cursor.close();
            return (int) calId;
        }
        return 1;
    }

    public void showDialog(Context context, final String buttonText, final ArrayList<Events> events, final int position) {
        Timber.i("showDialog() - For %s", buttonText);
        final Dialog dialog = new Dialog(context, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.calendar_popup, null);
        dialog.setContentView(view);
        FrameLayout contentLayout = view.findViewById(R.id.content_frame_layout);
        ImageView closeBtn = view.findViewById(R.id.close_dialog);
        final Button registerNowBtn = view.findViewById(R.id.doneBtn);
        TextView dialogTitle = view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = view.findViewById(R.id.dialog_content);
        int heightValue = getScreenHeight(context);
        contentLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, heightValue));
        dialogTitle.setText(events.get(position).getTitle());
        registerNowBtn.setText(buttonText);
        if (buttonText.equalsIgnoreCase(context.getResources().getString(R.string.register_now))) {
            registerNowBtn.setEnabled(false);
            registerNowBtn.setTextColor(context.getResources().getColor(R.color.colorWhite));
            registerNowBtn.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorSemiTransparentGrey)));
        }
        dialogContent.setText(events.get(position).getLongDescription());

        registerNowBtn.setOnClickListener(view1 -> {
            Timber.i("Dialog - on Approval");
            if (buttonText.equalsIgnoreCase(context.getResources().getString(R.string.register_now))) {
                new Util().showComingSoonDialog((Activity) context, R.string.coming_soon_content);
                dialog.dismiss();
            } else {
                addToCalendar(context, events, position);
                dialog.dismiss();
            }

        });
        closeBtn.setOnClickListener(view12 -> {
            Timber.i("Dialog - on Cancelled");
            dialog.dismiss();

        });
        dialog.show();
    }

    private void addToCalendar(Context context, final ArrayList<Events> events, final int position) {
        Timber.i("addToCalendar() - Setting calendar data for %s", events.get(position).getTitle());
        contentResolver = context.getContentResolver();
        contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, events.get(position).getTitle());
        contentValues.put(CalendarContract.Events.DESCRIPTION, events.get(position).getLongDescription());
        contentValues.put(CalendarContract.Events.DTSTART, events.get(position).getStartTime().get(0));
        contentValues.put(CalendarContract.Events.DTEND, events.get(position).getEndTime().get(0));
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED) {
                Timber.i("Requesting CALENDAR READ & WRITE permission");
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_CALENDAR);

            } else {
                insertEventToCalendar(context, contentValues);
            }
        } else
            insertEventToCalendar(context, contentValues);


    }

    @SuppressLint("MissingPermission")
    public void insertEventToCalendar(Context context, ContentValues contentValues) {
        Timber.i("insertEventToCalendar()");
        try {
            if (contentValues == null) {
                this.contentValues.put(CalendarContract.Events.CALENDAR_ID, getCalendarId(context));
                contentResolver.insert(CalendarContract.Events.CONTENT_URI, this.contentValues);
            } else {
                contentValues.put(CalendarContract.Events.CALENDAR_ID, getCalendarId(context));
                contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
            }
            showToast(context.getString(R.string.event_added), context);
        } catch (Exception ex) {
            Timber.e("insertEventToCalendar() - Exception: %s", ex.getMessage());
            Toast.makeText(context, "Error in adding event on calendar : " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showNavigationDialog(Context context, String title, final String details) {
        Timber.i("showNavigationDialog()");
        Dialog dialog = new Dialog(context, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        ImageView closeBtn = view.findViewById(R.id.close_dialog);
        Button dialogActionButton = view.findViewById(R.id.doneBtn);
        TextView dialogTitle = view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = view.findViewById(R.id.dialog_content);
        dialogTitle.setText(title);
        dialogActionButton.setText(context.getResources().getString(R.string.open_settings));
        dialogContent.setText(details);

        dialogActionButton.setOnClickListener(view1 -> {
            Timber.i("Navigation - on Approve");
            navigateToSettings(context);
            dialog.dismiss();

        });
        closeBtn.setOnClickListener(view12 -> {
            Timber.i("Navigation - on Cancelled");
            dialog.dismiss();
        });
        dialog.show();
    }

    private void navigateToSettings(Context context) {
        Timber.i("navigateToSettings()");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        ((Activity) context).startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

}

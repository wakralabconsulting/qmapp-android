package com.qatarmuseums.qatarmuseumsapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity context;
    public Context mContext;
    public Dialog d;
    public Button closeButton, no,calendarCloseButton;
    TextView dialogTitle, dialogContent;
    String title,content,calendarContent;

    public CustomDialogClass(Activity a,String title,String content) {
        super(a);
        // TODO Auto-generated constructor stub
        this.context = a;
        this.title=title;
        this.content=content;
    }
    public CustomDialogClass(Context c,String content){
        super(c);
        this.mContext=c;
        this.calendarContent=content;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coming_soon_popup);
        closeButton = (Button) findViewById(R.id.close_btn);
        dialogTitle = (TextView) findViewById(R.id.dialog_tittle);
        dialogContent = (TextView) findViewById(R.id.dialog_content);
        calendarCloseButton=(Button)findViewById(R.id.calendar_close_btn);

        if(title == null){
            dialogTitle.setVisibility(View.GONE);
            dialogContent.setText(calendarContent);
            calendarCloseButton.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.GONE);
            calendarCloseButton.setText(mContext.getResources().getString(R.string.close));

        }else{
            dialogTitle.setVisibility(View.VISIBLE);
            dialogTitle.setText(title);
            dialogContent.setText(content);
            calendarCloseButton.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
            closeButton.setText(context.getResources().getString(R.string.close));
        }

        closeButton.setOnClickListener(this);
        calendarCloseButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_btn:
                dismiss();
                break;
            case R.id.calendar_close_btn:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}

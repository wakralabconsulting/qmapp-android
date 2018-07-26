package com.qatarmuseums.qatarmuseumsapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button closeButton, no;
    TextView dialogTitle, dialogContent;

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coming_soon_popup);
        closeButton = (Button) findViewById(R.id.close_btn);
        dialogTitle = (TextView) findViewById(R.id.dialog_tittle);
        dialogContent = (TextView) findViewById(R.id.dialog_content);
        dialogTitle.setText(c.getResources().getString(R.string.coming_soon_txt));
        dialogContent.setText(c.getResources().getString(R.string.coming_soon_content));
        closeButton.setText(c.getResources().getString(R.string.close));
        closeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_btn:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}

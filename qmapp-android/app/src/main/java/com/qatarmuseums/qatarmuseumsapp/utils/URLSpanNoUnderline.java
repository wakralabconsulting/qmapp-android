package com.qatarmuseums.qatarmuseumsapp.utils;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class URLSpanNoUnderline extends URLSpan {
    URLSpanNoUnderline(String p_Url) {
        super(p_Url);
    }

    public void updateDrawState(TextPaint p_DrawState) {
        super.updateDrawState(p_DrawState);
        p_DrawState.setUnderlineText(false);
    }
}

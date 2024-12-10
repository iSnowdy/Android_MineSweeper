package com.example.minesweeper.Utils;

import android.content.Context;
import android.widget.Toast;

/*
Util class to simply print Toasts whenever I want to
I did this because I had reused the same method to make toasts
in several classes. So as to not repeat code, I created this
 */

public class ToastUtil {
    public static void createToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

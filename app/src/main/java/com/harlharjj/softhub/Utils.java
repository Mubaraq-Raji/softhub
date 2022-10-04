
package com.harlharjj.softhub;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class Utils {
    public static void showSnackBar(View view, String string, String string2, View.OnClickListener onClickListener) {
        Snackbar.make((View)view, (CharSequence)string, (int)0).setAction((CharSequence)string2, onClickListener).show();
    }

    public static void showToastMessage(Context context, String string) {
        Toast.makeText((Context)context, (CharSequence)string, (int)1).show();
    }
}


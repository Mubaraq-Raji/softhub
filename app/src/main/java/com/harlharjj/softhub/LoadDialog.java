package com.harlharjj.softhub;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class LoadDialog extends AppCompatDialogFragment {
    public static final String TAG = "LoadingDialog";
    private TextView mConnectText;

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.connect_loading_view, viewGroup, false);
        this.mConnectText = (TextView) inflate.findViewById(R.id.loading_text);
        TextView textView = this.mConnectText;
        StringBuilder sb = new StringBuilder();
        sb.append("Broadcasting new wifi setup!");
        sb.append("\n\nPlease wait a moment...");
        textView.setText(sb.toString());
        textView.setTextColor(getResources().getColor(R.color.blacky));
        return inflate;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}

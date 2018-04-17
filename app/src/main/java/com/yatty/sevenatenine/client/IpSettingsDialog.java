package com.yatty.sevenatenine.client;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.yatty.sevenatenine.client.network.NetworkService;

public class IpSettingsDialog extends DialogFragment {
    private static final String TITLE = "Enter IP:";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ip_settings, null);
        final EditText ipEditText = view.findViewById(R.id.et_ip);
        final EditText portEditText = view.findViewById(R.id.et_port);
        ipEditText.setText(NetworkService.getIp());
        portEditText.setText(String.valueOf(NetworkService.getPort()));
        builder.setView(view)
                .setTitle(TITLE)
                .setPositiveButton(R.string.action_apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("TAG", "IP: " + ipEditText.getText().toString() + ".");
                        NetworkService.setIp(ipEditText.getText().toString());
                        NetworkService.setPort(Integer.valueOf(portEditText.getText().toString()));
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        IpSettingsDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}

package it.bff.sudoku;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class ModalDialog
{
    private Context context;
    private AlertDialog.Builder builder1;
    public ModalDialog(final Context context, String message)
    {
        this.context=context;
        builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(context.getResources().getString(R.string.msg_critical_error)+"\n\n"+ message);
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                context.getResources().getString(R.string.modal_error_btn_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((Activity) context).finish();
                        dialog.cancel();
                    }
                });
    }
    public void show()
    {
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}

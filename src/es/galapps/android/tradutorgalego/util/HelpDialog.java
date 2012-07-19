package es.galapps.android.tradutorgalego.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import es.galapps.android.tradutorgalego.R;

public class HelpDialog {

    public static AlertDialog create(Context context) {

        final Context theContext = context;

        View messageView = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.help, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.help_title));
        builder.setView(messageView);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.help_next,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        generalHelp(theContext).show();

                    }
                });
        return builder.create();

    }

    static AlertDialog generalHelp(Context context) {

        final Context theContext = context;

        View messageView = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.help2, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.help_title));
        builder.setView(messageView);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.help_next,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        createTraductionHelp(theContext).show();

                    }
                });
        return builder.create();

    }

    private static AlertDialog createTraductionHelp(Context context) {

        View messageView = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.help3, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.help_title));
        builder.setView(messageView);
        builder.setPositiveButton(R.string.finish, null);
        return builder.create();

    }
}

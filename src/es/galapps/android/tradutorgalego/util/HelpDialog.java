/*
 * This file is part of TradutorGalego.

 * TradutorGalego is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * TradutorGalego is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with TradutorGalego.  If not, see <http://www.gnu.org/licenses/>.
 */
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

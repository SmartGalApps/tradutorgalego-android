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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import es.galapps.android.tradutorgalego.R;

public class AboutDialog {

    public static AlertDialog create(Context context)
            throws NameNotFoundException {

        PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                context.getPackageName(), PackageManager.GET_META_DATA);

        String versionInfo = pInfo.versionName;

        View messageView = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.about, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.app_name) + " " + versionInfo);
        builder.setView(messageView);
        builder.setPositiveButton(context.getString(android.R.string.ok), null);
        return builder.create();

    }
}
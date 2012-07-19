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
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;
import es.galapps.android.tradutorgalego.R;

public class CustomDialog {

    public static AlertDialog create(Context context, int titleId,
            int messageId, int drawableId) {

        final TextView message = new TextView(context);
        // i.e.: R.string.dialog_message =>
        // "Test this dialog following the link to dtmilano.blogspot.com"
        final SpannableString s = new SpannableString(
                context.getText(messageId));
        Linkify.addLinks(s, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        return new AlertDialog.Builder(context).setTitle(titleId)
                .setCancelable(true).setIcon(drawableId)
                .setPositiveButton(R.string.ok, null).setView(message).create();
    }
}
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class VolgaChecker {

    public static boolean exists(Context context, String verb)
            throws IOException {

        try {
            InputStream is = context.getResources().getAssets()
                    .open("verbosVolga.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase(verb)) {
                    is.close();
                    return true;
                }
            }
            is.close();

        } catch (IOException e) {
            throw e;
        }
        return false;
    }

}

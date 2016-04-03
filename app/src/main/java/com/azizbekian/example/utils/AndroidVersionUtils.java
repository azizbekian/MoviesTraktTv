package com.azizbekian.example.utils;

import android.os.Build;

/**
 * A utility class to check Android SDK version.
 * <p>
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
@SuppressWarnings("unused")
public class AndroidVersionUtils {

    /**
     * @return true, if Android SDK is higher/equal to Kit-Kat. False - otherwise.
     */
    public static boolean isHigherEqualToKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * @return true, if Android SDK is higher/equal to Lollipop. False - otherwise.
     */
    public static boolean isHigherEqualToLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}

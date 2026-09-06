/*
 * Copyright (C) 2026 XephiraOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util.xephira;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PixelPropsUtils: Dynamic device profile spoofer for XephiraOS.
 * - Spoofs Google Photos as Pixel XL for lifetime unlimited original-quality photo & video cloud storage.
 * - Spoofs high-FPS gaming profiles (ROG Phone 8) for high refresh rate gaming (90/120 FPS).
 */
public final class PixelPropsUtils {
    private static final String TAG = "XephiraPropsUtils";
    private static final boolean DEBUG = false;

    private static final String PACKAGE_GPHOTOS = "com.google.android.apps.photos";

    // Packages to spoof as Pixel XL for unlimited backup
    private static final Map<String, Object> propsToChangePixelXL = new HashMap<>();

    // Games to spoof as ROG Phone 8 for 120 FPS
    private static final List<String> packagesToSpoofGame120FPS = Arrays.asList(
            "com.tencent.ig",
            "com.pubg.krmobile",
            "com.vng.pubgmobile",
            "com.re联动.pubg",
            "com.activision.callofduty.shooter",
            "com.ea.gp.apexmobile",
            "com.epicgames.fortnite",
            "com.miHoYo.GenshinImpact",
            "com.levelinfinite.hotta.gp",
            "com.riotgames.league.wildrift"
    );

    private static final Map<String, Object> propsToChangeROGPhone = new HashMap<>();

    static {
        propsToChangePixelXL.put("BRAND", "google");
        propsToChangePixelXL.put("MANUFACTURER", "Google");
        propsToChangePixelXL.put("DEVICE", "marlin");
        propsToChangePixelXL.put("PRODUCT", "marlin");
        propsToChangePixelXL.put("MODEL", "Pixel XL");
        propsToChangePixelXL.put("FINGERPRINT", "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys");

        propsToChangeROGPhone.put("BRAND", "asus");
        propsToChangeROGPhone.put("MANUFACTURER", "Asus");
        propsToChangeROGPhone.put("DEVICE", "ASUS_AI2401_A");
        propsToChangeROGPhone.put("PRODUCT", "WW_AI2401");
        propsToChangeROGPhone.put("MODEL", "ASUS_AI2401_A");
    }

    private PixelPropsUtils() {}

    public static void setProps(Context context) {
        if (context == null) return;
        final String packageName = context.getPackageName();
        if (packageName == null) return;

        // Feature 95: Google Photos unlimited storage spoofing
        if (PACKAGE_GPHOTOS.equals(packageName)) {
            boolean gphotosSpoof = true;
            try {
                gphotosSpoof = Settings.System.getInt(context.getContentResolver(),
                        "google_photos_unlimited_spoof", 1) == 1;
            } catch (Exception ignored) {}

            if (gphotosSpoof) {
                if (DEBUG) Log.d(TAG, "Spoofing Google Photos as Pixel XL for unlimited cloud backup");
                for (Map.Entry<String, Object> prop : propsToChangePixelXL.entrySet()) {
                    setPropValue(prop.getKey(), prop.getValue());
                }
            }
            return;
        }

        // Feature 79: 120 FPS Gaming spoofing
        if (packagesToSpoofGame120FPS.contains(packageName)) {
            boolean gameSpoof = true;
            try {
                gameSpoof = Settings.System.getInt(context.getContentResolver(),
                        "game_spoofing_120fps", 1) == 1;
            } catch (Exception ignored) {}

            if (gameSpoof) {
                if (DEBUG) Log.d(TAG, "Spoofing game package " + packageName + " as ASUS ROG Phone 8 for 120 FPS");
                for (Map.Entry<String, Object> prop : propsToChangeROGPhone.entrySet()) {
                    setPropValue(prop.getKey(), prop.getValue());
                }
            }
        }
    }

    private static void setPropValue(String key, Object value) {
        try {
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }
}

package com.ritmofit.app.data.session;

import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import android.util.Base64;

public final class JwtHelper {
    public static String tryExtractUserId(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;
            byte[] payload = Base64.decode(parts[1], Base64.URL_SAFE);
            JSONObject json = new JSONObject(new String(payload, StandardCharsets.UTF_8));
            // Convención típica: "sub" o "userId"
            if (json.has("userId")) return json.optString("userId", null);
            return json.optString("sub", null);
        } catch (Exception e) {
            return null;
        }
    }
}
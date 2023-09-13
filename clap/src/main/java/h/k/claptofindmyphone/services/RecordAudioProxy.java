package h.k.claptofindmyphone.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class RecordAudioProxy {
    public static Class<?> serverCls;

    public static void onApplicationCreate(Context context, Class<?> cls) {
        serverCls = cls;

        sharedPeref = context.getSharedPreferences("ctfmf", Context.MODE_PRIVATE);
        sharedPerefEditor = sharedPeref.edit();
    }

    public static void startForegroundService(Context context) {
        try {
            Intent intent = new Intent(context, ServiceRecordAudio.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            }
        } catch (Exception e) {
        }
    }

    public static void stopService(Context context) {
        try {
            context.stopService(new Intent(context, ServiceRecordAudio.class));
        } catch (Exception e) {
        }
    }

    private static SharedPreferences sharedPeref;
    private static SharedPreferences.Editor sharedPerefEditor;

    public static void onConfigInit() {
        if (sharedPeref != null && sharedPerefEditor != null && !sharedPeref.contains("flash_clap")) {
            sharedPerefEditor.putBoolean("flash_whistle", false);
            sharedPerefEditor.putBoolean("flash_clap", false);
            sharedPerefEditor.putBoolean("vibrate_whistle", false);
            sharedPerefEditor.putBoolean("vibrate_clap", false);
            sharedPerefEditor.putBoolean("melody_whistle", true);
            sharedPerefEditor.putBoolean("melody_clap", true);
            sharedPerefEditor.putInt("melody_length_clap", 500);
            sharedPerefEditor.putInt("melody_length_whistle", 500);
            sharedPerefEditor.putInt("melody_volume_whistle", 100);
            sharedPerefEditor.putInt("melody_volume_clap", 100);
        }
    }

    public static void putBoolean(String key, boolean value) {
        sharedPerefEditor.putBoolean(key, value).apply();
    }

    public static void putInt(String key, int value) {
        sharedPerefEditor.putInt(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sharedPeref.getBoolean(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return sharedPeref.getInt(key, defValue);
    }
}

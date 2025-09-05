package ir.ham3da.darya.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import ir.ham3da.darya.tools.PermissionMediaType;


public class PermissionHelper {


    public interface PermissionCallback {
        void onPermissionGranted();

        void onPermissionDenied();
    }

    public static void requestMediaPermission(Context context,
                                              PermissionMediaType mediaType,
                                              ActivityResultLauncher<String> launcher,
                                              PermissionCallback callback) {

        String permission = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            switch (mediaType) {
                case IMAGES:
                    permission = Manifest.permission.READ_MEDIA_IMAGES;
                    break;
                case AUDIO:
                    permission = Manifest.permission.READ_MEDIA_AUDIO;
                    break;
                case VIDEO:
                    permission = Manifest.permission.READ_MEDIA_VIDEO;
                    break;
            }
        } else {
            permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            callback.onPermissionGranted();
        } else {
            launcher.launch(permission);
        }
    }
}
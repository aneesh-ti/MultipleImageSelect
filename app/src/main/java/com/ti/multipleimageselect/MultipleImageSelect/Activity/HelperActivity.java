package com.ti.multipleimageselect.MultipleImageSelect.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ti.multipleimageselect.MultipleImageSelect.helpers.ConstantsAlbum;

public class HelperActivity extends Activity {
    protected View view;

    private final int maxLines = 4;
    private final String[] permissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE };

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted();

        } else {
            ActivityCompat.requestPermissions(this, permissions, ConstantsAlbum.PERMISSION_REQUEST_CODE);
        }
    }

      @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != ConstantsAlbum.PERMISSION_REQUEST_CODE
                || grantResults.length == 0
                || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            permissionDenied();

        } else {
            permissionGranted();
        }
    }

    protected void permissionGranted() {}

    private void permissionDenied() {
        hideViews();
    }

    protected void hideViews() {}

    protected void setView(View view) {
        this.view = view;
    }
}

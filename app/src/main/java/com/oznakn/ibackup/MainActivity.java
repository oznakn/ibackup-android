package com.oznakn.ibackup;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        init();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }

    private void init() {
        if (SettingsManager.getInstance(this).getFirstRun()) {
            runFirstRun();
        }

        startService();
    }

    private void runFirstRun() {
        SettingsManager.getInstance(MainActivity.this).setFirstRun(false);

        final ArrayList<Image> images = BackupManager.getInstance(this).getImagesCursor();

        if (images.size() == 0) {
            startService();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(images.size() + " image found")
                    .setPositiveButton("Sync", (dialog, which) -> {
                        for (Image image : images) {
                            LocalDBHelper.getInstance(MainActivity.this).saveImage(image);
                        }

                        startService();
                    })
                    .create()
                    .show();
        }
    }

    private void startService() {
        startService(new Intent(this, BackupService.class));
    }
}
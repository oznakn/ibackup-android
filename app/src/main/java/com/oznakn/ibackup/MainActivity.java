package com.oznakn.ibackup;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                })
                .check();
    }

    private void init() {
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MediaStoreManager.getInstance(this).getImageDirectories()));

        if (SettingsManager.getInstance(this).getFirstRun()) {
            runFirstRun();
        } else {
            startService();
        }
    }

    private void runFirstRun() {
        SettingsManager.getInstance(MainActivity.this).setFirstRun(false);

        final ArrayList<Image> images = MediaStoreManager.getInstance(this).getImagesCursor();

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

    @Override
    protected void onResume() {
        super.onResume();

        startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buttonSettings:
                final EditText editText = new EditText(this);
                editText.setText(SettingsManager.getInstance(this).getServerUrl());

                new AlertDialog.Builder(this)
                        .setMessage("Server URL")
                        .setView(editText)
                        .setPositiveButton("Save", (dialog, which) -> {
                            SettingsManager.getInstance(MainActivity.this).setServerUrl(editText.getText().toString());

                            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        })
                        .create()
                        .show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
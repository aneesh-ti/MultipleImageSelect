package com.ti.multipleimageselect;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ti.multipleimageselect.MultipleImageSelect.Activity.HelperActivity;
import com.ti.multipleimageselect.MultipleImageSelect.ImageViewActivity;
import com.ti.multipleimageselect.MultipleImageSelect.adapters.CustomImageSelectAdapter;
import com.ti.multipleimageselect.MultipleImageSelect.helpers.ConstantsAlbum;
import com.ti.multipleimageselect.MultipleImageSelect.models.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class ImageSelectActivity extends HelperActivity {


    private int countSelected=0;
    LinearLayout title;

    public static ArrayList<Image> images;
    String imageurl = "", image_name = "";
    private TextView errorDisplay;
    private ProgressBar progressBar;
    private GridView gridView;
    private CustomImageSelectAdapter adapter;

    private ContentObserver observer;
    private Handler handler;
    private Thread thread;
    RelativeLayout nextLayer;
    TextView selected, next, preview;
    private final String[] projection = new String[]{
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        nextLayer = findViewById(R.id.next_layout);

        selected = findViewById(R.id.selected);
        next = findViewById(R.id.next);
        preview = findViewById(R.id.preview);
        // Album
        final Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        errorDisplay = findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progress_bar_album_select);
        gridView = findViewById(R.id.grid_view_album_select);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                toggleSelection(position);

                    selected.setText(countSelected + " " + getString(R.string.selected));
                if (countSelected == 0) {
                    nextLayer.setVisibility(View.GONE);
                }else{
                    nextLayer.setVisibility(View.VISIBLE);
                }
            }
        });

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Image> images = getSelected();
                String stringArray[] = new String[images.size()];
                for (int i = 0, l = images.size(); i < l; i++) {
                    stringArray[i]= images.get(i).path;

                }
                imageurl =stringArray[0];

                Intent intent1 = new Intent(getApplicationContext(),ImageViewActivity.class);
                intent1.putExtra("path",stringArray);
                startActivity(intent1);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Image> images = getSelected();
                if( images!=null && images.size()>0){
                   JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < images.size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("path",images.get(i).path.toString());
                            jsonObject.put("name",images.get(i).name.toString());
                            jsonObject.put("id",images.get(i).id);
                            jsonObject.put("isSelected",images.get(i).isSelected);
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                   Intent intent1 = new Intent(getApplicationContext(), CartActivity.class);
                   intent1.putExtra("imagecount", images.size());
                    intent1.putExtra("source", "gallery");
                   intent1.putExtra("imagearray", jsonArray.toString());
                   startActivity(intent1);
                }else if(CartActivity.cameraImageArrayFromAdapter.length()>0){
                    Intent intent1 = new Intent(getApplicationContext(), CartActivity.class);
                    startActivity(intent1);
                }
            }
        });

        LoadImage();


    }

    private void LoadImage() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ConstantsAlbum.PERMISSION_GRANTED: {
                        loadAlbums();
                        break;
                    }

                    case ConstantsAlbum.FETCH_STARTED: {
                        progressBar.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.INVISIBLE);
                        break;
                    }

                    case ConstantsAlbum.FETCH_COMPLETED: {
                        if (adapter == null) {
                            adapter = new CustomImageSelectAdapter(getApplicationContext(), images);
                            gridView.setAdapter(adapter);

                            progressBar.setVisibility(View.INVISIBLE);
                            gridView.setVisibility(View.VISIBLE);
                            orientationBasedUI(getResources().getConfiguration().orientation);

                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    }

                    case ConstantsAlbum.ERROR: {
                        progressBar.setVisibility(View.INVISIBLE);
                        errorDisplay.setVisibility(View.VISIBLE);
                        break;
                    }

                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
//                loadAlbums();
            }
        };
        getApplicationContext().getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);

        checkPermission();
    }


    private ArrayList<Image> getSelected() {
        ArrayList<Image> selectedImages = new ArrayList<>();
        for (int i = 0, l = images.size(); i < l; i++) {
        if (images.get(i).isSelected) {
        selectedImages.add(images.get(i));
        }
        }
        return selectedImages;
        }


private void toggleSelection(int position) {
        if (!images.get(position).isSelected && countSelected >= ConstantsAlbum.limit) {
        Toast.makeText(
        getApplicationContext(),
        String.format(getString(R.string.limit_exceeded), ConstantsAlbum.limit),
        Toast.LENGTH_SHORT)
        .show();
        return;
        }

        images.get(position).isSelected = !images.get(position).isSelected;
        if (images.get(position).isSelected) {
        countSelected++;
        } else {
        countSelected--;
        }
        adapter.notifyDataSetChanged();
        }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

        stopThread();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (adapter != null) {
            adapter.releaseResources();
        }
        gridView.setOnItemClickListener(null);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }

    private void orientationBasedUI(int orientation) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        if (adapter != null) {
            int size = orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels / 2 : metrics.widthPixels / 4;
            adapter.setLayoutParams(size);
        }
        gridView.setNumColumns(orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null) {
            adapter.notifyDataSetChanged();

            ArrayList<Image> images1 = getSelected();
            countSelected = images1.size();

            selected.setText(countSelected + " " + getString(R.string.selected));

        }
        if (countSelected == 0) {
            nextLayer.setVisibility(View.GONE);
        }else{
            nextLayer.setVisibility(View.VISIBLE);
        }
    }
    private void loadAlbums() {
        startThread(new AlbumLoaderRunnable());
    }

private class AlbumLoaderRunnable implements Runnable {
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            /*
            If the adapter is null, this is first time this activity's view is
            being shown, hence send FETCH_STARTED message to show progress bar
            while images are loaded from phone
             */
        if (adapter == null) {
            sendMessage(ConstantsAlbum.FETCH_STARTED);
        }
try {
    File file;
    HashSet<Long> selectedImages = new HashSet<>();
    if (images != null) {
        Image image;
        for (int i = 0, l = images.size(); i < l; i++) {
            image = images.get(i);
            file = new File(image.path);
            if (file.exists() && image.isSelected) {
                selectedImages.add(image.id);
            }
        }
    }

        Cursor cursor = getApplicationContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.Media.DATE_ADDED);

//            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
//                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{ album }, MediaStore.Images.Media.DATE_ADDED);
        if (cursor == null) {
            sendMessage(ConstantsAlbum.ERROR);
            return;
        }

        /*
        In case this runnable is executed to onChange calling loadImages,
        using countSelected variable can result in a race condition. To avoid that,
        tempCountSelected keeps track of number of selected images. On handling
        FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
         */
        int tempCountSelected = 0;
        ArrayList<Image> temp = new ArrayList<>(cursor.getCount());
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }

                long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                boolean isSelected = selectedImages.contains(id);
                if (isSelected) {
                    tempCountSelected++;
                }

                file = new File(path);
                if (file.exists()) {
                    temp.add(new Image(id, name, path, isSelected));
                }

            } while (cursor.moveToPrevious());
        }
        cursor.close();

        if (images == null) {
            images = new ArrayList<>();
        }
        images.clear();
        images.addAll(temp);

        sendMessage(ConstantsAlbum.FETCH_COMPLETED, tempCountSelected);
}catch (Exception e){ e.printStackTrace();}
    }
}

    private void startThread(Runnable runnable) {
        stopThread();
        thread = new Thread(runnable);
        thread.start();
    }

    private void stopThread() {
        if (thread == null || !thread.isAlive()) {
            return;
        }

        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(int what) {
        if (handler == null) {
            return;
        }

        Message message = handler.obtainMessage();
        message.what = what;
        message.sendToTarget();
    }

    private void sendMessage(int what, int arg1) {
        if (handler == null) {
            return;
        }

        Message message = handler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.sendToTarget();
    }

    @Override
    protected void permissionGranted() {
        Message message = handler.obtainMessage();
        message.what = ConstantsAlbum.PERMISSION_GRANTED;
        message.sendToTarget();
    }
    protected void hideViews() {
        progressBar.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.INVISIBLE);
    }



private void loadAlbumsCart() {
    startThread(new AlbumLoaderRunnableCart());
}

    private class AlbumLoaderRunnableCart implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            /*
            If the adapter is null, this is first time this activity's view is
            being shown, hence send FETCH_STARTED message to show progress bar
            while images are loaded from phone
             */
            if (adapter == null) {
                sendMessage(ConstantsAlbum.FETCH_STARTED);
            }

            File file;
            HashSet<Long> selectedImages = new HashSet<>();
            if (images != null) {
                Image image;
                for (int i = 0, l = images.size(); i < l; i++) {
                    image = images.get(i);
                    file = new File(image.path);
                    if (file.exists() && image.isSelected) {
                        selectedImages.add(image.id);
                    }
                }
            }

            Cursor cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                            null, null, MediaStore.Images.Media.DATE_ADDED);

//            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
//                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{ album }, MediaStore.Images.Media.DATE_ADDED);
            if (cursor == null) {
                sendMessage(ConstantsAlbum.ERROR);
                return;
            }

        /*
        In case this runnable is executed to onChange calling loadImages,
        using countSelected variable can result in a race condition. To avoid that,
        tempCountSelected keeps track of number of selected images. On handling
        FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
         */
            int tempCountSelected = 0;
            ArrayList<Image> temp = new ArrayList<>(cursor.getCount());
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    boolean isSelected = selectedImages.contains(path);
                    if (isSelected) {
                        tempCountSelected++;
                    }

                    file = new File(path);
                    if (file.exists()) {
                        temp.add(new Image(id, name, path, isSelected));
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            if (images == null) {
                images = new ArrayList<>();
            }
            images.clear();
            images.addAll(temp);

            sendMessage(ConstantsAlbum.FETCH_COMPLETED, tempCountSelected);
        }
    }

}

package com.ti.multipleimageselect.MultipleImageSelect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.ti.multipleimageselect.MultipleImageSelect.helpers.CommonCall;
import com.ti.multipleimageselect.R;


public class ImageViewActivity extends AppCompatActivity {
    ImageView preview;
    String[] path;
    String imageurl;
    int count = 0;
    TextView pre, next;
    LinearLayout footer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Preview");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        preview= (ImageView) findViewById(R.id.preview);
        next = (TextView) findViewById(R.id.nex_t);
        pre = (TextView) findViewById(R.id.pre_vious);
        footer= (LinearLayout) findViewById(R.id.footer);

        preview.setOnTouchListener(new ImageMatrixTouchHandler(getApplicationContext()));

        if(getIntent().hasExtra("path")){

        path = getIntent().getStringArrayExtra("path");

            imageurl = path[count];

            if(path.length>1)
                footer.setVisibility(View.VISIBLE);

        Bitmap photo = null;



            CommonCall.LoadImage(getApplicationContext(), "file://"+imageurl,preview,R.drawable.ic_no_image,R.drawable.ic_no_image);

        }else {
            preview.setImageResource(R.drawable.ic_error);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(count<path.length)
               {count++;
                imageurl = path[count];
                previewImage(imageurl);}
            }
        });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count>0){
                    count--;
                    imageurl = path[count];
                    previewImage(imageurl);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void previewImage(String path){
        Bitmap photo = null;

        photo = BitmapFactory.decodeFile(path);
//            photo = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(path));

        preview.setImageBitmap(photo);
    }

}

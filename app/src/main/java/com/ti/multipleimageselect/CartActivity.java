package com.ti.multipleimageselect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.ti.multipleimageselect.MultipleImageSelect.adapters.CartAdapter;
import com.ti.multipleimageselect.MultipleImageSelect.models.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CartActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, CartAdapter.OnDataChangeListener {
    ListView selectedList;
    TextView selected, emptyCart;
    int imageCount=0;
    int totalImageCount = 0;
    Boolean remove_clicked= false;
    CartAdapter cartAdapter = null;
    public static JSONArray galleryImageArrayFromAdapter = new JSONArray();
    public static JSONArray cameraImageArrayFromAdapter = new JSONArray();
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("MY CART");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        selectedList = (ListView) findViewById(R.id.selected_list);
        emptyCart = (TextView) findViewById(R.id.empty_cart);
        selected = findViewById(R.id.selected_count);




        final Intent intent = getIntent();
 if(intent.hasExtra("source")&&intent.getStringExtra("source").equals("gallery"))
        {if(intent.hasExtra("imagearray")){
            imageCount = intent.getIntExtra("imagecount",0);
            String image = intent.getStringExtra("imagearray");
            try {
                int i;
                JSONArray jsonArray = new JSONArray(image);
                selectedList.setAdapter(cartAdapter);
                galleryImageArrayFromAdapter = new JSONArray();
                for(int k= 0; k<jsonArray.length();k++)
                {
                JSONObject jsonObject = jsonArray.getJSONObject(k);
                JSONObject object = new JSONObject();
                    object.put("path",jsonObject.getString("path"));
                    object.put("id",jsonObject.getLong("id"));
                    object.put("isSelected",jsonObject.getBoolean("isSelected"));
                    object.put("name",jsonObject.getString("name"));
                    object.put("isDuplicate",false);

                galleryImageArrayFromAdapter.put(object);
                }

                cartAdapter = new CartAdapter(getApplicationContext(), galleryImageArrayFromAdapter, this);
                selectedList.setAdapter(cartAdapter);

                imageCount = galleryImageArrayFromAdapter.length();
//
        } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    totalImageCount = imageCount;
        selected.setText(""+totalImageCount+" selected");
        selectedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("click","clicked");
            }
        });

        Utility.setListViewHeightBasedOnChildren(selectedList);
   }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.setListViewHeightBasedOnChildren(selectedList);

        if (cartAdapter != null){
            cartAdapter.setOnDataChangeListener(new CartAdapter.OnDataChangeListener() {
                public void onDataChanged(int size, JSONArray imagearray, String pathAdapter) {
                    for (int i = 0; i < ImageSelectActivity.images.size(); i++) {
                        if (pathAdapter.equals(ImageSelectActivity.images.get(i).path)) {
                            ImageSelectActivity.images.get(i).isSelected = false;
                        }
                    }

                    remove_clicked = true;

                    galleryImageArrayFromAdapter = imagearray;

                    imageCount=size;
                    totalImageCount = imageCount;
                    selected.setText(totalImageCount + " selected");
                    selectedList.setAdapter(cartAdapter);
                    if (totalImageCount == 0)
                        emptyCart.setVisibility(View.VISIBLE);
                }
            });
    }

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Log.e("click","clicked");

    }



    @Override
    public void onDataChanged(int size, JSONArray array, String pathAdapter) {
        Utility.setListViewHeightBasedOnChildren(selectedList);

        for(int i=0; i< ImageSelectActivity.images.size();i++){
            if (pathAdapter.equals(ImageSelectActivity.images.get(i).path)){
                ImageSelectActivity.images.get(i).isSelected=false;
            }
        }
        remove_clicked = true;
        galleryImageArrayFromAdapter = array;
        imageCount=size;
        totalImageCount = imageCount;
        selected.setText(totalImageCount+" selected");
        if (totalImageCount == 0)
            emptyCart.setVisibility(View.VISIBLE);

    }

    public static class Utility {
        public static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }



}

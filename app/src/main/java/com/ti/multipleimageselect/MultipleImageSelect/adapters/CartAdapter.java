package com.ti.multipleimageselect.MultipleImageSelect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ti.multipleimageselect.MultipleImageSelect.helpers.CommonCall;
import com.ti.multipleimageselect.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CartAdapter extends BaseAdapter {
    JSONArray imageArray;
    Context context;
    Object pos;
    public CartAdapter(Context context, JSONArray jsonArray, OnDataChangeListener listener){
        this.imageArray = jsonArray;
this.mOnDataChangeListener=listener;
        this.context = context;
    }

        @Override
    public int getCount() {
        return imageArray.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            pos = imageArray.get(i);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pos;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final int position =i;
        CustomViewHolder holder = null;
//        if (view == null) {
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cart_list_item, viewGroup, false);
        holder = new CustomViewHolder();
        holder.image = view.findViewById(R.id.image);
        holder.imageCount = view.findViewById(R.id.image_count);
        holder.remove = view.findViewById(R.id.remove);
        holder.duplicateText = view.findViewById(R.id.duplicate_text);

        String imageurl;
        try {
            JSONObject jsonObject = imageArray.getJSONObject(i);
            imageurl = jsonObject.getString("path");
            CommonCall.LoadImage(context, "file://"+imageurl,holder.image,R.drawable.ic_no_image,R.drawable.ic_no_image);

            view.setTag(jsonObject.getString("id"));

            if(jsonObject.getBoolean("isDuplicate")){
                holder.duplicateText.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               deleteItem(position);
            }
        });
        holder.imageCount.setText("Image_"+i);
        view.setTag(holder);
        return view;
    }

    public interface OnDataChangeListener{
    public void onDataChanged(int size, JSONArray array, String pathAdapter);
    }
    OnDataChangeListener mOnDataChangeListener;
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
    private void deleteItem(int position) {
        String pathAdapter="";
        try {
            pathAdapter = imageArray.getJSONObject(position).getString("path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imageArray.remove(position);
        mOnDataChangeListener.onDataChanged(imageArray.length(),imageArray,pathAdapter);
        // remove(int) does not exist for arrays, you would have to write that method yourself or use a List instead of an array
        notifyDataSetChanged();
    }

    public class CustomViewHolder {

        TextView imageCount, duplicateText;
        ImageView remove,image;

    }
}

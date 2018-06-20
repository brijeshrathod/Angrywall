package com.angrywallpaper.name;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<ImageUploadInfo> MainImageUploadInfoList;

    RecyclerViewAdapter(Context context, List<ImageUploadInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);

        holder.imageNameTextView.setText(UploadInfo.getImageName());

        //Loading image from Glide library.
        Glide.with(context)
                .load(UploadInfo.getImageURL())
                .into(holder.imageView);
    }


    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView imageNameTextView;
        View mySuperView;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            imageNameTextView = itemView.findViewById(R.id.ImageNameTextView);
            mySuperView = itemView.findViewById(R.id.mySuperView);

        }
    }
}

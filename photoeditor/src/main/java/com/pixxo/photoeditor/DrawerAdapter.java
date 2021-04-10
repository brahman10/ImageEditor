package com.pixxo.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.ArrayList;


public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {
    Context context;
    ArrayList<ImageModel> list;

    public DrawerAdapter(Context context, ArrayList<ImageModel> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_adapter,null);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull DrawerAdapter.ViewHolder holder, int position) {

        ImageModel model = list.get(position);
        File imgFile = new File(list.get(position).getPath());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.img_drawer.setImageBitmap(myBitmap);
        }
        if (list.get(position).isSelected())
        {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.red_color_picker));
        }
        else
        {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.white));
        }



    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_drawer;
        MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_drawer = itemView.findViewById(R.id.img_item);
            cardView = itemView.findViewById(R.id.card_item);
        }
    }
}
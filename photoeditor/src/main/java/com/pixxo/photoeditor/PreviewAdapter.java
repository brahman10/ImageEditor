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


public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {
    Context context;
    ArrayList<BitmapModel> list;

    public PreviewAdapter(Context context, ArrayList<BitmapModel> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public PreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_adapter,null);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull PreviewAdapter.ViewHolder holder, int position) {

        BitmapModel model = list.get(position);
        Bitmap myBitmap = model.getBitmap();
        holder.img_drawer.setImageBitmap(myBitmap);
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
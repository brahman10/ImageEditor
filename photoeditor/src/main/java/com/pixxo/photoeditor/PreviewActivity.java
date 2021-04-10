package com.pixxo.photoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.pixxo.photoeditor.Interface.ClickListener;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    ArrayList <BitmapModel> bitmapModels = new ArrayList<>();
    PreviewAdapter previewAdapter;
    RecyclerView rec_prev;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        rec_prev = findViewById(R.id.rec_preview);
        imageView = findViewById(R.id.img_preview);
        bitmapModels = (ArrayList<BitmapModel>) getIntent().getSerializableExtra("list");
        bitmapModels.get(0).setSelected(true);
        imageView.setImageBitmap(bitmapModels.get(0).getBitmap());
        previewAdapter = new PreviewAdapter(this,bitmapModels);

        rec_prev.addOnItemTouchListener(new RecyclerTouchListene(this,
                rec_prev, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                for (BitmapModel m:bitmapModels
                ) {
                    m.setSelected(false);
                }
                bitmapModels.get(position).setSelected(true);
                imageView.setImageBitmap(bitmapModels.get(position).getBitmap());
                previewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(View view, int position) {
                return;
            }
        }));

    }
}
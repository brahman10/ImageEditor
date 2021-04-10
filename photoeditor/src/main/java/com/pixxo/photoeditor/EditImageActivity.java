package com.pixxo.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.pixxo.photoeditor.Interface.ClickListener;
import com.pixxo.photoeditor.base.BaseActivity;
import com.pixxo.photoeditor.filters.FilterListener;
import com.pixxo.photoeditor.filters.FilterViewAdapter;
import com.pixxo.photoeditor.tools.EditingToolsAdapter;
import com.pixxo.photoeditor.tools.ToolType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditImageActivity extends BaseActivity
    implements OnPhotoEditorListener,
    View.OnClickListener,
    PropertiesBSFragment.Properties,
    EmojiBSFragment.EmojiListener,
    StickerBSFragment.StickerListener,
    EditingToolsAdapter.OnItemSelected,
    FilterListener {

  public static final String FILE_PROVIDER_AUTHORITY =
      "com.burhanrashid52.photoeditor.fileprovider";
  private static final String TAG = EditImageActivity.class.getSimpleName();
  public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
  public static String EDIT_IMAGE_URI_STRING = "edit_uri_string";
  private static final int CAMERA_REQUEST = 1;
  private static final int PICK_REQUEST = 2;
  PhotoEditor mPhotoEditor;
  private PhotoEditorView mPhotoEditorView;
  private PropertiesBSFragment mPropertiesBSFragment;
  private EmojiBSFragment mEmojiBSFragment;
  private StickerBSFragment mStickerBSFragment;
  private Typeface mWonderFont;
  private RecyclerView mRvTools, mRvFilters;
  private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
  private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
  private ConstraintLayout mRootView;
  private ConstraintSet mConstraintSet = new ConstraintSet();
  private boolean mIsFilterVisible;
  Uri uri;
  String savedImagePath = null;
  ArrayList<ImageModel> imageModelList , editedImageList;
  RecyclerView rec_image;
  DrawerAdapter drawerAdapter;
  ArrayList<Bitmap> initial_list , final_list;
  int currentposition =0;

  @Nullable
  @VisibleForTesting
  Uri mSaveImageUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    makeFullScreen();
    setContentView(R.layout.activity_edit_image);
    initViews();
    handleIntentImage(mPhotoEditorView.getSource());

    mWonderFont =
        Typeface.createFromAsset(getAssets(), getString(R.string.beyond_wonderland_dot_ttf));

    mPropertiesBSFragment = new PropertiesBSFragment();
    mEmojiBSFragment = new EmojiBSFragment();
    mStickerBSFragment = new StickerBSFragment();
    mStickerBSFragment.setStickerListener(this);
    mEmojiBSFragment.setEmojiListener(this);
    mPropertiesBSFragment.setPropertiesChangeListener(this);

    LinearLayoutManager llmTools =
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    mRvTools.setLayoutManager(llmTools);
    mRvTools.setAdapter(mEditingToolsAdapter);

    LinearLayoutManager llmFilters =
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    mRvFilters.setLayoutManager(llmFilters);
    mRvFilters.setAdapter(mFilterViewAdapter);

    mPhotoEditor =
        new PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            // .setDefaultTextTypeface(mTextRobotoTf)
            // .setDefaultEmojiTypeface(mEmojiTypeFace)
            .build(); // build photo editor sdk

    mPhotoEditor.setOnPhotoEditorListener(this);


    rec_image.addOnItemTouchListener(new RecyclerTouchListene(this,
            rec_image, new ClickListener() {
      @Override
      public void onClick(View view, final int position) {
        for (ImageModel m:imageModelList
             ) {
          m.setSelected(false);
        }
        imageModelList.get(position).setSelected(true);
        mPhotoEditorView.getSource().setImageBitmap(final_list.get(position));
        currentposition = position;
        drawerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onLongClick(View view, int position) {
        return;
      }
    }));
    // Set Image Dynamically
    // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);
  }

  private void handleIntentImage(ImageView source) {
    Intent intent = getIntent();
    if (intent != null) {
      imageModelList = (ArrayList<ImageModel>) intent.getSerializableExtra("LIST");
      for (ImageModel m:imageModelList
      ) {
        m.setSelected(false);
        File file = new File(m.getPath());
        if (file.exists())
        {
          Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
          initial_list.add(myBitmap);
        }
      }
      final_list = initial_list;
      drawerAdapter = new DrawerAdapter(this,imageModelList);
      rec_image.setAdapter(drawerAdapter);
      mPhotoEditorView.getSource().setImageBitmap(initial_list.get(0));

    }
  }

  private void initViews() {
    ImageView imgUndo;
    ImageView imgRedo;
    ImageView imgSave;
    ImageView imgShare;
    ImageView imgBack;
    editedImageList = new ArrayList<>();
    initial_list = new ArrayList<>();
    final_list = new ArrayList<>();
    mPhotoEditorView = findViewById(R.id.photoEditorView);
    mRvTools = findViewById(R.id.rvConstraintTools);
    mRvFilters = findViewById(R.id.rvFilterView);
    mRootView = findViewById(R.id.rootView);
    rec_image = findViewById(R.id.rec_image);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
    rec_image.setLayoutManager(linearLayoutManager);
    imgUndo = findViewById(R.id.imgUndo);
    imgUndo.setOnClickListener(this);

    imgRedo = findViewById(R.id.imgRedo);
    imgRedo.setOnClickListener(this);


    imgSave = findViewById(R.id.imgDone);
    imgSave.setOnClickListener(this);


    imgShare = findViewById(R.id.imgShare);
    imgShare.setOnClickListener(this);

    imgBack = findViewById(R.id.imgBack);
    imgBack.setOnClickListener(this);

  }

  @Override
  public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
    TextEditorDialogFragment textEditorDialogFragment =
        TextEditorDialogFragment.show(this, text, colorCode);
    textEditorDialogFragment.setOnTextEditorListener(
        new TextEditorDialogFragment.TextEditor() {
          @Override
          public void onDone(String inputText, int colorCode) {
            final TextStyleBuilder styleBuilder = new TextStyleBuilder();
            styleBuilder.withTextColor(colorCode);

            mPhotoEditor.editText(rootView, inputText, styleBuilder);
            //mTxtCurrentTool.setText(R.string.label_text);
          }
        });
  }

  @Override
  public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
    Log.d(
        TAG,
        "onAddViewListener() called with: viewType = ["
            + viewType
            + "], numberOfAddedViews = ["
            + numberOfAddedViews
            + "]");
  }

  @Override
  public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
    Log.d(
        TAG,
        "onRemoveViewListener() called with: viewType = ["
            + viewType
            + "], numberOfAddedViews = ["
            + numberOfAddedViews
            + "]");
  }

  @Override
  public void onStartViewChangeListener(ViewType viewType) {
    Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
  }

  @Override
  public void onStopViewChangeListener(ViewType viewType) {
    Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.imgUndo) {
      mPhotoEditor.undo();
    } else if (id == R.id.imgRedo) {
      mPhotoEditor.redo();
    } else if (id == R.id.imgDone) {
      saveImage(getApplicationContext());
    } else if (id == R.id.imgShare) {
      shareImage();
    }else if(id == R.id.imgBack){
      goBack();
    }
  }

  private void shareImage() {
    Intent intent = new Intent(this,PreviewActivity.class);
    ArrayList<BitmapModel> bitmapModels = new ArrayList<>();
    for (Bitmap b:final_list
         ) {
      bitmapModels.add(new BitmapModel(b));
    }
    intent.putExtra("list", (Serializable) bitmapModels);
    startActivity(intent);
  }

  private Uri buildFileProviderUri(@NonNull Uri uri) {
    return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, new File(uri.getPath()));
  }

  @SuppressLint("MissingPermission")
  private void saveImage(final Context context) {
    mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
      @Override
      public void onBitmapReady(Bitmap saveBitmap) {
        try {
          final_list.remove(currentposition);
        }
        catch (Exception e)
        {

        }
        final_list.add(currentposition,saveBitmap);
      }

      @Override
      public void onFailure(Exception e) {

      }
    });

  }

  private static void galleryAddPic(Context context, String imagePath) {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File f = new File(imagePath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    context.sendBroadcast(mediaScanIntent);
  }


  @Override
  public void onColorChanged(int colorCode) {
    mPhotoEditor.setBrushColor(colorCode);
    //mTxtCurrentTool.setText(R.string.label_brush);
  }

  @Override
  public void onOpacityChanged(int opacity) {
    mPhotoEditor.setOpacity(opacity);
    //mTxtCurrentTool.setText(R.string.label_brush);
  }

  @Override
  public void onBrushSizeChanged(int brushSize) {
    mPhotoEditor.setBrushSize(brushSize);
    //mTxtCurrentTool.setText(R.string.label_brush);
  }

  @Override
  public void onEmojiClick(String emojiUnicode) {
    mPhotoEditor.addEmoji(emojiUnicode);
   // mTxtCurrentTool.setText(R.string.label_emoji);
  }

  @Override
  public void onStickerClick(Bitmap bitmap) {
    mPhotoEditor.addImage(bitmap);
    //mTxtCurrentTool.setText(R.string.label_sticker);
  }

  @Override
  public void isPermissionGranted(boolean isGranted, String permission) {
    if (isGranted) {
      saveImage(getApplicationContext());
    }
  }

  private void showSaveDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.are_you_want_to_exit_without_saving_image);
    builder.setPositiveButton(
        R.string.save,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            saveImage(getApplicationContext());
          }
        });
    builder.setNegativeButton(
        R.string.cancel,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    builder.setNeutralButton(
        R.string.discard,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        });
    builder.create().show();
  }

  @Override
  public void onFilterSelected(PhotoFilter photoFilter) {
    mPhotoEditor.setFilterEffect(photoFilter);
  }

  @Override
  public void onToolSelected(ToolType toolType) {
    switch (toolType) {
      case BRUSH:
        mPhotoEditor.setBrushDrawingMode(true);
        //mTxtCurrentTool.setText(R.string.label_brush);
        mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
        break;
      case TEXT:
        TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
        textEditorDialogFragment.setOnTextEditorListener(
            new TextEditorDialogFragment.TextEditor() {
              @Override
              public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.addText(inputText, styleBuilder);
                //mTxtCurrentTool.setText(R.string.label_text);
              }
            });
        break;
      case ERASER:
        mPhotoEditor.brushEraser();
        //mTxtCurrentTool.setText(R.string.label_eraser);
        break;
      case FILTER:
        //mTxtCurrentTool.setText(R.string.label_filter);
        showFilter(true);
        break;
      case EMOJI:
        mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
        break;
      case STICKER:
        mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
        break;
    }
  }

  void showFilter(boolean isVisible) {
    mIsFilterVisible = isVisible;
    mConstraintSet.clone(mRootView);

    if (isVisible) {
      mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
      mConstraintSet.connect(
          mRvFilters.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
      mConstraintSet.connect(
          mRvFilters.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
    } else {
      mConstraintSet.connect(
          mRvFilters.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END);
      mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
    }

    ChangeBounds changeBounds = new ChangeBounds();
    changeBounds.setDuration(350);
    changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
    TransitionManager.beginDelayedTransition(mRootView, changeBounds);

    mConstraintSet.applyTo(mRootView);
  }

  @Override
  public void onBackPressed() {
    if (mIsFilterVisible) {
      showFilter(false);
      //mTxtCurrentTool.setText(R.string.app_name);
    } else if (!mPhotoEditor.isCacheEmpty()) {
      showSaveDialog();
    } else {
      super.onBackPressed();
    }
  }

  private void goBack(){
    if (!mPhotoEditor.isCacheEmpty()) {
      showSaveDialog();
    }else {
      super.onBackPressed();
    }
  }
}

package ir.ham3da.darya;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.AnticipateOvershootInterpolator;

import android.widget.ImageView;

import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;


import androidx.core.content.FileProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ir.ham3da.darya.adaptors.BackGroundAdapter;
import ir.ham3da.darya.adaptors.BackGroundItem;
import ir.ham3da.darya.filters.FilterListener;
import ir.ham3da.darya.filters.FilterViewAdapter;
import ir.ham3da.darya.imageeditor.EmojiBSFragment;
import ir.ham3da.darya.imageeditor.PropertiesBSFragment;
import ir.ham3da.darya.imageeditor.ShadowColorDialogFragment;
import ir.ham3da.darya.imageeditor.StickerBSFragment;
import ir.ham3da.darya.imageeditor.TextEditorDialogFragment;
import ir.ham3da.darya.tools.EditingToolsAdapter;
import ir.ham3da.darya.tools.PermissionMediaType;
import ir.ham3da.darya.tools.ToolType;
import ir.ham3da.darya.utility.AppFontManager;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.CustomProgress;
import ir.ham3da.darya.utility.PermissionHelper;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;

public class ActivityImageEdit extends AppCompatActivity implements
        OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener,
        EditingToolsAdapter.OnItemSelected,
        FilterListener {

    String poemText;
    int fontId;

    private static final String TAG = ActivityImageEdit.class.getSimpleName();
    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    // private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters, rvBackground;

    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible, shareRequest, mIsBackgroundVisible;

    private Uri imagSevePath;
    private Typeface mTextIranSansTf;

    private File photoFile;

    List<BackGroundItem> resListBackGroundItem;
    BackGroundAdapter backGroundAdapter;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri photoUri;

    private ActivityResultLauncher<String> saveImagePermissionLauncher;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_image_menu, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        } else if (id == R.id.action_save) {
            saveImage(false);
        } else if (id == R.id.action_share) {
            shareImage();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void shareImage() {
        if (imagSevePath != null) {
            UtilFunctions.shareImage(ActivityImageEdit.this, imagSevePath);
        } else {
            saveImage(true);
        }

    }

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this);
        setContentView(R.layout.activity_image_edit);

        toolbar = findViewById(R.id.img_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.poetry_in_image);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (mIsFilterVisible || mIsBackgroundVisible) {
                    if (mIsFilterVisible) {
                        showFilter(false);
                    }
                    if (mIsBackgroundVisible) {
                        showBackgrounds(false);
                    }
                } else if (!mPhotoEditor.isCacheEmpty()) {
                    showSaveDialog();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }


            }
        });

        AppSettings.Init(this);

        String signature = AppSettings.getSignature();

        EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(ActivityImageEdit.this, this);

        poemText = getIntent().getStringExtra("poemText");
        String poetName = getIntent().getStringExtra("poetName");
        PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);
        fontId = AppSettings.getPoemsFont();
        mTextIranSansTf = AppFontManager.getTypeface(this, fontId);
        initViews();
        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                .setDefaultTextTypeface(mTextIranSansTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
        textStyleBuilder.withTextColor(Color.rgb(0, 0, 0));
        textStyleBuilder.withTextFont(mTextIranSansTf);


        poemText += System.lineSeparator() + poetName;

        if (!signature.isEmpty()) {
            signature = "«" + signature + "»";
        }

        mPhotoEditor.addText(poemText, textStyleBuilder);
        mPhotoEditor.setOnPhotoEditorListener(this);



        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {



                        Glide.with(this)
                                .asBitmap()
                                .load(photoUri)
                                .override(1024, 1024)
                                .centerInside()
                                .into(new CustomTarget<Bitmap>() {
                                   @Override
                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                @Nullable @org.jetbrains.annotations.Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {


                                        Drawable drawable = new BitmapDrawable(getResources(), resource);
                                        mPhotoEditorView.getSource().setImageDrawable(drawable);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // اینجا اگر لازم شد منابع را آزاد کن
                                    }
                                });

                    }
                }
        );

        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        Glide.with(this)
                                .asBitmap()
                                .load(uri)
                                .override(1024, 1024)
                                .centerInside()
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                @Nullable @org.jetbrains.annotations.Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {


                                        Drawable drawable = new BitmapDrawable(getResources(), resource);
                                        mPhotoEditorView.getSource().setImageDrawable(drawable);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // اینجا اگر لازم شد منابع را آزاد کن
                                    }
                                });
                    }
                });

        saveImagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        doSaveImage(shareRequest);
                    } else {
                        Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    //    ActivityResultLauncher<String> cameraPermissionLauncher;
    ActivityResultLauncher<PickVisualMediaRequest> galleryActivityResultLauncher;

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        //mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        rvBackground = findViewById(R.id.rvBackground);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);
        imgCamera.setVisibility(View.VISIBLE);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);
        imgGallery.setVisibility(View.VISIBLE);

        mRvFilters.setVisibility(View.GONE);
        rvBackground.setVisibility(View.GONE);


        resListBackGroundItem = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            BackGroundItem backGroundItem = new BackGroundItem(this);
            backGroundItem.setId(i);
            resListBackGroundItem.add(backGroundItem);
        }


        backGroundAdapter = new BackGroundAdapter(this, resListBackGroundItem);
        LinearLayoutManager llmBackground = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvBackground.setLayoutManager(llmBackground);
        rvBackground.setAdapter(backGroundAdapter);

        backGroundAdapter.setItemClickListener((position, view) -> {
            int bgID = resListBackGroundItem.get(position).getResIDBig();
            if (view.getId() == R.id.card_view_top) {
                mPhotoEditorView.getSource().setImageResource(bgID);
            }
        });

    }


    public static Bitmap loadOptimizedBitmapFromUri(Context context, Uri uri, int maxSize) {
        if (context == null || uri == null) {
            return null;
        }

        try {
            if (Build.VERSION.SDK_INT >= 28) {
                // استفاده از ImageDecoder برای API 28+
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                return ImageDecoder.decodeBitmap(source, (decoder, info, src) -> {
                    int width = info.getSize().getWidth();
                    int height = info.getSize().getHeight();

                    if (width > maxSize || height > maxSize) {
                        float scale = Math.min((float) maxSize / width, (float) maxSize / height);
                        decoder.setTargetSize((int) (width * scale), (int) (height * scale));
                    }
                    decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE); // جلوگیری از OOM در بعضی دستگاه‌ها
                });
            } else {
                // استفاده از BitmapFactory برای API پایین‌تر
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                try (InputStream input = context.getContentResolver().openInputStream(uri)) {
                    BitmapFactory.decodeStream(input, null, options);
                }

                options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize);
                options.inJustDecodeBounds = false;

                try (InputStream input = context.getContentResolver().openInputStream(uri)) {
                    return BitmapFactory.decodeStream(input, null, options);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * محاسبه inSampleSize برای کاهش ابعاد تصویر
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode1) -> {
            final TextStyleBuilder styleBuilder = new TextStyleBuilder();
            styleBuilder.withTextColor(colorCode1);
            styleBuilder.withTextFont(mTextIranSansTf);

            mPhotoEditor.editText(rootView, inputText, styleBuilder);
            // mTxtCurrentTool.setText(R.string.label_text);
        });
    }


    @Override
    public void onShadowColorChangeListener(View rootView, int colorCode, float shadowDx, float shadowDy, float shadowRadius) {
        ShadowColorDialogFragment shadowColorDialogFragment =
                ShadowColorDialogFragment.show(this, shadowDx, shadowDy, shadowRadius, colorCode);


        shadowColorDialogFragment.setOnShadowColorListener((shadow_Dx, shadow_Dy, shadowRadius1, colorCode1) -> mPhotoEditor.setTextShadow(rootView, shadow_Dx, shadow_Dy, shadowRadius1, colorCode1));
    }


    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews, View rootView) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.imgUndo) {
            mPhotoEditor.undo();
        } else if (id == R.id.imgRedo) {
            mPhotoEditor.redo();
        } else if (id == R.id.imgCamera) {
            takePhoto();
        } else if (id == R.id.imgGallery) {
            pickFromGallery();
        }
    }

    private void takePhoto() {
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo_" + System.currentTimeMillis() + ".jpg");
        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
        takePictureLauncher.launch(photoUri);
    }

    private void pickFromGallery() {
        galleryActivityResultLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    protected void doSaveImage(final boolean share) {
        final CustomProgress customProgressDlg = new CustomProgress(this);
        customProgressDlg.showProgress(getString(R.string.saving), getString(R.string.please_wait2), false, false, true);

        try {
            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(false)
                    .setTransparencyEnabled(true)
                    .build();

            String fileName = "darya_" + System.currentTimeMillis() + ".png";
            String imgFullName = getExternalFilesDir(null).getAbsolutePath() + "/" + fileName;

            new Thread(() -> {
                mPhotoEditor.saveAsFile(imgFullName, fileName, saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        showSnackbar(getString(R.string.saved));
                        customProgressDlg.dismiss();
                        Log.e(TAG, "onSuccess: imagePath" + imagePath);
                        imagSevePath = Uri.parse(imagePath);

                        if (share) {
                            shareImage();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "onFailure saveImage: " + exception.getMessage());
                        exception.printStackTrace();
                        showSnackbar(getString(R.string.failed_save));
                        customProgressDlg.dismiss();
                    }
                });
            }).start();


        } catch (Exception e) {
            Log.e(TAG, "saveImage: " + e.getMessage());
            showSnackbar(Objects.requireNonNull(e.getMessage()));
            customProgressDlg.dismiss();
        }
    }


    private void saveImage(boolean share) {
        shareRequest = share;
        PermissionHelper.requestMediaPermission(
                this,
                PermissionMediaType.IMAGES,
                saveImagePermissionLauncher,
                new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        doSaveImage(share);
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
    }


    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alret_save);

        builder.setPositiveButton(R.string.save, (dialog, which) -> saveImage(false));


        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton(R.string.discard, (dialog, which) -> finish());
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
            case BACKGROUND:
                showBackgrounds(true);
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);

                textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);
                    styleBuilder.withTextFont(mTextIranSansTf);

                    mPhotoEditor.addText(inputText, styleBuilder);
                    //mTxtCurrentTool.setText(R.string.label_text);
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                //  mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case FILTER:
                // mTxtCurrentTool.setText(R.string.label_filter);
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


    protected void showBackgrounds(boolean isVisible) {
        mIsBackgroundVisible = isVisible;
        mConstraintSet.clone(mRootView);
        Log.e(TAG, "showBackgrounds: " + isVisible);
        if (isVisible) {
            rvBackground.setVisibility(View.VISIBLE);
        } else {
            rvBackground.setVisibility(View.GONE);

        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    protected void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);
        Log.e(TAG, "showFilter: " + isVisible);
        if (isVisible) {

            mRvFilters.setVisibility(View.VISIBLE);

        } else {
            mRvFilters.setVisibility(View.GONE);

        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    protected void showSnackbar(@NonNull String message) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


}

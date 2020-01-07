package ir.ham3da.darya.imageeditor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ir.ham3da.darya.R;

/**
 * Created by Javad Ahshamian 2019-11-04.
 */

public class ShadowColorDialogFragment extends DialogFragment
{
    public static final String TAG = ShadowColorDialogFragment.class.getSimpleName();

    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    public static final String EXTRA_SHADOW_DX = "extra_shadow_dx";
    public static final String EXTRA_SHADOW_DY = "extra_shadow_dy";
    public static final String EXTRA_SHADOW_RADIUS = "extra_shadow_radius";

    private int mColorCode;
    private float mShadowDx, mShadowDy, mShadowRadius;
    private SeekBar seekBarRadius, seekBarDx, seekBarDy;


    private TextView mTextView;
    private TextView mAddTextDoneTextView;
    private  ShadowColor  mShadowColor;



    //Show dialog with default text input as empty and text color white
    public static ShadowColorDialogFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity,
                1.5f, 1.5f, 2.0f, ContextCompat.getColor(appCompatActivity, R.color.white));
    }

    //Show dialog with provide text and text color
    public static ShadowColorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @FloatRange float shadow_Dx,
                                                @FloatRange float shadow_Dy,
                                                @FloatRange float shadowRadius,
                                                @ColorInt int colorCode) {
        Bundle args = new Bundle();


        args.putInt(EXTRA_COLOR_CODE, colorCode);
        args.putFloat(EXTRA_SHADOW_DX, shadow_Dx);
        args.putFloat(EXTRA_SHADOW_DY, shadow_Dy);
        args.putFloat(EXTRA_SHADOW_RADIUS, shadowRadius);


        ShadowColorDialogFragment fragment = new ShadowColorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }


    public interface ShadowColor
    {
        /**
         *
         * @param shadow_Dx shadow Dx
         * @param shadow_Dy shadow Dy
         * @param shadowRadius shadow Radius
         * @param colorCode color Code
         */
        void onDone(float shadow_Dx, float shadow_Dy, float shadowRadius, int colorCode);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shadow_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView = view.findViewById(R.id.mTextView);

        seekBarRadius = view.findViewById(R.id.seekBarRadius);
        seekBarDx =  view.findViewById(R.id.seekBarDx);
        seekBarDy =  view.findViewById(R.id.seekBarDy);

        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv);

        //Setup the color picker for text color
        RecyclerView addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                mColorCode = colorCode;
                mTextView.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mColorCode);

            }
        });
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);

        mTextView.setText(R.string.sample_text);

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mShadowRadius = (float) progress / 2;
                    mTextView.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mColorCode);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarDx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mShadowDx = (float) progress / 2;
                    mTextView.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mColorCode);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarDy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mShadowDy = (float) progress / 2;
                    mTextView.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mColorCode);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mColorCode = getArguments().getInt(EXTRA_COLOR_CODE);

        mShadowDx = getArguments().getFloat(EXTRA_SHADOW_DX);
        mShadowDy = getArguments().getFloat(EXTRA_SHADOW_DY);
        mShadowRadius = getArguments().getFloat(EXTRA_SHADOW_RADIUS);

        mTextView.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mColorCode);

        setSeekBarsValue();
        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mTextView != null) {
                    mShadowColor.onDone(mShadowDx, mShadowDy, mShadowRadius, mColorCode);
                  }
            }
        });

    }

   private void setSeekBarsValue()
   {
       int r = (int) (mShadowRadius*2);
       seekBarRadius.setProgress( r);

       int dx = (int) (mShadowDx*2);
       seekBarDx.setProgress(dx);

       int dy = (int) (mShadowDy*2);
       seekBarDy.setProgress(dy);
   }

    public void setOnShadowColorListener(ShadowColor shadowColor)
    {
        mShadowColor = shadowColor;
    }
}

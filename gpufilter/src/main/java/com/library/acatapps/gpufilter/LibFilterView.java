package com.library.acatapps.gpufilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.library.gpu.filter.FilterModel;

import org.jetbrains.annotations.NotNull;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;


public class LibFilterView extends ConstraintLayout {

    private AppCompatSeekBar mAdjustSeekBar;
    private RecyclerView mFilterListView;
    private FilterListAdapter mFilterListAdapter;
    private AppCompatImageView mIconCheck;
    private AppCompatTextView mFilterNameLabel;

    private Callback mCallback;

    public LibFilterView(Context context) {
        super(context);
        init();
    }

    public LibFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LibFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setClickable(true);
        this.setFocusable(true);
        initIconCheck();
        initFilterNameLabel();
        initAdjustSeekBar();
        initFilterListView();
        addView(mIconCheck);
        addView(mFilterNameLabel);
        addView(mAdjustSeekBar);
        addView(mFilterListView);
        mIconCheck.setId(View.generateViewId());
        mFilterNameLabel.setId(View.generateViewId());
        mAdjustSeekBar.setId(View.generateViewId());
        mFilterListView.setId(View.generateViewId());
        constraint();
    }


    private void initFilterNameLabel() {
        mFilterNameLabel = new AppCompatTextView(getContext());
        mFilterNameLabel.setTextColor(Color.parseColor("#000000"));
        mFilterNameLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    }

    private void initIconCheck() {
        int padding = DimenUtils.INSTANCE.dpToPx(getContext(), 12);
        mIconCheck = new AppCompatImageView(getContext());
        mIconCheck.setPadding(padding, padding, padding, padding);
        mIconCheck.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_check_vector));
        mIconCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickOk();
            }
        });
    }

    private void initFilterListView() {
        mFilterListView = new RecyclerView(getContext());
        mFilterListAdapter = new FilterListAdapter(getContext());
        mFilterListView.setAdapter(mFilterListAdapter);
        mFilterListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    private void constraint() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        constraintSet.connect(mFilterListView.getId(), ConstraintSet.BOTTOM, this.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(mFilterListView.getId(), ConstraintSet.START, this.getId(), ConstraintSet.START);
        constraintSet.connect(mFilterListView.getId(), ConstraintSet.END, this.getId(), ConstraintSet.END);

        constraintSet.connect(mAdjustSeekBar.getId(), ConstraintSet.BOTTOM, mFilterListView.getId(), ConstraintSet.TOP);
        constraintSet.connect(mAdjustSeekBar.getId(), ConstraintSet.START, this.getId(), ConstraintSet.START);
        constraintSet.connect(mAdjustSeekBar.getId(), ConstraintSet.END, this.getId(), ConstraintSet.END);

        constraintSet.connect(mIconCheck.getId(), ConstraintSet.BOTTOM, mAdjustSeekBar.getId(), ConstraintSet.TOP);
        constraintSet.connect(mIconCheck.getId(), ConstraintSet.END, this.getId(), ConstraintSet.END);

        constraintSet.connect(mFilterNameLabel.getId(), ConstraintSet.TOP, mIconCheck.getId(), ConstraintSet.TOP);
        constraintSet.connect(mFilterNameLabel.getId(), ConstraintSet.END, this.getId(), ConstraintSet.END);
        constraintSet.connect(mFilterNameLabel.getId(), ConstraintSet.START, this.getId(), ConstraintSet.START);

        constraintSet.applyTo(this);
    }


    public void changeImagePreview(Bitmap bitmap) {
        mFilterListAdapter.changePreviewImage(bitmap);
    }

    private void initAdjustSeekBar() {
        mAdjustSeekBar = new AppCompatSeekBar(getContext());
        mAdjustSeekBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtils.INSTANCE.dpToPx(getContext(), 32)));
        mAdjustSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mFilterListAdapter.adjust(seekBar.getProgress());
            }
        });
        mAdjustSeekBar.setVisibility(View.INVISIBLE);
    }

    public void setCallback(final Callback callback) {
        mCallback = callback;
        mFilterListAdapter.setOnChange(new FilterListAdapter.OnChange() {
            @Override
            public void onAdjust(@NotNull FilterModel filterModel) {
                callback.onAdjust(filterModel.getGpuImageFilter());
            }

            @Override
            public void onChangeFilter(@NotNull FilterModel filterModel) {
                mFilterNameLabel.setText(filterModel.getTitle());
                if (filterModel.getFilterAdjuster().canAdjust()) {
                    mAdjustSeekBar.setVisibility(View.VISIBLE);
                } else {
                    mAdjustSeekBar.setVisibility(View.INVISIBLE);
                }
                mAdjustSeekBar.setProgress(filterModel.getCurrentPercent());
                callback.onChangeFilter(filterModel.getGpuImageFilter());
            }
        });
    }

    public void destroy() {
        mFilterListAdapter.dispose();
    }

    public interface Callback {
        void onChangeFilter(GPUImageFilter gpuImageFilter);
        void onAdjust(GPUImageFilter gpuImageFilter);
        void onClickClose();
        void onClickOk();
    }

}

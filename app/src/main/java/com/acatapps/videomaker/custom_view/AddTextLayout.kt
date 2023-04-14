package com.acatapps.videomaker.custom_view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.acatapps.videomaker.R
import com.acatapps.videomaker.adapter.ColorListAdapter
import com.acatapps.videomaker.adapter.FontListAdapter
import com.acatapps.videomaker.databinding.LayoutAddTextBinding
import com.acatapps.videomaker.models.TextStickerAttrData
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import kotlinx.android.synthetic.main.layout_add_text.view.*
import kotlinx.android.synthetic.main.layout_edit_text_color.view.*
import kotlinx.android.synthetic.main.layout_edit_text_fonts.view.*
import kotlinx.android.synthetic.main.layout_edit_text_style.view.*

class AddTextLayout :
    LinearLayout {
    private var mEditState = false
    private var mMainTextSticker: EditTextSticker? = null
    private val mColorListAdapter = ColorListAdapter {
        mMainTextSticker?.changeColor(it)
    }
    private val mFontListAdapter = FontListAdapter {
        onChangeFont(it)
    }
    private var mEditMode = EditMode.TEXT

    private var mTextAttrData: TextStickerAttrData? = null

    constructor(context: Context?, mMainTextSticker: EditTextSticker?) : super(context) {
        this.mMainTextSticker = mMainTextSticker
        mEditState = true
        mTextAttrData = mMainTextSticker?.getTextAttrData()
        initAttrs()
        updateIcon()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        mMainTextSticker: EditTextSticker?
    ) : super(context, attrs) {
        this.mMainTextSticker = mMainTextSticker

        mEditState = true
        mTextAttrData = mMainTextSticker?.getTextAttrData()
        initAttrs()
        updateIcon()
    }


    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        mMainTextSticker: EditTextSticker?
    ) : super(context, attrs, defStyleAttr) {
        this.mMainTextSticker = mMainTextSticker

        mEditState = true
        mTextAttrData = mMainTextSticker?.getTextAttrData()
        initAttrs()
        updateIcon()
    }

    private fun initAttrs() {
        val view = LayoutAddTextBinding.inflate(LayoutInflater.from(context))
        addView(view.root)
        mColorListAdapter.setItemList(Utils.readTextColorFile())
        initAction()
        initView()
    }

    private fun initAction() {
        icKeyboard.setOnClickListener {
            mEditMode = EditMode.TEXT
            showKeyboard()

            updateIcon()
        }
        icColor.setOnClickListener {
            if (mEditMode == EditMode.COLOR) return@setOnClickListener
            hideKeyboard()
            mEditMode = EditMode.COLOR
            showChangeColorLayout()
            updateIcon()
        }
        icFonts.setOnClickListener {
            if (mEditMode == EditMode.FONTS) return@setOnClickListener
            mEditMode = EditMode.FONTS
            hideKeyboard()
            showChangeFontLayout()
            updateIcon()
        }
        icStyle.setOnClickListener {
            if (mEditMode == EditMode.STYLE) return@setOnClickListener
            mEditMode = EditMode.STYLE
            hideKeyboard()
            showChangeStyleLayout()
            updateIcon()
        }

    }

    fun showKeyboard() {
        mEditMode = EditMode.TEXT
        toolsDetails.removeAllViews()
        openKeyboard()
        mMainTextSticker?.requestFocus()
        //hideKeyboard()

    }

    private fun initView() {
        if (mMainTextSticker == null) {
            mMainTextSticker = EditTextSticker(context, null).apply {
                id = View.generateViewId()
            }
        }

        val screenW = Utils.screenWidth(context)
        val videoPreviewScale = Utils.videoPreviewScale()

        textContainer.layoutParams.width = (screenW * videoPreviewScale).toInt()
        textContainer.layoutParams.height = (screenW * videoPreviewScale).toInt()
        textContainer.addView(mMainTextSticker)


    }

    private var autoShowKeyboard = false
    fun onResume() {
        Logger.e("add text layout on resume")
        object : CountDownTimer(500, 500) {
            override fun onFinish() {
                if (autoShowKeyboard)
                    openKeyboard()
            }

            override fun onTick(millisUntilFinished: Long) {

            }

        }.start()

    }

    private fun showChangeFontLayout() {
        val view = View.inflate(context, R.layout.layout_edit_text_fonts, null)
        showToolsView(view)
        view.fontsListView.adapter = mFontListAdapter
        view.fontsListView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun showChangeColorLayout() {
        val view = View.inflate(context, R.layout.layout_edit_text_color, null)
        showToolsView(view)
        view.textColorListView.apply {
            adapter = mColorListAdapter
            layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun showChangeStyleLayout() {
        val view = View.inflate(context, R.layout.layout_edit_text_style, null)
        showToolsView(view)
        view.icTextAlignLeft.setOnClickListener {
            mMainTextSticker?.changeAlign(EditTextSticker.AlignMode.LEFT)
        }
        view.icTextAlignCenter.setOnClickListener {
            mMainTextSticker?.changeAlign(EditTextSticker.AlignMode.CENTER)
        }
        view.icTextAlignRight.setOnClickListener {
            mMainTextSticker?.changeAlign(EditTextSticker.AlignMode.RIGHT)
        }
        view.textStyleRegular.setOnClickListener {
            mMainTextSticker?.changeTextStyle(Typeface.NORMAL)
        }
        view.textStyleBold.setOnClickListener {
            mMainTextSticker?.changeTextStyle(Typeface.BOLD)
        }
        view.textStyleItalic.setOnClickListener {
            mMainTextSticker?.changeTextStyle(Typeface.ITALIC)
        }
        view.textStyleBoldItalic.setOnClickListener {
            mMainTextSticker?.changeTextStyle(Typeface.BOLD_ITALIC)
        }
        view.textStyleStrike.setOnClickListener {
            mMainTextSticker?.changeTextFlag(Paint.STRIKE_THRU_TEXT_FLAG)
        }
        view.textStyleUnderline.setOnClickListener {
            mMainTextSticker?.changeTextFlag(Paint.UNDERLINE_TEXT_FLAG)
        }
    }

    private fun showToolsView(view: View) {
        toolsDetails.removeAllViews()
        toolsDetails.addView(view)
        playTranslationYAnimation(view)
    }


    private fun openKeyboard() {
        autoShowKeyboard = true
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)

    }

    fun hideKeyboard() {

        autoShowKeyboard = false
        val imm: InputMethodManager =
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


    private fun playTranslationYAnimation(view: View) {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1f),
            ObjectAnimator.ofFloat(view, "translationY", 64f, 0f)
        )
        animatorSet.duration = 250
        animatorSet.interpolator = FastOutLinearInInterpolator()
        animatorSet.start()
    }

    fun getEditTextView(): EditTextSticker? {
        if (mMainTextSticker?.getMainText()!!.isNotEmpty()) {
            textContainer.removeView(mMainTextSticker)
            return mMainTextSticker
        } else {
            Toast.makeText(context, context.getString(R.string.type_your_text), Toast.LENGTH_LONG)
                .show()
            return null
        }
    }

    private fun onChangeFont(fontId: Int) {
        mMainTextSticker?.changeFonts(fontId)
    }

    enum class EditMode {
        NONE, TEXT, FONTS, COLOR, STYLE
    }


    fun onBackPress(): EditTextSticker? {
        return if (mEditState) {
            textContainer.removeView(mMainTextSticker)
            mMainTextSticker
        } else {
            null
        }
    }

    fun onCancelEdit(): EditTextSticker? {
        hideKeyboard()
        return if (mEditState) {
            if (mTextAttrData == null) return null
            mTextAttrData?.let {
                mMainTextSticker?.setAttr(it)
            }
            textContainer.removeView(mMainTextSticker)
            mMainTextSticker?.clearFocus()
            mMainTextSticker
        } else {
            mMainTextSticker?.clearFocus()
            null
        }
    }

    fun editState(): Boolean = mEditState

    private fun updateIcon() {

        icKeyboard.setImageResource(R.drawable.ic_keyboard_default)
        icFonts.setImageResource(R.drawable.ic_font_default)
        icColor.setImageResource(R.drawable.ic_color_default)
        icStyle.setImageResource(R.drawable.ic_font_style_default)
        when (mEditMode) {
            EditMode.TEXT -> {
                icKeyboard.setImageResource(R.drawable.ic_keyboard_active)
            }
            EditMode.FONTS -> {
                icFonts.setImageResource(R.drawable.ic_font_active)
            }
            EditMode.STYLE -> {
                icStyle.setImageResource(R.drawable.ic_font_style_active)
            }
            EditMode.COLOR -> {
                icColor.setImageResource(R.drawable.ic_color_active)
            }
        }
    }

}
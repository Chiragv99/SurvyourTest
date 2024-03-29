package com.pickfords.surveyorapp.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.pickfords.surveyorapp.R
import java.util.*
import java.util.regex.Pattern
import kotlin.math.ceil

class EditCredit @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : EditText(context, attrs, defStyleAttr) {

    private var mCCPatterns = SparseArray<Pattern>()
    private var mSeparator: Separator = Separator.NONE
    private var mDrawableGravity: Gravity = Gravity.END
    private var isValidCard: Boolean = false
    private var mCurrentDrawableResId = Card.UNKNOWN.drawableRes

    val textWithoutSeparator
        get() = if (mSeparator == Separator.NONE) {
            text.toString()
        } else {
            text.toString().replace(mSeparator.toRegex(), "")
        }

    val isCardValid: Boolean
        get() = textWithoutSeparator.length > 12 && isValidCard

    val cardType: Card
        get() = Card.from(mCurrentDrawableResId)

    enum class Separator(private val stringValue: String) {
        NONE(""), SPACES(" "), DASHES("-");

        override fun toString() = stringValue

        internal fun toRegex() = stringValue.toRegex()

        internal val length
            get() = stringValue.length
    }

    enum class Gravity {
        START, END, LEFT, RIGHT
    }

    enum class Card(internal val value: Int, @field:DrawableRes internal val drawableRes: Int) {
        VISA(1, R.drawable.ic_master_card),
        MASTERCARD(2, R.drawable.ic_master_card),
        AMEX(4, R.drawable.ic_master_card),
        DISCOVER(8, R.drawable.ic_master_card),
        UNKNOWN(-1, R.drawable.ic_creditcard);

        companion object {
            internal fun from(@DrawableRes drawableRes: Int): Card {
                for (card in values()) {
                    if (card.drawableRes == drawableRes) {
                        return card
                    }
                }
                return UNKNOWN
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(
            text: CharSequence,
            start: Int,
            lengthBefore: Int,
            lengthAfter: Int
        ) {
            val textWithoutSeparator = textWithoutSeparator

            var mDrawableResId = 0
            for (i in 0 until mCCPatterns.size()) {
                val key = mCCPatterns.keyAt(i)

                val p = mCCPatterns.get(key)

                val m = p.matcher(textWithoutSeparator)
                isValidCard = m.find()
                if (isValidCard) {
                    mDrawableResId = key
                    break
                }
            }
            if (mDrawableResId != 0 && mDrawableResId != mCurrentDrawableResId) {
                mCurrentDrawableResId = mDrawableResId
            } else if (mDrawableResId == 0) {
                mCurrentDrawableResId = Card.UNKNOWN.drawableRes
            }
            addDrawable()
            addSeparators()
        }
    }

    init {
        setDisabledCards()
        inputType = InputType.TYPE_CLASS_PHONE
        setSeparator(Separator.NONE)
        setDrawableGravity(Gravity.END)
        attrs?.let { applyAttributes(it) }
        addTextChangedListener(textWatcher)
    }

    private fun applyAttributes(attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.EditCredit,
            0, 0
        )
        try {
            setSeparator(
                Separator.values()[a.getInt(
                    R.styleable.EditCredit_separator,
                    Separator.NONE.ordinal
                )]
            )
            setDisabledCardsInternal(a.getInt(R.styleable.EditCredit_disabledCards, 0))
            setDrawableGravity(
                Gravity.values()[a.getInt(
                    R.styleable.EditCredit_drawableGravity,
                    Gravity.END.ordinal
                )]
            )
        } finally {
            a.recycle()
        }
    }

    private fun addDrawable() {
        var currentDrawable = ContextCompat.getDrawable(context, mCurrentDrawableResId)
        if (currentDrawable != null && error.isNullOrEmpty()) {
            currentDrawable = resize(currentDrawable)
            when (mDrawableGravity) {
                Gravity.START -> setDrawablesRelative(start = currentDrawable)
                Gravity.RIGHT -> setDrawables(right = currentDrawable)
                Gravity.LEFT -> setDrawables(left = currentDrawable)
                else -> setDrawablesRelative(end = currentDrawable)
            }
        }
    }

    private fun addSeparators() {
        val text = text.toString()
        if (mSeparator != Separator.NONE) {
            if (text.length > 4 && !text.matches("(?:[0-9]{4}$mSeparator)+[0-9]{1,4}".toRegex())) {
                val sp = StringBuilder()
                val caretPosition = selectionEnd
                val segments = splitString(text.replace(mSeparator.toRegex(), ""))
                for (segment in segments) {
                    sp.append(segment).append(mSeparator)
                }
                setText("")
                append(sp.delete(sp.length - mSeparator.length, sp.length).toString())
                if (caretPosition < text.length)
                    setSelection(caretPosition)
            }
        }
    }

    private fun removeSeparators() {
        var text = text.toString()
        text = text.replace(" ".toRegex(), "").replace("-".toRegex(), "")
        setText("")
        append(text)
    }

    private fun splitString(s: String): Array<String?> {
        val arrayLength = ceil(s.length / 4.toDouble()).toInt()
        val result = arrayOfNulls<String>(arrayLength)

        var j = 0
        val lastIndex = result.size - 1
        for (i in 0 until lastIndex) {
            result[i] = s.substring(j, j + 4)
            j += 4
        }
        result[lastIndex] = s.substring(j)

        return result
    }

    @Deprecated(
        "Please use the method that accepts a Separator enum instead.",
        ReplaceWith("this.setSeparator(Separator.)")
    )
    fun setSeparator(@IntRange(from = 0, to = 2) separator: Int) {
        require(!(separator > 2 || separator < 0)) {
            "The separator has to be one of the following:" +
                    "NO_SEPARATOR." +
                    "SPACES_SEPARATOR." +
                    "DASHES_SEPARATOR."
        }
        setSeparator(Separator.values()[separator])
    }

    /**
     * Use this method to set the separator style.
     * The default separator is [Separator.NONE].
     *
     * @param separator the style of the separator.
     */
    fun setSeparator(separator: Separator) {
        mSeparator = separator
        if (mSeparator != Separator.NONE) {
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(23))
            keyListener = DigitsKeyListener.getInstance("0123456789$mSeparator")
            addSeparators()
        } else {
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(19))
            keyListener = DigitsKeyListener.getInstance("0123456789")
            removeSeparators()
        }
    }

    /**
     * Use this method to set the location of the card drawable.
     * The default gravity is [Gravity.END].
     *
     * @param gravity the drawable location.
     */
    fun setDrawableGravity(gravity: Gravity) {
        mDrawableGravity = gravity
        addDrawable()
    }

    private fun setDisabledCardsInternal(disabledCards: Int) {
        val cards = ArrayList<Card>()
        if (containsFlag(disabledCards, Card.VISA.value)) {
            cards.add(Card.VISA)
        }
        if (containsFlag(disabledCards, Card.MASTERCARD.value)) {
            cards.add(Card.MASTERCARD)
        }
        if (containsFlag(disabledCards, Card.AMEX.value)) {
            cards.add(Card.AMEX)
        }
        if (containsFlag(disabledCards, Card.DISCOVER.value)) {
            cards.add(Card.DISCOVER)
        }
        setDisabledCards(*cards.toTypedArray())
    }

    @Deprecated(
        "Please use the method that accepts an array of Cards instead.",
        ReplaceWith("this.setDisabledCards(cards)")
    )
    fun setDisabledCards(disabledCards: Int) {
        setDisabledCardsInternal(disabledCards)
    }

    /**
     * Use this method to set which cards are disabled.
     * By default all supported cards are enabled.
     *
     * @param cards the cards to be disabled.
     */
    fun setDisabledCards(vararg cards: Card) {
        var disabledCards = 0
        for (card in cards) {
            disabledCards = disabledCards or card.value
        }
        mCCPatterns.clear()
        if (!containsFlag(disabledCards, Card.VISA.value)) {
            mCCPatterns.put(Card.VISA.drawableRes, Pattern.compile("^4[0-9]{1,12}(?:[0-9]{6})?$"))
        }
        if (!containsFlag(disabledCards, Card.MASTERCARD.value)) {
            mCCPatterns.put(Card.MASTERCARD.drawableRes, Pattern.compile("^5[1-5][0-9]{0,14}$"))
        }
        if (!containsFlag(disabledCards, Card.AMEX.value)) {
            mCCPatterns.put(Card.AMEX.drawableRes, Pattern.compile("^3[47][0-9]{0,13}$"))
        }
        if (!containsFlag(disabledCards, Card.DISCOVER.value)) {
            mCCPatterns.put(
                Card.DISCOVER.drawableRes,
                Pattern.compile("^6(?:011|5[0-9]{1,2})[0-9]{0,12}$")
            )
        }
        textWatcher.onTextChanged("", 0, 0, 0)
    }

    private fun containsFlag(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var noDrawablesVisible = true
        for (drawable in compoundDrawables) {
            if (drawable != null) {
                noDrawablesVisible = false
                break
            }
        }
        if (noDrawablesVisible) {
            addDrawable()
        }
    }

    private fun resize(image: Drawable) =
        when (val height = measuredHeight - (paddingTop + paddingBottom)) {
            in 1 until image.intrinsicHeight -> {
                val bitmap = (image as BitmapDrawable).bitmap
                val ratio = image.getIntrinsicWidth().toFloat() / image.intrinsicHeight.toFloat()
                val resizedBitmap =
                    Bitmap.createScaledBitmap(bitmap, (height * ratio).toInt(), height, false)
                resizedBitmap.density = Bitmap.DENSITY_NONE
                BitmapDrawable(resources, resizedBitmap)
            }
            in Int.MIN_VALUE..0 -> null
            else -> image
        }

    private fun setDrawablesRelative(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) =
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            this,
            start,
            top,
            end,
            bottom
        )

    private fun setDrawables(
        left: Drawable? = null,
        top: Drawable? = null,
        right: Drawable? = null,
        bottom: Drawable? = null
    ) =
        setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)

    companion object {
        @Deprecated("This constant has been replace with an enum.", ReplaceWith("Separator.NONE"))
        const val NO_SEPARATOR = 0
        @Deprecated("This constant has been replace with an enum.", ReplaceWith("Separator.SPACES"))
        const val SPACES_SEPARATOR = 1
        @Deprecated("This constant has been replace with an enum.", ReplaceWith("Separator.DASHES"))
        const val DASHES_SEPARATOR = 2

        @Deprecated("This constant has been replace with an enum.", ReplaceWith("null"))
        const val NONE = 0
        @Deprecated("This constant has been replace with an enum.", ReplaceWith("Card.VISA"))
        const val VISA = 1
        @Deprecated("This constant has been replace with an enum.", ReplaceWith("Card.MASTERCARD"))
        const val MASTERCARD = 2
        @Deprecated("This constant has been replace with an enum.", ReplaceWith("Card.AMEX"))
        const val AMEX = 4
        @Deprecated("This constant has been replace with an enum.", ReplaceWith("Card.DISCOVER"))
        const val DISCOVER = 8
    }
}
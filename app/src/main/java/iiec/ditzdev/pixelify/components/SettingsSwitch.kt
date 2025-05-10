package iiec.ditzdev.pixelify.components

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.google.android.material.materialswitch.MaterialSwitch
import iiec.ditzdev.pixelify.R

class SettingsSwitch : LinearLayout {
    private val TAG = "SettingsSwitch"

    private var iconView: ImageView? = null
    private var titleView: TextView? = null
    private var subtitleView: TextView? = null
    private var switchWidget: MaterialSwitch? = null

    private var title: String = ""
    private var subtitle: String = ""
    private var iconResource: Int = 0
    private var _isChecked: Boolean = false
    private var listener: OnSwitchChangeListener? = null
    private var isEnabled: Boolean = true

    interface OnSwitchChangeListener {
        fun onSwitchChanged(isChecked: Boolean)
    }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        try {
            orientation = HORIZONTAL
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.settings_switch_layout, this, true)

            try {
                iconView = findViewById(R.id.settings_icon)
                titleView = findViewById(R.id.settings_title)
                subtitleView = findViewById(R.id.settings_subtitle)
                switchWidget = findViewById(R.id.settings_switch)
            } catch (e: Exception) {
                Log.e(TAG, "Error finding views: ${e.message}")
                return
            }

            if (attrs != null) {
                try {
                    val a: TypedArray = context.theme.obtainStyledAttributes(
                        attrs,
                        R.styleable.SettingsSwitch,
                        defStyleAttr, 0
                    )

                    try {
                        title = a.getString(R.styleable.SettingsSwitch_title) ?: ""
                        subtitle = a.getString(R.styleable.SettingsSwitch_subtitle) ?: ""
                        iconResource = a.getResourceId(R.styleable.SettingsSwitch_icon, 0)
                        _isChecked = a.getBoolean(R.styleable.SettingsSwitch_checked, false)
                        isEnabled = a.getBoolean(R.styleable.SettingsSwitch_android_enabled, true)
                    } finally {
                        a.recycle()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading attributes: ${e.message}")
                }
            }
            updateViews()
            setEnabled(isEnabled)

        } catch (e: Exception) {
            Log.e(TAG, "Error in init: ${e.message}")
        }
    }

    private fun updateViews() {
        try {
            if (iconResource != 0) {
                try {
                    iconView?.setImageResource(iconResource)
                    iconView?.visibility = VISIBLE
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting icon: ${e.message}")
                    iconView?.visibility = GONE
                }
            } else {
                iconView?.visibility = GONE
            }

            titleView?.text = title

            if (subtitle.isNotEmpty()) {
                subtitleView?.text = subtitle
                subtitleView?.visibility = VISIBLE
            } else {
                subtitleView?.visibility = GONE
            }

            switchWidget?.apply {
                isChecked = this@SettingsSwitch._isChecked
                setOnCheckedChangeListener { _, isChecked ->
                    this@SettingsSwitch._isChecked = isChecked
                    listener?.onSwitchChanged(isChecked)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating views: ${e.message}")
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        this.isEnabled = enabled
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurface,
            typedValue,
            true
        )
        val colorOnSurface = typedValue.data
        val disabledColor = Color.argb(
            (0.38 * 255).toInt(),
            Color.red(colorOnSurface),
            Color.green(colorOnSurface),
            Color.blue(colorOnSurface)
        )

        titleView?.apply {
            isEnabled = enabled
            setTextColor(if (enabled) colorOnSurface else disabledColor)
        }

        subtitleView?.apply {
            isEnabled = enabled
            setTextColor(if (enabled) colorOnSurface else disabledColor)
        }

        switchWidget?.apply {
            isEnabled = enabled
            if (!enabled) {
                val thumbColor = Color.argb(
                    (0.38 * 255).toInt(),
                    Color.red(colorOnSurface),
                    Color.green(colorOnSurface),
                    Color.blue(colorOnSurface)
                )
                val trackColor = Color.argb(
                    (0.12 * 255).toInt(),
                    Color.red(colorOnSurface),
                    Color.green(colorOnSurface),
                    Color.blue(colorOnSurface)
                )
                thumbTintList = ColorStateList.valueOf(thumbColor)
                trackTintList = ColorStateList.valueOf(trackColor)
            }
        }
    }

    fun setOnSwitchChangeListener(listener: OnSwitchChangeListener?) {
        this.listener = listener
    }

    fun setChecked(checked: Boolean) {
        _isChecked = checked
        switchWidget?.isChecked = checked
    }

    fun isChecked(): Boolean {
        return switchWidget?.isChecked ?: _isChecked
    }

    fun setTitle(title: String?) {
        this.title = title ?: ""
        titleView?.text = this.title
    }

    fun setSubtitle(subtitle: String?) {
        this.subtitle = subtitle ?: ""
        if (this.subtitle.isNotEmpty()) {
            subtitleView?.text = this.subtitle
            subtitleView?.visibility = VISIBLE
        } else {
            subtitleView?.visibility = GONE
        }
    }

    fun setIcon(resourceId: Int) {
        this.iconResource = resourceId
        if (resourceId != 0) {
            try {
                iconView?.setImageResource(resourceId)
                iconView?.visibility = VISIBLE
            } catch (e: Exception) {
                Log.e(TAG, "Error setting icon: ${e.message}")
                iconView?.visibility = GONE
            }
        } else {
            iconView?.visibility = GONE
        }
    }
}
package iiec.ditzdev.pixelify.components

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import iiec.ditzdev.pixelify.R

class SettingsLayout : LinearLayout {
    private val TAG = "SettingsLayout"

    private var iconView: ImageView? = null
    private var titleView: TextView? = null
    private var subtitleView: TextView? = null

    private var title: String = ""
    private var subtitle: String = ""
    private var iconResource: Int = 0

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
            inflater.inflate(R.layout.settings_layout, this, true)

            try {
                iconView = findViewById(R.id.settings_icon)
                titleView = findViewById(R.id.settings_title)
                subtitleView = findViewById(R.id.settings_subtitle)
            } catch (e: Exception) {
                Log.e(TAG, "Error finding views: ${e.message}")
                return
            }

            if (attrs != null) {
                try {
                    val a: TypedArray = context.theme.obtainStyledAttributes(
                        attrs,
                        R.styleable.SettingsLayout,
                        defStyleAttr, 0
                    )

                    try {
                        title = a.getString(R.styleable.SettingsLayout_title) ?: ""
                        subtitle = a.getString(R.styleable.SettingsLayout_subtitle) ?: ""
                        iconResource = a.getResourceId(R.styleable.SettingsLayout_icon, 0)
                    } finally {
                        a.recycle()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading attributes: ${e.message}")
                }
            }
            updateViews()

        } catch (e: Exception) {
            Log.e(TAG, "Error in init: ${e.message}")
        }
    }

    private fun updateViews() {
        try {
            if (iconResource != 0 && iconView != null) {
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
        } catch (e: Exception) {
            Log.e(TAG, "Error updating views: ${e.message}")
        }
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

    fun getTitle(): String {
        return title
    }

    fun getSubtitle(): String {
        return subtitle
    }
}
package io.alcatraz.afkprotect.extended

import android.content.Context
import android.util.AttributeSet
import android.widget.ExpandableListView

class NoScrollExpandableListView : ExpandableListView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val expandSpec =
            MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, expandSpec)
    }
}

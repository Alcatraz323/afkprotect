package io.alcatraz.afkprotect.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import io.alcatraz.afkprotect.beans.SetupPage

class SetupPagerAdapter(
    private val pages: List<SetupPage>,
    val context: Context
) : PagerAdapter() {
    private val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return pages.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        obj: Any
    ) {
        container.removeView(obj as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val element = pages[position]
        var root = element.rootView
        if (root == null) {
            root = layoutInflater.inflate(element.layout_id, null)
            element.rootView = root
        }
        container.addView(root)
        return root!!
    }

}
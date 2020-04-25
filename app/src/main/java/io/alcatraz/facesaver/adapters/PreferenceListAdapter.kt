package io.alcatraz.facesaver.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.beans.PreferenceHeader
import io.alcatraz.facesaver.databinding.ItemPreferenceHeaderBinding

class PreferenceListAdapter(
    private val context: Context,
    private val data: List<PreferenceHeader>
) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(i: Int): Any {
        return data[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val itemBinding = ItemPreferenceHeaderBinding.inflate(layoutInflater)
        val header = data[i]
        itemBinding.itemPrefHeaderTitle.text = header.title
        itemBinding.itemPrefHeaderSummary.text = header.summary
        itemBinding.itemPrefHeaderImage.setImageResource(header.icon_res)
        return itemBinding.root
    }
}

package io.alcatraz.afkprotect.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import io.alcatraz.afkprotect.beans.AuthorElement
import io.alcatraz.afkprotect.databinding.ItemAuthorMainListBinding

class AuthorAdapter(
    private val context: Context,
    private val data: List<AuthorElement>,
    private val imgData: List<Int>
) : BaseAdapter() {
    private val lf: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(p1: Int): Any {
        return data[p1]
    }

    override fun getItemId(p1: Int): Long {
        return p1.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p1: Int, p2: View?, p3: ViewGroup): View {
        val itemBinding : ItemAuthorMainListBinding = ItemAuthorMainListBinding.inflate(lf)
        val element = data[p1]
        itemBinding.authorItemTitle.text = element.title
        itemBinding.authorItemDesc.text = element.desc
        itemBinding.authorItemIcon.setImageResource(imgData[p1])
        if (p1 == 2)
            itemBinding.authorItemDesc.visibility = View.GONE
        else
            itemBinding.authorItemDesc.visibility = View.VISIBLE
        return itemBinding.root
    }

}

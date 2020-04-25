package io.alcatraz.facesaver.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.beans.QueryElement

class QueryElementAdapter(private val context: Context,
                          private val data: List<QueryElement>) :
    RecyclerView.Adapter<QueryElementContainer>() {
    private var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(p1: ViewGroup, p2: Int): QueryElementContainer {
        val viewHolder = layoutInflater.inflate(R.layout.item_opensource_holder, p1, false)
        return QueryElementContainer(viewHolder)
    }

    override fun onBindViewHolder(p1: QueryElementContainer, p2: Int) {
        val element = data[p2]
        p1.cardRoot.setOnClickListener {
            val localIntent = Intent("android.intent.action.VIEW")
            localIntent.data = Uri.parse(element.url)
            context.startActivity(localIntent)
        }
        p1.txvName.text = element.name
        p1.txvAuthor.text = element.author
        p1.txvIntro.text = element.intro
        p1.txvLicense.text = element.license
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class QueryElementContainer(val view: View) : RecyclerView.ViewHolder(view){
    internal val txvName : TextView = view.findViewById(R.id.open_source_name)
    internal val txvLicense : TextView = view.findViewById(R.id.open_source_license)
    internal val txvIntro : TextView = view.findViewById(R.id.open_source_intro)
    internal val txvAuthor : TextView = view.findViewById(R.id.open_source_author)
    internal val cardRoot : CardView = view.findViewById(R.id.open_source_item_card)
}
package com.r42914lg.tutukt.ui

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.r42914lg.tutukt.R
import com.r42914lg.tutukt.domain.Category
import java.text.MessageFormat

class FeedAdapter(private val catList: List<Category>) : RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder>() {

    class FeedAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.category_title_text)
        var cluesTextView: TextView = itemView.findViewById(R.id.category_clues_count)
        var idTextView: TextView = itemView.findViewById(R.id.category_id_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapterViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item_recycler_row, parent, false)
        return FeedAdapterViewHolder(v)
    }

    override fun onBindViewHolder(holder: FeedAdapterViewHolder, position: Int) {
        val current: Category = catList[position]

        holder.titleTextView.text = MessageFormat.format(
            "{0}{1}",
            holder.itemView.context.getString(R.string.category_title_prefix),
            current.title
        )

        holder.cluesTextView.text = MessageFormat.format(
            "{0}{1}",
            holder.itemView.context.getString(R.string.category_clue_count),
            current.cluesCount
        )

        holder.idTextView.text = MessageFormat.format(
            "{0}{1}",
            holder.itemView.context.getString(R.string.category_id_prefix),
            current.id
        )
    }

    override fun getItemCount() = catList.size
}

interface OnItemClickListener {
    fun onItemClick(view: View?, position: Int)
    fun onLongItemClick(view: View?, position: Int)
}

class RecyclerItemClickListener(
    private val ctx: Context,
    private val recyclerView: RecyclerView,
    private val listener: OnItemClickListener
    ) : RecyclerView.OnItemTouchListener {

    private val mGestureDetector = GestureDetector(ctx, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true;
        }

        override fun onLongPress(e: MotionEvent) {
            val child = recyclerView.findChildViewUnder(e.x, e.y);
            child?.apply {
                listener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
            }
        }
    });

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView: View? = rv.findChildViewUnder(e.x, e.y)
        if (childView != null && mGestureDetector.onTouchEvent(e)) {
            listener.onItemClick(childView, rv.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
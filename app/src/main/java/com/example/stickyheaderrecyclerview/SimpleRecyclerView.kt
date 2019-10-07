package com.example.stickyheaderrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView



class SimpleRecyclerView : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyHeaderItemDecoration.StickyHeaderInterface {
    private var mData = ArrayList<ViewHolder.Data>()

    init {
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(1))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(1))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(1))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(1))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
        mData.add(ViewHolder.Data(0))
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 ->
                HeaderViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.header_item,
                        parent,
                        false
                    )
                )
            else ->
                ViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item,
                        parent,
                        false
                    )
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bindData(position)
            is ViewHolder -> holder.bindData(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mData[position].getViewType()
    }


    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var itPos = itemPosition
        var headerPosition = 0
        do {
            if (this.isHeader(itPos)) {
                headerPosition = itemPosition
                break
            }
            itPos -= 1
        } while (itPos >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.header_item
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val tv = header.findViewById<AppCompatTextView>(R.id.header)

        tv.text = (headerPosition / 5).toString()
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return mData[itemPosition].getViewType() == 1
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvHeader: AppCompatTextView = itemView.findViewById(R.id.header)

        fun bindData(position: Int) {
            tvHeader.text = (position / 5).toString()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvRows: AppCompatTextView = itemView.findViewById(R.id.text)


        fun bindData(position: Int) {
            tvRows.text = "saber  $position"
        }

        class Data(viewType: Int) {
            private var viewType: Int = 0

            init {
                this.viewType = viewType
            }

            fun getViewType(): Int {
                return viewType
            }

            fun setViewType(viewType: Int) {
                this.viewType = viewType
            }
        }
    }

}
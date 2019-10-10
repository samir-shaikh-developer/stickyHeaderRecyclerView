package com.example.stickyheaderrecyclerview

import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderItemDecoration(private val mListener: StickyHeaderInterface) :
    RecyclerView.ItemDecoration() {

    private var mStickyHeaderHeight: Int = 0
    private var firstHeaderPosition: Int = -1

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }

        val headerPos = mListener.getHeaderPositionForItem(topChildPosition)
        Log.d("TAGGG", "headerPos $headerPos")
//        if (headerPos != 0) return
        val currentHeader = getHeaderViewForItem(headerPos, parent)
        fixLayoutSize(parent, currentHeader)
        val contactPoint = currentHeader.bottom
        val childInContact = getChildInContact(parent, contactPoint, headerPos)


        if (firstHeaderPosition == -1) {
            firstHeaderPosition = getFirstHeaderPositionInRecyclerView(parent) - 1
            Log.d("TAGGG", "firstHeaderPosition $firstHeaderPosition")
        }

        if (childInContact != null) {
            val childAdapterPosition = parent.getChildAdapterPosition(childInContact)
            Log.d("TAGGG", "childInContactPosition $childAdapterPosition")
            val header = mListener.isHeader(childAdapterPosition)
            if (header && headerPos >= firstHeaderPosition) {
                moveHeader(c, currentHeader, childInContact)
                return
            }
        }

        if (headerPos > firstHeaderPosition) {
            drawHeader(c, currentHeader)
        }
    }

    private fun getHeaderViewForItem(headerPosition: Int, parent: RecyclerView): View {
        headerPosition
            .takeIf { it == 1 }
            .let {
                val layoutResId = mListener.getHeaderLayout(headerPosition)
                val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
                mListener.bindHeaderData(header, headerPosition)
                return header
            }
    }

    private fun drawHeader(c: Canvas, header: View) {
        Log.d("TAGGG", "drawHeader")
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        Log.d("TAGGG", "moveHeader")
        c.save()
        c.translate(0f, nextHeader.top.toFloat() - currentHeader.height.toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    private fun getFirstHeaderPositionInRecyclerView(parent: RecyclerView): Int {
        var firstHeaderPos: Int = -1
        for (i in 0 until parent.childCount) {
            if(mListener.isHeader(parent.getChildAdapterPosition(parent.getChildAt(i)))) {
                firstHeaderPos = i
                break
            }
        }
        return firstHeaderPos
    }

    private fun getChildInContact(
        parent: RecyclerView,
        contactPoint: Int,
        currentHeaderPos: Int
    ): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            var heightTolerance = 0
            val child = parent.getChildAt(i)

            //measure height tolerance with child if child is another header
            if (currentHeaderPos != i) {
                val isChildHeader = mListener.isHeader(parent.getChildAdapterPosition(child))
                if (isChildHeader) {
                    heightTolerance = mStickyHeaderHeight - child.height
                }
            }

            //add heightTolerance if child top be in display area
            val childBottomPosition = if (child.top > 0) {
                child.bottom + heightTolerance
            } else {
                child.bottom
            }

            if (childBottomPosition > contactPoint) {
                if (child.top <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    /**
     * Properly measures and layouts the top sticky header.
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)
        mStickyHeaderHeight = view.measuredHeight
        view.layout(0, 0, view.measuredWidth, mStickyHeaderHeight)
    }

    interface StickyHeaderInterface {
        /**
         * This method gets called by [StickHeaderItemDecoration] to fetch the position of the header item in the adapter
         * that is used for (represents) item at specified position.
         * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
         * @return int. Position of the header item in the adapter.
         */
        fun getHeaderPositionForItem(itemPosition: Int): Int

        /**
         * This method gets called by [StickHeaderItemDecoration] to get layout resource id for the header item at specified adapter's position.
         * @param headerPosition int. Position of the header item in the adapter.
         * @return int. Layout resource id.
         */
        fun getHeaderLayout(headerPosition: Int): Int

        /**
         * This method gets called by [StickHeaderItemDecoration] to setup the header View.
         * @param header View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        fun bindHeaderData(header: View, headerPosition: Int)

        /**
         * This method gets called by [StickHeaderItemDecoration] to verify whether the item represents a header.
         * @param itemPosition int.
         * @return true, if item at the specified adapter's position represents a header.
         */
        fun isHeader(itemPosition: Int): Boolean
    }
}
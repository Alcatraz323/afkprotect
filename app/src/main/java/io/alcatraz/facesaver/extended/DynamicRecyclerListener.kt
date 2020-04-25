package io.alcatraz.facesaver.extended

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class DynamicRecyclerListener(//声明一个LinearLayoutManager
    private val mLinearLayoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    //当前页，从0开始
    private var currentPage = 0

    //已经加载出来的Item的数量
    private var totalItemCount: Int = 0

    //主要用来存储上一个totalItemCount
    private var previousTotal = 0

    //在屏幕上可见的item数量
    private var visibleItemCount: Int = 0

    //在屏幕可见的Item中的第一个
    private var firstVisibleItem: Int = 0

    //是否正在上拉数据
    private var loading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
        if (loading) {

            if (totalItemCount > previousTotal) {
                //说明数据已经加载结束
                loading = false
                previousTotal = totalItemCount
            }
        }
        //这里需要好好理解
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem) {
            currentPage++
            onLoadMore(currentPage)
            loading = true
        }
    }

    abstract fun onLoadMore(currentPage: Int)

}

package com.rajat.pdfviewer.port

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.rajat.pdfviewer.R


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//https://developer.android.google.cn/develop/ui/views/layout/recyclerview?hl=zh-cn
//https://github.com/afreakyelf/Pdf-Viewer/blob/master/pdfViewer/src/main/java/com/rajat/pdfviewer/PdfViewAdapter.kt
internal class PdfViewAdapter(
    private val context: Context,
    private val renderer: PdfRendererCore,
    private val parentView: PdfRendererView,
    private val pageSpacing: Rect,
    private val enableLoadingForPages: Boolean
) : RecyclerView.Adapter<PdfViewAdapter.PdfPageViewHolder>() {
    override fun getItemCount(): Int = renderer.getPageCount()

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PdfPageViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_pdf_page, parent, false)
        return PdfPageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onViewRecycled(holder: PdfPageViewHolder) {
        super.onViewRecycled(holder)
        holder.cancelJobs()
    }



    inner class PdfPageViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        private var currentBoundPage: Int = -1
        private var hasRealBitmap: Boolean = false
        private val fallbackHandler = Handler(Looper.getMainLooper())
        private var scope = MainScope()

        private val DEBUG_LOGS_ENABLED = false

        private val root: FrameLayout
        private val pageView: ImageView
        private val pageLoadingProgress: ProgressBar
        init {
            root = view.findViewById(R.id.container_view)
            pageView = view.findViewById(R.id.pageView)
            pageLoadingProgress = view.findViewById(R.id.page_loading_progress)
        }

        fun bind(position: Int) {
            cancelJobs()
            currentBoundPage = position
            hasRealBitmap = false
            //scope = MainScope()

            val displayWidth = pageView.width.takeIf { it > 0 }
                ?: context.resources.displayMetrics.widthPixels

            pageView.setImageBitmap(null)

            pageLoadingProgress.visibility =
                if (enableLoadingForPages) View.VISIBLE else View.GONE

            scope.launch {
                val cached = withContext(Dispatchers.IO) {
                    renderer.getBitmapFromCache(position)
                }
                //从缓存中找到position位置的pdf页面，有动画效果加载，同时隐藏pageLoadingProgress
                if (cached != null && currentBoundPage == position) {
                    if (DEBUG_LOGS_ENABLED) Log.d("PdfViewAdapter", "✅ Loaded page $position from cache")
                    showBitmap(cached)
                    return@launch
                }

                renderer.getPageDimensionsAsync(position) { size ->
                    if (currentBoundPage != position) return@getPageDimensionsAsync

                    val aspectRatio = size.width.toFloat() / size.height.toFloat()
                    val height = (displayWidth / aspectRatio).toInt()
                    updateLayoutParams(height)

                    renderAndApplyBitmap(position, displayWidth, height)
                }
            }

            startPersistentFallbackRender(position)
        }

        private fun renderAndApplyBitmap(page: Int, width: Int, height: Int) {
            val bitmap = CommonUtils.Companion.BitmapPool.getBitmap(width, maxOf(1, height))

            renderer.renderPage(page, bitmap) { success, pageNo, rendered ->
                scope.launch {
                    if (success && currentBoundPage == pageNo) {
                        if (DEBUG_LOGS_ENABLED) Log.d("PdfViewAdapter", "✅ Render complete for page $pageNo")
                        showBitmap(rendered ?: bitmap)

                        val fallbackHeight = pageView.height.takeIf { it > 0 }
                            ?: context.resources.displayMetrics.heightPixels

                        renderer.schedulePrefetch(
                            currentPage = pageNo,
                            width = width,
                            height = fallbackHeight,
                            direction = parentView.getScrollDirection()
                        )
                    } else {
                        if (DEBUG_LOGS_ENABLED) Log.w("PdfViewAdapter", "🚫 Skipping render for page $pageNo — ViewHolder now bound to $currentBoundPage")
                        CommonUtils.Companion.BitmapPool.recycleBitmap(bitmap)
                        retryRenderOnce(page, width, height)
                    }
                }
            }
        }

        private fun retryRenderOnce(page: Int, width: Int, height: Int) {
            val retryBitmap = CommonUtils.Companion.BitmapPool.getBitmap(width, height)
            renderer.renderPage(page, retryBitmap) { success, retryPageNo, rendered ->
                scope.launch {
                    if (success && retryPageNo == currentBoundPage && !hasRealBitmap) {
                        if (DEBUG_LOGS_ENABLED) Log.d("PdfViewAdapter", "🔁 Retry success for page $retryPageNo")
                        showBitmap(rendered ?: retryBitmap)
                    } else {
                        CommonUtils.Companion.BitmapPool.recycleBitmap(retryBitmap)
                    }
                }
            }
        }

        private fun startPersistentFallbackRender(
            page: Int,
            retries: Int = 10,
            delayMs: Long = 200L
        ) {
            var attempt = 0

            lateinit var task: Runnable
            task = object : Runnable {
                override fun run() {
                    if (currentBoundPage != page || hasRealBitmap) return

                    scope.launch {
                        val cached = withContext(Dispatchers.IO) {
                            renderer.getBitmapFromCache(page)
                        }

                        if (cached != null && currentBoundPage == page) {
                            if (DEBUG_LOGS_ENABLED) Log.d("PdfViewAdapter", "🕒 Fallback applied for page $page on attempt $attempt")
                            showBitmap(cached)
                        } else {
                            attempt++
                            if (attempt < retries) {
                                fallbackHandler.postDelayed(task, delayMs)
                            }
                        }
                    }
                }
            }

            fallbackHandler.postDelayed(task, delayMs)
        }

        private fun showBitmap(cached: Bitmap){
            pageView.setImageBitmap(cached)
            hasRealBitmap = true
            applyFadeInAnimation(pageView)
            pageLoadingProgress.visibility = View.GONE
        }

        private fun updateLayoutParams(height: Int) {
            root.layoutParams = root.layoutParams.apply {
                this.height = height
                (this as? ViewGroup.MarginLayoutParams)?.setMargins(
                    pageSpacing.left, pageSpacing.top, pageSpacing.right, pageSpacing.bottom
                )
            }
        }

        fun cancelJobs() {
            scope.cancel()
        }

        private fun applyFadeInAnimation(view: View) {
            view.startAnimation(AlphaAnimation(0F, 1F).apply {
                interpolator = LinearInterpolator()
                duration = 300
            })
        }
    }
}
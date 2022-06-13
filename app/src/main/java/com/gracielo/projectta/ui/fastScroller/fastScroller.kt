package com.gracielo.projectta.ui.fastScroller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R

internal class fastScroller : LinearLayout {
    private var bubble: View? = null
    private var handle: View? = null
    private var recyclerView: RecyclerView? = null
    private val handleHider: HandleHider = HandleHider()
    private val scrollListener: ScrollListener = ScrollListener()
    private var heighy = 0
    private var currentAnimator: AnimatorSet? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialise(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialise(context)
    }

    private fun initialise(context: Context) {
        orientation = HORIZONTAL
        clipChildren = false
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.fastscroller, this)
        bubble = findViewById(R.id.fastscroller_bubble)
        handle = findViewById(R.id.fastscroller_handle)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        heighy = h
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            setPosition(event.y)
            if (currentAnimator != null) {
                currentAnimator!!.cancel()
            }
            handler.removeCallbacks(handleHider)
            if (handle!!.visibility == INVISIBLE) {
                showHandle()
            }
            setRecyclerViewPosition(event.y)
            return true
        } else if (event.action == MotionEvent.ACTION_UP) {
            handler.postDelayed(handleHider, HANDLE_HIDE_DELAY.toLong())
            return true
        }
        return super.onTouchEvent(event)
    }

    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun setRecyclerViewPosition(y: Float) {
        if (recyclerView != null) {
            val itemCount = recyclerView!!.adapter!!.itemCount
            val proportion: Float
            proportion = if (bubble!!.y == 0f) {
                0f
            } else if (bubble!!.y + bubble!!.height >= heighy - TRACK_SNAP_RANGE) {
                1f
            } else {
                y / heighy.toFloat()
            }
            val targetPos =
                getValueInRange(0, itemCount - 1, (proportion * itemCount.toFloat()).toInt())
            recyclerView!!.scrollToPosition(targetPos)
        }
    }

    private fun getValueInRange(min: Int, max: Int, value: Int): Int {
        val minimum = Math.max(min, value)
        return Math.min(minimum, max)
    }

    private fun setPosition(y: Float) {
        val position = y / heighy
        val bubbleHeight = bubble!!.height
        bubble!!.y = getValueInRange(
            0,
            heighy - bubbleHeight,
            ((heighy - bubbleHeight) * position).toInt()
        ).toFloat()
        val handleHeight = handle!!.height
        handle!!.y = getValueInRange(
            0,
            heighy - handleHeight,
            ((heighy - handleHeight) * position).toInt()
        ).toFloat()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun showHandle() {
        val animatorSet = AnimatorSet()
        handle!!.pivotX = handle!!.width.toFloat()
        handle!!.pivotY = handle!!.height.toFloat()
        handle!!.visibility = VISIBLE
        val growerX: Animator = ObjectAnimator.ofFloat(handle, SCALE_X, 0f, 1f).setDuration(
            HANDLE_ANIMATION_DURATION.toLong()
        )
        val growerY: Animator = ObjectAnimator.ofFloat(handle, SCALE_Y, 0f, 1f).setDuration(
            HANDLE_ANIMATION_DURATION.toLong()
        )
        val alpha: Animator = ObjectAnimator.ofFloat(handle, ALPHA, 0f, 1f).setDuration(
            HANDLE_ANIMATION_DURATION.toLong()
        )
        animatorSet.playTogether(growerX, growerY, alpha)
        animatorSet.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun hideHandle() {
        currentAnimator = AnimatorSet()
        handle!!.pivotX = handle!!.width.toFloat()
        handle!!.pivotY = handle!!.height.toFloat()
        val shrinkerX: Animator = ObjectAnimator.ofFloat(handle, SCALE_X, 1f, 0f).setDuration(
            HANDLE_ANIMATION_DURATION.toLong()
        )
        val shrinkerY: Animator = ObjectAnimator.ofFloat(handle, SCALE_Y, 1f, 0f).setDuration(
            HANDLE_ANIMATION_DURATION.toLong()
        )
        val alpha: Animator = ObjectAnimator.ofFloat(handle, ALPHA, 1f, 0f).setDuration(
            HANDLE_ANIMATION_DURATION.toLong()
        )
        currentAnimator!!.playTogether(shrinkerX, shrinkerY, alpha)
        currentAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                handle!!.visibility = INVISIBLE
                currentAnimator = null
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                handle!!.visibility = INVISIBLE
                currentAnimator = null
            }
        })
        currentAnimator!!.start()
    }

    private inner class HandleHider : Runnable {
        override fun run() {
            hideHandle()
        }
    }

    private inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            val firstVisibleView = recyclerView!!.getChildAt(0)
            val firstVisiblePosition = recyclerView!!.getChildPosition(firstVisibleView)
            val visibleRange = recyclerView!!.childCount
            val lastVisiblePosition = firstVisiblePosition + visibleRange
            val itemCount = recyclerView!!.adapter!!.itemCount
            val position: Int
            position = if (firstVisiblePosition == 0) {
                0
            } else if (lastVisiblePosition == itemCount - 1) {
                itemCount - 1
            } else {
                firstVisiblePosition
            }
            val proportion = position.toFloat() / itemCount.toFloat()
            setPosition(heighy * proportion)
        }
    }

    companion object {
        private const val HANDLE_HIDE_DELAY = 1000
        private const val HANDLE_ANIMATION_DURATION = 100
        private const val TRACK_SNAP_RANGE = 5
        private const val SCALE_X = "scaleX"
        private const val SCALE_Y = "scaleY"
        private const val ALPHA = "alpha"
    }
}
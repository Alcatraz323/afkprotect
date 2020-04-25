package io.alcatraz.facesaver.utils

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.TextView

object AnimateUtils {
    fun textChange(textView: TextView, text: CharSequence) {
        val animationSet = AnimationSet(true)
        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = 200
        animationSet.addAnimation(alphaAnimation)
        textView.startAnimation(animationSet)
        animationSet.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(p1: Animation) {

            }

            override fun onAnimationEnd(p1: Animation) {
                textView.visibility = View.GONE
                textView.text = text
                val animationSet1 = AnimationSet(true)
                val alphaAnimation1 = AlphaAnimation(0f, 1f)
                alphaAnimation1.duration = 200
                animationSet1.addAnimation(alphaAnimation1)
                textView.startAnimation(animationSet1)
                animationSet1.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(p1: Animation) {

                    }

                    override fun onAnimationEnd(p1: Animation) {
                        textView.visibility = View.VISIBLE

                    }

                    override fun onAnimationRepeat(p1: Animation) {

                    }
                })
            }

            override fun onAnimationRepeat(p1: Animation) {

            }
        })

    }

    fun fadeIn(target: View, animateInterface: SimpleAnimateInterface) {
        val animationSet = AnimationSet(true)
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 1200
        animationSet.addAnimation(alphaAnimation)
        target.startAnimation(animationSet)
        animationSet.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(p1: Animation) {}

            override fun onAnimationEnd(p1: Animation) {
                target.visibility = View.VISIBLE
                animateInterface.onEnd()
            }

            override fun onAnimationRepeat(p1: Animation) {

            }
        })
    }

    fun fadeOut(target: View, animateInterface: SimpleAnimateInterface) {
        val animationSet = AnimationSet(true)
        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = 300
        animationSet.addAnimation(alphaAnimation)
        target.startAnimation(animationSet)
        animationSet.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(p1: Animation) {}

            override fun onAnimationEnd(p1: Animation) {
                target.visibility = View.GONE
                animateInterface.onEnd()
            }

            override fun onAnimationRepeat(p1: Animation) {

            }
        })
    }

    interface SimpleAnimateInterface {
        fun onEnd()
    }
}
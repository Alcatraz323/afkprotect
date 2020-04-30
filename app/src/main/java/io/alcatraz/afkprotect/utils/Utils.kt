package io.alcatraz.afkprotect.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.ArrayList

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.google.gson.Gson

import io.alcatraz.afkprotect.R

object Utils {
    fun dp2Px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun isStringNotEmpty(target: String?): Boolean {
        return target != null && target != "" && target != "null"
    }

    fun getLocalBitmap(url: String): Bitmap? {
        return try {
            val fis = FileInputStream(url)
            BitmapFactory.decodeStream(fis)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun extractStringArr(arr: Array<String>): String {
        var out = ""
        for (i in arr.indices) {
            out += arr[i]
            if (i != arr.size - 1)
                out += ","
        }
        return out
    }

    fun getRandomIndex(listSize: Int): Int {
        return (Math.random() * listSize).toInt()
    }

    fun getSystemBattery(context: Context): Int {
        val batteryInfoIntent = context.applicationContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val level = batteryInfoIntent!!.getIntExtra("level", 0)
        val batterySum = batteryInfoIntent.getIntExtra("scale", 100)
        return 100 * level / batterySum
    }

    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
        }

        return hasNavigationBar
    }

    fun getStrList(inputString: String, length: Int): List<String> {
        var size = inputString.length / length
        if (inputString.length % length != 0) {
            size += 1
        }
        return getStrList(inputString, length, size)
    }

    fun getStrList(
        inputString: String, length: Int,
        size: Int
    ): List<String> {
        val list = ArrayList<String>()
        for (index in 0 until size) {
            val childStr = substring(
                inputString, index * length,
                (index + 1) * length
            )
            list.add(childStr!!)
        }
        return list
    }

    fun substring(str: String, f: Int, t: Int): String? {
        if (f > str.length)
            return null
        return if (t > str.length) {
            str.substring(f, str.length)
        } else {
            str.substring(f, t)
        }
    }

    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    @SuppressLint("InflateParams")
    fun setupAdapterView(adapterView: AdapterView<*>) {
        var parent: View? = adapterView.parent.parent as View
        if (parent == null) {
            parent = adapterView
        }
        parent.setPadding(
            parent.paddingLeft,
            parent.paddingTop,
            parent.paddingRight,
            getNavigationBarHeight(parent.context)
        )
        val inflater =
            adapterView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val emptyView = inflater.inflate(R.layout.panel_empty_view, null)
        (adapterView.parent as ViewGroup).addView(emptyView)
        adapterView.emptyView = emptyView
    }

    fun getNavigationBarHeight(context: Context): Int {
        if (!checkDeviceHasNavigationBar(context))
            return 0
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    @JvmOverloads
    fun getProcessingDialog(
        ctx: Context,
        out: MutableList<View>,
        cancelable: Boolean,
        showProgressBar: Boolean,
        needAsync: Boolean = false,
        async: Runnable? = null
    ): AlertDialog {
        val lf = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @SuppressLint("InflateParams") val root = lf.inflate(R.layout.dialog_processing, null)
        val content = root.findViewById<TextView>(R.id.ad_content)
        val pb = root.findViewById<ProgressBar>(R.id.ad_processing_progress)
        if (!showProgressBar) {
            pb.visibility = View.GONE
        }
        out.add(content)
        out.add(pb)
        val builder = AlertDialog.Builder(ctx)
            .setTitle(R.string.ad_processing)
            .setView(root)

        if (cancelable) {
            builder.setNegativeButton(R.string.ad_nb3, null)
        }
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(cancelable)

        if (needAsync) {
            Thread(async).start()
        }
        return alertDialog
    }

    fun setSpinnerItemSelectedByValue(spinner: Spinner, value: String) {
        val apsAdapter = spinner.adapter
        val k = apsAdapter.count
        for (i in 0 until k) {
            if (value == apsAdapter.getItem(i).toString()) {
                spinner.setSelection(i)
                break
            }
        }
    }

    fun setupSRL(target: SwipeRefreshLayout) {
        target.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.green_colorPrimary,
            R.color.orange_colorPrimary,
            R.color.pink_colorPrimary
        )
    }

    fun copyToClipboard(content: String, c: Context) {
        val clipboardManager =
            c.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val myClip = ClipData.newPlainText("text", content)
        clipboardManager.primaryClip = myClip
        Toast.makeText(c, R.string.toast_copied, Toast.LENGTH_SHORT).show()
    }

    fun setImageWithTint(imgv: ImageView, resId: Int, color: Int) {
        imgv.setImageDrawable(getTintedDrawable(imgv.context, resId, color))
    }

    fun setImageWithTint(imgv: ImageButton, resId: Int, color: Int) {
        imgv.setImageDrawable(getTintedDrawable(imgv.context, resId, color))
    }

    fun setViewsEnabled(views: List<View>, enabled: Boolean) {
        for (v in views) {
            v.isEnabled = enabled
        }
    }

    fun retintImage(imgv: ImageView, color: Int) {
        val drawable = imgv.drawable
        val compat = DrawableCompat.wrap(drawable)
        compat.setBounds(0, 0, compat.minimumWidth, compat.minimumHeight)
        DrawableCompat.setTint(compat, color)
        imgv.setImageDrawable(compat)
    }

    fun getTintedDrawable(context: Context, resId: Int, color: Int): Drawable {
        val up = ContextCompat.getDrawable(context, resId)
        val drawableUp = DrawableCompat.wrap(up!!)
        drawableUp.setBounds(0, 0, drawableUp.minimumWidth, drawableUp.minimumHeight)
        DrawableCompat.setTint(drawableUp, color)
        return drawableUp
    }

    fun <T> json2Object(s: String, type: Class<T>): T? {
        return Gson().fromJson(s, type)
    }

    fun <T> obj2Json(obj: T) : String{
        return Gson().toJson(obj)
    }

    fun extractPackageName(process: String): String {
        var processVar = process
        if (processVar.contains(":")) {
            processVar = processVar.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }
        return processVar
    }

    fun getFinalProcessName(isweakkey: Boolean, process: String): String {
        var processVar = process
        if (isweakkey)
            processVar = extractPackageName(processVar)
        return processVar
    }

    @SuppressLint("BatteryLife")
    fun ignoreBatteryOptimization(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        var hasIgnored = false
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(context.packageName)

            if (!hasIgnored) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            }
        }
    }

    fun setViewSize(view: View, width: Int, height: Int) {
        val params = view.layoutParams
        params.width = width
        params.height = height
        view.layoutParams = params
    }

    fun setListViewHeight(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
    }
}

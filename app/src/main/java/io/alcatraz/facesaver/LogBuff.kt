package io.alcatraz.facesaver

import android.annotation.SuppressLint
import android.text.Html

import java.text.SimpleDateFormat
import java.util.Date

object LogBuff {
    var MESSAGE_DEFAULT_MAX_AMOUNT = 256

    var COLOR_LEVEL_INFO = "#4caf50"
    var COLOR_LEVEL_WARN = "#ff9800"
    var COLOR_LEVEL_ERROR = "#f44336"
    var COLOR_LEVEL_DEBUG = "#1565C0"
    var HTML_BRLINE = "<br/>"

    var accessibilityStatus = false

    private var whole_message = ""
    private var num = 0

    private val time: String
        get() {
            val currentTime = System.currentTimeMillis()
            @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("MM-dd HH:mm:ss")
            val date = Date(currentTime)
            return formatter.format(date)
        }

    val finalLog: CharSequence
        get() = Html.fromHtml(whole_message)

    fun I(infoMsg: String) {
        val className = Exception().stackTrace[1].className.replace(Constants.MY_PACKAGE_NAME, "")
        val methodName = Exception().stackTrace[1].methodName
        commitMessageChange(
            wrapFontString(
                time + " [INFO][" + className + "::" + methodName + "]" +
                        infoMsg, COLOR_LEVEL_INFO
            )
        )
    }

    fun W(warnMsg: String) {
        val className = Exception().stackTrace[1].className.replace(Constants.MY_PACKAGE_NAME, "")
        val methodName = Exception().stackTrace[1].methodName
        commitMessageChange(
            wrapFontString(
                time + " [WARNING][" + className + "::" + methodName + "]" +
                        warnMsg, COLOR_LEVEL_WARN
            )
        )
    }

    fun E(errMsg: String) {
        val className = Exception().stackTrace[1].className.replace(Constants.MY_PACKAGE_NAME, "")
        val methodName = Exception().stackTrace[1].methodName
        commitMessageChange(
            wrapFontString(
                time + " [ERROR][" + className + "::" + methodName + "]" +
                        errMsg, COLOR_LEVEL_ERROR
            )
        )
    }

    fun D(dbgMsg: String) {
        val className = Exception().stackTrace[1].className.replace(Constants.MY_PACKAGE_NAME, "")
        val methodName = Exception().stackTrace[1].methodName
        commitMessageChange(
            wrapFontString(
                time + " [DEBUG][" + className + "::" + methodName + "]" +
                        dbgMsg, COLOR_LEVEL_DEBUG
            )
        )
    }

    @JvmOverloads
    fun wrapFontString(
        raw: String,
        rgb_color: String,
        isBold: Boolean = false,
        isItalic: Boolean = false
    ): String {
        var rawVar = raw
        if (isBold) {
            rawVar = "<b>$rawVar</b>"
        }
        if (isItalic) {
            rawVar = "<i>$rawVar</i>"
        }

        return "<font color=\"$rgb_color\">$rawVar</font>"
    }

    fun log(content: String) {
        commitMessageChange(content)
    }

    fun addDivider() {
        whole_message += "<br/>============================"
    }

    fun clearLog() {
        whole_message = ""
        num = 0
        W("Cleared operation log")
    }

    private fun checkAndClearup() {
        if (num >= MESSAGE_DEFAULT_MAX_AMOUNT) {
            whole_message = ""
            num = 0
            W("Automatically cleared log ( reaching max :$MESSAGE_DEFAULT_MAX_AMOUNT)")
        }
    }

    private fun commitMessageChange(content: String) {
        checkAndClearup()
        whole_message += HTML_BRLINE + content
        num++
    }
}

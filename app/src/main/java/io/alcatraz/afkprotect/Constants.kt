package io.alcatraz.afkprotect

import io.alcatraz.afkprotect.beans.QueryElement

object Constants {
    const val DATA_HISTORY_VERSION = 1
    const val DATA_PROFILE_VERSION = 1

    const val BUILD_DONATE = true

    const val MY_PACKAGE_NAME = "io.alcatraz.afkprotect"
    const val MY_PROJECT_CODE = "afkprotect"
    const val MY_GITHUB = "https://github.com/Alcatraz323/"
    const val MY_GITHUB_PAGE = "https://alcatraz323.github.io/"
    const val OPEN_SOURCE_URL = MY_GITHUB + MY_PROJECT_CODE
    const val SUPPORT_URL = MY_GITHUB_PAGE + MY_PROJECT_CODE
    const val EASTER_URL = "$MY_GITHUB_PAGE$MY_PROJECT_CODE/easter"

    //===============================================

    const val PREF_TRIGGER_IGNORE_PACKAGE = "ignore_trigger_package"

    //===============================================

    const val BROADCAST_ACTION_UPDATE_PREFERENCES = "update_preferences"

    //gson
    val openSourceProjects: List<QueryElement>
        get() {
            return listOf(
                QueryElement(
                    "google",
                    "gson",
                    "https://github.com/google/gson",
                    "Apache 2.0",
                    "A Java serialization/deserialization library to convert Java Objects into JSON and back"
                )
            )
        }
}

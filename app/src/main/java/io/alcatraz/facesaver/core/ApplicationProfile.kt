package io.alcatraz.facesaver.core

class ApplicationProfile(
    var pack: String,
    var afkTime: Int
) {
    var intendedActs: MutableList<String> = mutableListOf("")
    var useActIndividualCtl: Boolean = false
    var backClickTimes: Int = 1

    constructor(
        pack: String,
        afkTime: Int,
        intendedActs: MutableList<String>,
        useActIndividualCtl: Boolean,
        backClickTimes: Int
    ) : this(pack, afkTime) {
        this.intendedActs = intendedActs
        this.useActIndividualCtl = useActIndividualCtl
        this.backClickTimes = backClickTimes
    }
}
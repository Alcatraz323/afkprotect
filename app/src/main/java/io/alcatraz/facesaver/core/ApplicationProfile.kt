package io.alcatraz.facesaver.core

class ApplicationProfile(
    var pack: String,
    var afkTime: Int
) {
    var intendedActs: MutableList<String> = mutableListOf("")
    var useActIndividualCtl: Boolean = false
    var backClickTimes: Int = 1
    var doPauseMusicBroadcast = true
    var doRequestAudioFocus = true
    var doSetStreamVolumeZero = true
    var extendedOptions = ""

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

    constructor(
        pack: String,
        afkTime: Int,
        intendedActs: MutableList<String>,
        useActIndividualCtl: Boolean,
        backClickTimes: Int,
        doPauseMusicBroadcast: Boolean,
        doRequestAudioFocus: Boolean,
        doSetStreamVolumeZero: Boolean
    ) : this(pack, afkTime, intendedActs, useActIndividualCtl, backClickTimes) {
        this.doPauseMusicBroadcast = doPauseMusicBroadcast
        this.doRequestAudioFocus = doRequestAudioFocus
        this.doSetStreamVolumeZero = doSetStreamVolumeZero
    }

}
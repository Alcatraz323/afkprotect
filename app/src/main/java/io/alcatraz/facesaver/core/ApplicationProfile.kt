package io.alcatraz.facesaver.core

class ApplicationProfile(val pack: String,
                         val afkTime: Int) {
    lateinit var intendedActs: MutableList<String>
    var useActIndividualCtl: Boolean = false
    var performPause: Boolean = false

    constructor(pack: String,
                afkTime: Int,
                intendedActs: MutableList<String>,
                useActIndividualCtl: Boolean,
                performPause: Boolean):this(pack,afkTime){
        this.intendedActs = intendedActs
        this.useActIndividualCtl = useActIndividualCtl
        this.performPause = performPause
    }
}
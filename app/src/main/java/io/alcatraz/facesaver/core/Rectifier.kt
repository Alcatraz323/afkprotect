package io.alcatraz.facesaver.core

object Rectifier {
    fun isPackageIgnored(list: MutableList<String>,pack: String): Boolean{
        for (i in list){
            if(pack == i){
                return true
            }
        }
        return false
    }
}
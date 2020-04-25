package io.alcatraz.facesaver.utils

import java.io.File

object IOUtils {
    fun write(file: File, content: String) {
        if (!file.exists()) {
            try {
                file.parentFile.mkdirs()
                file.createNewFile()
            } catch (ignored : Exception) {
            }
        }
        file.writeText(content);
    }

    fun read(dir: String, rm: ReadMonitor?): String {
        var content = ""
        val file = File(dir)
        file.forEachLine {
            content += "\n"
            content += it
            rm?.onLine(it)
        }
        return content
    }

    fun renameFile(oldPath: String, newPath: String) {
        val oleFile = File(oldPath)
        val newFile = File(newPath)
        oleFile.renameTo(newFile)
    }

    fun delete(path: String): Boolean {
        val file = File(path)
        if (!file.exists()) {
            return false
        }
        if (file.isFile) {
            return file.delete()
        }
        val files = file.listFiles()
        for (f in files) {
            if (f.isFile) {
                if (!f.delete()) {
                    return false
                }
            } else {
                if (!delete(f.absolutePath)) {
                    return false
                }
            }
        }
        return file.delete()
    }

    interface ReadMonitor {
        fun onLine(line: String)

        fun callFinish()
    }
}
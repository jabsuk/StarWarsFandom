package edu.juanageitos.fandom.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import edu.juanageitos.fandom.R
import edu.juanageitos.fandom.model.Fandom
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

var fandomMutableList = mutableListOf<Fandom>()

fun readRawFile(context: Context): MutableList<Fandom> {
    fandomMutableList = mutableListOf<Fandom>()

    val removed = readFandomOptions(context, R.string.filenameDeleted)
    val favorites = readFandomOptions(context, R.string.filenameFavs)

    try {
        val entrada = InputStreamReader(context.resources.openRawResource(R.raw.data))

        val br = BufferedReader(entrada)

        br.readLine()
        var linea = br.readLine()
        var data: List<String>
        var visible : Boolean

        while (!linea.isNullOrEmpty()) {
            data = linea.split(";")

            visible = !removed.contains(data[0].toInt())

            if(visible){
                fandomMutableList.add(
                    Fandom(
                        data[0].toInt(),
                        data[1],
                        data[2],
                        data[3],
                        data[4],
                        data[5],
                        favorites.contains(data[0].toInt()),
                        visible)
                    )
            }

            linea = br.readLine()
        }
        br.close()
        entrada.close()
    } catch (e: IOException) {
        fandomMutableList = mutableListOf<Fandom>()
    }

    fandomMutableList.sortBy {
        it.name
    }

    return fandomMutableList
}

fun updateFilesOptions(context: Context, file: Int) {
    val ids = mutableListOf<Int>()

    for (fandom in fandomMutableList) {
        if (
            (file == R.string.filenameDeleted && (!fandom.visible)) ||
            (file == R.string.filenameFavs && fandom.fav)
        ) ids.add(fandom.id)
    }

    writeFandomOptions(context, file, ids)
}

fun deleteFilesOptions(context: Context) {
    context.deleteFile(context.getString(R.string.filenameDeleted))
    context.deleteFile(context.getString(R.string.filenameFavs))
}

private fun readFandomOptions(context: Context, file: Int): MutableList<Int> {
    val ids = mutableListOf<Int>()

    if (context.fileList().contains(context.getString(file))) {
        try {
            val entrada = InputStreamReader(context.openFileInput(context.getString(file)))
            val br = BufferedReader(entrada)

            var linea = br.readLine()

            while (!linea.isNullOrEmpty()) {
                ids.add(linea.toInt())
                linea = br.readLine()
            }
            br.close()
            entrada.close()
        } catch (_: IOException) {
        }
    }

    return ids;
}

private fun writeFandomOptions(context: Context, file: Int, listOfFandomIds: MutableList<Int>) {
    try {
        val salida = OutputStreamWriter(
            context.openFileOutput(
                context.getString(file),
                Activity.MODE_PRIVATE
            )
        )

        for (id: Int in listOfFandomIds) salida.write(id.toString() + "\n")

        salida.flush()
        salida.close()

    } catch (e: Exception) {
    }
}

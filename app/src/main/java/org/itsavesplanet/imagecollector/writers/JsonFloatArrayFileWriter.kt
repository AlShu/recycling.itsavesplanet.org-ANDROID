package org.itsavesplanet.imagecollector.writers

import java.io.BufferedWriter
import java.io.File
import java.io.Writer

class JsonFloatArrayFileWriter(file: File) {
    var writer: Writer ?= null
    val file = file
    var firstElement = true

    init {
    }

    fun open() {
        writer = file.bufferedWriter()
        writer?.append('[')
    }

    fun close() {
        writer?.append(']')
        writer?.close()
    }

    fun write(timeStamp: Number, valuesArray: FloatArray) {
        if (!firstElement) {
            firstElement = false
            writer?.append(",\n")
        }
        writer?.append('[')
        writer?.append(timeStamp.toString())
        for (v in valuesArray) {
            writer?.append(',')
            writer?.append(v.toString())
        }
        writer?.append(']')
    }

}
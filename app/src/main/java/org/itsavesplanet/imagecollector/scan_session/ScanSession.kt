package org.itsavesplanet.imagecollector.scan_session

import java.util.*

class ScanSession {
    val uid = UUID.randomUUID().toString()
    val images = ArrayList<ScanImage>(0)

    init {
    }

}
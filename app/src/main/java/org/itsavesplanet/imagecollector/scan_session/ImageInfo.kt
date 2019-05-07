package org.itsavesplanet.imagecollector.scan_session


data class ImageInfo (
    var timestamp: Number? = null,
    var fileName: String? = null,
    var cannelsInfo: String? = null,
    var width: Int? = null,
    var height: Int? = null,
    var sensorsInfo: SensorsInfo? = null,
//    var location: Location? = null,
    var orientation: Orientation? = null
)
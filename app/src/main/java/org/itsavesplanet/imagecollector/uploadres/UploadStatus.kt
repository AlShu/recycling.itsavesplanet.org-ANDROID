package org.itsavesplanet.imagecollector.uploadres

enum class UploadStatus(val code: Int) {
    NOT_INITIALIZED(0),
    PENDING(1),
    FINISHED(2),
    ERROR(3),
    IN_PROGRESS(4)
}
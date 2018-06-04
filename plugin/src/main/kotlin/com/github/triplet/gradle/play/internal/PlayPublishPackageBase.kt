package com.github.triplet.gradle.play.internal

import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.LocalizedText
import com.google.api.services.androidpublisher.model.Track
import com.google.api.services.androidpublisher.model.TrackRelease
import java.io.File

abstract class PlayPublishPackageBase : PlayPublishTaskBase() {
    protected fun AndroidPublisher.Edits.updateTracks(
            editId: String,
            inputFolder: File,
            versions: List<Long>,
            trackName: String = extension.track,
            releaseStatus: String = extension.releaseStatus,
            publishFraction: Double = extension.userFraction
    ) {
        val track = tracks()
                .list(variant.applicationId, editId)
                .execute().tracks
                ?.firstOrNull { it.track == trackName } ?: Track()

        val releaseTexts = if (inputFolder.exists()) {
            inputFolder.listFiles(LocaleFileFilter).mapNotNull { locale ->
                val fileName = ListingDetail.WHATS_NEW.fileName
                val file = run {
                    File(locale, "$fileName-$trackName").orNull()
                            ?: File(locale, fileName).orNull()
                } ?: return@mapNotNull null

                val recentChanges = File(file, fileName).readProcessed(
                        ListingDetail.WHATS_NEW.maxLength,
                        extension.errorOnSizeLimit
                )
                LocalizedText().setLanguage(locale.name).setText(recentChanges)
            }
        } else {
            null
        }
        val trackRelease = TrackRelease().apply {
            releaseNotes = releaseTexts
            status = releaseStatus
            userFraction = if (releaseStatus == ReleaseStatus.IN_PROGRESS.publishedName) {
                publishFraction
            } else {
                null
            }
            versionCodes = versions
        }

        track.releases = listOf(trackRelease)

        tracks()
                .update(variant.applicationId, editId, trackName, track)
                .execute()
    }
}
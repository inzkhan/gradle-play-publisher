package com.github.triplet.gradle.play

import com.github.triplet.gradle.play.internal.PlayPublishPackageBase
import com.google.api.services.androidpublisher.AndroidPublisher
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ModifyTrackTask : PlayPublishPackageBase() {
    lateinit var inputFolder: File

    @TaskAction
    fun modifyTrack() = write { editId: String ->
        val modifiers = requireNotNull(extension.releaseModifiers) {
            "No release modifiers found"
        }
        val modifyVersions = requireNotNull(trackByType(editId, modifiers.fromTrack).releases?.firstOrNull()?.versionCodes) {
            "No versions found to modify on the `${modifiers._fromTrack.publishedName}` track"
        }
        updateTracks(editId,
                inputFolder,
                modifyVersions,
                modifiers._releaseStatus.publishedName,
                modifiers._toTrack.publishedName,
                modifiers.userFraction)
    }

    private fun AndroidPublisher.Edits.trackByType(editId: String, track: String?) = tracks()
            .get(variant.applicationId, editId, track)
            .execute()
}

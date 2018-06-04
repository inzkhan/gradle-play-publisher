package com.github.triplet.gradle.play

import com.github.triplet.gradle.play.internal.ReleaseStatus
import com.github.triplet.gradle.play.internal.TrackType

open class PlayReleaseModifiers {
    internal var _fromTrack = TrackType.INTERNAL
    /**
     * Specify the track that contains the release being modified. May be one of internal, alpha, beta, rollout,
     * or production. Default is internal.
     */
    var fromTrack: String?
        get() = _fromTrack.publishedName
        set(value) {
            _fromTrack = requireNotNull(TrackType.values().find { it.name.equals(value, true) }) {
                "fromTrack must be one of ${TrackType.values().joinToString { "'${it.publishedName}'" }}"
            }
            //TODO: Move this logic to the validation
            if (toTrack == null)
                _toTrack = _fromTrack
        }

    internal var _toTrack = _fromTrack
    /**
     * Optional track to move the release being modified. May be one of internal, alpha, beta, rollout,
     * or production. Default is to match fromTrack
     */
    var toTrack: String?
        get() = _toTrack.publishedName
        set(value) {
            _toTrack = requireNotNull(TrackType.values().find { it.name.equals(value, true) }) {
                "toTrack must be one of ${TrackType.values().joinToString { "'${it.publishedName}'" }}"
            }
            //TODO: Move this logic to the validation
            if (_toTrack < _fromTrack)
                throw IllegalArgumentException("toTrack must be the same or higher release level.")
        }

    internal var _releaseStatus = ReleaseStatus.COMPLETED
    /**
     * Specify the status to apply to the release being modified. May be one of completed, draft,
     * halted, or inProgress. Default is completed.
     */
    var releaseStatus: String?
        get() = _releaseStatus.publishedName
        set(value) {
            _releaseStatus = requireNotNull(ReleaseStatus.values().find { it.name.equals(value, true) }) {
                "Release Status must be one of ${ReleaseStatus.values().joinToString { "'${it.publishedName}'" }}"
            }
        }

    /**
     * Specify the new user percent intended to receive a 'rollout' update (see [toTrack]).
     * Default is 10% == 0.1.
     */
    var userFraction = 0.1
}

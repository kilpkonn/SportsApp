package ee.taltech.iti0213.sportsapp.util

import ee.taltech.iti0213.sportsapp.api.controller.TrackSyncController
import ee.taltech.iti0213.sportsapp.api.dto.GpsLocationDto
import ee.taltech.iti0213.sportsapp.api.dto.GpsSessionDto
import ee.taltech.iti0213.sportsapp.component.imageview.TrackTypeIcons
import ee.taltech.iti0213.sportsapp.db.domain.OfflineSession
import ee.taltech.iti0213.sportsapp.db.repository.*
import ee.taltech.iti0213.sportsapp.track.TrackType
import java.text.SimpleDateFormat
import java.util.*

class TrackUtils {
    companion object {
        private val trackNameRegex = "\\w+\\s\\d{1,2}-\\d{1,2}+-\\d{4}".toRegex()
        private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

        fun generateNameIfNeeded(name: String, type: TrackType): String {
            if (name == "" || TrackTypeIcons.OPTIONS.any { t -> name.startsWith(t) } && name.matches(trackNameRegex)) {
                val date = Date()
                return "${TrackTypeIcons.getString(type)} ${dateFormatter.format(date)}"
            }
            return name
        }

        fun syncTracks(
            offlineSessionsRepository: OfflineSessionsRepository,
            trackSummaryRepository: TrackSummaryRepository,
            trackLocationsRepository: TrackLocationsRepository,
            checkpointsRepository: CheckpointsRepository,
            wayPointsRepository: WayPointsRepository,
            trackSyncController: TrackSyncController
        ) {
            val sessionsToSync = offlineSessionsRepository.readOfflineSessions()

            sessionsToSync.forEach { sessionId ->
                val session = trackSummaryRepository.readTrackSummary(sessionId.trackId)
                val locations = trackLocationsRepository.readTrackLocations(sessionId.trackId, 0L, Long.MAX_VALUE)
                val checkpoints = checkpointsRepository.readTrackCheckpoints(sessionId.trackId)
                val wayPoints = wayPointsRepository.readTrackWayPoints(sessionId.trackId)

                val sessionDto = GpsSessionDto(
                    name = session.name,
                    description = session.name,
                    recordedAt = Date(session.startTimestamp)
                )
                trackSyncController.createNewSession(sessionDto, { resp ->
                    locations.forEach { location ->
                        val locationDto = GpsLocationDto.fromTrackLocation(location, resp.id!!)
                        trackSyncController.addLocationToSession(locationDto) { }
                    }

                    checkpoints.forEach { cp ->
                        val cpDto = GpsLocationDto.fromCheckpoint(cp, resp.id!!)
                        trackSyncController.addLocationToSession(cpDto) { }
                    }

                    wayPoints.forEach { wp ->
                        val wpDto = GpsLocationDto.fromWayPoint(wp, resp.id!!)
                        trackSyncController.addLocationToSession(wpDto) { }
                    }

                    offlineSessionsRepository.deleteOfflineSession(sessionId.id)
                    // Check for failure?
                }, { }
                )
            }
        }
    }
}
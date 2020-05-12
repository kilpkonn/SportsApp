package ee.taltech.iti0213.sportsapp.util

import android.content.Context
import android.widget.Toast
import ee.taltech.iti0213.sportsapp.api.controller.TrackSyncController
import ee.taltech.iti0213.sportsapp.api.dto.GpsLocationDto
import ee.taltech.iti0213.sportsapp.api.dto.GpsSessionDto
import ee.taltech.iti0213.sportsapp.component.imageview.TrackTypeIcons
import ee.taltech.iti0213.sportsapp.db.domain.OfflineSession
import ee.taltech.iti0213.sportsapp.db.repository.*
import ee.taltech.iti0213.sportsapp.track.Track
import ee.taltech.iti0213.sportsapp.track.TrackType
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.Checkpoint
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.TrackLocation
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.WayPoint
import io.jenetics.jpx.GPX

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
            trackSyncController: TrackSyncController,
            context: Context
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
                    val locationsToUpload = mutableListOf<GpsLocationDto>()
                    locations.forEach { location ->
                        locationsToUpload.add(GpsLocationDto.fromTrackLocation(location, resp.id!!))
                    }

                    checkpoints.forEach { cp ->
                        locationsToUpload.add(GpsLocationDto.fromCheckpoint(cp, resp.id!!))
                    }

                    wayPoints.forEach { wp ->
                        locationsToUpload.add(GpsLocationDto.fromWayPoint(wp, resp.id!!))
                    }

                    trackSyncController.addMultipleLocationsToSession(locationsToUpload, resp.id!!, {
                        offlineSessionsRepository.deleteOfflineSession(sessionId.id)
                        Toast.makeText(context, "Uploaded session: ${session.name}", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(context, "Error uploading session: ${session.name}", Toast.LENGTH_SHORT).show()
                    })
                }, { }
                )
            }
            if (sessionsToSync.isEmpty()) {
                Toast.makeText(context, "Everything is up to date!", Toast.LENGTH_SHORT).show()
            }
        }

        fun serializeToGpx(trackLocations: List<TrackLocation>, checkpoints: List<Checkpoint>, wayPoints: List<WayPoint>): GPX {
            val gpx = GPX.builder()
                .addTrack { gpxTrack ->
                    gpxTrack.addSegment { gpxSegment ->
                        trackLocations.forEach { trackLocation ->
                            gpxSegment.addPoint { p ->
                                p.lat(trackLocation.latitude)
                                    .lon(trackLocation.longitude)
                                    .ele(trackLocation.altitude)
                                    .hdop(trackLocation.accuracy.toDouble())
                                    .vdop(trackLocation.altitudeAccuracy.toDouble())
                                    .time(trackLocation.timestamp)
                                    .links(null)
                            }
                        }
                        checkpoints.forEach { cp ->
                            gpxSegment.addPoint { p ->
                                p.lat(cp.latitude)
                                    .lon(cp.longitude)
                                    .ele(cp.altitude)
                                    .hdop(cp.accuracy)
                                    .vdop(cp.altitudeAccuracy)
                                    .time(cp.timestamp)
                                    .links(null)
                                    .desc("CP")

                            }
                        }
                        wayPoints.forEach { wp ->
                            gpxSegment.addPoint { p ->
                                p.lat(wp.latitude)
                                    .lon(wp.longitude)
                                    .time(wp.timeAdded)
                                    .links(null)
                                    .desc("WP")

                            }
                        }
                    }
                }.build()
            return gpx
        }

        fun serializeToGpx(track: Track): GPX {
            var lastPause = 0
            val pauses = track.pauses
            pauses.add(track.track.size)

            val gpx = GPX.builder()
                .addTrack { gpxTrack ->
                    pauses.forEach { pause ->
                        gpxTrack.addSegment { gpxSegment ->
                            track.track.subList(lastPause, pause).forEach { trackLocation ->
                                gpxSegment.addPoint { p ->
                                    p.lat(trackLocation.latitude)
                                        .lon(trackLocation.longitude)
                                        .ele(trackLocation.altitude)
                                        .hdop(trackLocation.accuracy.toDouble())
                                        .vdop(trackLocation.altitudeAccuracy.toDouble())
                                        .time(trackLocation.timestamp)
                                }
                            }
                        }
                        lastPause = pause
                    }
                }.build()
            return gpx
        }
    }
}
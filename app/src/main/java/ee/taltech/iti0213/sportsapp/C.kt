package ee.taltech.iti0213.sportsapp

class C {
    companion object {
        const val NOTIFICATION_CHANNEL = "ee.taltech.iti0213.sportsapp.default_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Default channel2"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Default channel description"
        const val NOTIFICATION_ACTION_ADD_WP = "ee.taltech.iti0213.sportsapp.notification.wp"
        const val NOTIFICATION_ACTION_ADD_WP_DATA = "ee.taltech.iti0213.sportsapp.notification.wp.lat_lng"
        const val NOTIFICATION_ACTION_ADD_CP = "ee.taltech.iti0213.sportsapp.notification.cp"
        const val NOTIFICATION_ACTION_ADD_CP_DATA = "ee.taltech.iti0213.sportsapp.notification.cp.lat_lng"

        const val TRACK_ACTION_ADD_WP = "ee.taltech.iti0213.sportsapp.main.wp"
        const val TRACK_ACTION_ADD_WP_DATA = "ee.taltech.iti0213.sportsapp.main.wp.lat_lng"
        const val TRACK_ACTION_ADD_CP = "ee.taltech.iti0213.sportsapp.main.cp"
        const val TRACK_ACTION_ADD_CP_DATA = "ee.taltech.iti0213.sportsapp.main.cp.lat_lng"

        const val LOCATION_UPDATE_ACTION = "ee.taltech.iti0213.sportsapp.location_update"
        const val LOCATION_UPDATE_ACTION_TRACK_LOCATION = "ee.taltech.iti0213.sportsapp.location_update.track_location"

        const val TRACK_STATS_UPDATE_ACTION = "ee.taltech.iti0213.sportsapp.track_stats_update"
        const val TRACK_STATS_UPDATE_ACTION_DATA = "ee.taltech.iti0213.sportsapp.track_stats_update.stats_data"

        const val TRACK_SYNC_RESPONSE = "ee.taltech.iti0213.sportsapp.track_sync"
        const val TRACK_SYNC_REQUEST = "ee.taltech.iti0213.sportsapp.track_request"
        const val TRACK_SYNC_REQUEST_TIME = "ee.taltech.iti0213.sportsapp.track_sync.time"
        const val TRACK_SYNC_DATA = "ee.taltech.iti0213.sportsapp.track_sync.data"

        const val TRACK_ACTION_REMOVE_WP = "ee.taltech.iti0213.sportsapp.wp.remove"
        const val TRACK_ACTION_REMOVE_WP_LOCATION = "ee.taltech.iti0213.sportsapp.wp.remove.location"

        const val TRACK_SET_RABBIT = "ee.taltech.iti0213.sportsapp.rabbit"
        const val TRACK_SET_RABBIT_NAME = "ee.taltech.iti0213.sportsapp.rabbit.name"
        const val TRACK_SET_RABBIT_VALUE = "ee.taltech.iti0213.sportsapp.rabbit.value"
        const val TRACK_SET_RABBIT_START_TIME = "ee.taltech.iti0213.sportsapp.rabbit.start_time"

        const val TRACK_RESET = "ee.taltech.iti0213.sportsapp.track.reset"
        const val TRACK_SAVE = "ee.taltech.iti0213.sportsapp.track.save"
        const val TRACK_START = "ee.taltech.iti0213.sportsapp.track.start"
        const val TRACK_STOP = "ee.taltech.iti0213.sportsapp.track.stop"

        const val TRACK_DETAIL_REQUEST = "ee.taltech.iti0213.sportsapp.track.detail.request"
        const val TRACK_DETAIL_REQUEST_DATA = "ee.taltech.iti0213.sportsapp.track.detail.request.data"
        const val TRACK_DETAIL_RESPONSE = "ee.taltech.iti0213.sportsapp.track.detail.response"
        const val TRACK_DETAIL_DATA = "ee.taltech.iti0213.sportsapp.track.detail.data"

        const val NOTIFICATION_ID = 43210
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 324;


        const val SNAKBAR_REQUEST_FINE_LOCATION_ACCESS_TEXT = "Hey, i really need to access GPS!"
        const val SNAKBAR_REQUEST_FINE_LOCATION_CONFIRM_TEXT = "OK"
        const val SNAKBAR_REQUEST_DENIED_TEXT = "You denied GPS! What can I do?"
        const val SNAKBAR_OPEN_SETTINGS_TEXT = "Settings"

        const val TOAST_USER_INTERACTION_CANCELLED_TEXT = "User interaction was cancelled."
        const val TOAST_PERMISSION_GRANTED_TEXT = "Permission was granted"
    }
}

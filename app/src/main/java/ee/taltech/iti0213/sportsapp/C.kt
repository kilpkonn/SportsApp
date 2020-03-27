package ee.taltech.iti0213.sportsapp

class C {
    companion object {
        const val NOTIFICATION_CHANNEL = "ee.taltech.iti0213.sportsapp.default_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Default channel2"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Default channel description"
        const val NOTIFICATION_ACTION_ADD_WP = "ee.taltech.iti0213.sportsapp.wp"
        const val NOTIFICATION_ACTION_ADD_WP_DATA = "ee.taltech.iti0213.sportsapp.wp.lat_lng"
        const val NOTIFICATION_ACTION_CP = "ee.taltech.iti0213.sportsapp.cp"

        const val LOCATION_UPDATE_ACTION = "ee.taltech.iti0213.sportsapp.location_update"
        const val LOCATION_UPDATE_ACTION_TRACK_LOCATION = "ee.taltech.iti0213.sportsapp.location_update.track_location"

        const val TRACK_STATS_UPDATE_ACTION = "ee.taltech.iti0213.sportsapp.track_stats_update"
        const val TRACK_STATS_UPDATE_ACTION_DATA = "ee.taltech.iti0213.sportsapp.track_stats_update.stats_data"

        const val TRACK_ACTION_REMOVE_WP = "ee.taltech.iti0213.sportsapp.wp.remove"
        const val TRACK_ACTION_REMOVE_WP_LOCATION = "ee.taltech.iti0213.sportsapp.wp.remove.location"

        const val NOTIFICATION_ID = 43210
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 34;


        const val SNAKBAR_REQUEST_FINE_LOCATION_ACCESS_TEXT = "Hey, i really need to access GPS!"
        const val SNAKBAR_REQUEST_FINE_LOCATION_CONFIRM_TEXT = "OK"
        const val SNAKBAR_REQUEST_DENIED_TEXT = "You denied GPS! What can I do?"
        const val SNAKBAR_OPEN_SETTINGS_TEXT = "Settings"

        const val TOAST_USER_INTERACTION_CANCELLED_TEXT = "User interaction was cancelled."
        const val TOAST_PERMISSION_GRANTED_TEXT = "Permission was granted"
    }
}

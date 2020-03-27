package ee.taltech.iti0213.sportsapp.spinner

class RotationMode {
    companion object {
        const val NORTH_UP = "North Up"
        const val USER_CHOSEN_UP = "User chosen up"
        const val DIRECTION_UP = "Move direction up"
        val OPTIONS = arrayOf(
                NORTH_UP,
                USER_CHOSEN_UP,
                DIRECTION_UP
        )
    }
}
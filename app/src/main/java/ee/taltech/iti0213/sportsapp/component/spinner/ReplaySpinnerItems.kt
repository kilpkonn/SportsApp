package ee.taltech.iti0213.sportsapp.component.spinner

class ReplaySpinnerItems {
    companion object {
        const val BLUE = "Blue"
        const val GREEN = "Green"
        const val ORANGE = "Orange"
        const val PURPLE = "Purple"
        const val NONE = "None"
        val OPTIONS = arrayOf(
            NONE,
            BLUE,
            GREEN,
            ORANGE,
            PURPLE
        )

        val COLORS = hashMapOf(
            NONE to 0xFFFFFFFF,
            BLUE to 0xFF001c5c,
            GREEN to 0xFF073800,
            ORANGE to 0xFF521000,
            PURPLE to 0xFF2e0052
        )

        val COLORS_MAX_SPEED = hashMapOf(
            NONE to 0xFFFFFFFF,
            BLUE to 0xFFc7d8ff,
            GREEN to 0xFFb8ffad,
            ORANGE to 0xFFffce47,
            PURPLE to 0xFFffc2ff
        )
    }
}
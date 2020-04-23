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
            BLUE to 0xFF0939ab,
            GREEN to 0xFF0e5404,
            ORANGE to 0xFFbf4300,
            PURPLE to 0xFF6b00bd
        )

        val COLORS_MAX_SPEED = hashMapOf(
            NONE to 0xFFFFFFFF,
            BLUE to 0xFFc7d8ff,
            GREEN to 0xFF9fff91,
            ORANGE to 0xFFffb891,
            PURPLE to 0xFFdeb3ff
        )
    }
}
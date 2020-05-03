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
            BLUE to 0xFF004eff,
            GREEN to 0xFF0f7a00,
            ORANGE to 0xFF521000,
            PURPLE to 0xFF50008f
        )

        val COLORS_MAX_SPEED = hashMapOf(
            NONE to 0xFFffc9c9,
            BLUE to 0xFFc7d8ff,
            GREEN to 0xFFb8ffad,
            ORANGE to 0xFFfcce4e,
            PURPLE to 0xFFffc2ff
        )
    }
}
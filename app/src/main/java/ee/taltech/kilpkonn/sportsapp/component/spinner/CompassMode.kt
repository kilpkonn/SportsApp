package ee.taltech.kilpkonn.sportsapp.component.spinner

class CompassMode {
    companion object {
        const val NONE = "None"
        const val IMAGE = "Icon"
        const val NUMERIC = "Numeric"
        val OPTIONS = arrayOf(
            IMAGE, NUMERIC, NONE
        )
    }
}
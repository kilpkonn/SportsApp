package ee.taltech.iti0213.sportsapp.component.spinner

class OverviewMode {
    companion object {
        const val WEEK = "Week"
        const val TWO_WEEKS = "2 Weeks"
        const val MONTH = "Month"
        const val THREE_MONTHS = "3 months"
        const val SIX_MONTH = "6 months"
        const val YEAR = "Year"
        val OPTIONS = arrayOf(
            WEEK, TWO_WEEKS, MONTH, THREE_MONTHS, SIX_MONTH, YEAR
        )
    }
}
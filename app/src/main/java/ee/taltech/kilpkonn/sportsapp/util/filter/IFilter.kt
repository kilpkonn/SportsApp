package ee.taltech.kilpkonn.sportsapp.util.filter

interface IFilter<T> {

    fun process(input: T): T
}
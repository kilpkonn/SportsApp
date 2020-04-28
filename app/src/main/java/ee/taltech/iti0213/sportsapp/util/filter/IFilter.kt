package ee.taltech.iti0213.sportsapp.util.filter

interface IFilter<T> {

    fun process(input: T): T
}
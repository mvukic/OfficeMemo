package ruazosa.hr.fer.officememo.Model

/**
 * Created by shimu on 29.6.2017..
 */
data class Department(
        var did: String = "",
        var name: String = "",
        var shortName: String = "",
        var about: String = "",
        var location: String = "",
        var imageUrl: String = "",
        var coverUrl: String = ""
)
{
    override fun toString(): String {
        return name
    }
}
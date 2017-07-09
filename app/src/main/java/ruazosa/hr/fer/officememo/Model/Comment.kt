package ruazosa.hr.fer.officememo.Model

/**
 * Created by shimu on 29.6.2017..
 */

data class Comment(
        var uid: String = "",
        var content: String = "",
        var timeStamp: String = "",
        var upVotes: Long = 0)

package ruazosa.hr.fer.officememo.Model

import java.util.Arrays.asList

/**
 * Created by shimu on 29.6.2017..
 */
data class Post(
        var uid: String = "",
        var did: String = "",
        var title: String = "",
        var content: String = "",
        var imageUrl: String = "",
        var timeStamp: String = "",
        var location: String = "",
        var comments: List<Comment> = asList(),
        var upVotes: Long = 0)

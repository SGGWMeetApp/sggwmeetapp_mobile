package pl.sggw.sggwmeet.domain

import java.util.Date

data class Review(
    var id: String,
    var comment: String,
    var author: Reviewer,
    var isPositive: Boolean,
    var upvoteCount: Int,
    var downvoteCount: Int,
    var publicationDate: Date,
    var userVote: Boolean?,
    var isOwnedByUser: Boolean
) {

    var isLikeProcessing = false
    var isDislikeProcessing = false

    fun containsSameDataAs(review : Review) : Boolean {
        return comment == comment &&
                author == review.author &&
                isPositive == review.isPositive &&
                upvoteCount == review.upvoteCount &&
                downvoteCount == review.downvoteCount &&
                publicationDate == review.publicationDate &&
                userVote == review.userVote &&
                isOwnedByUser == review.isOwnedByUser
    }
}

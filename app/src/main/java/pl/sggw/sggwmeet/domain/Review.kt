package pl.sggw.sggwmeet.domain

import java.util.Date

data class Review(
    val id: String,
    val comment: String,
    val author: Reviewer,
    val isPositive: Boolean,
    val upvoteCount: Int,
    val downvoteCount: Int,
    val publicationDate: Date,
    val userVote: Boolean?,
    val isOwnedByUser: Boolean
)

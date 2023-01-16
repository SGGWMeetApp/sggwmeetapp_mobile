package pl.sggw.sggwmeet.domain

data class Reviewer(
    val firstName: String,
    val lastName: String,
    val avatarUrl: String?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reviewer

        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (avatarUrl != other.avatarUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + (avatarUrl?.hashCode() ?: 0)
        return result
    }
}
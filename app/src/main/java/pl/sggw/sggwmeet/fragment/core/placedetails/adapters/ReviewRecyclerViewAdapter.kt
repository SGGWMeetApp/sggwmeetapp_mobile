package pl.sggw.sggwmeet.fragment.core.placedetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.Review
import pl.sggw.sggwmeet.util.getPolishMonthName

class ReviewRecyclerViewAdapter(
    private val context : Context,
    private val picasso : Picasso
) : ListAdapter<Review, RecyclerView.ViewHolder>(DiffCallback()){

    private var onAllowedLikeClick: ((reviewId: String) -> Unit)? = null
    private var onAllowedDislikeClick: ((reviewId: String) -> Unit)? = null

    private var processingPosition: Int? = null

    fun confirmUserLike() {
        if(processingPosition != null) {
            val review = getItem(processingPosition!!)
            if(review.userVote == false) {
                review.downvoteCount--
            }
            review.upvoteCount++
            review.userVote = true
            review.isLikeProcessing = false
            notifyItemChanged(processingPosition!!)
            processingPosition = null
        }
    }

    fun confirmUserDislike() {
        if(processingPosition != null) {
            val review = getItem(processingPosition!!)
            if(review.userVote == true) {
                review.upvoteCount--
            }
            review.downvoteCount++
            review.userVote = false
            review.isDislikeProcessing = false
            notifyItemChanged(processingPosition!!)
            processingPosition = null
        }
    }

    fun cancelLikeOrDislikeProcessing() {
        if(processingPosition != null) {
            val review = getItem(processingPosition!!)
            review.isDislikeProcessing = false
            review.isLikeProcessing = false
            notifyItemChanged(processingPosition!!)
            processingPosition = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_review, parent, false)
        return ReviewVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review = getItem(position)
        val binder = holder as ReviewVH

        loadAvatar(review.author.avatarUrl, binder.authorAvatarIV)
        binder.authorFulNameTV.text = "${review.author.firstName} ${review.author.lastName}"
        binder.publicationDateTV.text = "${review.publicationDate.getPolishMonthName()} ${review.publicationDate.year}"
        setIsPositiveIndicator(review.isPositive, binder.isPositiveIV)

        binder.likeCounterTV.text = review.upvoteCount.toString()
        binder.dislikeCounterTV.text = review.downvoteCount.toString()
        binder.commentContentTV.text = review.comment

        setLikeIndicator(review, binder.likeIV, binder.likeCounterTV)
        setDislikeIndicator(review, binder.dislikeIV, binder.dislikeCounterTV)

        setLikeButtonListener(position, review, binder.likeBT, binder.likeIV)
        setDislikeButtonListener(position, review, binder.dislikeBT, binder.dislikeIV)
    }

    private fun setLikeButtonListener(position: Int, review: Review, button: LinearLayoutCompat, image: AppCompatImageView) {
        button.setOnClickListener {
            if(processingPosition != null) {
                return@setOnClickListener
            }
            if(review.userVote == true) {
                return@setOnClickListener
            }
            if(onAllowedLikeClick == null) {
                return@setOnClickListener
            }
            processingPosition = position
            val review = getItem(processingPosition!!)
            review.isLikeProcessing = true
            onAllowedLikeClick!!(review.id)
            notifyItemChanged(processingPosition!!)
        }
    }

    private fun setDislikeButtonListener(position: Int, review: Review, button: LinearLayoutCompat, image: AppCompatImageView) {
        button.setOnClickListener {
            if(processingPosition != null) {
                return@setOnClickListener
            }
            if(review.userVote == false) {
                return@setOnClickListener
            }
            if(onAllowedDislikeClick == null) {
                return@setOnClickListener
            }
            processingPosition = position
            val review = getItem(processingPosition!!)
            review.isDislikeProcessing = true
            onAllowedDislikeClick!!(review.id)
            notifyItemChanged(processingPosition!!)
        }
    }

    private fun setDislikeIndicator(review: Review, image: AppCompatImageView, counter: TextView) {
        if(review.isDislikeProcessing) {
            image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_loading))
            return
        }
        if(review.userVote == false) {
            image.setColorFilter(ContextCompat.getColor(context, R.color.dislike_red))
            counter.setTextColor(ContextCompat.getColor(context, R.color.dislike_red))
            return
        }
        image.clearColorFilter()
        counter.setTextColor(ContextCompat.getColor(context, R.color.default_text_view_color))
    }

    private fun setLikeIndicator(review: Review, image: AppCompatImageView, counter: TextView) {
        if(review.isLikeProcessing) {
            image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_loading))
            return
        }
        if(review.userVote == true) {
            image.setColorFilter(ContextCompat.getColor(context, R.color.like_green))
            counter.setTextColor(ContextCompat.getColor(context, R.color.like_green))
            return
        }
        image.clearColorFilter()
        counter.setTextColor(ContextCompat.getColor(context, R.color.default_text_view_color))
    }

    private fun setIsPositiveIndicator(isPositive: Boolean, image: AppCompatImageView) {
        if(isPositive) {
            image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_like))
            image.setColorFilter(ContextCompat.getColor(context, R.color.like_green))
            return
        }
        image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_dislike))
        image.setColorFilter(ContextCompat.getColor(context, R.color.dislike_red))

    }

    private fun loadAvatar(imageUrl : String?, image : ImageView) {
        imageUrl?.let { photoPath ->
            picasso
                .load(photoPath)
                .placeholder(R.drawable.asset_loading)
                .into(image)
        } ?: run {
            picasso
                .load(R.drawable.asset_no_image_available)
                .into(image)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.containsSameDataAs(newItem)
        }
    }

    class ReviewVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorAvatarIV: AppCompatImageView = itemView.findViewById(R.id.author_avatar_IV)
        val authorFulNameTV: TextView = itemView.findViewById(R.id.author_full_name_TV)
        val publicationDateTV: TextView = itemView.findViewById(R.id.publication_date_TV)

        val isPositiveIV: AppCompatImageView = itemView.findViewById(R.id.is_positive_IV)

        val likeBT: LinearLayoutCompat = itemView.findViewById(R.id.like_wrapper)
        val likeIV: AppCompatImageView = itemView.findViewById(R.id.like_IV)
        val likeCounterTV: TextView = itemView.findViewById(R.id.like_counter_TV)

        val dislikeBT: LinearLayoutCompat = itemView.findViewById(R.id.dislike_wrapper)
        val dislikeIV: AppCompatImageView = itemView.findViewById(R.id.dislike_IV)
        val dislikeCounterTV: TextView = itemView.findViewById(R.id.dislike_counter_TV)

        val commentContentTV: TextView = itemView.findViewById(R.id.comment_content_TV)
    }

    fun setOnAllowedLikeClickListener(action : (reviewId: String) -> Unit) {
        this.onAllowedLikeClick = action
    }

    fun setOnAllowedDislikeClickListener(action : (reviewId: String) -> Unit) {
        this.onAllowedDislikeClick = action
    }
}
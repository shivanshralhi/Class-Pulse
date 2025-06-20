package Functions

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory


class InAppReview {
    companion object {
        fun showInAppReviewDialog(context: Context) {
            val reviewManager = ReviewManagerFactory.create(context)
            val request: Task<ReviewInfo> = reviewManager.requestReviewFlow()

            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    // Launch the in-app review flow
                    reviewManager.launchReviewFlow(context as Activity, reviewInfo)
                        .addOnCompleteListener {
                            Log.d("InAppReview", "In-app review flow completed successfully")
                        }
                }
                else {
                    Log.d("InAppReview", "Error Launching In-app review")
                }
            }
        }
    }
}
package Functions

import Activities.MainActivity
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat.startActivityForResult
import com.collegetracker.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.content.Context
import androidx.core.content.ContextCompat.getString


class GoogleAuthentication(context: Context) {

    lateinit var auth: FirebaseAuth

    fun googleAuth(context: Context){
        auth=FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        val user = auth.currentUser
        if(user==null){
            googleSignInClient.signOut()
            (context as Activity).startActivityForResult(googleSignInClient.signInIntent, 888)
        }
        else{
            val builder = AlertDialog.Builder(context)
                .setTitle("Logout")
                .setIcon(R.drawable.baseline_logout_24)
                .setMessage("Do you want to Logout ?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    try {

                        Toast.makeText(context, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        (context as Activity).finish()


                    } catch (e: Exception) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        Log.w("crash-attendance", e)
                    }

                }.setNegativeButton("No")
                { dialogInterface, i ->
                    TODO()
                }
            builder.show()
        }
    }



    fun firebaseAuthWithGoogle(idToken: String, context: Context) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Signed in Successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    context.finish()
                } else {
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_LONG).show()
                    Log.d("CollegeTracker", task.exception?.localizedMessage ?: "Unknown error")
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show()
                Log.d("CollegeTracker", it.localizedMessage ?: "Unknown failure")
            }
    }
}
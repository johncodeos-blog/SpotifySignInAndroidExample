package com.example.spotifysigninexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {


    private var spotifyAccessToken: String? = null
    private var spotifyAccessCode: String? = null
    private var mCall: Call? = null
    private val mOkHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spotify_login_btn.setOnClickListener {
            val request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN)
            AuthenticationClient.openLoginActivity(
                this,
                SpotifyConstants.AUTH_TOKEN_REQUEST_CODE,
                request
            )
        }

    }

    private fun getAuthenticationRequest(type: AuthenticationResponse.Type): AuthenticationRequest {
        return AuthenticationRequest.Builder(
            SpotifyConstants.CLIENT_ID,
            type,
            SpotifyConstants.REDIRECT_URI
        )
            .setShowDialog(false)
            .setScopes(arrayOf("user-read-email"))
            .build()
    }


    private fun fetchSpotifyUserProfile() {
        Log.d("Status: ", "Please Wait...")
        if (spotifyAccessToken == null) {
            Log.i("Status: ", "Something went wrong - No Access Token found")
            return
        }

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer " + spotifyAccessToken!!)
            .build()

        cancelCall()
        mCall = mOkHttpClient.newCall(request)

        mCall!!.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Status: ", "Failed to fetch data: $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {
                try {
                    val jsonObject = JSONObject(response.body!!.string())
                    val spotifyId = jsonObject.getString("id")
                    Log.d("Spotify Id :", spotifyId)
                    val spotifyDisplayName = jsonObject.getString("display_name")
                    Log.d("Spotify Display Name :", spotifyDisplayName)
                    val spotifyEmail = jsonObject.getString("email")
                    Log.d("Spotify Email :", spotifyEmail)
                    val spotifyProfilePic = jsonObject.getJSONArray("images")
                    //Check if user has Avatar
                    var spotifyPicURL = ""
                    if (spotifyProfilePic.length() > 0) {
                        spotifyPicURL = spotifyProfilePic.getJSONObject(0).getString("url")
                        Log.d("Spotify Avatar :", spotifyPicURL)
                    }
                    val accessToken = spotifyAccessToken
                    Log.d("Spotify AccessToken :", accessToken ?: "")

                    val myIntent = Intent(this@MainActivity, DetailsActivity::class.java)
                    myIntent.putExtra("spotify_id", spotifyId)
                    myIntent.putExtra("spotify_display_name", spotifyDisplayName)
                    myIntent.putExtra("spotify_email", spotifyEmail)
                    myIntent.putExtra("spotify_avatar", spotifyPicURL)
                    myIntent.putExtra("spotify_access_token", accessToken)
                    startActivity(myIntent)
                    Log.d("Status: ", "Success get all JSON ${jsonObject.toString(3)}")
                } catch (e: JSONException) {
                    Log.d("Status: ", "Failed to parse data: $e")
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (SpotifyConstants.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            spotifyAccessToken = response.accessToken
            fetchSpotifyUserProfile()
        }
    }


    override fun onDestroy() {
        cancelCall()
        super.onDestroy()
    }

    private fun cancelCall() {
        if (mCall != null) {
            mCall!!.cancel()
        }
    }

}

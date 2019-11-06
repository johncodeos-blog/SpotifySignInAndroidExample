package com.example.spotifysigninexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_details.*


class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val spotifyId = intent.getStringExtra("spotify_id")
        val spotifyDisplayName = intent.getStringExtra("spotify_display_name")
        val spotifyEmail = intent.getStringExtra("spotify_email")
        val spotifyAvatarURL = intent.getStringExtra("spotify_avatar")
        val spotifyAccessToken = intent.getStringExtra("spotify_access_token")

        spotify_id_textview.text = spotifyId
        spotify_displayname_textview.text = spotifyDisplayName
        spotify_email_textview.text = spotifyEmail
        if (spotifyAvatarURL == "") {
            spotify_avatar_url_textview.text = "Not Exist"
        }else {
            spotify_avatar_url_textview.text = spotifyAvatarURL
        }
        spotify_access_token_textview.text = spotifyAccessToken
    }
}

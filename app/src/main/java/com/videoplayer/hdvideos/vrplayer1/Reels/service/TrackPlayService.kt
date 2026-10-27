package com.videoplayer.hdvideos.vrplayer1.Reels.service

import android.os.Bundle
import android.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class TrackPlayService : MediaSessionService() {

    companion object {
        const val TAG = "TrackPlayService"
        const val CMD_GET_AUDIO_SESSION_ID = "get_audio_session_id"
        const val CMD_PAUSE_AUDIO = "pause_audio"
        const val CMD_RESUME_AUDIO = "resume_audio"
        const val CMD_STOP_AUDIO = "stop_audio"
        const val CMD_GET_PLAYBACK_STATE = "CMD_GET_PLAYBACK_STATE"

    }

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()

//        val pendingIntent = NavDeepLinkBuilder(this)
//            .setComponentName(MainActivity::class.java)
//            .setGraph(R.navigation.app_navigation)
//            .setDestination(R.id.nowPlayingFragment)
//            .setArguments(NowPlayingFragmentArgs(null, false).toBundle())
//            .createPendingIntent()

        val callback = object : MediaSession.Callback {

            @androidx.annotation.OptIn(UnstableApi::class)
            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ): MediaSession.ConnectionResult {
                val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                    .buildUpon()
                    .add(SessionCommand(CMD_GET_AUDIO_SESSION_ID, Bundle.EMPTY))
                    .add(SessionCommand(CMD_GET_PLAYBACK_STATE, Bundle.EMPTY))
                    .add(SessionCommand(CMD_PAUSE_AUDIO, Bundle.EMPTY))
                    .add(SessionCommand(CMD_RESUME_AUDIO, Bundle.EMPTY))
                    .add(SessionCommand(CMD_STOP_AUDIO, Bundle.EMPTY))
                    .build()

                return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(sessionCommands)
                    .build()
                /*val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                    .buildUpon()
                    .add(SessionCommand(CMD_GET_AUDIO_SESSION_ID, Bundle.EMPTY))
                    .build()
                return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(sessionCommands)
                    .build()*/
            }

            @androidx.annotation.OptIn(UnstableApi::class)
            @OptIn(UnstableApi::class)
            override fun onCustomCommand(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
                customCommand: SessionCommand,
                args: Bundle
            ): ListenableFuture<SessionResult> {
                val action = customCommand.customAction
                Log.d(TAG, "Received customCommand: $action")

                return try {
                    when (action) {
                        CMD_GET_AUDIO_SESSION_ID -> {
                            val sessionId = player.audioSessionId
                            val resultBundle = Bundle().apply {
                                putInt("audioSessionId", sessionId)
                            }
                            Futures.immediateFuture(
                                SessionResult(
                                    SessionResult.RESULT_SUCCESS,
                                    resultBundle
                                )
                            )
                        }

                        CMD_PAUSE_AUDIO -> {
                            Log.d(TAG, "Pausing via MediaSession.player")
                            // Use session.player to ensure session state & notification are updated
                            session.player.pause()
                            Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                        }

                        CMD_RESUME_AUDIO -> {
                            Log.d(TAG, "Resuming via MediaSession.player")
                            session.player.play()
                            Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                        }

                        CMD_STOP_AUDIO -> {
                            Log.d(TAG, "Stopping via MediaSession.player")
                            session.player.stop()
                            Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                        }

                        CMD_GET_PLAYBACK_STATE -> {
                            val stateBundle = Bundle().apply {
                                putBoolean("isPlaying", player.isPlaying)
                            }
                            Futures.immediateFuture(
                                SessionResult(
                                    SessionResult.RESULT_SUCCESS,
                                    stateBundle
                                )
                            )
                        }


                        else -> super.onCustomCommand(session, controller, customCommand, args)

                    }
                } catch (e: Exception) {
                    Log.e(TAG, "onCustomCommand failed for $action", e)
                    Futures.immediateFuture(SessionResult(SessionError.ERROR_NOT_SUPPORTED))
                }

            }
        }

//        mediaSession = MediaSession.Builder(this, player)
//            .setSessionActivity(pendingIntent)
//            .setCallback(callback)
//            .build()

        Log.d(TAG, "MediaSession created")
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        try {
            mediaSession.run {
                player.release()
                release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing session/player", e)
        }
        super.onDestroy()
    }
}
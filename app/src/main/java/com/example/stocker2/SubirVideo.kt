package com.example.stocker2

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.VideoView
import com.example.stocker2.databinding.ActivitySubirVideoBinding


class SubirVideo : AppCompatActivity() {
    private lateinit var binding:ActivitySubirVideoBinding
    private lateinit var btn_atras: ImageView
    private var mVideoView: VideoView? = null

    private var pos: Int = 0
    companion object {
        // Controla que haya una reproducción en proceso
        var isPlaying = false
        // Controla el estado de pausa
        var isPaused = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXML();
        if (mVideoView == null) {
            mVideoView = binding.videoView
            mVideoView!!.setOnCompletionListener { pararReproduccion() }
            mVideoView!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.video))
        }
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btn_atras = findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            // Si es el primer onCreate, configuramos los botones y la reproducción inicial
            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.pauseButton.isEnabled = false

            binding.retrButton.isEnabled = false
            binding.avanButton.isEnabled = false

            controlVideo()
        } else {
            if (isPlaying) {

                binding.playButton.isEnabled = false
                binding.stopButton.isEnabled = true
                binding.pauseButton.isEnabled = true

                binding.retrButton.isEnabled = true
                binding.avanButton.isEnabled = true
            } else {

                binding.playButton.isEnabled = true
                binding.stopButton.isEnabled = false
                binding.pauseButton.isEnabled = false

                binding.retrButton.isEnabled = false
                binding.avanButton.isEnabled = false
            }
        }
    }
    private fun crearObjetosDelXML(){
        binding=ActivitySubirVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun controlVideo() {
        binding.playButton.setOnClickListener {
            isPlaying = true
            cargarMultimedia()
        }

        binding.stopButton.setOnClickListener {
            isPlaying = false
            pararReproduccion()
        }

        binding.pauseButton.setOnClickListener {
            if (mVideoView != null) {
                if (mVideoView!!.isPlaying) {
                    isPaused = true
                    mVideoView!!.pause()
                } else {
                    isPaused = false
                    mVideoView!!.start()
                }
            }
        }

        binding.retrButton.setOnClickListener {
            retrocederReproduccion(10000)
        }

        binding.avanButton.setOnClickListener {
            avanzarReproduccion(10000)
        }
    }
    private fun cargarMultimedia() {

        if (mVideoView == null) {
            mVideoView = binding.videoView
            mVideoView!!.setOnCompletionListener { pararReproduccion() }
            mVideoView!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.video))
        }

        mVideoView!!.start()

        binding.playButton.isEnabled = false
        binding.stopButton.isEnabled = true
        binding.pauseButton.isEnabled = true
        binding.retrButton.isEnabled = true
        binding.avanButton.isEnabled = true
    }

    private fun retrocederReproduccion(millis: Int) {
        if (mVideoView != null) {
            val newPosition = mVideoView!!.currentPosition - millis
            mVideoView!!.seekTo(newPosition.coerceIn(0, mVideoView!!.duration))
        }
    }

    private fun avanzarReproduccion(millis: Int) {
        if (mVideoView != null) {
            val newPosition = mVideoView!!.currentPosition + millis
            mVideoView!!.seekTo(newPosition.coerceIn(0, mVideoView!!.duration))
        }
    }



    private fun pararReproduccion() {
        if (mVideoView != null) {
            mVideoView!!.pause()
            pos = 0
            mVideoView!!.seekTo(pos)

            mVideoView = null

            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.pauseButton.isEnabled = false
            binding.retrButton.isEnabled = false
            binding.avanButton.isEnabled = false
        }
    }

    override fun onPause() {
        super.onPause()


        if (mVideoView != null) {
            pos = mVideoView!!.currentPosition
            mVideoView!!.pause()
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)


        if (mVideoView != null) {
            bundle.putInt("posicion", pos)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mVideoView != null) {
            mVideoView = null
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle!!)

        if (bundle != null) {
            pos = bundle.getInt("posicion")

        } else {

        }
    }

    override fun onResume(){
        super.onResume()


        if (mVideoView != null) {


            if (isPlaying && !isPaused) {
                mVideoView!!.start()
            }

            mVideoView!!.seekTo(pos)

            controlVideo()
        }
    }

}
package edu.juanageitos.fandom

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import edu.juanageitos.fandom.databinding.ActivityDetailBinding
import edu.juanageitos.fandom.model.Fandom
import edu.juanageitos.fandom.utils.fandomMutableList
import java.math.BigInteger
import java.security.MessageDigest

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var fandom: Fandom

    /**
     * Al crear la actividad se coge un fandom mediante EXTRA_POSITION pasado desde el main activity que indica la posición
     * del fandom en la lista, con el objeto ponemos la información en la vista, creamos los eventos on click, la imagen desde una url, etc.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        this.fandom = fandomMutableList[intent.getIntExtra(MainActivity.EXTRA_POSITION, 0)]

        Glide.with(this)
            .load(fandom.image)
            .override(300, 300)
            .into(binding.imageView)

        /**
         * Apply del binding para ahorrar código
         */
        this.binding.apply {
            tvDescription.text = fandom.description
            tvMore.text = getString(R.string.txt_more_info, fandom.info)
            ivFav.setImageState(intArrayOf(R.attr.state_fav_on), fandom.fav)

            ivFav.setOnClickListener {
                fandom.fav = !fandom.fav
                intent.apply {
                    putExtra(MainActivity.EXTRA_VALUE, fandom.fav)
                    putExtra(MainActivity.EXTRA_POSITION, fandomMutableList.indexOf(fandom))
                }
                ivFav.setImageState(intArrayOf(R.attr.state_fav_on), fandom.fav)
            }

            tvMore.setOnClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(fandom.info)).apply {
                        startActivity(this)
                }
            }

            materialToolbar.setNavigationOnClickListener {
                intentReturnParsed()
            }
        }
    }

    /**
     * Cuando el usuario quiere retroceder devolvemos un resultado a la actividad principal
     */
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            intentReturnParsed()
        }
    }

    /**
     * Declaramos que el resultado del intent ha sido OK y terminado la actividad, retrocediendo a la anterior
     */
    private fun intentReturnParsed() {
        setResult(RESULT_OK, intent)
        finish()
    }
}
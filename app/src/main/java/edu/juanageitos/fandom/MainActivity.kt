package edu.juanageitos.fandom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.juanageitos.fandom.adapters.FandomAdapter
import edu.juanageitos.fandom.databinding.ActivityMainBinding
import edu.juanageitos.fandom.model.Fandom
import edu.juanageitos.fandom.utils.deleteFilesOptions
import edu.juanageitos.fandom.utils.fandomMutableList
import edu.juanageitos.fandom.utils.readRawFile
import edu.juanageitos.fandom.utils.updateFilesOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fandomAdapter: FandomAdapter
    private lateinit var fandomList: MutableList<Fandom>

    companion object {
        const val EXTRA_VALUE = "EXTRA_VALUE"
        const val EXTRA_POSITION = "EXTRA_POSITION"
    }

    /**
     * Creamos un resgistro para el resultado de llamar la actividad SeconActivity
     *
     *  Al obtener un resultado si hay datos coge la posición del fandom y cambia su estado
     *  del favorito al valor devuelto
     */
    private var resultadoActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.apply {
                getBooleanExtra(EXTRA_VALUE, false).let {
                    setFav(getIntExtra(EXTRA_POSITION, 0), it)
                }
            }
        }

    /**
     * Lanzamos la segunda actividad enviandole como datos la posición del fandom a mostrar
     */
    private fun callSecondActivity(pos: Int) {
        resultadoActivity.launch(Intent(
            this, SecondActivity::class.java
        ).apply {
            putExtra(EXTRA_POSITION, pos)
        })
    }

    /**
     * Se muestra un snackbar, el cliente tiene la opción de eliminar ese fandom, al darle click
     * se cambia la visibilidad, se notifica que se ha eliminado y se toma en cuenta guardando la visibilidad
     * en un fichero
     */
    private fun removeVisibleFandom(pos: Int) {
        Snackbar.make(
            binding.root,
            getString(R.string.txt_delete, fandomMutableList[pos].name),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.txt_doit)) {
            fandomMutableList[pos].visible = false
            updateFilesOptions(binding.root.context, R.string.filenameDeleted)

            fandomList.removeAt(pos)

            fandomAdapter.notifyItemRemoved(pos)
        }.show()
    }

    /**
     * Alternar el valor del favorito de un fandom mediante su posicion
     */
    private fun switchFav(pos: Int) {
        setFav(pos, !fandomMutableList[pos].fav)
    }

    /**
     * Establecer el valor del favorito de un fandom mediante su posición y se actualizan los datos en los ficheros
     */
    private fun setFav(pos:Int, value:Boolean){
        fandomMutableList[pos].fav = value
        fandomAdapter.notifyItemChanged(pos)
        updateFilesOptions(binding.root.context, R.string.filenameFavs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fandomList = readRawFile(binding.root.context)

        fandomAdapter = FandomAdapter(fandomList, {
            switchFav(it)
        }, {
            removeVisibleFandom(it)
        }, {
            callSecondActivity(it)
        })

        /**
         * Apply del binding para ahorrar código
         */
        binding.apply {
            swipeRefresh.setOnRefreshListener {
                deleteFilesOptions(root.context)
                readRawFile(root.context)

                fandomList.apply {
                    clear()
                    addAll(readRawFile(root.context))
                }

                fandomAdapter.notifyDataSetChanged()
                swipeRefresh.isRefreshing = false
            }

            listFandoms.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(root.context)
                adapter = fandomAdapter
            }
        }
    }
}
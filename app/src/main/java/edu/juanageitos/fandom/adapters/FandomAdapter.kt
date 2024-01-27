package edu.juanageitos.fandom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.juanageitos.fandom.R
import edu.juanageitos.fandom.databinding.ItemFandomBinding
import edu.juanageitos.fandom.model.Fandom

class FandomAdapter(
    private val fandomList: MutableList<Fandom>,
    private val listenerFav: (pos:Int) -> Unit,
    private val delFandom: (pos:Int) -> Unit,
    private val showFandom:(pos:Int) -> Unit
): RecyclerView.Adapter<FandomAdapter.FanViewHolder>() {

    /**
     * Crear un ViewHolder por cada fandom
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FanViewHolder {
        return FanViewHolder(ItemFandomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    /**
     * Tamaño del listado de fandoms cargados en el adapter
     */
    override fun getItemCount() = this.fandomList.size

    /**
     * Cuando se tiene que bindear cada item, asi le podemos pasar los valores que queramos
     */
    override fun onBindViewHolder(holder: FanViewHolder, position: Int) {
        holder.bind(this.fandomList[position])
    }

    inner class FanViewHolder(view : View):RecyclerView.ViewHolder(view) {
        private val bind = ItemFandomBinding.bind(view)
        private lateinit var fandom : Fandom

        /**
         * Establecer los valores en la vista item_fandom, cargar la imagen desde una url
         * y crear los listeners en los elementos
         */
        fun bind(fandom : Fandom){
            this.fandom = fandom


            /**
             * Uso del apply en el bind para ahorrar codigo
             */
            this.bind.apply{
                txtName.text = fandom.name
                txtUniverse.text = fandom.universe

                ivFav.setOnClickListener{
                    listenerFav(adapterPosition)
                }

                ivFav.setImageState(intArrayOf(R.attr.state_fav_on), fandom.fav)

            }

            Glide.with(itemView)
                .load(fandom.image)
                .circleCrop()
                .override(300, 300)
                .into(this.bind.ivPreview)

            itemView.setOnLongClickListener{
                delFandom(adapterPosition)
                true
            }

            itemView.setOnClickListener{
                showFandom(adapterPosition)
            }

        }
    }
}
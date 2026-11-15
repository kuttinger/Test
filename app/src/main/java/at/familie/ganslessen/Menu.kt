package at.familie.ganslessen

import androidx.annotation.DrawableRes

data class MenuItem(
    val id: String,
    val name: String,
    val hint: String,
    @DrawableRes val image: Int,
)

data class Step(
    val title: String,
    val subtitle: String,
    val items: List<MenuItem>,
)

object GansMenu {
    val fleisch = listOf(
        MenuItem("bein", "Bein", "Saftig und knusprig", R.drawable.img_bein),
        MenuItem("fluegel", "Flügel", "Für Knusper-Fans", R.drawable.img_fluegel),
        MenuItem("brust", "Brust", "Zart aufgeschnitten", R.drawable.img_brust),
    )
    val beilagen = listOf(
        MenuItem("speckfisolen", "Speckfisolen", "Im Speckmantel", R.drawable.img_speckfisolen),
        MenuItem("knoedel", "Knödel", "Flaumig und rund", R.drawable.img_knoedel),
        MenuItem("rotkraut", "Rotkraut", "Mit Apfel und Zimt", R.drawable.img_rotkraut),
        MenuItem("karotten", "Karotten", "Glasiert", R.drawable.img_karotten),
        MenuItem("kroketten", "Kroketten", "Goldbraun", R.drawable.img_kroketten),
    )
    val extras = listOf(
        MenuItem("bratapfel", "Bratapfel", "Mit Preiselbeeren", R.drawable.img_bratapfel),
        MenuItem("orangen", "Orangen", "Frisch und fruchtig", R.drawable.img_orangen),
        MenuItem("sauce", "Sauce", "Ohne Sauce geht nix!", R.drawable.img_sauce),
    )

    val steps = listOf(
        Step("Fleisch", "Welche Stücke von der Gans magst du?", fleisch),
        Step("Beilagen", "Was darf zur Gans dazu?", beilagen),
        Step("Extras", "Das Tüpfelchen auf dem i", extras),
    )

    val allItems = fleisch + beilagen + extras
    fun byId(id: String) = allItems.first { it.id == id }
}

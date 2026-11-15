package at.familie.ganslessen

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject

data class Bestellung(val name: String, val mengen: Map<String, Int>)

class GansViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("ganslessen", Context.MODE_PRIVATE)

    /** Alle Personen mit ihrer gespeicherten Auswahl (bleibt auch nach App-Neustart erhalten). */
    val bestellungen = mutableStateListOf<Bestellung>().apply { addAll(laden()) }

    /** null = Namensauswahl/Übersicht, 1..n = Auswahl-Schritte, n+1 = Zusammenfassung */
    var schritt by mutableStateOf<Int?>(null)
        private set
    var name by mutableStateOf("")
        private set
    var mengen by mutableStateOf(mapOf<String, Int>())
        private set

    val zusammenfassungsSchritt get() = GansMenu.steps.size + 1

    private fun index(n: String) = bestellungen.indexOfFirst { it.name.equals(n.trim(), ignoreCase = true) }

    fun hatAuswahl(b: Bestellung) = b.mengen.values.any { it > 0 }

    /** Name gewählt → gespeicherte Auswahl laden (oder leer beginnen) und los geht's. */
    fun starte(neuerName: String) {
        val n = neuerName.trim()
        if (n.isEmpty()) return
        val i = index(n)
        if (i >= 0) {
            name = bestellungen[i].name
            mengen = bestellungen[i].mengen
        } else {
            name = n
            mengen = emptyMap()
            bestellungen.add(Bestellung(n, emptyMap()))
            speichernAufHandy()
        }
        schritt = 1
    }

    fun menge(id: String) = mengen[id] ?: 0

    fun aendern(id: String, delta: Int) {
        val neu = (menge(id) + delta).coerceIn(0, 9)
        mengen = if (neu == 0) mengen - id else mengen + (id to neu)
        zwischenspeichern()
    }

    fun weiter() {
        val s = schritt ?: return
        if (s < zusammenfassungsSchritt) schritt = s + 1
    }

    fun zurueck() {
        val s = schritt ?: return
        schritt = if (s <= 1) null else s - 1
    }

    fun gehZu(s: Int) {
        schritt = s
    }

    /** Jede Änderung wird sofort unter dem Namen gespeichert. */
    private fun zwischenspeichern() {
        val i = index(name)
        val b = Bestellung(name, mengen)
        if (i >= 0) bestellungen[i] = b else bestellungen.add(b)
        speichernAufHandy()
    }

    fun fertig() {
        zwischenspeichern()
        schritt = null
    }

    fun loeschen(b: Bestellung) {
        bestellungen.remove(b)
        speichernAufHandy()
    }

    /** Auswahl aller zurücksetzen, Namen bleiben (z. B. fürs nächste Jahr). */
    fun auswahlZuruecksetzen() {
        for (i in bestellungen.indices) bestellungen[i] = bestellungen[i].copy(mengen = emptyMap())
        speichernAufHandy()
    }

    fun gesamt(): List<Pair<MenuItem, Int>> = GansMenu.allItems.mapNotNull { item ->
        val summe = bestellungen.sumOf { it.mengen[item.id] ?: 0 }
        if (summe > 0) item to summe else null
    }

    private fun speichernAufHandy() {
        val arr = JSONArray()
        bestellungen.forEach { b ->
            val m = JSONObject()
            b.mengen.forEach { (k, v) -> m.put(k, v) }
            arr.put(JSONObject().put("name", b.name).put("mengen", m))
        }
        prefs.edit().putString("bestellungen", arr.toString()).apply()
    }

    private fun laden(): List<Bestellung> = try {
        val arr = JSONArray(prefs.getString("bestellungen", "[]"))
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val m = o.getJSONObject("mengen")
            Bestellung(o.getString("name"), m.keys().asSequence().associateWith { m.getInt(it) })
        }
    } catch (e: Exception) {
        emptyList()
    }
}

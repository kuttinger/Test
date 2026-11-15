package at.familie.ganslessen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class Bestellung(val name: String, val mengen: Map<String, Int>)

class GansViewModel : ViewModel() {
    val bestellungen = mutableStateListOf<Bestellung>()

    /** null = Übersicht, 0 = Name, 1..n = Auswahl-Schritte, n+1 = Zusammenfassung */
    var schritt by mutableStateOf<Int?>(null)
        private set
    var name by mutableStateOf("")
    var mengen by mutableStateOf(mapOf<String, Int>())
        private set
    private var bearbeiteIndex: Int? = null

    val anzahlAuswahlSchritte get() = GansMenu.steps.size
    val zusammenfassungsSchritt get() = anzahlAuswahlSchritte + 1

    fun neueBestellung() {
        bearbeiteIndex = null
        name = ""
        mengen = emptyMap()
        schritt = 0
    }

    fun bearbeiten(index: Int) {
        val b = bestellungen[index]
        bearbeiteIndex = index
        name = b.name
        mengen = b.mengen
        schritt = 0
    }

    fun menge(id: String) = mengen[id] ?: 0

    fun aendern(id: String, delta: Int) {
        val neu = (menge(id) + delta).coerceIn(0, 9)
        mengen = if (neu == 0) mengen - id else mengen + (id to neu)
    }

    fun weiter() {
        val s = schritt ?: return
        if (s < zusammenfassungsSchritt) schritt = s + 1
    }

    fun zurueck() {
        val s = schritt ?: return
        schritt = if (s == 0) null else s - 1
    }

    fun gehZu(s: Int) {
        schritt = s
    }

    fun speichern() {
        val b = Bestellung(name.trim().ifEmpty { "Gast ${bestellungen.size + 1}" }, mengen)
        val idx = bearbeiteIndex
        if (idx != null && idx in bestellungen.indices) bestellungen[idx] = b else bestellungen.add(b)
        bearbeiteIndex = null
        schritt = null
    }

    fun abbrechen() {
        bearbeiteIndex = null
        schritt = null
    }

    fun loeschen(index: Int) {
        if (index in bestellungen.indices) bestellungen.removeAt(index)
    }

    fun allesLoeschen() = bestellungen.clear()

    fun gesamt(): List<Pair<MenuItem, Int>> = GansMenu.allItems.mapNotNull { item ->
        val summe = bestellungen.sumOf { it.mengen[item.id] ?: 0 }
        if (summe > 0) item to summe else null
    }
}

package at.familie.ganslessen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val Weinrot = Color(0xFF7A1F1F)
private val Gold = Color(0xFFE3A63B)
private val Creme = Color(0xFFFFF8F0)

private val Farben = lightColorScheme(
    primary = Weinrot,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF6DADA),
    onPrimaryContainer = Weinrot,
    secondary = Color(0xFF8E4519),
    secondaryContainer = Color(0xFFFBE3C8),
    onSecondaryContainer = Color(0xFF4A2208),
    background = Creme,
    surface = Creme,
    surfaceVariant = Color(0xFFF3E9DC),
)

@Composable
fun GansApp(vm: GansViewModel = viewModel()) {
    MaterialTheme(colorScheme = Farben) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val s = vm.schritt
            when {
                s == null -> UebersichtScreen(vm)
                else -> {
                    BackHandler { vm.zurueck() }
                    WizardScreen(vm, s)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Kopf(titel: String) {
    TopAppBar(
        title = { Text(titel, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Weinrot,
            titleContentColor = Color.White,
        ),
    )
}

/* ---------------- Übersicht ---------------- */

@Composable
private fun UebersichtScreen(vm: GansViewModel) {
    var frageAllesLoeschen by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { Kopf("Ganslessen") },
        bottomBar = {
            Box(Modifier.padding(16.dp)) {
                Button(
                    onClick = { vm.neueBestellung() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) { Text("+  Neue Bestellung", fontSize = 18.sp) }
            }
        },
    ) { innen ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(innen).fillMaxSize(),
        ) {
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painterResource(R.drawable.img_gans), null,
                        Modifier.size(160.dp),
                    )
                    Text(
                        "Wer isst was?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Weinrot,
                    )
                    Text(
                        if (vm.bestellungen.isEmpty())
                            "Tippe unten auf „Neue Bestellung“ und stell dir Schritt für Schritt deinen Ganslteller zusammen."
                        else "${vm.bestellungen.size} Teller bestellt",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            itemsIndexed(vm.bestellungen) { index, b ->
                BestellungKarte(b, onEdit = { vm.bearbeiten(index) }, onDelete = { vm.loeschen(index) })
            }

            val gesamt = vm.gesamt()
            if (gesamt.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Einkaufs- & Kochliste (gesamt)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            gesamt.forEach { (item, n) -> PostenZeile(item, n) }
                        }
                    }
                }
                item {
                    TextButton(onClick = { frageAllesLoeschen = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Alle Bestellungen löschen")
                    }
                }
            }
        }
    }

    if (frageAllesLoeschen) {
        AlertDialog(
            onDismissRequest = { frageAllesLoeschen = false },
            title = { Text("Alles löschen?") },
            text = { Text("Alle Bestellungen werden entfernt.") },
            confirmButton = {
                TextButton(onClick = { vm.allesLoeschen(); frageAllesLoeschen = false }) { Text("Löschen") }
            },
            dismissButton = { TextButton(onClick = { frageAllesLoeschen = false }) { Text("Abbrechen") } },
        )
    }
}

@Composable
private fun BestellungKarte(b: Bestellung, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(Weinrot),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(b.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Text(b.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = onEdit) { Text("Ändern") }
                TextButton(onClick = onDelete) { Text("✕") }
            }
            Spacer(Modifier.height(4.dp))
            if (b.mengen.isEmpty()) {
                Text("Nichts ausgewählt", color = Color.Gray)
            } else {
                Text(
                    GansMenu.allItems.filter { (b.mengen[it.id] ?: 0) > 0 }
                        .joinToString(" · ") { "${b.mengen[it.id]}× ${it.name}" },
                )
            }
        }
    }
}

@Composable
private fun PostenZeile(item: MenuItem, n: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
    ) {
        Image(painterResource(item.image), null, Modifier.size(36.dp))
        Spacer(Modifier.width(10.dp))
        Text(item.name, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Text("${n}×", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

/* ---------------- Wizard ---------------- */

@Composable
private fun WizardScreen(vm: GansViewModel, schritt: Int) {
    val gesamtSchritte = vm.zusammenfassungsSchritt + 1
    val titel = when (schritt) {
        0 -> "Wer bestellt?"
        vm.zusammenfassungsSchritt -> "Dein Teller"
        else -> GansMenu.steps[schritt - 1].title
    }
    Scaffold(
        topBar = {
            Column {
                Kopf("Schritt ${schritt + 1} von $gesamtSchritte · $titel")
                LinearProgressIndicator(
                    progress = { (schritt + 1f) / gesamtSchritte },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = Gold,
                    trackColor = Color(0xFFF3E9DC),
                )
            }
        },
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp).imePadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { vm.zurueck() },
                    modifier = Modifier.weight(1f).height(56.dp),
                ) { Text(if (schritt == 0) "Abbrechen" else "‹ Zurück") }
                if (schritt == vm.zusammenfassungsSchritt) {
                    Button(
                        onClick = { vm.speichern() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F7F24)),
                    ) { Text("✓ Bestellen", fontSize = 17.sp) }
                } else {
                    Button(
                        onClick = { vm.weiter() },
                        modifier = Modifier.weight(1f).height(56.dp),
                    ) { Text("Weiter ›", fontSize = 17.sp) }
                }
            }
        },
    ) { innen ->
        Box(Modifier.padding(innen).fillMaxSize()) {
            when (schritt) {
                0 -> NameSchritt(vm)
                vm.zusammenfassungsSchritt -> Zusammenfassung(vm)
                else -> AuswahlSchritt(vm, GansMenu.steps[schritt - 1])
            }
        }
    }
}

@Composable
private fun NameSchritt(vm: GansViewModel) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painterResource(R.drawable.img_gans), null, Modifier.size(200.dp))
        Text(
            "Für wen ist der Teller?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Weinrot,
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = vm.name,
            onValueChange = { vm.name = it },
            label = { Text("Name") },
            placeholder = { Text("z. B. Oma, Papa, Lena …") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AuswahlSchritt(vm: GansViewModel, step: Step) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                step.subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = Weinrot,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        items(step.items, key = { it.id }) { item ->
            AuswahlKarte(item, vm.menge(item.id), onChange = { vm.aendern(item.id, it) })
        }
    }
}

@Composable
private fun AuswahlKarte(item: MenuItem, menge: Int, onChange: (Int) -> Unit) {
    val gewaehlt = menge > 0
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (gewaehlt) MaterialTheme.colorScheme.secondaryContainer else Color.White,
        ),
        elevation = CardDefaults.cardElevation(if (gewaehlt) 6.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (gewaehlt) Modifier.border(3.dp, Gold, RoundedCornerShape(20.dp)) else Modifier)
            .clickable { if (!gewaehlt) onChange(1) },
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Image(
                    painterResource(item.image), item.name,
                    Modifier.fillMaxWidth().aspectRatio(1f),
                )
                if (gewaehlt) {
                    Box(
                        Modifier.align(Alignment.TopEnd).size(34.dp).clip(CircleShape).background(Weinrot),
                        contentAlignment = Alignment.Center,
                    ) { Text("$menge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                }
            }
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(item.hint, fontSize = 13.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalButton(
                    onClick = { onChange(-1) },
                    enabled = gewaehlt,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(44.dp),
                ) { Text("−", fontSize = 22.sp) }
                Text(
                    "$menge",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(40.dp),
                )
                FilledTonalButton(
                    onClick = { onChange(1) },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(44.dp),
                ) { Text("+", fontSize = 22.sp) }
            }
        }
    }
}

@Composable
private fun Zusammenfassung(vm: GansViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text(
                "Mahlzeit, ${vm.name.trim().ifEmpty { "Gast" }}!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Weinrot,
            )
            Text("Bitte kontrollieren – tippe auf einen Abschnitt, um ihn zu ändern.")
        }
        GansMenu.steps.forEachIndexed { i, step ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { vm.gehZu(i + 1) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(step.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                            Text("Ändern ›", color = Weinrot)
                        }
                        HorizontalDivider(Modifier.padding(vertical = 6.dp))
                        val gewaehlt = step.items.filter { vm.menge(it.id) > 0 }
                        if (gewaehlt.isEmpty()) Text("— nichts —", color = Color.Gray)
                        gewaehlt.forEach { PostenZeile(it, vm.menge(it.id)) }
                    }
                }
            }
        }
    }
}

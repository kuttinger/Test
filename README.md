# 🦢 Ganslessen – Android App

Schritt-für-Schritt-Auswahl fürs Familien-Gansessen, mit Bildern.

**Ablauf pro Person:**
1. Auf der Startseite den eigenen Namen antippen oder einen neuen eingeben.
   Die Auswahl wird unter dem Namen dauerhaft am Handy gespeichert.
2. Fleisch: Bein, Flügel, Brust
3. Beilagen: Speckfisolen, Knödel, Rotkraut, Karotten, Kroketten
4. Extras: Bratapfel, Orangen, Sauce
5. Zusammenfassung → Speichern

Auf der Startseite gibt es alle Bestellungen der Familie und eine **Gesamtliste**,
also z. B. 4× Bein, 9× Knödel. Damit weiß die Küche genau, was gebraucht wird.

## App aufs Handy bringen
1. Auf GitHub unter **Releases** die neueste `Ganslessen.apk` am Handy herunterladen.
2. Die Datei öffnen. Android fragt, ob der Browser „Apps aus unbekannten Quellen installieren“ darf → erlauben.
3. Installieren, fertig.

Jeder Push baut die APK automatisch neu (GitHub Actions, `.github/workflows/build.yml`).

## Selbst bauen
Mit Android Studio öffnen oder `./gradlew assembleRelease` ausführen (Android SDK nötig).

# DHBW Karlsruhe, Vorlesung Programmieren I+II

## Programmentwurf Teil II

**Montag, 12.10.2026** · Bearbeitungszeit Teil II: 90 Minuten

## Aufgabe

Schreiben Sie eine Java-Anwendung `JTide`, die basierend auf Gezeiten-Daten (Hoch- und Niedrigwasser, d.h. Flut und Ebbe) den optimalen Zeitpunkt für eine Wattwanderung bzw. einen Schwimmausflug an einer bestimmten Stelle der deutschen Nordseeküste ermittelt.

## Teilaufgabe a) [17%]

Schreiben Sie zunächst eine Klasse `Location` zur Repräsentation eines Orts, für den später eine Aktivität geplant werden kann. Eine `Location` besteht aus einer ID (`id`) und einem Namen (`name`, beides Zeichenketten) sowie geografischen Koordinaten in Form von Breitengrad (`lat`) und Längengrad (`lon`, beides Gleitkommawerte).

Definieren Sie einen geeigneten Konstruktor basierend auf der Verwendung in der bereitgestellten `parseLocation`-Methode der Klasse `JTide`.

Sorgen Sie dafür, dass die `toString`-Methode der Klasse den Namen und die ID (Muster: `"$name ($id)"`) zurückliefert für die spätere Anzeige.

Definieren Sie nun einen *komplexen Aufzählungstyp* `TideType`, welcher die Gezeitenzustände Hoch- bzw. Niedrigwasser beschreibt. Jeder Aufzählungstyp speichert zusätzlich zur Konstanten noch ein Anzeige-Label und die Aktivität, die zu diesem Zeitpunkt durchgeführt werden kann (`label`, `activity`, beides Zeichenketten).

Ebenso werden Zeitangaben gespeichert von wann bis wann die Aktivität relativ zum Zustand durchgeführt werden kann (`from`, `to`, beides ganze Zahlen). Bspw. kann 2,5 Stunden (9.000 Sekunden) *vor* Niedrigwasser mit der Wattwanderung begonnen werden, 1 Stunde *nach* Niedrigwasser (3.600 Sekunden) sollte man wieder zurück sein.

Folgende Werte sind für die Gezeitenzustände zu definieren:

| TideType | Anzeige-Label | Aktivität     | Von    | Bis    |
|----------|---------------|---------------|--------|--------|
| `HIGH`   | Hochwasser    | Schwimmen     | 3.600  | 10.800 |
| `LOW`    | Niedrigwasser | Wattwanderung | -9.000 | 3.600  |

> *Hinweis:* Die Konstanten des Aufzählungstyps müssen **exakt** so (`HIGH`, `LOW`) benannt werden, da sie für das Einlesen aus der Datei bzw. der Dummy-Daten (vgl. Teilaufgabe d)) verwendet werden!

Zur Abbildung der Gezeiten-Werte realisieren Sie nun die Klasse `TideValue`. Ein Wert besteht aus der ID der Location (`locationId`), dem Zeitpunkt des Ereignisses (`time`), dem Gezeitenzustand (`type`) und dem Wasserstand in Meter zu diesem Zeitpunkt (`value`). Die Datentypen können Sie aus der Verwendung in der bereitgestellten Methode `parseTideValue` der Klasse `JTide` ableiten, ebenso den notwendigen Konstruktor!

## Teilaufgabe b) [18%]

Die grafische Benutzeroberfläche für das Programm wird ihnen – noch ohne Interaktivität – in der Klasse `JTideTerm` zur Verfügung gestellt. Die Namen der jeweiligen UI-Elemente sind im Screenshot vermerkt. Hinweise zur Nutzung finden Sie am Ende der Aufgabenstellung!

Prüfen Sie zunächst im Konstruktor von `JTideTerm`, ob mindestens ein Ort übergeben wurde. Sollte dies nicht der Fall sein, ist eine selbst zu schreibende `LocationRequiredException` zu werfen, der eine sprechende Nachricht (`"At least one location is required!"`) zu übergeben ist.

Sorgen Sie zunächst dafür, dass bei Auswahl eines Orts die Angaben zu Breiten- und Längengrad an die Kartendarstellung (`map`) übergeben werden, damit der Punkt den entsprechenden Ort in der Karte darstellt (*siehe Hinweise am Ende*).

Zusätzlich soll auch bereits ohne Interaktion direkt beim Starten des Programms die Markierung für den ersten Ort, den auch die Auswahlbox automatisch anzeigt, in der Karte gesetzt sein!

> **Screenshot:** Fenster „JTide - Watt willste!?“ mit den UI-Elementen:
> - `cbxLoc` – Auswahlbox Ort
> - `dateFrom`, `dateTo` – Datumsauswahl
> - `rbtnWadden` (Wattwanderung), `rbtnSwim` (Schwimmen) – Radio-Buttons
> - `infoContainer` – großer Bereich links
> - `map` – Karte rechts
> - `btnOk` – OK-Button
>
> Beispielinhalt `infoContainer`:
>
> ```
> Amrum Odde, Amrum
>
> Niedrigwasser mit 3.96m um 12.10.2026 09:19
> Wattwanderung: 06:49 bis 10:19
>
> Niedrigwasser mit 4.01m um 12.10.2026 21:43
> Wattwanderung: 19:13 bis 22:43
>
> Niedrigwasser mit 3.95m um 13.10.2026 09:53
> Wattwanderung: 07:23 bis 10:53
>
> Niedrigwasser mit 4.05m um 13.10.2026 22:12
> Wattwanderung: 19:42 bis 23:12
> ```

Beim Drücken des „OK“-Buttons sollen nun (wie im Screenshot dargestellt), im großen `infoContainer`-Bereich Informationen zur Freizeitaktivität im angegebenen Zeitraum angezeigt werden.

Den Beginn des relevanten Zeitraums liefert Ihnen der `DatePicker dateFrom`, das Ende des Zeitraums entsprechend `dateTo` (*beachten Sie die Hinweise am Ende*). Sollte der Zeitraum ungültig sein, also das Enddatum vor dem Startdatum liegen, zeigen Sie eine Fehlermeldung an, welche über diesen Umstand informiert. Ist der Zeitraum gültig wird der Inhalt des Info-Bereichs generiert.

Der Ort wird über die `JComboBox cbxLoc` bestimmt. Welche Aktivität geplant werden soll erkennen Sie daran, welcher der beiden Radio-Buttons (`rbtnWadden` & `rbtnSwim`) selektiert ist. Welcher `TideType` zu welchem Button gehört entnehmen Sie der obigen Tabelle!

Anhand dieser Informationen müssen Sie nun aus der übergebenen Liste der Gezeiten-Werte (`values`) sämtliche Werte bestimmen, die

- zum Ort gehören,
- den passenden Gezeiten-Typ haben
- und deren Zeitpunkt innerhalb des gewählten Zeitraums liegt.

Zeigen Sie im Info-Bereich zunächst den Namen der Station an, anschließend jeweils getrennt durch eine leere Zeile die Informationen zu allen Werten. (*Leerzeile und Formatierung: siehe Hinweise*)

- Erste Zeile des Werts: Gezeiten-Typ, Wasserstand, Datum und Uhrzeit
- Zweite Zeile des Werts: Name der Aktivität, beste Start- und Endzeit – leiten Sie diese aus den Eigenschaften des Gezeitentyps ab!

> *Hinweis: die Aktionen des „OK“-Buttons werden in den Teilaufgaben c) und e) noch erweitert.*

## Teilaufgabe c) [8%]

Für die Analyse des Nutzungsverhaltens in der App sollen nun alle angezeigten Anfragen protokolliert werden.

Erweitern Sie Ihre Logik beim Drücken des „OK“-Buttons so, dass alle für die Anfrage relevanten Parameter in eine Datei `infolog.txt` geschrieben werden:
Das sind der aktuelle Zeitpunkt beim Drücken des Buttons, der Ort, die Aktivität sowie Start- und End-Datum. Eine Zeile könnte also bspw. so aussehen (*Formatierung: siehe Hinweise*):

```
30.09.2026 20:59: Amrum Odde, Amrum -> Wattwanderung von 12.10.2026 bis 13.10.2026
```

Sollte die Datei bereits existieren, sind weitere Inhalte an das Ende der Datei anzuhängen. Fehler beim Schreiben der Datei sind abzufangen, müssen aber nicht weiter behandelt werden.

## Teilaufgabe d) [12%]

Aktuell sind in den Methoden der Klasse `JTide`, welche die Ortsdaten und Messwerte bereitstellen, nur einige wenige Dummy-Daten statisch hinterlegt. Erweitern Sie diese Methoden nun so, dass die bereitgestellten Textdateien eingelesen werden und deren Daten die Dummy-Daten ersetzen!

Für die Methode `readLocations` lesen Sie die Datei `locations.txt` zeilenweise ein. Jede Zeile muss dabei analog zu den Dummy-Daten mit der `parseLocation`-Methode in ein entsprechendes Objekt umgewandelt und der Ergebnisliste hinzugefügt werden. Sortieren Sie die Orte vor dem Zurückgeben nach Namen!

Ähnliches soll nun bei der Methode `readValues` gemacht werden. Die Daten für die Gezeiten-Werte lesen Sie zeilenweise aus der Datei `values.txt`. Die `TideValue`-Objekte erzeugt hier die bereitgestellte Methode `parseTideValue` aus dem Inhalt der einzelnen Zeilen. Auch hier ersetzen die eingelesenen Werte die Dummy-Daten. Eine Sortierung ist hier nicht notwendig.

## Teilaufgabe e) [5%]

Erweitern Sie die Aktion beim „OK“-Button nun so, dass die Information nur für 10 Sekunden angezeigt und danach aus dem Info-Bereich entfernt wird (*Entfernen: siehe Hinweise!*). Die 10 Sekunden sollen als Countdown im Titel des Fensters heruntergezählt werden:

> **Screenshot:** Fenstertitel `JTide - Watt willste!? [9]`

Diese Aktion soll nebenläufig ablaufen! Die Nebenläufigkeit endet, wenn die 10 Sekunden abgelaufen und der Info-Bereich geleert ist, oder wenn der OK-Button vor Ablauf der 10 Sekunden erneut gedrückt wird. Der Countdown startet dann natürlich von neuem.

---

## Allgemeine Hinweise

### Starten

Starten Sie die Anwendung mit der gegebenen Klasse `JTide` (siehe USB-Stick/Download).

### Sichtbarkeit von Instanz-Attributen

Sämtliche Instanz-Attribute sind als <u>privat</u> zu definieren und von außerhalb der Klasse ggf. mittels Getter- und/oder Setter-Methoden zu verwenden.

## Hinweise zu bereitgestellten Klassen und Methoden in JTideHelpers

### Karten-Anzeige für die ausgewählte Station (rechter Bereich)

Den roten Punkt können Sie mit der Methode `map.setMarker( double lat, double lon )` setzen. Breitengrad (`lat`) und Längengrad (`lon`) müssen in der richtigen Reihenfolge angegeben werden (ist dies nicht der Fall erscheint ein Warnhinweis in der Ausgabe).

### Datumsauswahl

Um den Zeitstempel für den ausgewählten Tag zu erhalten, nutzen Sie die Methode `getSelectedAsTimestamp` des `DatePicker`. `dateFrom` wird Ihnen dabei den Zeitstempel zu Beginn des Tages (0 Uhr) und `dateTo` den Zeitstempel am Ende des ausgewählten Tages (23:59:59 Uhr) liefern – Sie können also direkt damit weiterrechnen!

### Formatieren von Zeitstempeln

Zur formatierten Ausgabe von Zeitstempeln stellt Ihnen die Klasse `JTide` drei statische Methoden bereit:

- `JTide.formatDateTime(long timestamp)`: liefert bspw. `"12.10.2026, 09:00"`
- `JTide.formatDate(long timestamp)`: liefert bspw. `"12.10.2026"`
- `JTide.formatTime(long timestamp)`: liefert bspw. `"09:00"`

### InfoContainer für die Ausgabe der gefundenen Aktivitäten (großer Bereich auf der linken Seite)

Die Klasse `InfoContainer` stellt drei Methoden für Sie bereit:

- `infoContainer.add("Abc")`: Hinzufügen einer Zeichenkette (hier: „Abc“) als Zeile
- `infoContainer.addEmptyLine()`: Fügt eine leere Zeile hinzu
- `infoContainer.clear()`: Löscht alle Zeilen aus der Anzeige

---

## Hinweis zur Bewertung

- 25% der Gesamtpunkte werden für den bereits bearbeiteten Teil I des Programmentwurfs vergeben.
- 15% der Gesamtpunkte werden nach Funktionstests Ihrer Lösung für Teil II vergeben.
- 60% der Gesamtpunkte werden anhand des Schlüssels, der an den Teilaufgaben von Teil II steht, auf Basis des Quellcodes vergeben.

*Quelle für Gezeiten-Daten: Bundesamt für Seeschifffahrt und Hydrographie*

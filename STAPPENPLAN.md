# Stappenplan wandelapp (versie 1)

Dit stappenplan volg je fase per fase met Claude Code. De **wat** (scope, kleuren, OSM-selectie, statistieken) staat in `README.md`; dit document beschrijft het **hoe en in welke volgorde**.

**Uitgangspunt: wandelen staat centraal.** Daarom komen opnemen en wandelstatistieken vroeg: na fase 5 heb je al een bruikbare app om mee te gaan wandelen. Kaartlagen, GR-paden, GPX-import en zoeken bouwen daarop verder.

## Hoe je elke fase aanpakt

1. Geef Claude Code de **opdracht** van de fase (staat onderaan elke fase).
2. Laat eerst een **plan** maken en keur het goed.
3. Laat het **stap voor stap** bouwen; installeer na elke stap op je telefoon en probeer het.
4. Werkt het? Laat Claude Code **committen** en de fase afvinken in `README.md`.
5. Ga pas naar de volgende fase als alle punten onder **Klaar als** kloppen.

Loopt iets vast: beschrijf aan Claude Code wat je ziet (foutmelding, schermafbeelding, wat je verwachtte) in plaats van zelf in de code te zoeken.

## Overzicht

| Fase | Onderwerp | Schatting |
|---|---|---|
| 0 | Voorbereiding (tools, telefoon) | 2–4 u |
| 1 | Project opzetten | 2–4 u |
| 2 | Offline basiskaart en kaartstijl | 5–9 u |
| 3 | Positie op de kaart | 2–3 u |
| 4 | Wandeling opnemen | 5–10 u |
| 5 | GPS-filters en wandelstatistieken (live) | 6–10 u |
| 6 | Reliëf, hoogtelijnen en hoogtedata | 6–12 u |
| 7 | Hoogtemeters, hoogteprofiel en calorieën | 4–8 u |
| 8 | Tochtenoverzicht en samenvatting | 4–7 u |
| 9 | GR-paden | 4–8 u |
| 10 | GPX importeren en volgen | 3–6 u |
| 11 | Zoeken | 4–8 u |
| 12 | Afwerking | 3–6 u |
| | **Totaal** | **± 52–93 u** |

Mijlpalen:
- **Na fase 3**: offline kaart met je positie.
- **Na fase 5**: je kan echt gaan wandelen en je tocht met statistieken opnemen.
- **Na fase 8**: je bewaart en bekijkt al je tochten.
- **Na fase 12**: versie 1 is klaar.

---

## Fase 0 – Voorbereiding

**Doel**: alles klaarzetten om te kunnen bouwen en testen.

**Taken**
- Android Studio installeren (met Android SDK).
- Op Windows: WSL met Ubuntu installeren (voor de data-pipeline).
- Claude Code installeren en aanmelden.
- Git installeren; projectmap `wandelapp/` maken met `README.md`, `CLAUDE.md` en `STAPPENPLAN.md`; `git init`.
- Xiaomi 15 voorbereiden:
  - Ontwikkelaarsopties aanzetten (7× tikken op "OS-versie" in "Over de telefoon").
  - USB-debugging aan; bij Xiaomi ook "Installeren via USB" aan.
- Package-naam kiezen (bv. `be.<jouwnaam>.wandelapp`) en in `README.md` noteren.

**Klaar als**
- Android Studio herkent je telefoon (`adb devices` toont het toestel).
- Claude Code start in de projectmap.

---

## Fase 1 – Project opzetten

**Doel**: een leeg Android-project dat bouwt en een kaart toont.

**Taken (app)**
- Android-project in `app/` met Kotlin, Jetpack Compose, MapLibre Native Android.
- `values/strings.xml` (Engels) en `values-nl/strings.xml` (Nederlands).
- Basisnavigatie met een hoofdscherm (kaart) en een lege instellingenpagina.
- `.gitignore` (inclusief `data-pipeline/output/`).
- Kaart toont tijdelijk een online demostijl.

**Opdracht voor Claude Code**
> Lees README.md, CLAUDE.md en STAPPENPLAN.md. Maak een plan voor fase 1: Android-project in `app/` met Kotlin, Compose en MapLibre, package-naam `<jouw package>`, een kaartscherm met een online demostijl, een lege instellingenpagina, strings in Engels en Nederlands, en een `.gitignore`. Toon eerst het plan.

**Klaar als**
- `./gradlew assembleDebug` slaagt.
- De app start op de telefoon en toont een kaart die je kan verschuiven en zoomen.
- Telefoon in het Nederlands → teksten in het Nederlands; in het Engels → Engels.

**Valkuilen**
- Xiaomi blokkeert soms installatie via USB: "Installeren via USB" moet aan staan (vraagt soms een Xiaomi-account).

---

## Fase 2 – Offline basiskaart en kaartstijl

**Doel**: de kaart van België werkt zonder internet en toont alleen wat een wandelaar nodig heeft.

**Taken (data-pipeline)**
- `config.yaml` met de bbox van België, maximum zoomniveau en paden.
- `01_basemap.sh`: Protomaps-build uitknippen met `pmtiles extract` → `output/basemap.pmtiles`. Eerst testen met een klein gebied.

**Taken (app)**
- Scherm of knop "Kaartbestanden importeren": bestandskiezer, kopieert `.pmtiles` naar app-opslag.
- Kaartstijl lokaal in de app (assets), inclusief **lettertypes en sprites**.
- Kaartstijl aanpassen volgens "Selectie van OSM-data" en "Kleuren en zichtbaarheid" in de README:
  - Paden duidelijk zichtbaar; onverhard anders dan verhard.
  - Look volgens "Kaartstijl (visuele referentie)" in de README en de voorbeelden in `docs/stijlvoorbeeld/`: lichte kleuren, okerbruine paden die opvallen, lichtgele grote wegen, weinig detail.
  - Gebouwen alleen als bebouwde zone.
  - Horeca, winkels, overnachtingen, fietsroutes verborgen.
  - Water lichtblauw; geen donkerblauw of roos in de kaart.
- Naamsvermelding "© OpenStreetMap, Protomaps" zichtbaar op de kaart.
- Het laden van kaartbestanden zo opzetten dat later **meerdere** bestanden (regio's) kunnen.
- **Kaartrichting wisselen** met één knop op de kaart:
  - **Noord boven**: de kaart staat vast met het noorden boven.
  - **Wandelrichting boven**: de kaart draait mee met de richting waarin je kijkt (kompas van de telefoon, geen locatietoestemming nodig).
  - Het knopicoon toont de huidige stand (kompasnaald wijst naar het noorden).
  - Handmatig draaien met twee vingers blijft mogelijk; één tik op de knop zet terug naar noord boven.
  - De gekozen stand wordt onthouden.
  - Kompas afvlakken zodat de kaart niet schokt.

> In fase 3 wordt "wandelrichting" verbeterd: tijdens het wandelen de GPS-looprichting gebruiken, bij stilstand het kompas.

**Opdracht voor Claude Code**
> Fase 2. Maak `data-pipeline/config.yaml` en `01_basemap.sh` om een Protomaps-uitsnede van België te maken (test eerst met een klein gebied). Laat de app een `.pmtiles`-bestand importeren via een bestandskiezer en volledig offline tonen met een lokale stijl, lettertypes en sprites. Pas de stijl aan volgens "Kaartstijl (visuele referentie)", "Selectie van OSM-data" en "Kleuren" in de README; bekijk de voorbeelden in `docs/stijlvoorbeeld/`. Voeg een knop toe om te wisselen tussen "noord boven" en "wandelrichting boven" (kompas, afgevlakt, keuze onthouden). Toon eerst het plan.

**Klaar als**
- Met vliegtuigmodus aan toont de app de kaart van België, met labels.
- Paden vallen op; winkels, horeca en hotels zijn niet zichtbaar.
- Naast een voorbeeld uit `docs/stijlvoorbeeld/` gelegd ziet de kaart er gelijkaardig uit: even rustig, paden even duidelijk.
- App opnieuw starten → kaart is er nog (bestand blijft bewaard).
- Knop wisselt tussen noord boven en wandelrichting boven; bij wandelrichting draait de kaart rustig mee als je met de telefoon ronddraait; labels blijven leesbaar.

**Valkuilen**
- Labels ontbreken offline → lettertypes (glyphs) worden nog online gezocht.
- Controleer dat de MapLibre-versie lokale PMTiles ondersteunt.

---

## Fase 3 – Positie op de kaart

**Doel**: je ziet waar je bent.

**Taken (app)**
- Toestemming voor locatie vragen (precieze locatie), met uitleg waarom.
- Eigen positie tonen met nauwkeurigheidscirkel en kijkrichting (kompas).
- Knop "Centreer op mij" en modus "Volg mij" (kaart volgt je positie).
- Stand "wandelrichting boven" (uit fase 2) verbeteren: tijdens het wandelen de GPS-looprichting gebruiken, bij stilstand het kompas. In "Volg mij" + wandelrichting staat je positie iets onder het midden, zodat je meer ziet van wat voor je ligt.
- Schaal (meter/km) op de kaart.

**Opdracht voor Claude Code**
> Fase 3. Toon de eigen positie op de kaart via `LocationManager` (geen Play Services): toestemming vragen met uitleg, positie met nauwkeurigheidscirkel en kijkrichting, knoppen "Centreer op mij" en "Volg mij", en een schaal. Verbeter de stand "wandelrichting boven": GPS-looprichting tijdens het wandelen, kompas bij stilstand, en de positie iets onder het midden. Toon eerst het plan.

**Klaar als**
- Buiten verschijnt je positie binnen ± 30 seconden, ook in vliegtuigmodus met GPS aan.
- "Volg mij" houdt je positie in beeld tijdens het wandelen.

---

## Fase 4 – Wandeling opnemen

**Doel**: een wandeling betrouwbaar opnemen, ook met het scherm uit.

Zie "Fase 4 – Opnemen op de achtergrond (Xiaomi / HyperOS)" in de README voor alle details.

**Taken (app)**
- Lokale database (Room) met tabellen `hikes` (tocht) en `track_points` (punten: tijd, lat, lon, GPS-hoogte, nauwkeurigheid, snelheid).
- Foreground service van het type `location`, gestart met de knop **Start**.
- Knoppen **Pauze / Hervat** en **Stop**.
- Doorlopende melding met duur en afstand, en een knop Stop.
- Elk punt meteen opslaan; onderbroken opname na herstart hervatten of opslaan.
- Alle ruwe punten bewaren met een vlag (geldig / uitschieter / geschat / voertuig); filters bepalen welke meetellen. Zo kunnen filters later bijgesteld en tochten herberekend worden.
- Opgenomen track live tekenen in **roos** (`#EC407A`), zonder rand.
- Export als GPX en delen via het Android-deelmenu.
- Controle batterijoptimalisatie + hulpscherm met de Xiaomi-instellingen.

**Opdracht voor Claude Code**
> Fase 4. Bouw het opnemen van een wandeling volgens de sectie "Fase 4 – Opnemen op de achtergrond" in de README: Room-database voor tochten en punten, foreground service van het type location, Start/Pauze/Stop, doorlopende melding, punten meteen opslaan, hervatten na afsluiten, filteren van onnauwkeurige punten, live track in roos, GPX-export en het hulpscherm voor Xiaomi. Toon eerst het plan en bouw het in kleine stappen.

**Klaar als**
- Opname van minstens 1 uur met het scherm uit, zonder gaten.
- Opname loopt door bij wisselen naar andere apps.
- Na een gedwongen afsluiting blijft de tocht tot dan bewaard.
- GPX-export opent correct in een andere app of op gpx.studio.

**Valkuilen**
- Xiaomi-instellingen (autostart, batterij "Geen beperkingen") vergeten → opname stopt.
- Melding niet zichtbaar → `POST_NOTIFICATIONS`-toestemming ontbreekt.

---

## Fase 5 – GPS-filters en wandelstatistieken (live)

**Doel**: tijdens het wandelen zie je je belangrijkste cijfers, en die kloppen ook bij slecht GPS-signaal of als je in een auto stapt.

Zie "Wandelstatistieken" en "Bescherming tegen foute GPS-punten en voertuigen" in de README voor de definities.

**Taken (app)**
- **GPS-filters** volgens "Bescherming tegen foute GPS-punten en voertuigen" in de README, als aparte, testbare module (`trackfilter`):
  - Alleen GPS-locaties; starten bij nauwkeurigheid < ± 20 m; punten > ± 25 m negeren.
  - Sprongfilter op schijnbare snelheid (> ± 10 km/h = uitschieter).
  - Na signaalverlies pas hervatten na 3 opeenvolgende goede punten; het tussenstuk alleen meetellen als het plausibel is (≤ ± 7 km/h), anders segment splitsen.
  - **Voertuigdetectie**: > ± 12 km/h langer dan ± 30 s → automatisch pauzeren, melding "Stop opname / Hervat", afstand en tijd niet meetellen; bij terug wandeltempo vragen om te hervatten.
- Berekeningen in een aparte, testbare module (`stats`), los van de UI:
  - Afstand
  - Totale tijd
  - Bewegingstijd
  - Rustpauzes: aantal en totale duur (automatisch + handmatige pauze)
  - Gemiddelde snelheid totaal
  - Gemiddelde wandelsnelheid (alleen bewegingstijd)
  - Huidige snelheid (afgevlakt)
- Unit tests met vaste testtracks:
  - stilstand, rechte lijn, GPS-ruis
  - **signaalverlies met één ver verwijderd vals punt** (afstand mag niet stijgen)
  - **tunnel / dicht bos** (onderbreking met plausibel tussenstuk → geschat meegeteld)
  - **autorit midden in een wandeling** (rit telt niet mee, opname gepauzeerd)
  - korte snelheidspiek van enkele seconden (geen voertuigpauze)
- Statistiekenpaneel onder of over de kaart: compact (afstand, tijd, snelheid), uitklapbaar voor alles.
- Grote, goed leesbare cijfers (buiten, in de zon, tijdens het lopen).

**Opdracht voor Claude Code**
> Fase 5. Bouw eerst de GPS-filters volgens "Bescherming tegen foute GPS-punten en voertuigen" in de README (sprongfilter, hervatten na signaalverlies, voertuigdetectie met automatische pauze en melding) in een aparte module `trackfilter`. Bouw daarna de live wandelstatistieken volgens "Wandelstatistieken": afstand, totale tijd, bewegingstijd, rustpauzes, gemiddelde snelheid totaal en gemiddelde wandelsnelheid, in een module `stats`. Unit tests voor alle testtracks in STAPPENPLAN.md. Toon de cijfers in een compact, uitklapbaar paneel met grote cijfers. Toon eerst het plan.

**Klaar als**
- Unit tests slagen.
- Testwandeling met een bekende afstand: afwijking kleiner dan ± 3–5 %.
- 5 minuten stilstaan (bv. op een bank) telt als rustpauze, niet als wandeltijd, en voegt geen afstand toe.
- Praktijktest: wandelen, dan een stuk met de auto of bus, dan verder wandelen → de rit telt niet mee en de app vraagt om te stoppen of te hervatten.
- Praktijktest: een stuk wandelen met slecht signaal (tunnel, gebouw binnen, dicht bos) → geen plotse sprong in de afstand.

**Valkuilen**
- GPS-ruis bij stilstand laat de afstand oplopen → stilstandsdetectie en minimale verplaatsing gebruiken.
- Te strenge filters schrappen echte wandelstukken → uitgefilterde punten bewaren en in de samenvatting tonen hoeveel er niet meetelde.

---

## Fase 6 – Reliëf, hoogtelijnen en hoogtedata

**Doel**: je ziet het reliëf, en de app kent de hoogte van elk punt.

**Taken (data-pipeline)**
- `02_terrain.sh`: Mapterhorn-hoogtedata voor België uitknippen → `output/terrain.pmtiles`.
- `03_contours.sh`: hoogtedata (Copernicus GLO-30 of Mapterhorn) → `gdal_contour` (interval uit `config.yaml`: standaard 20 m, elke 100 m dikker) → `tippecanoe` → `output/contours.pmtiles`.
- Eerst testen op een klein heuvelachtig gebied (bv. rond Durbuy).

**Taken (app)**
- Laag reliëfschaduw (raster-dem) uit `terrain.pmtiles`.
- Laag hoogtelijnen in grijs (zie "Kaartstijl"), met hoogtelabels op de dikke lijnen (elke 100 m).
- Toppen met driehoekje, naam en hoogte.
- **Huidige hoogte** uit de hoogtedata tonen (plaats op het scherm: zie open punt "Bediening en schermindeling" in de README).
- Lagenmenu: reliëf en hoogtelijnen aan/uit; legende.
- **Hoogte opzoeken** voor een punt uit `terrain.pmtiles` (functie `elevationAt(lat, lon)`), nodig voor fase 7.

**Opdracht voor Claude Code**
> Fase 6. Maak `02_terrain.sh` en `03_contours.sh` volgens de README (test eerst op een klein gebied rond Durbuy). Voeg in de app een reliëflaag en een hoogtelijnenlaag toe, een lagenmenu met legende, en een functie die de hoogte van een punt opzoekt in `terrain.pmtiles`. Toon eerst het plan.

**Klaar als**
- Reliëf en hoogtelijnen zichtbaar, offline, en ze verdwijnen netjes bij uitzetten.
- `elevationAt` geeft voor een bekende top een hoogte die binnen ± 10 m klopt.

**Valkuilen**
- Hoogtelijnen te druk op lage zoom → pas tonen vanaf bv. zoom 12.

---

## Fase 7 – Hoogtemeters, hoogteprofiel en calorieën

**Doel**: stijgen, dalen, een hoogteprofiel en een schatting van je verbruik.

Zie "Wandelstatistieken" in de README voor de rekenwijze.

**Taken (app)**
- Hoogte per trackpunt bepalen via de hoogtedata (fase 6); GPS-hoogte alleen als terugvaloptie.
- Hoogte afvlakken en **hoogtemeters stijgen** en **dalen** berekenen met een drempel (hysterese).
- Hoogste en laagste punt.
- Hoogteprofiel (grafiek hoogte t.o.v. afstand), met je huidige positie erop.
- Instellingen: lichaamsgewicht en optioneel rugzakgewicht (alleen lokaal bewaard).
- Geschat calorieverbruik, live en per tocht.
- Statistiekenpaneel aanvullen met stijgen, dalen en kcal.
- Unit tests met testtracks (vlak, één helling, op en neer).

**Opdracht voor Claude Code**
> Fase 7. Bereken hoogtemeters stijgen en dalen met de hoogtedata uit fase 6, met afvlakking en drempel. Voeg een hoogteprofiel toe en een geschat calorieverbruik op basis van gewicht (instelling) volgens "Wandelstatistieken" in de README. Unit tests voor vlak, één helling en op en neer. Toon eerst het plan.

**Klaar als**
- Vlakke testtrack: stijgen en dalen bijna 0.
- Testwandeling met bekende hoogteverschillen: binnen ± 10–15 %.
- Calorieën verschijnen pas als het gewicht is ingevuld, met de vermelding "schatting".

---

## Fase 8 – Tochtenoverzicht en samenvatting

**Doel**: je tochten terugvinden en bekijken.

**Taken (app)**
- Lijst van tochten: datum, naam, afstand, duur, hoogtemeters; nieuwste bovenaan.
- Tocht een naam geven, verwijderen (met bevestiging), exporteren als GPX.
- Samenvattingsscherm na **Stop** en vanuit de lijst:
  - Alle statistieken (afstand, totale tijd, bewegingstijd, pauzes, snelheden, stijgen, dalen, kcal)
  - "x km niet meegeteld (GPS-sprongen / voertuig)" als er iets gefilterd werd
  - Hoogteprofiel
  - **Overzichtskaart** met de volledige route in roos, start- en eindpunt; niet-meegetelde stukken grijs gestreept (minder belangrijk: laatste stap van deze fase)
- Eventueel totalen: per maand / per jaar (afstand, aantal tochten).

**Opdracht voor Claude Code**
> Fase 8. Bouw een lijst van opgenomen tochten (naam geven, verwijderen, exporteren) en een samenvattingsscherm met alle statistieken en het hoogteprofiel. Als laatste stap: een overzichtskaart met de volledige route in roos en start- en eindpunt. Toon eerst het plan.

**Klaar als**
- Na Stop verschijnt de samenvatting; de tocht staat in de lijst.
- Een oude tocht openen toont dezelfde cijfers als bij het afsluiten.

---

## Fase 9 – GR-paden

**Doel**: GR- en streek-GR-paden van België als laag.

**Taken (data-pipeline)**
- `04_gr_routes.sh`: Geofabrik-extract België downloaden → `osmium tags-filter` op `route=hiking` met `network=iwn/nwn/rwn` → GeoJSON → `tippecanoe` → `output/gr.pmtiles`.
- Wandelknooppunten: alleen de knooppuntnummers meenemen (zie OSM-selectie).

**Taken (app)**
- GR-laag als dunne rood-witte gestreepte lijn, met routenummer (bv. "GR 5") als label.
- Knooppuntnummers tonen vanaf een bepaald zoomniveau.
- Tikken op een GR-pad toont naam en nummer.
- GR-laag en knooppunten aan/uit in het lagenmenu.

**Opdracht voor Claude Code**
> Fase 9. Maak `04_gr_routes.sh` volgens de README (GR- en streek-GR-paden van België uit OSM, plus knooppuntnummers). Toon de laag als dunne rood-witte gestreepte lijn met routenummer, tik voor details, en aan/uit in het lagenmenu. Toon eerst het plan.

**Klaar als**
- Bekende GR-paden (bv. GR 5, GR 129) staan correct op de kaart.
- De GR-laag valt minder op dan de eigen tracks (donkerblauw en roos).

---

## Fase 10 – GPX importeren en volgen

**Doel**: een route uit een andere bron volgen.

**Taken (app)**
- GPX openen via bestandskiezer en via "Openen met" / delen vanuit andere apps.
- Route tonen in **donkerblauw** (`#0D47A1`), zonder rand; start- en eindmarkering.
- Info over de route: afstand, stijgen, dalen (via de hoogtedata), hoogteprofiel, geschatte duur.
- Tijdens het volgen: afgelegd en resterend op de route, je positie op het hoogteprofiel.
- Opname starten terwijl een route getoond wordt: roos ligt over donkerblauw.
- Geïmporteerde routes bewaren in een lijst (los van de opgenomen tochten).

**Opdracht voor Claude Code**
> Fase 10. Voeg GPX-import toe (bestandskiezer en "Openen met"), toon de route in donkerblauw volgens de README, met routeinfo (afstand, stijgen, dalen, hoogteprofiel, geschatte duur) en tijdens het volgen afgelegd/resterend. De opgenomen track in roos ligt erboven. Toon eerst het plan.

**Klaar als**
- Een GPX van groteroutepaden.be of waymarkedtrails.org opent en verschijnt correct.
- Donkerblauw en roos zijn buiten in de zon duidelijk van elkaar en van de kaart te onderscheiden.

---

## Fase 11 – Zoeken

**Doel**: snel naar een plaats of route springen.

**Taken (data-pipeline)**
- `05_search_index.py`: plaatsnamen (stad tot gehucht), toppen, uitzichtpunten en GR-/routenamen uit OSM → SQLite met full-text search → `output/search.db`.

**Taken (app)**
- Zoekbalk met resultaten terwijl je typt, ongevoelig voor hoofdletters en accenten.
- Resultaat aantikken → kaart springt erheen; bij een GR-pad wordt het pad gemarkeerd.

**Opdracht voor Claude Code**
> Fase 11. Maak `05_search_index.py` (plaatsnamen, toppen, uitzichtpunten en routenamen naar SQLite FTS) en een zoekbalk in de app die offline zoekt en de kaart naar het resultaat laat springen. Toon eerst het plan.

**Klaar als**
- "Durbuy", "GR 5" en een klein gehucht worden offline gevonden.

---

## Fase 12 – Afwerking

**Doel**: versie 1 netjes afronden.

**Taken (app)**
- Instellingen: taal (NL/EN, per-app taal), gewicht, eenheden, standaard kaartrichting bij het opstarten, GPS-interval, scherm aan laten tijdens opname.
- Pagina "Over" met naamsvermelding: © OpenStreetMap-bijdragers (ODbL), Protomaps, Copernicus DEM / Mapterhorn, grenzen/regio's indien gebruikt.
- App-icoon en naam.
- Controle: geen hardgecodeerde teksten, alle teksten in NL en EN.
- Lange testwandeling (2–4 uur) met alle functies tegelijk; batterijverbruik noteren.

**Opdracht voor Claude Code**
> Fase 12. Werk de instellingen af (taal, gewicht, eenheden, standaard kaartrichting, GPS-interval, scherm aan), maak de pagina "Over" met alle naamsvermeldingen, een app-icoon, en controleer dat alle teksten in het Nederlands en Engels bestaan. Toon eerst het plan.

**Klaar als**
- Volledige testwandeling zonder problemen.
- Versie 1 getagd in git (`v1.0`).

---

## Daarna

- **Versie 2**: eigen kaart met Planetiler en kaarten per regio downloaden (zie README).
- **Versie 3**: eventueel publicatie in de Play Store (zie README).

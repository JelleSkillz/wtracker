# Wandelapp

Een eenvoudige, offline wandelapp voor Android, voor eigen gebruik.
De app toont een kaart met hoogtelijnen en GR-paden, je positie, en laat je wandelingen opnemen en GPX-routes volgen.

## Doel

Een rustige kaartapp die alleen doet wat je bij het wandelen nodig hebt, volledig offline, zonder tracking.

## Scope versie 1

**In scope**
- Alleen Android, voor eigen gebruik (geen Play Store)
- Talen: Nederlands en Engels, via vertaalbestanden (`strings.xml`), zodat later extra talen makkelijk kunnen
- Offline basiskaart van België (Protomaps, PMTiles)
- Kaartlagen: reliëfschaduw, hoogtelijnen, GR-paden (België), elk aan/uit te zetten
- Eigen positie op de kaart
- Wandeling opnemen en opslaan als GPX
- **Wandelstatistieken**: afstand, totale tijd, bewegingstijd, rustpauzes, gemiddelde snelheid (totaal en wandelen), hoogtemeters stijgen en dalen, geschat calorieverbruik (zie "Wandelstatistieken")
- Overzicht van opgenomen tochten met samenvatting en overzichtskaart (kaart minder belangrijk)
- GPX importeren en tonen op de kaart om te volgen
- Hoogteprofiel van een geïmporteerde of opgenomen route
- Offline zoeken op plaatsnamen en routenamen (bv. "GR 5", "Durbuy")
- Taalkeuze in de instellingen

**Buiten scope (versie 1)**
- Routes plannen, gesproken navigatie
- iOS
- Favorieten / eigen markeringen
- Kaarten downloaden vanuit de app (→ versie 2)
- Publicatie in de Play Store (→ mogelijk versie 3, zie hieronder)

## Versies

| Versie | Inhoud |
|---|---|
| 1 | Alles hierboven, België, kaarten handmatig gekopieerd naar de telefoon |
| 2 | Kaarten **per regio downloaden** vanuit de app, voor heel Europa (zie "Versie 2 – Kaarten per regio") |
| 3 | Mogelijk publicatie in de **Google Play Store** (zie "Versie 3 – Play Store") |

Ook later: waarschuwing als je van de gevolgde route afwijkt, kleuren instelbaar.

**Keuzes die Play Store-publicatie open houden (geldt al vanaf versie 1)**
- Unieke package-naam vanaf de start (bv. `be.<jouwnaam>.wandelapp`); die kan later niet meer veranderen.
- Alleen kaartdata gebruiken die je mag verspreiden: Protomaps, Mapterhorn/Copernicus, eigen bewerkingen van OSM. **Geen** Organic Maps-`.mwm`-bestanden.
- Geen `ACCESS_BACKGROUND_LOCATION`: opname via een foreground service die de gebruiker zelf start.
- Geen tracking, analytics of advertenties: dat maakt het privacybeleid eenvoudig.
- Naamsvermelding van alle bronnen in de app.

## Architectuur

Het project bestaat uit twee delen:

1. **Data-pipeline** (op de computer): scripts die de kaartbestanden maken.
2. **Android-app**: toont die bestanden, offline.

```
Computer (data-pipeline)                     Telefoon (app)
────────────────────────                     ─────────────
Protomaps-build ──► basemap.pmtiles   ─┐
Hoogtedata      ──► terrain.pmtiles   ─┤
Hoogtedata      ──► contours.pmtiles  ─┼──► kopiëren ──► app laadt bestanden
OSM België      ──► gr.pmtiles        ─┤                   uit eigen opslag
OSM België      ──► search.db         ─┘
```

### Technische keuzes

| Onderdeel | Keuze |
|---|---|
| Taal / UI | Kotlin, Jetpack Compose |
| Kaartweergave | MapLibre Native Android |
| Kaartformaat | PMTiles (lokale bestanden) |
| Basiskaart | Protomaps daily build, uitgeknipt voor België |
| Hoogtedata | Copernicus GLO-30 (30 m) of Mapterhorn |
| Hoogtelijnen | GDAL `gdal_contour` → `tippecanoe` → PMTiles |
| GR-paden | OSM-wandelrelaties (`route=hiking`) uit Geofabrik-extract België |
| Zoeken | SQLite met full-text search (FTS), gemaakt door de pipeline |
| Locatie | Android `LocationManager` (geen Google Play Services) |
| Opnemen | Foreground service; punten in een lokale database (Room); export als GPX |
| Statistieken | Aparte module met unit tests; hoogte uit de hoogtedata (`terrain.pmtiles`) |
| Opslag van kaarten | App-opslag; bestanden importeren via bestandskiezer |

### Belangrijk voor offline werking
- Lettertypes (glyphs) en iconen (sprites) van de kaartstijl moeten **in de app zitten**, niet online geladen worden.
- De app mag na installatie **geen internet nodig hebben**. Test altijd in vliegtuigmodus.

## Projectstructuur

```
wandelapp/
├── README.md              dit document
├── CLAUDE.md              werkafspraken voor Claude Code
├── STAPPENPLAN.md         gedetailleerd stappenplan per fase
├── app/                   Android-project (Kotlin, Compose)
├── data-pipeline/
│   ├── config.yaml        gebied (bbox), zoomniveaus, interval hoogtelijnen
│   ├── 01_basemap.sh      Protomaps-uitsnede → basemap.pmtiles
│   ├── 02_terrain.sh      hoogtedata → terrain.pmtiles (reliëf)
│   ├── 03_contours.sh     hoogtedata → contours.pmtiles
│   ├── 04_gr_routes.sh    OSM → gr.pmtiles
│   ├── 05_search_index.py OSM → search.db
│   └── output/            gegenereerde bestanden (niet in git)
└── docs/                  notities, schermontwerpen
    └── stijlvoorbeeld/    schermafbeeldingen die de gewenste kaartstijl tonen
```

## Benodigde tools

**Android**
- Android Studio (met Android SDK)
- Android-telefoon met USB-debugging

**Data-pipeline**
- `pmtiles` CLI (Protomaps)
- `osmium-tool`
- GDAL
- `tippecanoe`
- Python 3

Op Windows: gebruik WSL (Ubuntu), want `tippecanoe` en `osmium` werken het makkelijkst op Linux of macOS.

## Stappenplan

Het gedetailleerde stappenplan, met taken, opdrachten voor Claude Code en testcriteria per fase, staat in **`STAPPENPLAN.md`**. Wandelen staat centraal: opnemen en statistieken komen vroeg.

| Fase | Onderwerp | Pipeline-script |
|---|---|---|
| 0 | Voorbereiding (tools, telefoon) | |
| 1 | Project opzetten | |
| 2 | Offline basiskaart en kaartstijl | `01_basemap.sh` |
| 3 | Positie op de kaart | |
| 4 | Wandeling opnemen | |
| 5 | GPS-filters en wandelstatistieken (live) | |
| 6 | Reliëf, hoogtelijnen en hoogtedata | `02_terrain.sh`, `03_contours.sh` |
| 7 | Hoogtemeters, hoogteprofiel en calorieën | |
| 8 | Tochtenoverzicht en samenvatting | |
| 9 | GR-paden | `04_gr_routes.sh` |
| 10 | GPX importeren en volgen | |
| 11 | Zoeken | `05_search_index.py` |
| 12 | Afwerking | |

## Wandelstatistieken

De berekeningen staan in een aparte module met unit tests en gelden zowel live als voor bewaarde tochten. Waarden hieronder zijn startwaarden en instelbaar in de code.

| Statistiek | Definitie |
|---|---|
| Afstand | Som van de afstanden tussen opeenvolgende geldige punten, na de filters in "Bescherming tegen foute GPS-punten en voertuigen". Tijdens stilstand en voertuigpauze telt verplaatsing niet mee. |
| Totale tijd | Van Start tot Stop (handmatige pauzes niet meegerekend). |
| Bewegingstijd | Tijd waarin je beweegt. |
| Rustpauze | Automatisch: minder dan ± 10 m verplaatsing in 60 s → stilstand. Een stilstand van minstens 2 minuten telt als rustpauze. Ook een handmatige pauzeknop. Toon aantal pauzes en totale pauzeduur. |
| Gem. snelheid totaal | Afstand ÷ totale tijd. |
| Gem. wandelsnelheid | Afstand ÷ bewegingstijd. |
| Huidige snelheid | Afgevlakt over de laatste ± 30 s. |
| Hoogtemeters stijgen / dalen | Hoogte per punt uit de hoogtedata (`terrain.pmtiles`), GPS-hoogte alleen als terugvaloptie. Hoogte afvlakken; een verschil telt pas mee vanaf een drempel van ± 3 m (hysterese). Dezelfde methode voor opgenomen en geïmporteerde routes, zodat ze vergelijkbaar zijn. |
| Hoogste / laagste punt | Uit de afgevlakte hoogte. |
| Geschat calorieverbruik | Op basis van lichaamsgewicht (+ optioneel rugzak) uit de instellingen. Per stuk wandelen: MET-waarde volgens snelheid (± 2,8 bij 3 km/h, 3,5 bij 5 km/h, 5,0 bij 6,5 km/h) × gewicht × uren, plus een toeslag voor stijgen (± gewicht × hoogtemeters × 9,81 ÷ 0,25 ÷ 4184 kcal). Pauzes tellen als rust (MET ± 1,3). Altijd tonen als **schatting**. |
| Geschatte duur (geïmporteerde route) | Op basis van afstand en hoogtemeters, bv. 4 km/h plus 1 uur per 600 hoogtemeters stijgen; later afstemmen op je eigen gemiddelde. |

Gewicht en andere persoonlijke gegevens blijven **alleen op de telefoon**.

### Bescherming tegen foute GPS-punten en voertuigen

Twee veelvoorkomende fouten bij wandelapps moeten voorkomen worden: **sprongen na GPS-verlies** (een ver verwijderd punt doet de afstand plots fors stijgen) en **doormeten in de auto**.

**Ruwe punten filteren**
- Alleen GPS-locaties gebruiken (`GPS_PROVIDER`), geen netwerklocaties (die zijn honderden meters onnauwkeurig).
- Bij de start pas beginnen tellen als de nauwkeurigheid beter is dan ± 20 m.
- Punten met nauwkeurigheid slechter dan ± 25 m negeren.

**GPS-sprongen na signaalverlies**
- Voor elk nieuw punt de **schijnbare snelheid** berekenen t.o.v. het laatste goede punt (afstand ÷ tijd).
- Is die hoger dan ± 10 km/h → het punt is een **uitschieter** en wordt niet gebruikt.
- Na een onderbreking (geen goed punt gedurende ± 15 s of langer): pas weer punten aanvaarden na **3 opeenvolgende goede punten** die onderling kloppen (nauwkeurig, plausibele snelheid). Zo wordt een eenmalig vals punt nooit het nieuwe vertrekpunt.
- Het stuk tussen het laatste goede punt vóór en het eerste goede punt ná de onderbreking:
  - Schijnbare snelheid ≤ ± 7 km/h → telt mee als rechte lijn, gemarkeerd als "geschat" (bv. een tunnel of dicht bos).
  - Hoger → telt **niet** mee voor afstand; de track wordt daar opgesplitst in twee segmenten.
- Uitgefilterde punten worden wel bewaard (met een vlag), zodat de filters later te controleren en bij te stellen zijn.

**Voertuig gedetecteerd (auto, bus, trein)**
- Is de snelheid **langer dan ± 30 s hoger dan ± 12 km/h**, dan pauzeert de opname automatisch: "Hoge snelheid gedetecteerd".
- Afstand en tijd tijdens die periode tellen **niet** mee (niet als wandeltijd en niet als rustpauze).
- Melding op de telefoon met keuze: **Stop opname** of **Hervat**.
- Daalt de snelheid weer naar wandeltempo (± 1 minuut onder ± 7 km/h), dan vraagt de app of de opname hervat moet worden; zonder antwoord blijft ze gepauzeerd.
- Korte pieken (enkele seconden) worden al door de sprongfilter opgevangen en tellen niet als voertuig.

**Zichtbaar voor de gebruiker**
- In de samenvatting: "x km niet meegeteld (GPS-sprongen / voertuig)", zodat duidelijk is wat er gefilterd werd.
- Op de overzichtskaart: niet-meegetelde stukken grijs gestreept, geschatte stukken dunner.

Alle drempels staan op één plek in de code en zijn later instelbaar.

Later: barometer van de telefoon gebruiken voor nauwkeurigere hoogtemeters.

## Kaartstijl (visuele referentie)

De gewenste look staat in `docs/stijlvoorbeeld/` (schermafbeeldingen van een bestaande wandelapp). Kenmerken: **lichte, rustige kleuren, weinig detail, paden springen eruit**. De stijl wordt in MapLibre nagebouwd; kleurcodes zijn gemeten uit de voorbeelden en mogen licht bijgesteld worden.

| Element | Kleur (benadering) | Stijl |
|---|---|---|
| Achtergrond / open terrein | Bijna wit `#F7F6F4` | Vlak |
| Bos | Zacht groen `#ACD0AD` | Vlak, optioneel een subtiel boompatroon |
| Weiden, akkers | Wit tot zeer lichtgroen | Bijna onzichtbaar, geen drukke landbouwkleuren |
| Water (meren) | Lichtblauw `#BEE3FF` | Vlak |
| Beken en rivieren | Iets donkerder lichtblauw | Dunne lijn |
| **Wandelpaden, bospaden, veldwegen** | Okerbruin `#9A6A04` | **Opvallend**: volle lijn, duidelijk dikker dan in een gewone kaart; het belangrijkste element van de kaart |
| Grote wegen | Lichtgeel `#FCDC34` (secundair `#FCEC8C`) met dunne grijze rand | Gewone dikte |
| Kleine wegen en straten | Wit met dunne grijze rand `#B8B8B8` | Dun |
| Bebouwing | Geen losse gebouwen; bebouwde zone hooguit een heel lichte grijstint | |
| Hoogtelijnen | Grijs `#808080`, licht transparant | Dun; interval 20 m, elke 100 m dikker met hoogtelabel |
| Toppen | Zwart driehoekje | Met naam en hoogte (bv. "Monte Musinè 1146") |
| POI's | Spaarzaam: parking (blauwe P), kerk/kapel (kruisje), top (driehoekje) | Klein, alleen vanaf voldoende inzoomen |
| Plaatsnamen | Donkergrijs, schuin (italic) met witte halo | Grotere plaatsen groter en vet |
| Straatnamen | Klein, langs de weg | Alleen bij ver inzoomen |

**Detailniveau**: liever te weinig dan te veel. Pas iets tonen als het bij dat zoomniveau helpt om je te oriënteren. Geen reclame, geen winkels of horeca (zie "Selectie van OSM-data").

**Bediening**: de knoppen en schermindeling uit de voorbeelden worden **niet** overgenomen. De voorbeelden gelden alleen voor de kaart zelf (kleuren, lijnen, detailniveau). Zie "Open punten".

## Kleuren en zichtbaarheid van routes (belangrijke feature)

Een geïmporteerde GPX en de gewandelde (opgenomen) track moeten **duidelijk van elkaar en van de kaart te onderscheiden** zijn, ook buiten in fel zonlicht en voor kleurenblinden.

**Kleurafspraken**

| Laag | Kleur | Stijl |
|---|---|---|
| Geïmporteerde GPX (te volgen) | Donkerblauw `#0D47A1` | Dikke volle lijn (± 6 dp), geen rand, licht transparant (± 85 %) |
| Gewandelde track (opgenomen) | Roos `#EC407A` | Dikke volle lijn (± 5 dp), geen rand, volledig dekkend |
| GR-paden | Rood-wit | Dunne gestreepte lijn, ondergeschikt aan de twee tracks |
| Hoogtelijnen | Grijs | Dunne lijnen, labels klein (zie "Kaartstijl") |
| Basiskaart | Lichte, rustige kleuren (zie "Kaartstijl") | Geen donkerblauw of roos in de kaartstijl; water lichtblauw houden zodat het niet verward wordt met de geïmporteerde GPX |

**Regels**
- Volgorde van lagen (van onder naar boven): basiskaart → reliëf → hoogtelijnen → GR-paden → geïmporteerde GPX → gewandelde track → eigen positie.
- De gewandelde track ligt **boven** de geïmporteerde GPX, zodat je ziet waar je al gelopen hebt.
- Geen randen rond de tracks. Contrast komt van de lijndikte en van de lichte basiskaart.
- Donkerblauw en roos verschillen sterk in helderheid, waardoor ze ook voor de meest voorkomende vormen van kleurenblindheid te onderscheiden blijven.
- Begin- en eindpunt van de geïmporteerde GPX tonen met een eigen markering (bv. groen start-, zwart-wit eindvlagje).
- Een kleine **legende** in het lagenmenu toont welke kleur wat betekent.
- Alle kleuren staan **op één centrale plek** in de code (bv. `MapColors.kt` en de kaartstijl), zodat ze later makkelijk aan te passen zijn.
- Testen: beide tracks tegelijk tonen boven bos, water, bebouwing en hoogtelijnen, op vol schermlicht buiten.

Later: kleuren instelbaar in de instellingen.

## Fase 4 – Opnemen op de achtergrond (Xiaomi / HyperOS)

Xiaomi (HyperOS) sluit achtergrondprocessen agressief af. De opname moet daar tegen bestand zijn.

**In de app**
- Opname via een **foreground service met `foregroundServiceType="location"`**, gestart wanneer de gebruiker op "Opnemen" tikt (app op de voorgrond).
  - Manifest: `FOREGROUND_SERVICE` en `FOREGROUND_SERVICE_LOCATION`, plus `ACCESS_FINE_LOCATION`.
  - Doorlopende melding met de duur en afstand, en een knop "Stop".
  - Vraag `POST_NOTIFICATIONS` (Android 13+) zodat de melding zichtbaar is.
- **Elk punt meteen wegschrijven** (bv. elke paar punten naar een bestand of een lokale database), zodat een tocht niet verloren gaat als het systeem de app afsluit.
- **Hervatten**: na een herstart van de app een onderbroken opname detecteren en aanbieden om verder te gaan of op te slaan.
- **Batterijcontrole**: met `PowerManager.isIgnoringBatteryOptimizations()` nagaan of de app beperkt wordt. Zo ja, een melding tonen met een knop naar de juiste instelling.
- **Hulpscherm** "Opname stopt vanzelf?" met de Xiaomi-instellingen hieronder en een verwijzing naar https://dontkillmyapp.com/xiaomi.
- GPS-interval instelbaar (bv. 1–5 s) om batterij en nauwkeurigheid af te wegen.

**Op de telefoon (Xiaomi 15, HyperOS)**
- Instellingen → Apps → Wandelapp:
  - Autostart: **aan**
  - Batterijbesparing: **Geen beperkingen**
  - "App-activiteit pauzeren als niet gebruikt": **uit**
- App vergrendelen in het overzicht van recente apps (lang drukken → slotje).
- De app **niet wegvegen** uit recente apps tijdens een opname.
- **Na elke HyperOS-update** deze instellingen opnieuw controleren: ze kunnen teruggezet worden.

**Testen (klaar als)**
- Opname van minstens 1 uur met het scherm uit, zonder gaten in de track.
- Opname blijft lopen na wisselen naar andere apps.
- Na een gedwongen afsluiting blijft de opgenomen tocht tot dan bewaard.

## Selectie van OSM-data

OpenStreetMap bevat veel meer dan een wandelaar nodig heeft. De kaart toont alleen wat nuttig is.

**Aanpak**
- **Versie 1**: Protomaps-basiskaart. De selectie hieronder wordt toegepast **in de kaartstijl** (lagen en filters die niet nodig zijn, worden niet getoond).
- **Versie 2**: eigen kaart bouwen met **Planetiler** en een eigen profiel (configuratiebestand in `data-pipeline/`), zodat alleen de geselecteerde data in de bestanden zit. Dat geeft kleinere regiopakketten en een rustigere kaart.

**Selectie**

| Categorie | Keuze | Details |
|---|---|---|
| Paden en veldwegen | Behouden | Alle voetpaden, bospaden, veldwegen, trappen; met ondergrond (`surface`), moeilijkheid (`sac_scale`) en zichtbaarheid (`trail_visibility`). Onverhard duidelijk anders tonen dan verhard. |
| Gewone wegen | Vereenvoudigen | Zoals in "Kaartstijl": grote wegen lichtgeel, kleine wegen wit met dunne grijze rand; alleen namen van grotere wegen. Geen rijstroken, snelheden e.d. |
| Gebouwen | Vereenvoudigen | Geen losse gebouwen; alleen bebouwde zones als vlak. |
| Natuur en landgebruik | Behouden | Bos, heide, weiden, akkers, rotsen, duinen, natuurgebieden. |
| Water | Behouden | Rivieren, beken, meren, bronnen (water lichtblauw, zie "Kleuren"). |
| Wandelvoorzieningen | Behouden | Bankjes, picknicktafels, schuilhutten, drinkwater, toiletten. |
| Uitzicht en reliëf | Behouden | Uitzichtpunten, toppen met hoogte. |
| Bezienswaardigheden | Vereenvoudigen | Alleen erfgoed buiten: kapellen, kruisen, ruïnes, kastelen, monumenten. Geen musea. |
| Bereikbaarheid | Behouden | Parkings, bus- en treinhaltes, stations. |
| Plaatsnamen | Behouden | Tot op het niveau van gehucht. |
| GR- en streek-GR-paden | Behouden | Eigen laag (zie fase 9). |
| Wandelknooppunten | Vereenvoudigen | Alleen de knooppuntnummers, geen verbindingen als aparte laag. |
| Fietsroutes en fietsknooppunten | Weglaten | |
| Horeca (cafés, restaurants) | Weglaten | |
| Winkels | Weglaten | |
| Overnachtingen (hotels, B&B's, campings) | Weglaten | |
| Adressen en huisnummers | Weglaten | Zoeken gebeurt op plaats- en routenamen. |
| Technische details | Weglaten | Stroomleidingen, verkeerslichten, industriële details, enz. |

De selectie staat op één plek (kaartstijl in versie 1, Planetiler-profiel in versie 2), zodat een categorie later makkelijk terug toegevoegd kan worden.

## Versie 2 – Kaarten per regio (naar het voorbeeld van Organic Maps)

**Hoe Organic Maps het doet**
- Een boomstructuur: continent → land → regio (→ deelregio). De app toont die boom; je downloadt alleen de regio's die je nodig hebt.
- Landen worden opgesplitst volgens hun **bestuurlijke indeling** (provincies, departementen, deelstaten). Grote of drukke gebieden worden verder opgesplitst tot bestanden van ruwweg **50–100 MB**.
- Voorbeelden (Organic Maps, stand november 2026):

| Land | Aantal regio's | Indeling |
|---|---|---|
| België | 10 | de 10 provincies (bv. `Belgium_Antwerp`, `Belgium_Liege`) |
| Nederland | 24 | provincies, drukke provincies verder gesplitst (bv. `Netherlands_North Holland_Amsterdam`) |
| Frankrijk | 94 | regio → departement (bv. `France_Brittany_Finistere`) |
| Duitsland | 48 | deelstaat → Regierungsbezirk, soms verder (bv. `Germany_Free State of Bavaria_Upper Bavaria_Munchen`) |
| Zwitserland | 8 | groepen kantons |
| Luxemburg | 1 | heel het land |

- De grenzen van elke regio staan als **`.poly`-tekstbestanden** in de broncode (`data/borders/`, ongeveer 1160 bestanden voor de hele wereld), met de boomstructuur in `data/hierarchy.txt` / `data/countries.json`.
- Deze grensbestanden zijn broncode (Apache 2.0), niet de beschermde binaire kaartbestanden. Ze kunnen dus waarschijnlijk hergebruikt worden mits naamsvermelding volgens `LICENSE`/`NOTICE`. **Eerst nakijken voor gebruik.**

**Aanpak voor deze app**
1. **Regio-indeling** overnemen of afleiden van de Organic Maps-`.poly`-bestanden (eerst Europa), omgezet naar GeoJSON.
2. **Eigen basiskaart** met Planetiler volgens "Selectie van OSM-data" (vervangt de Protomaps-basiskaart).
3. **Pipeline per regio**: voor elke regio met `pmtiles extract --region=<regio>.geojson` een pakket maken:
   - `basemap.pmtiles`, `terrain.pmtiles`, `contours.pmtiles`, `gr.pmtiles`, `search.db`
   - GR-paden en hoogtelijnen op Europees niveau één keer berekenen en daarna per regio uitknippen.
4. **Catalogus** (`catalog.json`): per regio id, naam (NL/EN), bovenliggende regio, grootte, versie/datum, download-URL en controlesom (sha256).
5. **Hosting**: statische bestanden op een goedkope opslagdienst (bv. object storage of GitHub Releases). Kosten van dataverkeer afwegen, zeker bij Play Store-publicatie.
6. **In de app**:
   - Scherm "Kaarten" met de boom (land → regio), grootte, status (gedownload / update beschikbaar).
   - Downloaden op de achtergrond, hervatbaar, met controle op vrije opslag en optie "alleen via wifi".
   - Verwijderen en bijwerken per regio.
   - Optioneel: voorstel "Regio downloaden?" als je naar een gebied zonder kaart zoomt of een GPX importeert.

**Technisch aandachtspunt**: aangrenzende regio's zijn aparte bestanden. Uitzoeken hoe MapLibre die naadloos samen toont (bv. één bron per regio met dezelfde stijllagen, of per land samenvoegen), zonder dubbele labels aan de grenzen.

## Versie 3 – Play Store (optioneel)

Nog niet beslist; de keuzes hierboven houden de mogelijkheid open. Extra werk bij publicatie:
- Ontwikkelaarsaccount bij Google Play en een veilige bewaring van de ondertekeningssleutel.
- Privacybeleid (eenvoudig zolang er geen tracking is).
- In de Play Console het gebruik van de foreground service van het type locatie toelichten (met een korte video van de opnamefunctie).
- Voldoen aan de vereiste target-SDK-versie van Google Play.
- Licenties en naamsvermelding van alle gebruikte data en code nakijken.
- Hostingkosten van de kaartdownloads inschatten bij meer gebruikers.

## Open punten
- **Bediening en schermindeling** (welke knoppen, waar, hoe groot; waar de huidige hoogte en statistieken staan): nog te bepalen. Tot dan bouwt Claude Code eenvoudige, tijdelijke knoppen en vraagt het voordat het de indeling vastlegt.
- Hoogteprofiel bij opgenomen tochten: GPS-hoogte is onnauwkeurig. Eventueel later hoogte opzoeken in de hoogtedata.
- Zoomniveaus en bestandsgrootte afwegen (detail vs. opslag).
- Interval hoogtelijnen: standaard 20 m met elke 100 m een dikkere, gelabelde lijn (instelbaar in `config.yaml`); in vlakke streken eventueel 10 m.

## Naamsvermelding
In de app (bv. onder "Over"):
- © OpenStreetMap-bijdragers (ODbL)
- Protomaps
- Hoogtedata: Copernicus DEM / Mapterhorn

## Status
- [ ] Fase 0 – Voorbereiding
- [ ] Fase 1 – Project opzetten
- [ ] Fase 2 – Offline basiskaart en kaartstijl
- [ ] Fase 3 – Positie op de kaart
- [ ] Fase 4 – Wandeling opnemen
- [ ] Fase 5 – GPS-filters en wandelstatistieken (live)
- [ ] Fase 6 – Reliëf, hoogtelijnen en hoogtedata
- [ ] Fase 7 – Hoogtemeters, hoogteprofiel en calorieën
- [ ] Fase 8 – Tochtenoverzicht en samenvatting
- [ ] Fase 9 – GR-paden
- [ ] Fase 10 – GPX importeren en volgen
- [ ] Fase 11 – Zoeken
- [ ] Fase 12 – Afwerking

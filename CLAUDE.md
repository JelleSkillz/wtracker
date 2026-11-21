# Werkafspraken voor Claude Code

Lees eerst `README.md` (scope, architectuur, kleuren, OSM-selectie, definities van de statistieken) en `STAPPENPLAN.md` (taken en testcriteria per fase).

## Werkwijze
- **Antwoord altijd in het Nederlands** (chat, plannen, uitleg). Code en commentaar blijven in het Engels (zie "Code").
- Werk **per fase** uit `STAPPENPLAN.md`, en binnen een fase **één functie tegelijk**.
- Een fase is pas klaar als alle punten onder "Klaar als" kloppen.
- Berekeningen (statistieken, hoogte, calorieën) in aparte modules met unit tests, los van de UI.
- Begin een fase met een kort plan (welke bestanden, welke stappen) en wacht op akkoord.
- Na elke wijziging: `./gradlew assembleDebug` moet slagen. Draai ook de tests als die er zijn.
- Commit na elke werkende stap, met een duidelijke boodschap.
- Vink in `README.md` onder "Status" een fase af als die klaar is.
- Twijfel je over scope? Kijk naar "Buiten scope" in de README en vraag het eerst.

## Technische regels
- Kotlin en Jetpack Compose. Gebruik de laatste stabiele versies.
- Kaart: MapLibre Native Android. Controleer dat de gebruikte versie lokale PMTiles ondersteunt (`pmtiles://file://...`).
- **Offline eerst**: de app doet na installatie geen netwerkverzoeken. Lettertypes en sprites van de kaartstijl zitten in de app (assets).
- Geen Google Play Services. Locatie via Android `LocationManager`.
- **Geen hardgecodeerde teksten** in de UI: alles via `strings.xml`.
  - `values/strings.xml` = Engels (standaard)
  - `values-nl/strings.xml` = Nederlands
  - Voeg elke nieuwe tekst meteen in beide talen toe.
- Kaartbestanden staan in app-opslag; de gebruiker importeert ze via een bestandskiezer. Geen absolute paden hardcoderen.
- De kaartlook volgt "Kaartstijl (visuele referentie)" in de README; bekijk de afbeeldingen in `docs/stijlvoorbeeld/` voordat je aan de kaartstijl werkt. Liever te weinig detail dan te veel. Neem de knoppen en schermindeling uit die voorbeelden **niet** over: de bediening is nog niet beslist, dus gebruik eenvoudige tijdelijke knoppen en vraag het eerst.
- Kleuren en volgorde van kaartlagen volgen de sectie "Kleuren en zichtbaarheid van routes" in de README. Kleuren alleen op één centrale plek definiëren, nooit los in de code.
- Houd publicatie in de Play Store mogelijk (zie "Keuzes die Play Store-publicatie open houden" in de README): unieke package-naam, alleen verspreidbare data, geen `ACCESS_BACKGROUND_LOCATION`, geen tracking.
- Ontwerp het laden van kaarten zo dat later meerdere regiobestanden naast elkaar kunnen (versie 2): geen vaste aanname van één basemap-bestand.
- Vraag eerst voordat je een grote nieuwe bibliotheek toevoegt.
- Houd het eenvoudig: liever weinig code die werkt dan een uitgebreide architectuur.

## Data-pipeline
- Scripts staan in `data-pipeline/` en lezen hun instellingen uit `config.yaml` (bbox, zoomniveaus, interval hoogtelijnen).
- Scripts moeten herhaalbaar zijn: opnieuw draaien overschrijft de output netjes.
- Gegenereerde bestanden gaan naar `data-pipeline/output/` en staan **niet** in git.
- Test scripts eerst met een klein gebied voordat je heel België draait.

## Code
- Code en commentaar in het Engels; README en documentatie in het Nederlands.
- Kleine, duidelijke bestanden. Eén scherm of functie per bestand waar dat logisch is.

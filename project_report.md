# Echtzeit-Strategie Spiel

## Strategie

### Breiter Pinsel
- möglichst viel Fläche abdecken

### Schmaler Pinsel
- bewegt sich zu Power Up Tropfen

### Dose
- bewegt sich zu Power Up Tropfen

### alle Spieler

- vermeiden Bombe und Tropfen
- decken Möglichst viel Fläche ab
- vermeiden Hindernisse


## Methoden

avoidPU(x, y)
vermeidet Power Ups Bombe und Uhr

seekPU(x, y)
bewegt zu position x, y von Power up

calcDir()
berechnet günstige Bewegungsrichtung

bool isFree(x, y)
ist feld frei?

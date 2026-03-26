# TravelCompanionApp

A simple unit converter Android app built for international travellers. This was made as part of my **SIT708 Mobile Application Development** unit at Deakin University (Trimester 1, 2026).

The idea is pretty straightforward — when you're travelling overseas you constantly need to convert between currencies, fuel/distance units, and temperatures. Instead of juggling three different apps or googling formulas, this puts everything in one clean interface.

---

## Features

- **Three conversion categories** switchable via a spinner: Currency, Fuel & Distance, and Temperature
- **Dynamic unit spinners** — the From and To dropdowns automatically update with the right units when you switch categories
- **Input validation with Toast messages** for all edge cases:
  - Empty or non-numeric input → *"Please enter a valid number"*
  - Same unit selected for both From and To → *"Please select different units"* (returns the input value back)
  - Negative values in Fuel & Distance → *"Fuel and distance values cannot be negative"*
- Results displayed to **4 decimal places** with the unit name appended
- Clean UI with a cream background, white CardViews, coral accent buttons, and plum-coloured labels

---

## Conversion Rates

All rates are fixed (hardcoded for the assignment — no live API calls).

### Currency (via USD as base)

| 1 USD equals | Rate   |
|--------------|--------|
| AUD          | 1.55   |
| EUR          | 0.92   |
| JPY          | 148.50 |
| GBP          | 0.78   |

Every currency conversion goes through USD first. So for example, AUD → EUR would convert AUD → USD → EUR.

### Fuel & Distance

| Conversion              | Rate    |
|-------------------------|---------|
| 1 MPG = KM/L            | 0.425   |
| 1 Gallon (US) = Litres  | 3.785   |
| 1 Nautical Mile = Kilometres | 1.852 |

Available units: MPG, KM/L, Gallon (US), Litre, Nautical Mile, Kilometre

### Temperature

Standard conversion formulas:

```
C → F = (C × 1.8) + 32
F → C = (F - 32) / 1.8
C → K = C + 273.15
K → C = K - 273.15
```

Fahrenheit ↔ Kelvin conversions go through Celsius as an intermediate step.

---

## Screenshots

> Screenshots coming soon.

---

## How to Clone and Run

1. Make sure you have **Android Studio** installed (I used Ladybug or newer, but anything recent should work).

2. Clone the repo:
   ```bash
   git clone https://github.com/your-username/TravelCompanionApp.git
   ```

3. Open Android Studio and select **File → Open**, then navigate to the cloned folder.

4. Let Gradle sync finish (it might take a minute the first time).

5. Run the app on an emulator or a physical device — I tested on a Pixel 7 emulator running API 34, but it should work on most devices.

That's basically it. There are no external dependencies or API keys to worry about since all the conversion rates are hardcoded.

---

## Tech Stack

- **Language:** Kotlin
- **UI:** XML layouts
- **Components:** Spinner, EditText, Button, TextView, CardView (`androidx.cardview`)
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34
- **IDE:** Android Studio

### UI Design

The app uses a warm, travel-inspired colour scheme:

| Element        | Colour    |
|----------------|-----------|
| Background     | `#FFF8F0` (cream/off-white) |
| Cards          | `#FFFFFF` with rounded corners |
| Accent/Buttons | `#FF6B6B` (coral/salmon) |
| Labels         | `#B08BBB` (soft plum) |

Typography is kept large and bold throughout for readability — handy when you're squinting at your phone in a foreign airport.

---

## Author

**Danidu Thejas Karunarathne Hetti Muhandiramge**
Student ID: 224388526
Deakin University — SIT708 Mobile Application Development

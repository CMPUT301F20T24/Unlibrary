# Unlibrary
![CI badge](https://github.com/CMPUT301F20T24/Unlibrary/workflows/Android/badge.svg)

Unlibrary is an app that allows people to borrow books amongst their community instead of using the library.

* [Installation](#installation)
* [Testing](#testing)
* [Documentation](#documentation)
* [Maintainers](#maintainers)

## Installation
Clone this repository and import it into Android Studio.

```
git clone https://github.com/CMPUT301F20T24/Unlibrary.git
```

## Testing

### Prerequisites
- [NodeJS](https://nodejs.org/en/)

### Set-up
```bash
cd firestore-emulator
```

```bash
npm install  # Install firebase CLI
```

### Running unit tests

> :warning: **Why are some tests commented out?**: We implemented Hilt/DI quite late in the project and missed some refactorings and in order to test some of the modules, we needed to refactor production code. Given the amount of time we have left, we decided not to finish some of the unit tests.

#### From CLI
```bash
cd firestore-emulator
npm run test
```

#### From Android Studio
1. In a shell, run the following commands to start the emulator
   
    ```bash
    cd firestore-emulator
    npm run start-emulator
    ```

2. Open the Unlibrary project in Android Studio
3. Right click on the `test` directory (not `androidTest`) in *Project View*, and click `Run`

### Running UI/instrumented tests
With the project open in Android Studio, right click on `androidTest` directory in *Project View* and click `Run`

## Documentation
- Wiki page [here](https://github.com/CMPUT301F20T24/Unlibrary/wiki)
- Generated Javadoc can be found in ./doc/javadoc

## Maintainers
- Armianto Sumitro
- Cyrus Diego
- Taranjot Singh
- Golnoush Hassanzadeh
- Daniel Rojas-Cardona
- Caleb Schoepp

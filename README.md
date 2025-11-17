PROG7312 POE README - WHAT TO PACK (J.A.C.K.D)

INTRODUCTION: What to Pack is a comprehensive mobile application designed to function as a digital assistant for travelers. Its core purpose is to simplify the travel experience by consolidating essential information and planning tools into a single platform. Key functionalities include seamless integration of real-time data (Weather, Currency, Events) with a personalised My Suitcase section for managing trip details and customisable packing lists

CONTENTS

	REQUIREMENTS + PREREQUISITES..........1
	GETTING STARTED.......................2
	USAGE.................................3
	KEY FEATURES..........................4
	NON-FUNCTIONAL REQUIREMENTS...........5
	Offline mode with smart caching.......6
	Multi-language support................7
	Biometrics for secure access..........8
	ARCHITECTURE..........................9
	AI Assistance Disclosure..............10
	FAQs..................................11
	CREDITS...............................12
	GITHUB LINK...........................13
	REFERENCES............................14

1 - REQUIREMENTS + PREREQUISITIES:
REQS (Technologies to be installed)

Android Studio (for Kotlin/Java development)
Node.js and npm (for the Custom Backend API)
Git (for version control)
Firebase Project (for secure authentication)
MongoDB Compass or Atlas CLI (for database setup)
PREREQUISITES (External Setup)

Firebase Configuration:
Create a new Firebase project at firebase.google.com
Enable Authentication -> Email/Password
Enable Firestore Database for storing user and trip data
Custom Node.js API Setup:
The custom backend is built with Node.js and Express.js
It is deployed via Render for high availability.
MongoDB Setup (Currency Data):
A MongoDB database is required to store the list of countries and their currency codes.
The database structure must include a collection named travel_api.countries that holds documents containing the country name, code (e.g., "ZA"), and currency code (e.g., "ZAR").
External API Keys:

API keys are required for external services. Specifically, the ExchangeRate-API key is needed for currency functionality.
These keys must be stored securely as environment variables on the custom backend hosting platform (Render).
Build and Run:

In Android Studio, select your target device or emulator.
Press the Run button to build and deploy the mobile application.

2 - GETTING STARTED

Step-by-Step Setup:

  1. Clone the Repository:

    Bash

		git clone https://github.com/ST10260507/PROG7312_PART-2_J.A.C.K.D.git
		cd PROG7312_PART-2_J.A.C.K.D

  2. Set Up the Mobile App (Android Studio):
	 - Open the project in Android Studio.
	 - Link your Firebase project configuration file (e.g., google-services.json).
	 - Update the base URL for the custom Node.js API to your hosted domain (e.g., https://jackd-api.onrender.com).

  3. Set Up and Deploy the Custom API (Node.js/Express.js):
	 - Navigate to the [Custom API Folder] directory.
	 - Run npm install to install dependencies.
	 - Define all required API keys and the MongoDB Connection String as environment variables in your Render or cloud hosting configuration.
 	 - Deploy the service to make the endpoints accessible to the mobile app.

  4. Populate MongoDB:
   - Insert the necessary country and currency code documents into the travel_api.countries collection in your MongoDB instance.

  5. Build and Run:
 	 - In Android Studio, select your target device or emulator.
	 - Press the Run button to build and deploy the mobile application.
3 - USAGE

Authentication: Swipe right from the splash screen, then Register or Login using your credentials.

Searching: Navigate to MY SEARCH and select a category (Weather, Currency, etc.) to get real-time information for a specified location.

Planning: Navigate to MY SUITCASE to manage Trip Details and create/edit your customizable Packing Lists.

Profile: Manage your account details and preferences in the MY PROFILE screen.

4 - KEY FEATURES

Hybrid Authentication: Uses Firebase for secure user login and account management.

Smart Currency Conversion: The custom API retrieves country names and currency codes from MongoDB for the drop-down menus and uses the ExchangeRate-API to calculate the current exchange rate, performing the final calculation on the API before sending the value back to the app.

Custom Packing List (My List): A customizable checklist to track items needed for a specific trip.

Real-Time Data Proxy: The custom Node.js API protects sensitive external API keys and standardizes data responses for Weather, Events, and Restaurants.

Comprehensive Trip Logging: Users can save and view multiple trip records including destination, dates, and flight times.

Intuitive UI Flow: Features a lock-screen mechanism, a primary MENU screen, and a Navigation Drawer for clear user guidance.

5 - NON-FUNCTIONAL REQUIREMENTS:

Security: Leverages Firebase for secure user authentication and utilizes a custom backend to protect all third-party API keys.

Maintainability: Built with a RESTful API approach using Node.js/Express.js, promoting a scalable and flexible service.

Availability & Scalability: The custom API is deployed on Render, ensuring high availability and automatic scaling in case of increased user traffic.

User Experience (UX): Features a clear, mobile-optimized interface with easy navigation between the Search and My Suitcase sections.

6 - OFFLINE MODE (SMART CACHING):

TravelMate includes a robust offline mode designed for travelers who may not always have internet access:

> What works offline?

Selecting currencies & calculating conversions

> How it works

The app uses a local caching system:

Stores your most recent searches (currencies)

Automatically syncs data when the device reconnects to WiFi

Ensures the currency remains fully functional even without network access

7 - BIOMETRICS SECURITY

For enhanced security, users can enable biometric login:

Fingerprint authentication

Face recognition (supported devices)

This protects personal trip data, notes, and saved travel plans.

8 - MULTI-LANGUAGE SUPPORT

TravelMate supports 3 languages:

English

Afrikaans

Zulu (isiZulu)

All UI text dynamically updates based on the selected language.

9 - ARCHITECTURE

The system utilizes a hybrid API architecture with the following components:

Frontend: The main application is a Kotlin/Java Android App.

Authentication: Handled securely by Firebase Authentication.

Database: Firestore Database is used to store all persistent user data, including profiles, trips, and packing lists.

Country/Currency Data: A MongoDB database holds the list of countries and their currency codes, which the custom API retrieves and uses.

Custom Backend/API: A RESTful API built with Node.js and the Express.js framework and deployed via Render.

Role: This custom API manages all calls to external services, retrieves the country list from MongoDB, and handles the final currency conversion calculation using data from the ExchangeRate-API.
External Data: Real-time information is sourced from External APIs, including ExchangeRate-API for currency.

10 - AI Assistance Disclosure

AI (ChatGPT) was used to support the development of this project through:

Guidance on API integration (weather, currency)

Debugging UI constraints in Android Studio

Explanation of Retrofit + coroutine implementation

Assistance with writing sample test structures

Helping refine technical documentation and this README file

All code was reviewed, customized, and implemented manually.

11 - FAQs

Q: What is the purpose of the custom Node.js API?

A: It acts as an intermediary, centralizing all external API calls (Weather, Events, etc.) to protect sensitive API keys. Crucially, it retrieves the country list from MongoDB, fetches exchange rates from the ExchangeRate-API, and performs the final currency calculation before sending the result to the mobile app.
Q: Where does the app get the list of currencies for the drop-down boxes?

A: The custom Node.js API retrieves the list of countries with their currency codes directly from the MongoDB database and sends that list to the Android app.
Q: Where is my personal data stored?

A: User authentication data is managed by Firebase Authentication. All personalized trip details, packing lists, and profile information are stored in the Firestore Database.

8 - CREDITS

This group project was completed as part of the PROG7312 Module.

Joshua De Wet – ST10313014

Ankriya Padayachee – ST10260507

Cade Gamble – ST10262209

Kyle Govender – ST10145498

Dinaley Mc Murray – ST10249944

10 - GITHUB LINK

Repository:

https://github.com/ST10260507/PROG7312_PART-2_J.A.C.K.D.git

11 - REFERENCES

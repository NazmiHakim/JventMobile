# Jvent

Jvent is an Android application designed to be a centralized hub for information about Japanese cultural events and anime conventions in Indonesia. It addresses the challenge of scattered and hard-to-find event information by providing a single platform for both event-goers and organizers.

## About The Project

The Japanese pop culture scene, including anime, cosplay, and J-pop, has a significant following in Indonesia. However, information about related events is often dispersed across various social media platforms, making it difficult for enthusiasts to stay updated. Jvent aims to solve this by creating a dedicated mobile application where users can easily discover, track, and get reminders for events, while organizers can effectively reach their target audience.

The application is built for the Android platform, leveraging modern technologies like Jetpack Compose for the UI, MVVM architecture, and Room for local data caching to ensure a responsive and reliable user experience, even with limited internet connectivity.

## Features

Jvent offers a comprehensive set of features for both event attendees and organizers:

* **Event Discovery:** Browse a comprehensive list of current and past Japanese cultural events.
* **Detailed Event Information:** Get all the necessary details about an event, including date, time, location, organizer, description, and a link to buy tickets.
* **Search and Filter:** Easily find events by name, or filter them by time (today, tomorrow, this week, etc.) and price (free or paid).
* **Favorites:** Save events you're interested in to a personal list for easy access.
* **Event Reminders:** Set reminders for your favorite events and receive notifications before they start.
* **Admin Dashboard:** A dedicated dashboard for event organizers to manage their events.
* **BREAD Functionality:** Admins can Browse, Read, Edit, Add, and Delete events.
* **Multi-language Support:** The app is available in both Indonesian and English.
* **Dark Mode:** A dark theme for comfortable viewing in low-light conditions.

## Technologies Used

Jvent is built with a modern Android technology stack:

* **UI:** Jetpack Compose
* **Architecture:** Model-View-ViewModel (MVVM)
* **Asynchronous Programming:** Kotlin Coroutines
* **Backend as a Service:**
    * **Firebase Firestore:** For real-time data storage and synchronization.
    * **Firebase Authentication:** For user authentication.
    * **Firebase Crashlytics & Analytics:** For monitoring crashes and analyzing user engagement.
* **Image Hosting:** Imgur API
* **Local Database:** Android Room for offline caching.
* **Networking:** Retrofit

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* Android Studio
* An Android device or emulator

### Installation

1.  **Clone the repo**
    ```sh
    git clone [https://github.com/nazmihakim/jventmobile.git](https://github.com/nazmihakim/jventmobile.git)
    ```
2.  **Open in Android Studio**
    Open the cloned project in Android Studio.
3.  **Run the app**
    Build and run the app on your Android device or emulator.

## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact

Linkedin - https://www.linkedin.com/in/nazmi-hakim-b0717a2a2/

Project Link: https://github.com/nazmihakim/jventmobile

# Madrid Orientation Race App

## Overview

The Madrid Orientation Race App is an interactive and engaging application designed to facilitate virtual orientation races in the city of Madrid. This project combines elements of sports and tourism, offering a unique experience for users to explore the city, enhance their navigation skills, and compete with friends.
This app was developed as part of the Mobile Devices Programming course in the IoT Master at the Universidad Politecnica de Madrid.

## Features

### 1. Activities

The application consists of three main and twoo side activities:

- **Main Activity:** Landing page where users can define a username and sign up for the race.
- **Participants Activity:** Allows users to view all other participants. The race starts when enough participants join.
- **Race Activity:** Displays checkpoints, compass, and participant information. Users can click on checkpoints to view their locations on Google Maps.
- **Google Maps Activities:** Shows the current position of the user every five minutes for 30 seconds. Also displays the location of parks in the city.

### 2. Custom Adapter and UI Elements

The app includes a custom adapter for two RecyclerViews: one displaying a list of participants and the other showing a grid of checkpoints. Various UI elements such as ImageViews, TextViews, EditTexts, and Buttons enhance the user interface.

### 3. Open Data Integration

The application downloads Madrid gardens files from the internet and parses them for information. This process creates a list of checkpoints, providing users with names and locations for navigation during the race.

### 4. MQTT Interaction

The app utilizes the Paho library for MQTT interaction with the online Mosquitto MQTT Broker. Each user acts as a publisher and subscriber to two topics: `madridOrientationRace/participants` and `madridOrientationRace/checkpoints`. This enables real-time communication and gameplay whenever users are connected to the internet.
The online Moquitto MQTT Broker can be found at: ([https://test.mosquitto.org](https://test.mosquitto.org/))

### 5. Sensor Integration

The app reads and uses the readings from the built-in compass sensor. The Race Activity prominently features the compass, helping users navigate through the race course.

### 6. Accessibility

For users with visual disabilities, the application includes an AppToSpeech functionality. When the sensor points toward any of the four cardinal directions, the phone announces the direction, ensuring inclusivity for all users.

### 7. Additional Functionalities

- Users can view their current position on Google Maps every five minutes for a limited time.
- The Google Maps Activity displays the locations of parks in the city.

## Getting Started

To get started with the Madrid Orientation Race App, follow these steps:

1. **Clone the repository.**
    ```bash
    git clone https://github.com/Radfahrer00/OrientationRace
    ```

2. **Open the project in your preferred Android development environment.**
   - Navigate to the project directory.
   - Open the project using your preferred IDE.

3. **Ensure the necessary dependencies are installed.**

4. **Build and run the application on an Android device or emulator.**

## Dependencies

- Paho MQTT Library: [Link to Paho Library](https://www.eclipse.org/paho/)

## Contributors

- Quentin Mathieu
- Hichem Hassen

## Acknowledgments

- Special thanks to the contributors and collaborators who have helped shape and improve this application.
- Appreciation to the creators of the Paho MQTT Library for providing a robust solution for MQTT communication.

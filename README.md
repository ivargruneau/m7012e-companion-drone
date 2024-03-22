# M7012E-Companion-Drone

## Project Overview

## Course Details

- **University:**  Luleå University of Technology
- **Course Name:**  Pervasive Computing (M7012E) 
- **Instructor:** Kåre Synnes
- **Semester/Year:** LP3/2024

## Project Team

### Students
- Ahmad Allahham
  - Email: ahmall-0@student.ltu.se
- Ivar Gruneau
  - Email: ivagru-9@student.ltu.se
- Sabina Elise Wickforsen
  - Email: sabwic-9@student.ltu.se
- Warsay Maharena
  - Email: warmah-0@student.ltu.se

## Project Components

The project is structured into several key components, each addressing specific aspects of drone functionality and intelligence:

### Pedestrian Detection

This component focuses on the drone's ability to recognize and track specific individuals within its environment. Utilizing advanced computer vision techniques, the drone can identify and monitor individuals, enabling applications such as surveillance, search and rescue, and crowd monitoring.

### Distance Estimation

The distance estimation component enables the drone to calculate the distance between itself and detected objects, particularly individuals. This capability is essential for ensuring safe navigation and interaction with the environment.

### Drone Instruction Calculator

This component calculates optimal maneuvering instructions for the drone to maintain a desired distance from identified individuals. By analyzing environmental factors and drone capabilities, the instruction calculator ensures precise and efficient drone control.

### Drone Controller App

The drone controller app serves as the interface for controlling and monitoring the drone's operations. It provides real-time information on the drone's movement, position, and status, allowing users to make informed decisions and adjustments as needed.

### Test Client

The test client component simulates image uploading to the server for testing purposes. It allows developers to evaluate system performance and identify potential issues or improvements.

## Requirements

Before running the code, ensure you have the following prerequisites:

- Python environment with necessary libraries (e.g., Flask, OpenCV)
- YOLO v8 model file (`yolov8n.pt`)
- Access to a server for hosting the Flask app

## Code Structure

The project's codebase is organized into different directories, each representing a specific component of the system. Within each directory, you'll find relevant code files, resources, and documentation to facilitate understanding and contribution.

## YouTube Demo

🎬 **YouTube Demo:** [Watch on YouTube](https://youtu.)

## Setup Instructions

To set up and run the project:

1. Clone the repository to your local machine.
2. Set up the required environment and dependencies.
### The Server
3. Start the server using the provided `start_server.bat` script, run it from the root of the project.
4. Execute the test client to simulate image uploads and interactions with the server.
5. Monitor the server for responses and behavior.
### The Android Application
3. In m7012e-companion-drone\Android\cDrone\app\src\main\res\xml\network_security_config.xml, add the IP address of the server
3. In m7012e-companion-drone\Android\cDrone\app\src\main\java\com\example\cdroneapp\utils\NetworkHandler.kt set the URL of the request to the address of the server.
4. Build and run the application on the controller.

##Additional Notes
1. Make sure to place the drone on the ground before turning it on.
2. The drone sometimes refuses to land, try to land at the same place as where it took off.
3. We had a problem with enabling `VirtualStick` when the slider on the middle of the controller was not in the middle position. 



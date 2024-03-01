from ultralytics import YOLO
import cv2
from DetectAndGetDistance import calculate_distance_to_camera

# Load yolov8 model
model = YOLO('yolov8n.pt')

# Load the video file
video_path = './PedestrianDetection/pedestrianDetection/testVideos/vid_6.mp4'
video_capture = cv2.VideoCapture(video_path)

# Function to calculate the size of the bounding box (object size in pixels)
def calculate_bbox_size(x_upper, y_upper, x_lower, y_lower, person_x, person_y):
    # Calculate bounding box dimensions
    bbox_width = x_lower - x_upper
    bbox_height = y_lower - y_upper
    
    # Calculate bounding box size
    bbox_size = bbox_width * bbox_height
    
    # Check if the person is out of the bounding box upper
    if person_y < y_upper:
        print("Person is above the bounding box")
    # Check if the person is much to the right of the bounding box
    elif person_x > x_lower:
        print("Person is much to the right of the bounding box")
    # Check if the person is much to the left of the bounding box
    elif person_x < x_upper:
        print("Person is much to the left of the bounding box")
    # If none of the above conditions are met, return the bounding box size
    else:
        return bbox_size

# Known parameters for calibration (in inches)
KNOWN_FACE_WIDTH = 6.0  # Average width of a human face in inches
KNOWN_DISTANCE_TO_FACE = 24.0  # Distance from the camera to the face in inches

# Initialize focal length
focal_length = None

# Loop through the video frames
while video_capture.isOpened():
    ret, frame = video_capture.read()
    if not ret:
        break

    # Get frame dimensions
    frame_height, frame_width = frame.shape[:2]

    # Detect faces in the frame using Haar Cascade classifier
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
    gray_image = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray_image, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

    # Assuming one face is detected
    if len(faces) > 0:
        x, y, w, h = faces[0]
        
        # Calculate distance to the detected face
        pixel_width = w  # Assuming the width of the bounding box represents the width of the face
        
        # Perform object detection on the frame
        results_list = model.predict(frame)  # Source already setup
        
        # Assuming results.pred contains the predictions
        # And results.names contains the class names
        for results in results_list:  # Result seems to be of len 1 only
            for result in results:
                c = int(result.boxes.cls)
                if c == 0:  # Assuming class 0 represents pedestrians
                    pos_array = result.boxes.xyxy.numpy()
                    pos_array = pos_array[0]
                    x_upper, y_upper, x_lower, y_lower = pos_array[0], pos_array[1], pos_array[2], pos_array[3]
                    
                    # Coordinates of the person 
                    person_x = x + (w // 2)  # Provide the x-coordinate of the person
                    person_y = y + (h // 2)  # Provide the y-coordinate of the person
                    
                    # Calculate bounding box size and check person position
                    bbox_size = calculate_bbox_size(x_upper, y_upper, x_lower, y_lower, person_x, person_y)
                    if bbox_size is not None:
                        print(f"Bounding box size: {bbox_size} pixels")
        
        # Call the calculate_distance_to_camera function from DetectAndGetDistance.py
        distance, turning, moving, elevation = calculate_distance_to_camera(KNOWN_FACE_WIDTH, focal_length, pixel_width, (x, y, x+w, y+h), frame_width, frame_height)

    # Display the frame with detected objects
    cv2.imshow('Detected Objects', frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release resources
video_capture.release()
cv2.destroyAllWindows()

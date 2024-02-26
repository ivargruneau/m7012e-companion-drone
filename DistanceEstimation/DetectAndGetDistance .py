import cv2

# Function to detect faces in the image using a Haar Cascade classifier
def detect_faces(image):
    # Load the pre-trained Haar Cascade face detector
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

    # Convert the image to grayscale for better face detection
    gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Detect faces in the grayscale image
    faces = face_cascade.detectMultiScale(gray_image, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

    # Filter out detections that are not actual faces
    actual_faces = []
    for (x, y, w, h) in faces:
        # Check if the detected region contains more white pixels than black pixels (likely to be a face)
        region_of_interest = gray_image[y:y+h, x:x+w]
        num_white_pixels = cv2.countNonZero(region_of_interest)
        num_black_pixels = region_of_interest.size - num_white_pixels
        if num_white_pixels > num_black_pixels:
            actual_faces.append((x, y, w, h))

    return actual_faces

# Function to calibrate the focal length of the camera
def calibrate_focal_length(image, known_distance, known_width):
    # This is a placeholder function, assuming a default focal length
    focal_length = 500.0  # Example value, adjust as needed
    return focal_length

# Function to calculate the distance from the camera to the detected face
def calculate_distance_to_camera(known_width, focal_length, pixel_width, bounding_box, frame_width, frame_height):
    # Extract bounding box coordinates
    (x1, y1, x2, y2) = bounding_box
    
    # Calculate center of the bounding box
    center_x = (x1 + x2) / 2
    center_y = (y1 + y2) / 2
    
    # Compute normalized center
    normalized_center_x = center_x / frame_width
    normalized_center_y = center_y / frame_height
    
    # Calculate area of the bounding box
    bounding_box_width = x2 - x1
    bounding_box_height = y2 - y1
    area = bounding_box_width * bounding_box_height
    
    # Determine drone actions based on bounding box information
    turning = ""
    moving = ""
    elevation = ""
    
    # Calculate normalized center and adjust drone actions accordingly
    if normalized_center_x > 0.6:
        turning = "turn_right"
    elif normalized_center_x < 0.4:
        turning = "turn_left"
        
    if normalized_center_y > 0.6:
        elevation = "ascend"
    elif normalized_center_y < 0.4:
        elevation = "descend"
    
    # Adjust drone movement based on bounding box area
    if area > 100:
        moving = "backward"
    elif area < 80:
        moving = "forward"
    
    # Calculate distance to the face using focal length and known width, convert inches to centimeters
    distance = (known_width * focal_length * 2.54) / pixel_width
    return distance, turning, moving, elevation

# Load the video file
video_path = 'IMG_1193.mp4'
video_capture = cv2.VideoCapture(video_path)

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

    # Detect faces in the frame
    detected_faces = detect_faces(frame)

    # Calculate focal length if not already calibrated
    if focal_length is None:
        focal_length = calibrate_focal_length(frame, KNOWN_DISTANCE_TO_FACE, KNOWN_FACE_WIDTH)
        if focal_length is None:
            print("Failed to calibrate focal length. Please ensure a face is present in the frame.")
            break

    # Calculate distance to each detected face and perform drone actions
    for (x, y, w, h) in detected_faces:
        # Calculate distance to the detected face
        pixel_width = w  # Assuming the width of the bounding box represents the width of the face
        distance, turning, moving, elevation = calculate_distance_to_camera(KNOWN_FACE_WIDTH, focal_length, pixel_width, (x, y, x+w, y+h), frame_width, frame_height)
        
        # Perform drone actions based on computed instructions (e.g., send commands to drone)
        # control the drone based on computed instructions

        # Display the calculated actions and distance on the frame
        cv2.putText(frame, f"Actions: {turning}, {moving}, {elevation}, Distance: {distance:.2f} cm", (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # Display the frame
    cv2.imshow('Frame', frame)

    # Press 'q' to quit
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release resources
video_capture.release()
cv2.destroyAllWindows()

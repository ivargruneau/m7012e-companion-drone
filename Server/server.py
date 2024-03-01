from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
import os
import cv2
from ultralytics import YOLO

app = Flask(__name__)

# Define the path for uploaded files
UPLOAD_FOLDER = 'uploads'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# Load yolov8 model
model = YOLO('yolov8n.pt')

# Known parameters for calibration (in inches)
KNOWN_FACE_WIDTH = 6.0  # Average width of a human face in inches
KNOWN_DISTANCE_TO_FACE = 24.0  # Distance from the camera to the face in inches

# Function to calculate the distance from the camera to the detected pedestrian
def calculate_distance_to_camera(known_width, focal_length, pixel_width, bounding_box, frame_width, frame_height):
    # Implement this function based on your requirements
    pass

@app.route('/upload', methods=['POST'])
def upload_file():
    print("Got a POST Request")
    if request.method == 'POST':
        # Ensure the upload folder exists
        if not os.path.exists(app.config['UPLOAD_FOLDER']):
            os.makedirs(app.config['UPLOAD_FOLDER'])

        # Get the 'sessionId' from the form data
        session_id = request.form['sessionId']
        print(f"Session ID received: {session_id}")

        # Check if the post request has the file part
        if 'image' not in request.files:
            return jsonify({'error': 'No file part'}), 400
        file = request.files['image']

        # If the user does not select a file, the browser submits an
        # empty file without a filename.
        if file.filename == '':
            return jsonify({'error': 'No selected file'}), 400
        
        if file:
            filename = secure_filename(file.filename)
            save_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            file.save(save_path)
            print(f"File '{filename}' saved to {save_path}")

            # Load the uploaded image
            image = cv2.imread(save_path)

            # Perform object detection on the image
            results_list = model.predict(image)

            # Process the detected pedestrians
            for results in results_list:
                for result in results:
                    c = int(result.boxes.cls)
                    if c == 0:  # Assuming class 0 represents pedestrians
                        pos_array = result.boxes.xyxy.numpy()
                        pos_array = pos_array[0]
                        x_upper, y_upper, x_lower, y_lower = pos_array[0], pos_array[1], pos_array[2], pos_array[3]
                        
                        # Assuming person coordinates are at the center of the bounding box
                        person_x = (x_upper + x_lower) / 2
                        person_y = (y_upper + y_lower) / 2
                        
                        # Calculate bounding box size (optional)
                        bbox_size = calculate_bbox_size(x_upper, y_upper, x_lower, y_lower, person_x, person_y)
                        if bbox_size is not None:
                            print(f"Bounding box size: {bbox_size} pixels")

                        # Calculate distance to the detected pedestrian and perform drone actions
                        distance, turning, moving, elevation = calculate_distance_to_camera(KNOWN_FACE_WIDTH, focal_length, x_lower - x_upper, (x_upper, y_upper, x_lower, y_lower), image.shape[1], image.shape[0])
                        
                        # Prepare response data
                        response_data = {
                            'pedestrianDetected': True,
                            'distance': distance,
                            'turning': turning,
                            'moving': moving,
                            'elevation': elevation
                        }
            
                        # Return the response data
                        return jsonify(response_data)

            # If no pedestrians are detected, return an error response
            return jsonify({'error': 'No pedestrians detected'}), 400

# Run the Flask app
if __name__ == '__main__':
    app.run(debug=True)

from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
import os
import sys
sys.path.insert(0, './PedestrianDetection')
import Detector

app = Flask(__name__)
detector = Detector.Detector()
print("Starting the server")
# Define the path for uploaded files
UPLOAD_FOLDER = 'Server/uploads'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

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

            params = detector.detect_on_image(save_path)
            # Here, you could add your logic to process the image
            # and compute the distance, horizontalAngle, and verticalAngle
            # For demonstration, we'll return dummy values
            detected, distance, horizontal_angle, vertical_angle  = params
            response_data = {
                'pedestrianDetected': detected,
                'distance': distance,
                'horizontalAngle': horizontal_angle,
                'verticalAngle': vertical_angle
            }
            print(f'Respons: {response_data}')
            return jsonify(response_data)


@app.route('/get', methods=['GET'])
def get_test():
    print("Got a GET Request")
    return "Server is online"

if __name__ == '__main__':
    app.run(debug=True)

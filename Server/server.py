from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
import os

app = Flask(__name__)

# Define the path for uploaded files
UPLOAD_FOLDER = 'uploads'
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

            # Here, you could add your logic to process the image
            # and compute the distance, horizontalAngle, and verticalAngle
            # For demonstration, we'll return dummy values

            response_data = {
                'distance': 1.4561,
                'horizontalAngle': 102,
                'verticalAngle': 15.2
            }
            return jsonify(response_data)


@app.route('/get', methods=['GET'])
def get_test():
    print("Got a GET Request")
    return "asdasd"

if __name__ == '__main__':
    app.run(debug=True)

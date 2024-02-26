from ultralytics import YOLO
import cv2

# load yolov8 model
model = YOLO('yolov8n.pt')

# load video
#video_path = './yellowcat.mp4'
video_path = './testVideos/vid_6.mp4'
cap = cv2.VideoCapture(video_path)
image=cap
ret = True

# read frames
while ret:
    ret, frame = cap.read()
    if ret:
        # detect objects
        # track objects
        results = model.track(frame, persist=True)

        # plot results
        frame_ = results[0].plot()

        # visualize
        cv2.imshow('frame', frame_)
        if cv2.waitKey(25) & 0xFF == ord('q'):
            break



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




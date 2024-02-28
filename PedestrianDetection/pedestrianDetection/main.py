from ultralytics import YOLO
import cv2


# load yolov8 model
model = YOLO('yolov8n.pt')

# load an image

image_path = './testVideos/img1.jpg'
image = cv2.imread(image_path)


results_list = model.predict(image) # source already setup
names = model.names
print(f' Name: ')

# Assuming results.pred contains the predictions
# And results.names contains the class names
def calc_somthing(x_Upper, y_Upper, x_Lower, y_Lower, image_original_size):
    print("Inside calc_somthing")
    print(f'    x_Upper {x_Upper}. y_Upper {y_Upper} x_Lower { x_Lower}. y_Lower {y_Lower}. image_original_size {image_original_size}')
for results in results_list: #Result seems to be of len 1 only
    
    for result in results:


        c = int(result.boxes.cls)
        if ( c == 0) :
            pos_array = result.boxes.xyxy .numpy()
            

            pos_array = pos_array[0]
            calc_somthing(pos_array[0], pos_array[1], pos_array[2], pos_array[3], "image original dimmensions")
            



        
# Display the image
cv2.imshow('Detected Objects', image)
cv2.waitKey(0)
cv2.destroyAllWindows()
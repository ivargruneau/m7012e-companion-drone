from ultralytics import YOLO
import cv2


# load yolov8 model
model = YOLO('yolov8n.pt')

# load an image


# Assuming results.pred contains the predictions
# And results.names contains the class names
def preform_calculation(x_Upper, y_Upper, x_Lower, y_Lower, image):
    image_width = image.shape[1]
    image_height = image.shape[0]
    optimal_person_pixel_height = 500
    optimal_box_height = 1600
    optimal_box_width = 800
    movement_threshold = 100 # high difference needs to be greater then the threshold

    optimal_box_upper_x = (image_width -optimal_box_width )/2
    optimal_box_upper_y = (image_height -optimal_box_height )/2
    optimal_box_lower_x = (image_width +optimal_box_width )/2
    optimal_box_lower_y = (image_height +optimal_box_height )/2
    horizonal_angle = 0
    vertical_angle = 0
    distance = 0
    if x_Upper < optimal_box_upper_x: 
        horizonal_angle = (optimal_box_upper_x-x_Upper) /optimal_box_upper_x #Will be 1 if x_Upper is at the edge of the image
        horizonal_angle = -horizonal_angle
    elif x_Lower > optimal_box_lower_x:
        horizonal_angle = (optimal_box_lower_x-x_Lower) /optimal_box_upper_x

    if y_Upper < optimal_box_upper_y:
        vertical_angle = (optimal_box_upper_y-y_Upper) /optimal_box_upper_y #Will be 1 if y_Upper is at the edge of the image
    elif y_Lower > optimal_box_lower_y:
        vertical_angle = (optimal_box_lower_y-y_Lower)/optimal_box_upper_y
        vertical_angle = -vertical_angle

    detected_person_height = y_Lower - y_Upper

    if detected_person_height > optimal_person_pixel_height + movement_threshold:
       distance =  optimal_person_pixel_height - detected_person_height #Negative distance (move backwards) #Change
    elif detected_person_height < optimal_person_pixel_height + movement_threshold:
        distance =  detected_person_height - optimal_person_pixel_height #Change and improve

    return_params = distance, horizonal_angle, vertical_angle
    return return_params

    #print(f'    x_Upper {x_Upper}. y_Upper {y_Upper} x_Lower { x_Lower}. y_Lower {y_Lower}. image_original_size {image_original_size}')


def detect_on_image(image):
    

    detected = False
    distance = 0
    horizontal_angle = 0
    vertical_angle = 0
    results_list = model.predict(image) # source already setup

    for results in results_list: #Result seems to be of len 1 only
        
        for result in results:


            c = int(result.boxes.cls)
            if ( c == 0) :
                detected = True
                pos_array = result.boxes.xyxy .numpy()
                

                pos_array = pos_array[0]
                params = preform_calculation(pos_array[0], pos_array[1], pos_array[2], pos_array[3], image)
                distance, horizontal_angle, vertical_angle = params

    params =  detected, distance, horizontal_angle, vertical_angle  
    return params 


image_path = './testVideos/img1.jpg'
print("loaded images")
image = cv2.imread(image_path)
#params = detect_on_image(image) 
#print(f'returnd params: {params}')  
print(f'shape {image.shape[0]} dimensions')   
# Display the image
cv2.imshow('Detected Objects', image)
cv2.waitKey(0)
cv2.destroyAllWindows()
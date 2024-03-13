from ultralytics import YOLO
import cv2
import numpy as np

# load yolov8 model
image_name = ""

# load an image
class Detector:
    def __init__(self):
        self.model = YOLO('PedestrianDetection/yolov8n.pt')



# Assuming results.pred contains the predictions
# And results.names contains the class names
    def calculate_distance_and_angle(self, x_Upper, y_Upper, x_Lower, y_Lower, image):
        global image_name
        image_width = image.shape[1]
        image_height = image.shape[0]
        base_distance = 10
        person_pixel_height_at_base_distance = 681
        distance_threshhold = 2

        optimal_box_height = 2000
        optimal_box_width = 1400
        
        detected_person_height_pixels = y_Lower - y_Upper
        #print(f'Detected persons pixel height: {detected_person_height_pixels}')
        optimal_box_upper_x = (image_width -optimal_box_width )/2
        optimal_box_upper_y = (image_height -optimal_box_height )/2
        optimal_box_lower_x = (image_width +optimal_box_width )/2
        optimal_box_lower_y = (image_height +optimal_box_height )/2


        horizonal_angle = 0
        vertical_angle = 0
        distance = 0
        if x_Upper < optimal_box_upper_x: 
            #print("To the left")
            horizonal_angle = (optimal_box_upper_x-x_Upper) /optimal_box_upper_x #Will be 1 if x_Upper is at the edge of the image
            horizonal_angle = -horizonal_angle
        elif x_Lower > optimal_box_lower_x:
            #print("To the right")
            horizonal_angle = (optimal_box_lower_x-x_Lower) /optimal_box_upper_x
            horizonal_angle = -horizonal_angle
        if y_Upper < optimal_box_upper_y:
            #print("Above")
            vertical_angle = (optimal_box_upper_y-y_Upper) /optimal_box_upper_y #Will be 1 if y_Upper is at the edge of the image
            vertical_angle = -vertical_angle
        elif y_Lower > optimal_box_lower_y:
            #print("Below")
            vertical_angle = (optimal_box_lower_y-y_Lower)/optimal_box_upper_y
            

        
        distance_in_image = (person_pixel_height_at_base_distance/detected_person_height_pixels)*base_distance
        #print(f'Distance to detected person: {distance_in_image}')
        if distance_in_image > base_distance + distance_threshhold:
            distance =   distance_in_image - base_distance
        elif distance_in_image < base_distance - distance_threshhold:
            distance =  distance_in_image - base_distance

        return_params = distance, horizonal_angle, vertical_angle

        #Draw the rectangles.
        image_bounding_box = image.copy()
        top_left_corner = (int(x_Upper), int(y_Upper)) 
        bottom_right_corner = (int(x_Lower), int(y_Lower)) 
        cv2.rectangle(image_bounding_box, top_left_corner, bottom_right_corner, (0, 0, 255), 8)
        file_name = './PedestrianDetection/testImages/' + image_name



        position = (int(optimal_box_upper_x), int(optimal_box_lower_y)+120)

        font = cv2.FONT_HERSHEY_SIMPLEX


        font_scale = 5

        color = (0, 255, 255) # Yellow


        thickness = 12


        cv2.imwrite(file_name + '_BB.JPG' , image_bounding_box)

        top_left_corner = (int(optimal_box_upper_x), int(optimal_box_upper_y)) 
        bottom_right_corner = (int(optimal_box_lower_x), int(optimal_box_lower_y)) 
        cv2.rectangle(image_bounding_box, top_left_corner, bottom_right_corner, (0, 255, 255), 8)
        cv2.imwrite(file_name + '_OB.JPG' , image_bounding_box)
        
        text = "distance: " + str(distance)
        position = (int(optimal_box_upper_x), int(optimal_box_lower_y)+140)
        cv2.putText(image_bounding_box, text, position, font, font_scale, color, thickness)

        text = "horizontal angle: " + str(horizonal_angle)
        position = (int(optimal_box_upper_x), int(optimal_box_lower_y)+(140*2))
        cv2.putText(image_bounding_box, text, position, font, font_scale, color, thickness)

        text = "verical angle: " + str(vertical_angle)
        position = (int(optimal_box_upper_x), int(optimal_box_lower_y)+(140*3))
        cv2.putText(image_bounding_box, text, position, font, font_scale, color, thickness)

        cv2.imwrite(file_name + '_OB+calc.JPG' , image_bounding_box)
        return return_params

    #print(f'    x_Upper {x_Upper}. y_Upper {y_Upper} x_Lower { x_Lower}. y_Lower {y_Lower}. image_original_size {image_original_size}')


    def detect_on_image(self, image_path):
        image = cv2.imread(image_path)
        detected = False
        distance = 0
        horizontal_angle = 0
        vertical_angle = 0
        results_list = self.model.predict(image) # source already setup

        for results in results_list: #Result seems to be of len 1 only
            
            for result in results:

                

                c = int(result.boxes.cls)
                if ( c == 0) :
                    detected = True
                    #print("DETECTED HUMAN")
                    pos_array = result.boxes.xyxy .numpy()
                    

                    pos_array = pos_array[0]
                    params = self.calculate_distance_and_angle(pos_array[0], pos_array[1], pos_array[2], pos_array[3], image)
                    distance, horizontal_angle, vertical_angle = params

        params =  detected, distance, horizontal_angle, vertical_angle  
        return params 




def test_with_images(dir):
    import glob
    import os
    
    directory_path = dir

    # Use glob.glob() to find all files ending with .JPG
    jpg_files = glob.glob(os.path.join(directory_path, '*.JPG'))
    m =Detector()
    # Print the list of .JPG files
    for file in jpg_files:
        print()
        print()

        
        params = m.detect_on_image(file)
        
        print(f'FOR {file}. Returnd params: {params}')  

#image_dir = './PedestrianDetection/testImages/'
#test_with_images(image_dir)
def draw_boxes():
    global image_name
    image_dir = './PedestrianDetection/testImages/'
    detector = Detector()
    
    print("Drawing comp")
    for name in ["img1", "img2", "img3", "img4", "img5", "img6"]:
        image_name = name
        image_file_path = image_dir+ name + ".JPG"
        detector.detect_on_image(image_file_path)

draw_boxes()
#image = cv2.imread(image_dir+ image_names[0]+ ".JPG")
#params = detect_on_image(image) 
#print(f'returnd params: {params}')  
#print(f'shape {image.shape[0]} dimensions')   
# Display the image
#cv2.imshow('Detected Objects', image)
#cv2.waitKey(0)
#cv2.destroyAllWindows()



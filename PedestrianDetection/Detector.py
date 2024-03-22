from ultralytics import YOLO
import cv2


# load yolov8 model


# load an image
class Detector:
    def __init__(self):
        self.model = YOLO('PedestrianDetection/yolov8n.pt')



# Assuming results.pred contains the predictions
# And results.names contains the class names
    def calculate_distance_and_angle(self, x_Upper, y_Upper, x_Lower, y_Lower, image):
        image_width = image.shape[1]
        image_height = image.shape[0]
        base_distance = 10
        person_pixel_height_at_base_distance = 681
        distance_threshhold = 2

        optimal_box_width = 1400
        optimal_box_height = 2000
        
        detected_person_height_pixels = y_Lower - y_Upper
        y_bbMiddle = (y_Upper + y_Lower) / 2
        x_bbMiddle = (x_Upper + x_Lower) / 2
        optimal_box_upper_x = (image_width -optimal_box_width )/2
        optimal_box_upper_y = (image_height -optimal_box_height )/2
        optimal_box_lower_x = (image_width +optimal_box_width )/2
        optimal_box_lower_y = (image_height +optimal_box_height )/2

        distance = 0
        horizonal_angle = 0
        vertical_angle = 0

        distance_in_image = (person_pixel_height_at_base_distance/detected_person_height_pixels)*base_distance

        if distance_in_image > base_distance + distance_threshhold:
            distance =   distance_in_image - base_distance
        elif distance_in_image < base_distance - distance_threshhold:
            distance =  distance_in_image - base_distance


        if x_bbMiddle < optimal_box_upper_x: #To the left
            horizonal_angle = (x_bbMiddle -(image_width/2)) / (image_width/2)

        elif x_bbMiddle > optimal_box_lower_x: # To the right
            horizonal_angle = (x_bbMiddle-(image_width/2)) / (image_width/2)

        if y_bbMiddle < optimal_box_upper_y: #Above
            vertical_angle = (y_bbMiddle-(image_height/2)) / (image_height/2)

        elif y_bbMiddle > optimal_box_lower_y: #Below
            vertical_angle = (y_bbMiddle- (image_height/2)) / (image_height/2)

            

        return_params = distance, horizonal_angle, vertical_angle
        return return_params



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

image_dir = './PedestrianDetection/testImages/'
test_with_images(image_dir)

#image = cv2.imread(image_dir+ image_names[0]+ ".JPG")
#params = detect_on_image(image) 
#print(f'returnd params: {params}')  
#print(f'shape {image.shape[0]} dimensions')   
# Display the image
#cv2.imshow('Detected Objects', image)
#cv2.waitKey(0)
#cv2.destroyAllWindows()



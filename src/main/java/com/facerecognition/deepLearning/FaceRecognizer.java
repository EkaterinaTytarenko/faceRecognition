package com.facerecognition.deepLearning;

import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.Net;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import java.util.*;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_dnn.blobFromImage;
import static org.bytedeco.opencv.global.opencv_dnn.readNetFromCaffe;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;

public class FaceRecognizer {

    private static final String PROTO_FILE = "src/main/java/com/facerecognition/deepLearning/configs/deploy.prototxt.txt";
    private static final String CAFFE_MODEL_FILE = "src/main/java/com/facerecognition/deepLearning/configs/res10_300x300_ssd_iter_140000.caffemodel";
    private static final OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
    private static Net net = null;
    private static LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();

    static {
        net = readNetFromCaffe(PROTO_FILE, CAFFE_MODEL_FILE);
        faceRecognizer.read("src/main/java/com/facerecognition/deepLearning/configs/faceRecognizer.xml");
    }

    public static String detectFaces(String filepath) {

        Mat colorimg = imread(filepath);

        resize(colorimg, colorimg, new Size(300, 300));//resize the image to match the input size of the model

        //create a 4-dimensional blob from image with NCHW (Number of images in the batch -for training only-, Channel, Height, Width) dimensions order,
        //for more detailes read the official docs at https://docs.opencv.org/trunk/d6/d0f/group__dnn.html#gabd0e76da3c6ad15c08b01ef21ad55dd8
        Mat blob = blobFromImage(colorimg, 1.0, new Size(300, 300), new Scalar(104.0, 177.0, 123.0, 0), false, false, CV_32F);

        net.setInput(blob);//set the input to network model
        Mat output = net.forward();//feed forward the input to the netwrok to get the output matrix

        Mat ne = new Mat(new Size(output.size(3), output.size(2)), CV_32F, output.ptr(0, 0));//extract a 2d matrix for 4d output matrix with form of (number of detections x 7)

        FloatIndexer srcIndexer = ne.createIndexer(); // create indexer to access elements of the matric

        //will be used for a message
        int numberOfFaces=0;
        Set<Integer> labels=new HashSet<>();

        for (int i = 0; i < output.size(3); i++) {//iterate to extract elements
            float confidence = srcIndexer.get(i, 2);
            float f1 = srcIndexer.get(i, 3);
            float f2 = srcIndexer.get(i, 4);
            float f3 = srcIndexer.get(i, 5);
            float f4 = srcIndexer.get(i, 6);
            if (confidence > .6) {
                numberOfFaces++;

                float tx = f1 * 300;//top left point's x
                float ty = f2 * 300;//top left point's y
                float bx = f3 * 300;//bottom right point's x
                float by = f4 * 300;//bottom right point's y
                rectangle(colorimg, new Rect(new Point((int) tx, (int) ty), new Point((int) bx, (int) by)), new Scalar(255, 0, 0, 0));//print blue rectangle

                Mat croppedImage = new Mat(colorimg, new Rect(new Point((int) tx, (int) ty), new Point((int) bx, (int) by)));
                resize(croppedImage, croppedImage, new Size(300, 300));
                opencv_imgproc.cvtColor(croppedImage, croppedImage, CV_RGB2GRAY);
                int label = faceRecognizer.predict_label(croppedImage);
                labels.add(label);
                String name = "";
                switch (label) {
                    case 1:
                        name = "Katya";
                        break;
                    case 2:
                        name = "Oksana";
                        break;
                    case 3:
                        name = "Zhanna";
                        break;
                    default:
                        name = "Austrian";
                }
                putText(colorimg, name, new Point((int) tx, (int) by), 2, 0.85, new Scalar(0, 255, 0, 0));
            }
        }
        //save a picture with text
        imwrite(filepath, colorimg);

        //form a message based on the people
       String message="";
        return message;

    }

}


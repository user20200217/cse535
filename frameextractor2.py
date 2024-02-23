# -*- coding: utf-8 -*-
"""
Created on Thu Jan 28 00:52:08 2021

@author: chakati
"""
#code to get the key frame from the video and save it as a png file.

import cv2
import os
import numpy as np
#videopath : path of the video file
#frames_path: path of the directory to which the frames are saved
#count: to assign the video order to the frane.

# we need 3 frames to describe a continuous motion

def frameExtractor(videopath,frames_dir,name,spacing):
    if not os.path.exists(frames_dir):
        os.mkdir(frames_dir)
    cap = cv2.VideoCapture(videopath)
    video_length = int(cap.get(cv2.CAP_PROP_FRAME_COUNT)) - 1
    frame_no= int(video_length/2)
    #print("Extracting frame..\n")
    cap.set(1,frame_no-spacing)
    ret,frame1=cap.read()
    frame1 = cv2.resize(frame1, (200, 200))
    cap.set(1,frame_no)
    ret,frame2=cap.read()
    frame2 = cv2.resize(frame2, (200, 200))
    cap.set(1,frame_no+spacing)
    ret,frame3=cap.read()
    frame3 = cv2.resize(frame3, (200, 200))
    frame = np.vstack((frame1, frame2, frame3))
    cv2.imwrite(frames_dir+"/"+name, frame)
    return frame

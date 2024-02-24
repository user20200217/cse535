# -*- coding: utf-8 -*-
"""
Created on Thu Jan 28 00:52:08 2021

@author: chakati
# Edited by Kai Chi on 2/23/2024
# CSE535
# Arizona State University
"""
#code to get the key frame from the video and save it as a png file.

import cv2
import os
import numpy as np
#videopath : path of the video file
#frames_path: path of the directory to which the frames are saved
#count: to assign the video order to the frane.

def frameExtractor(videopath):
    cap = cv2.VideoCapture(videopath)
    video_length = int(cap.get(cv2.CAP_PROP_FRAME_COUNT)) - 1
    frame_no= int(video_length/2)
    #print("Extracting frame..\n")
    cap.set(1,frame_no)
    ret,frame=cap.read()
    frame = cv2.resize(frame, (200, 200))
    return frame

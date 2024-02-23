# -*- coding: utf-8 -*-
"""
Created on Thu Jan 28 00:44:25 2021

@author: chakati
# edited by Kai Chi on 2/21/2024
# CSE535
# Arizona State University
"""
import cv2
import numpy as np
import os
import tensorflow as tf

## import the handfeature extractor class
import frameextractor2 as fe

names = os.listdir("./test")
labels = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "LightOn", "LightOff", "FanOn", "FanOff", "IncreaseFanSpeed", "DecreaseFanSpeed", "SetThermo"]

predictions = np.zeros(len(names))
for i in range(len(names)):
    for j in (range(len(labels))):
        if names[i].find(labels[j])>-1:
            predictions[i] = j
            break

# =============================================================================
# Get the penultimate layer for trainig data
# =============================================================================
# your code goes here
# Extract the middle frame of each gesture video
    

# =============================================================================
# Get the penultimate layer for test data
# =============================================================================
# your code goes here 
# Extract the middle frame of each gesture video


# =============================================================================
# Recognize the gesture (use cosine similarity for comparing the vectors)
# =============================================================================
fout = open("Results.csv", "w")
for i in range(len(names)):
    fout.write(str(predictions[i])+"\n")
fout.close()

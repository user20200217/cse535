# -*- coding: utf-8 -*-
"""
Created on Thu Jan 28 00:44:25 2021

@author: chakati
# edited by Kai Chi on 2/23/2024
# CSE535
# Arizona State University
"""
import cv2
import numpy as np
import os
import tensorflow as tf

## import the handfeature extractor class
import frameextractor2 as fe
import train2 as t

names = os.listdir("./test")
labels = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "LightOn", "LightOff", "FanOn", "FanOff", "IncreaseFanSpeed", "DecreaseFanSpeed", "SetThermo"]


# =============================================================================
# Get the penultimate layer for trainig data
# =============================================================================
# your code goes here
# Extract the middle frame of each gesture video

frames = list()
for i in range(len(names)):
    frame = fe.frameExtractor("./test/"+names[i])
    frame = frame / 255.0
    frames.append(frame)

frames = np.array(frames)
    

# =============================================================================
# Get the penultimate layer for test data
# =============================================================================
# your code goes here 
# Extract the middle frame of each gesture video

predictions = list()
#model = tf.keras.models.load_model("./model.h5")
model = t.main()
probabilities = model.predict(frames)
predictions=np.argmax(probabilities, axis=1)
print(predictions)

# =============================================================================
# Recognize the gesture (use cosine similarity for comparing the vectors)
# =============================================================================
fout = open("Results.csv", "w")
for i in range(len(predictions)):
    fout.write(str(predictions[i])+"\n")
fout.close()

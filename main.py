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


# =============================================================================
# Get the penultimate layer for trainig data
# =============================================================================
# your code goes here
# Extract the middle frame of each gesture video

frames = list()
#for name in names:
#    frames.append(fe.frameExtractor("./test/"+name, "./frames/", 0))

for i in range(len(names)):
    fe.frameExtractor("./test/"+names[i], "./frames/"+ "/%#05d.png" % (i+1), 20)

names2 = os.listdir("./frames")
for i in range(len(names2)):
    path = "./frames/"+ "/%#05d.png" % (i+1)
    if os.path.exists(path):
        img = cv2.imread(path, cv2.IMREAD_GRAYSCALE)
        img = np.array(img) / 255.0
        img = img.reshape(1, 600, 200)
        frames.append(img)
    else:
        print("error")

    

# =============================================================================
# Get the penultimate layer for test data
# =============================================================================
# your code goes here 
# Extract the middle frame of each gesture video

predictions = list()
model = tf.keras.models.load_model("./model.h5")
for frame in frames:
    probabilities = model.predict(frame)
    predictions.append(np.argmax(probabilities))

# =============================================================================
# Recognize the gesture (use cosine similarity for comparing the vectors)
# =============================================================================
fout = open("Results.csv", "w")
for i in range(len(names)):
    fout.write(str(predictions[i])+"\n")
fout.close()

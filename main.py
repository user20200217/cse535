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
import frameextractor as fe

# =============================================================================
# Get trainig data
# =============================================================================
# your code goes here
# Extract the middle frames of each gesture video

names = os.listdir("./train")
labels = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "LightOn", "LightOff", "FanOn", "FanOff", "IncreaseFanSpeed", "DecreaseFanSpeed", "SetThermo"]

for i in range(len(labels)):
    labels[i] = "H-" + labels[i] + ".mp4"

frames = list()
y_train = np.zeros(len(names))
for i in range(len(names)):
    frame = fe.frameExtractor("./train/"+names[i])
    frame = frame / 255.0
    for j in (range(len(labels))):
        if names[i].endswith(labels[j]):
            y_train[i] = j
            break
    frames.append(frame)
X_train = np.array(frames)

# =============================================================================
# train
# =============================================================================

model = tf.keras.models.Sequential([
    tf.keras.layers.Input(shape=(200, 200, 3)),  # 200 x 200 RGB images
    tf.keras.layers.Conv2D(3,3),
    tf.keras.layers.MaxPooling2D(),
    tf.keras.layers.Conv2D(3,3),
    tf.keras.layers.MaxPooling2D(),
    tf.keras.layers.Flatten(),
    tf.keras.layers.Dense(1000, activation="relu"),
    tf.keras.layers.Dense(17, activation="softmax"),  # last layer should be the size of the output
])
model.compile(optimizer="adam", loss="sparse_categorical_crossentropy", metrics="accuracy")
model.fit(X_train, y_train, epochs=50)


# =============================================================================
# Get the penultimate layer for test data
# =============================================================================
# your code goes here 
# Extract the middle frame of each gesture video

names = os.listdir("./test")
frames = list()
for name in names:
    frame = fe.frameExtractor("./test/"+name)
    frame = frame / 255.0
    frames.append(frame)

frames = np.array(frames)


# =============================================================================
# Recognize the gesture (not using cosine similarity for comparing the vectors)
# =============================================================================

predictions = list()
probabilities = model.predict(frames)
predictions=np.argmax(probabilities, axis=1)

# =============================================================================
# Save results
# =============================================================================

fout = open("Results.csv", "w")
for i in range(len(predictions)):
    fout.write(str(predictions[i])+"\n")
fout.close()

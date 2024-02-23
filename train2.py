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

def main():
    names = os.listdir("./train")
    labels = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "LightOn", "LightOff", "FanOn", "FanOff", "IncreaseFanSpeed", "DecreaseFanSpeed", "SetThermo"]

    for i in range(len(labels)):
        labels[i] = "H-" + labels[i] + ".mp4"

    # =============================================================================
    # Get trainig data
    # =============================================================================
    # your code goes here
    # Extract the middle frames of each gesture video

    y_train = np.zeros(len(names))
    for i in range(len(names)):
        fe.frameExtractor("./train/"+names[i], "./frames", "%#05d.png" % (i+1), 20)
        for j in (range(len(labels))):
            if names[i].endswith(labels[j]):
                y_train[i] = j
                break

    frames = list()
    for i in range(len(names)):
        path = "./frames"+"/%#05d.png" % (i+1)
        if os.path.exists(path):
            img = cv2.imread(path, cv2.IMREAD_GRAYSCALE)
            img = np.array(img) / 255.0
            frames.append(img)
        else:
            print("error")

    X_train = np.array(frames)
    print(X_train.shape, y_train.shape)

    # =============================================================================
    # train
    # =============================================================================

    model = tf.keras.models.Sequential([
    tf.keras.layers.Flatten(input_shape=(600, 200)),  # 600 x 200 grayscale images
        tf.keras.layers.Dense(17),  # last layer should be the size of the output
    ])
    loss_fn = tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True)
    model.compile(optimizer='adam', loss=loss_fn, metrics=['accuracy'])
    model.fit(X_train, y_train, epochs=50)

    # =============================================================================
    # save model
    # =============================================================================

    return model
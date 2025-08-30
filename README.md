# 🖼️ On-Device Image Classification App  

**On-Device Image Classification** is a simple Android app built with **Kotlin** and **Jetpack Compose**, powered by **TensorFlow Lite**.  
It uses an on-device machine learning model to classify images into categories such as **Cat, Horse, Truck, and Car**.  

This project demonstrates an end-to-end workflow for training an image classification model on the CIFAR-10 dataset and converting it into a TensorFlow Lite (TFLite) model for efficient on-device inference.
. The app demonstrates how to integrate **TensorFlow Lite models** in a modern Android application without requiring an internet connection.  

---

<div style="display: flex; flex-wrap: wrap;">
  <img src="https://github.com/user-attachments/assets/28590cea-b792-407b-b3c9-7f2bcd700cbd" width="32%" />
  <img src="https://github.com/user-attachments/assets/ea73a3db-5b8a-4505-8737-c87f0e6b7969" width="32%" />
  <img src="https://github.com/user-attachments/assets/0a30737e-55ed-4c09-80e7-7bb006deed61" width="32%" />
</div>
---

## Overview

- **Dataset:** CIFAR-10 (60,000 32x32 color images in 10 classes)
- **Framework:** TensorFlow / Keras
- **Deployment Target:** Mobile or Edge devices using TensorFlow Lite
- **Key Output:** Optimized `.tflite` model file for on-device use

---

## 🚀 Features  

- **Built with Jetpack Compose** – clean and modern UI  
- **Model Training:** Trains a compact neural network suitable for edge devices.
- **Model Conversion:** Converts the trained model to TensorFlow Lite format.
- **On-Device Ready:** The generated `.tflite` file is optimized for speed and size, making it ideal for mobile, embedded, or IoT applications.

---

## 🛠️ Tech Stack  

- **Kotlin** – primary development language  
- **Jetpack Compose** – modern UI toolkit  
- **TensorFlow Lite** – machine learning model inference  
- **Dependencies Used**:  
  ```toml
  tensorflow-lite-support = { group = "org.tensorflow", name = "tensorflow-lite-support", version.ref = "tensorflowLiteSupport" }
  tensorflow-lite-metadata = { group = "org.tensorflow", name = "tensorflow-lite-metadata", version.ref = "tensorflowLiteMetadata" }
  ```
---
## 📥 Clone the Project

```bash
https://github.com/Codexyze/OnDevice_Image-Classification

```
---

## Getting Started

### 1. Training the Model

The training workflow is implemented in the notebook [`Training/cidar-10.ipynb`](Training/cidar-10.ipynb). The notebook covers:

- Loading and preprocessing the CIFAR-10 dataset.
- Building a simple but effective CNN architecture.
- Training and evaluating the model.
- Exporting the trained model for conversion.

### 2. TFLite Model Conversion

The notebook guides you through converting the trained Keras model to the TensorFlow Lite format:

```python
import tensorflow as tf

# Load your trained Keras model
model = tf.keras.models.load_model('path/to/your/model.h5')

# Convert the model to TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Save the .tflite file
with open('model.tflite', 'wb') as f:
    f.write(tflite_model)
```

### 3. Using the TFLite Model

You can deploy the `model.tflite` file on Android, iOS, Raspberry Pi, or other compatible devices using TensorFlow Lite.

**Example for Android:**
- Integrate `model.tflite` with the TensorFlow Lite Android library for fast, on-device inference.

---

## Highlights

- **TFLite Model:**  
  The main artifact of this workflow is the TFLite model (`model.tflite`), which is:
  - Highly optimized for mobile and edge devices.
  - Small in size and fast in inference.
  - Easily integrated into mobile apps and embedded systems.

- **Deployment Ready:**  
  The exported TFLite model can be directly used in your mobile or IoT applications for real-time image classification.


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---


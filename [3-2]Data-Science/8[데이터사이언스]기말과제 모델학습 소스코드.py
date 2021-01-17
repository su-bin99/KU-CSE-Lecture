#!/usr/bin/env python
# coding: utf-8

# In[1]:


import numpy as np
import matplotlib.pyplot as plt

import tensorflow as tf
from tensorflow.keras.models import Sequential, load_model
from tensorflow.keras.layers import Input, Conv2D, MaxPooling2D, Dense, Flatten, Dropout
from keras.utils import to_categorical

from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report

from PIL import Image
import glob
import sys
from datetime import datetime


# In[2]:


def version_check():
    print(tf.__version__)
    print(np.__version__)
    print(sys.version)
    
version_check()


# In[3]:


data_dir = "C:/Users/Subin/Desktop/images"
categories = ['food','interior','exterior']
nb_class = len(categories)

X_test_all = []
X_train_all = []
Y_test_all = []
Y_train_all = []

###################################################
# 이미지 전처리 
###################################################


# 리스트 shuffle    
def np_shuffle(x,y):
    s = np.arange(x.shape[0])
    np.random.shuffle(s)
    
    x = x[s]
    y = y[s]
    return x,y

for idx,c in enumerate(categories):
    x = []
    y = []
    
    label = [0 for i in range(nb_class)]
    label[idx] = 1
    img_dir = data_dir + "/" +  c + "/"
    files = glob.glob(img_dir + '*.jpg')
    
    
    for i, f in enumerate(files) :
        img = Image.open(f)
        img = img.convert("RGB")
        img = img.resize((300, 300))
        data = np.asarray(img)
        x.append(data)
        y.append(label)

    # 4:1 비율로 train, test 데이터 분할  
    X_train, X_test, Y_train,Y_test = train_test_split(x, y, test_size=0.2, train_size=0.8,shuffle = True)
    X_train_all.extend(X_train)
    Y_train_all.extend(Y_train)
    X_test_all.extend(X_test)
    Y_test_all.extend(Y_test)
    
X_train_all = np.array(X_train_all)
X_test_all = np.array(X_test_all)
Y_train_all = np.array(Y_train_all)
Y_test_all = np.array(Y_test_all)

X_train, Y_train = np_shuffle(X_train_all , Y_train_all)
X_test, Y_test = np_shuffle(X_test_all , Y_test_all)


# In[4]:


###################################################
# 모델 생성 및 학습, 저장
###################################################

model = Sequential([
    Input(shape=(300,300,3), name='input_layer'),
    Conv2D(32, kernel_size=3, activation='relu', name='conv_layer1'),
    MaxPooling2D(pool_size=3),
    Conv2D(32, kernel_size=3, activation='relu', name='conv_layer2'),
    MaxPooling2D(pool_size=3),
    Conv2D(32, kernel_size=3, activation='softmax', name='conv_layer3'),
    Flatten(),
    Dense(32, activation='relu', name='Dense2'),
    Dense(3, activation='softmax', name='output_layer')
    ])

model.summary() 
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
    
##학습시작시간
s = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
print("학습 시작시간 : " , s)

history = model.fit(X_train, Y_train, validation_data=(X_test, Y_test), batch_size=32, epochs=5)

##학습종료시간
s = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
print("학습 종료시간 : " , s)

model.save('C:/Users/Subin/Desktop/model/finalmodel/model-201811259')

# train 데이터가 총 36000개
# batch size가 32
# epoch 5

# 1 epoch는 각 데이터의 size가 32인 batch가 들어간 1125번의 iteration
# 전체 데이터 셋에 대해서는 5번의 학습
# iteration기준으로 보면 1125 * 5 번의 학습


# In[5]:


###################################################
# 모델의 loss, accuracy, train_loss, train_accuracy 를 그래프로 나타내기
###################################################

ig, loss_ax = plt.subplots()

acc_ax = loss_ax.twinx()

loss_ax.plot(history.history['loss'], 'g', label='train loss')
loss_ax.plot(history.history['val_loss'], 'b', label='val loss')

acc_ax.plot(history.history['accuracy'], 'orange', label='train acc')
acc_ax.plot(history.history['val_accuracy'], 'coral', label='val acc')
 
loss_ax.set_xlabel('epoch')
loss_ax.set_ylabel('loss')
acc_ax. set_ylabel('accuracy')
 
loss_ax.legend(loc='upper left')
acc_ax.legend(loc='upper right')
 
plt.show()


# In[6]:


###################################################
# Test 데이터를 이용해서 prediction하는 함수 
###################################################

def predict_image_sample(model, X_test, y_test, test_id=-1):

    if test_id < 0:
        from random import randrange
        test_sample_id = randrange(len(X_test))
    else:
        test_sample_id = test_id
        
    test_image = X_test[test_sample_id]
    
    plt.imshow(test_image)
    
    test_image = test_image.reshape(1,300,300,3)

    y_actual = y_test[test_sample_id]

    if y_actual[0] == 1:
        print("Actual : 음식")
    elif y_actual[1] == 1:
        print("Actual : 실내")
    else:
        print("Actual : 실외")
        
    y_pred = model.predict(test_image)
    y_pred_n = np.argmax(y_pred, axis=1)[0]
    
    if y_pred_n == 0:
        print("Prediction : 음식")
    elif y_pred_n == 1:
        print("Prediction : 실내")
    else:
        print("Prediction : 실외")
    
    print("\n== Predictions ==")
    print("음식 확률 : ", y_pred[0][0])
    print("실내 확률 : ", y_pred[0][1])
    print("실외 확률 : ", y_pred[0][2])
    
model = load_model('C:/Users/Subin/Desktop/model/finalmodel/model-201811259')
predict_image_sample(model, X_test, Y_test)


# In[7]:


###################################################
# test데이터의 자체 성능 분석  
###################################################

model.evaluate(X_test, Y_test, batch_size = 32)


# In[8]:



Y_pred = model.predict(X_test, batch_size=32, verbose=1)
Y_pred_classNum = np.argmax(Y_pred, axis = 1)
Y_test_classNum = np.argmax(Y_test, axis = 1)

print(classification_report(Y_test_classNum, Y_pred_classNum))


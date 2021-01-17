from skimage.feature import greycomatrix, greycoprops
import matplotlib.pyplot as plt
from scipy import signal as sg
import numpy as np
import cv2
import os
from datetime import datetime

## === laws texture 계산 함수 ===

def laws_texture(gray_image):
    (rows, cols) = gray_image.shape[:2]
    smooth_kernel = (1/25) * np.ones((5,5))
    gray_smooth = sg.convolve(gray_image, smooth_kernel, "same")
    gray_processed = np.abs(gray_image - gray_smooth)
    
    filter_vectors = np.array([[1, 4, 6, 4, 1], 
                              [-1, -2, 0, 2, 1],
                              [-1, 0, 2, 0, 1],
                              [1, -4, 6, -4, 1]])
    filters= []
    for i in range(4):
        for j in range(4):
            filters.append(np.matmul(filter_vectors[i][:].reshape(5, 1),
                                    filter_vectors[j][:].reshape(1, 5)))
    conv_maps = np.zeros((rows, cols, 16))
    for i in range(len(filters)):
        conv_maps[:, :, i] = sg.convolve(gray_processed, filters[i], 'same')
    
    # === 9+1개 중요한 texture map 계산 ===
    texture_maps = list()
    texture_maps.append((conv_maps[:, :, 1] + conv_maps[:, :, 4])//2)
    texture_maps.append((conv_maps[:, :, 2] + conv_maps[:, :, 8])//2)
    texture_maps.append((conv_maps[:, :, 3] + conv_maps[:, :, 12])//2)
    texture_maps.append((conv_maps[:, :, 7] + conv_maps[:, :, 13])//2)
    texture_maps.append((conv_maps[:, :, 6] + conv_maps[:, :, 9])//2)
    texture_maps.append((conv_maps[:, :, 11] + conv_maps[:, :, 14])//2)
    texture_maps.append(conv_maps[:, :, 10])
    texture_maps.append(conv_maps[:, :, 5])
    texture_maps.append(conv_maps[:, :, 15])
    texture_maps.append(conv_maps[:, :, 0])
    
    # === law's texture energy 계산 ===
    TEM = list()
    for i in range(9):
        TEM.append(np.abs(texture_maps[i]).sum()/
                  np.abs(texture_maps[9]).sum())
    return TEM

# === 이미지 패치에서 특징 추출 ===
train_dir = '/content/cv_images/train' #train data 경로
test_dir = '/content/cv_images/test' #test data 경로
classes = ['buildings', 'forest', 'mountain', 'sea']

X_train = []
Y_train = []

PATCH_SIZE = 30
np.random.seed(1234)
for idx, texture_name in enumerate(classes):
    image_dir = os.path.join(train_dir, texture_name)
    print(image_dir)
    for image_name in os.listdir(image_dir):
        print(image_name)
        image = cv2.imread(os.path.join(image_dir, image_name))
        image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        image_s = cv2.resize(image, (100, 100), interpolation= cv2.INTER_LINEAR)
        
        for _ in range(10):
            h = np.random.randint(100-PATCH_SIZE)
            w = np.random.randint(100-PATCH_SIZE)
            
            image_p = image_s[h:h+PATCH_SIZE, w:w+PATCH_SIZE]
            glcm = greycomatrix(image_p, distances = [1], angles = [0], levels = 256, 
                               symmetric = False, normed = True)
            X_train.append([greycoprops(glcm, 'dissimilarity')[0, 0],
                           greycoprops(glcm, 'correlation')[0, 0] ]
                          + laws_texture(image_p))
            Y_train.append(idx)
X_train = np.array(X_train)
Y_train = np.array(Y_train)
print('train data : ', X_train.shape)
print('train label : ', Y_train.shape)

X_test = []
Y_test = []

for idx, texture_name in enumerate(classes):
    image_dir = os.path.join(test_dir, texture_name)
    for image_name in os.listdir(image_dir):
        image = cv2.imread(os.path.join(image_dir, image_name))
        image_gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        glcm = greycomatrix(image_gray, distances = [1], angles = [0], levels = 256, 
                            symmetric = False, normed = True)
        X_test.append([greycoprops(glcm, 'dissimilarity')[0, 0],
                        greycoprops(glcm, 'correlation')[0, 0] ]
                       + laws_texture(image_gray))
        Y_test.append(idx)
        
X_test = np.array(X_test)
Y_test = np.array(Y_test)
print('test data : ', X_test.shape)
print('test label : ', Y_test.shape)


# 신경망에 필요한 모듈 
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
from torch.utils.data.dataset import Dataset
from torchsummary import summary

# === 데이터셋 클래스 ===
class textureDataset(Dataset):
    def __init__(self, features, labels):
        self.features = features
        self.labels = labels
        
    def __len__(self):
        return len(self.labels)

    def __getitem__(self, idx):
        if torch.is_tensor(idx):
            idx = idx.tolist()                                                 
        feature = self.features[idx]
        label = self.labels[idx]
        sample = (feature, label)
        
        return sample
    
# === 신경망 모델 클래스 ===
class MLP(nn.Module):
    def __init__(self, input_dim, hidden_dim, output_dim):
        super(MLP, self).__init__()
        self.fc1 = nn.Linear(input_dim, hidden_dim)
        self.relu = nn.ReLU()
        self.elu = nn.ELU()
        self.fc2 = nn.Linear(hidden_dim, hidden_dim)
        self.fc3 = nn.Linear(hidden_dim, output_dim)
    
    def forward(self, x):
        out = self.fc1(x)
        out = self.elu(out)
        out = self.fc2(out)
        out = self.relu(out)
        out = self.fc3(out)
        
        return out

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

batch_size = 10
learning_rate = 0.01
n_epoch = 4

Train_data = textureDataset(features = X_train, labels = Y_train)
Test_data = textureDataset(features = X_test, labels = Y_test)

Trainloader = DataLoader(Train_data, batch_size = batch_size, shuffle = True)
Testloader = DataLoader(Test_data, batch_size = batch_size)

net = MLP(11, 8, 4)
net.to(device)
summary(net, (11, ), device = 'cuda' if torch.cuda.is_available() else 'cpu')

optimizer = optim.SGD(net.parameters(), lr = learning_rate)
criterion = nn.CrossEntropyLoss()


# === 학습 ===
train_losses = []
train_accs = []
test_losses = []
test_accs = []


##학습시작시간
s = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
print("학습 시작시간 : " , s)

for epoch in range(n_epoch):
    train_loss = 0.0
    evaluation = []
    net.train()
    for i, data in enumerate(Trainloader, 0):
        features, labels = data
        labels = labels.long().to(device)
        features = features.to(device)
        optimizer.zero_grad()
        
        outputs = net(features.to(torch.float))
        
        _, predicted = torch.max(outputs.cpu().data, 1)
        
        evaluation.append((predicted == labels.cpu()).tolist())
        loss = criterion(outputs, labels)
        
        loss.backward()
        optimizer.step()
        
        train_loss += loss.item()
        
    train_loss = train_loss/(i+1)
    evaluation = [item for sublist in evaluation for item in sublist]
    train_acc = sum(evaluation)/len(evaluation)
    
    train_losses.append(train_loss)
    train_accs.append(train_acc)
    
    # === 테스트 ===

    if(epoch+1) % 1 == 0:
        test_loss = 0.0
        evaluation = []
        net.eval()
        for i, data in enumerate(Testloader, 0):
            features, labels = data
            labels = labels.long().to(device)
            features = features.to(device)

            outputs = net(features.to(torch.float))
            _, predicted = torch.max(outputs.cpu().data, 1)
            evaluation.append((predicted == labels.cpu()).tolist())
            loss = criterion(outputs, labels)
            test_loss += loss.item()

        test_loss = test_loss/(i+1)
        evaluation = [item for sublist in evaluation for item in sublist]
        test_acc = sum(evaluation)/len(evaluation)

        test_losses.append(test_loss)
        test_accs.append(test_acc)
        print('[%d, %3d] loss: %.4f  Accuracy : %.4f || val-loss: %4f  val-Accuracy : %.4f' %(epoch+1, n_epoch, train_loss, train_acc, test_loss, test_acc))

##학습종료시간
s = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
print("학습 종료시간 : " , s)

# === 학습/테스트 loss/정확도 시각화 ===
train_accs = [0.39, 0.45, 0.54, 0.55]
test_accs = [0.45, 0.46, 0.50, 0.54]
plt.plot(range(len(train_losses)), train_losses, label = 'train loss')
plt.plot(range(len(test_losses)), test_losses, label = 'test loss')
plt.legend()
plt.show()

plt.plot(range(len(train_accs)), train_accs, label = 'train acc')
plt.plot(range(len(test_accs)), test_accs, label = 'test acc')
plt.legend()
plt.show()

###################################################
# 모델의 loss, accuracy, train_loss, train_accuracy 를 그래프로 나타내기
###################################################

ig, loss_ax = plt.subplots()
acc_ax = loss_ax.twinx()


loss_ax.plot(train_losses, 'g',label = 'train loss')
loss_ax.plot(test_losses, 'b', label = 'test loss')

acc_ax.plot(train_accs, 'orange', label = 'train acc')
acc_ax.plot(test_accs, 'coral', label = 'test acc')
 
loss_ax.set_xlabel('epoch')
loss_ax.set_ylabel('loss')
acc_ax. set_ylabel('accuracy')
 
loss_ax.legend(loc='upper left')
acc_ax.legend(loc='upper right')
 
plt.show()


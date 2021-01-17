import matplotlib.pyplot as plt
import numpy as np
import cv2
import os

# 신경망에 필요한 모듈 
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
from torch.utils.data.dataset import Dataset
from torchsummary import summary


# === 이미지 패치에서 특징 추출 ===
train_dir = '/content/cv_images/train' #train data 경로
test_dir = '/content/cv_images/test' #test data 경로
classes = ['buildings', 'forest', 'mountain', 'sea']


X_train = []
Y_train = []

PATCH_SIZE = 32
np.random.seed(1234)
for idx, texture_name in enumerate(classes):
    image_dir = os.path.join(train_dir, texture_name)
    for image_name in os.listdir(image_dir):
        image = cv2.imread(os.path.join(image_dir, image_name))
        # image = cv2.resize(image, (32, 32), interpolation= cv2.INTER_LINEAR)
        # X_train.append(image)
        # Y_train.append(idx)
        image_s = cv2.resize(image, (100, 100), interpolation= cv2.INTER_LINEAR)
        
        for _ in range(10):
            h = np.random.randint(100-PATCH_SIZE)
            w = np.random.randint(100-PATCH_SIZE)
            
            image_p = image_s[h:h+PATCH_SIZE, w:w+PATCH_SIZE]
            X_train.append(image_p)
            Y_train.append(idx)
X_train = np.array(X_train)/128-1
Y_train = np.array(Y_train)
print('train data : ', X_train.shape)
print('train label : ', Y_train.shape)

X_test = []
Y_test = []

for idx, texture_name in enumerate(classes):
    image_dir = os.path.join(test_dir, texture_name)
    for image_name in os.listdir(image_dir):
        image = cv2.imread(os.path.join(image_dir, image_name))
        image = cv2.resize(image, (32, 32), interpolation= cv2.INTER_LINEAR)

        X_test.append(image)
        Y_test.append(idx)
        
X_test = np.array(X_test)/128-1
Y_test = np.array(Y_test)
print('test data : ', X_test.shape)
print('test label : ', Y_test.shape)

class Dataset(Dataset):
    def __init__(self, images, labels):
        self.images = images
        self.labels = labels
    
    def __len__(self):
        return len(self.labels)
    
    def __getitem__(self, idx):
        if torch.is_tensor(idx):
            idx = idx.tolist()
        image = self.images[idx]
        label = self.labels[idx]
        sample = (image, label)
        return sample
    
class MLP(nn.Module):
    def __init__(self, input_dim, hidden_dim1, hidden_dim2, output_dim):
        super(MLP, self).__init__()
        self.fc1 = nn.Linear(input_dim, hidden_dim1)
        self.fc2 = nn.Linear(hidden_dim1, hidden_dim1)
        self.fc3 = nn.Linear(hidden_dim1, hidden_dim2)
        self.fc4 = nn.Linear(hidden_dim2, hidden_dim2)
        self.fc5 = nn.Linear(hidden_dim2, output_dim)
        self.dropout = nn.Dropout(p = 0.5)
        self.relu = nn.ReLU()
        self.elu = nn.ELU()
    
    def forward(self, x):
        x = torch.flatten(x, 1)
        out = self.fc1(x)
        out = self.elu(out)
        out = self.fc2(out)
        out = self.relu(out)
        out = self.fc3(out)
        out = self.elu(out)
        out = self.fc4(out)
        out = self.relu(out)
        out = self.fc5(out)
        return out
    
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

batch_size = 10
learning_rate = 0.001
n_epoch = 7

Train_data = Dataset(images = X_train, labels = Y_train)
Test_data = Dataset(images = X_test, labels = Y_test)

Trainloader = DataLoader(Train_data, batch_size = batch_size, shuffle = True)
Testloader = DataLoader(Test_data, batch_size = batch_size)



net = MLP(32*32*3 , 1024, 128, 4)
net.to(device)
summary(net, (32, 32, 3), device = 'cuda' if torch.cuda.is_available() else 'cpu')

optimizer = optim.Adam(net.parameters(), lr = learning_rate)
criterion = nn.CrossEntropyLoss()

# Commented out IPython magic to ensure Python compatibility.
# === 학습 ===


train_losses = []
train_accs = []
test_losses = []
test_accs = []

for epoch in range(n_epoch):
    train_loss = 0.0
    evaluation = []
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
        print('[%d, %3d] loss: %.4f  Accuracy : %.4f || val-loss: %4f  val-Accuracy : %.4f'   %(epoch+1, n_epoch, train_loss, train_acc, test_loss, test_acc))

# === 학습/테스트 loss/정확도 시각화 ===
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
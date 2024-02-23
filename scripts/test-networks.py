import torch
from torchvision import datasets, transforms
from torch.utils.data import DataLoader, Dataset, random_split
import glob
from torch import nn
import torch.nn.functional as F


class CNNMnist(nn.Module):

    def __init__(self):
        super(CNNMnist, self).__init__()
        self.conv1 = nn.Conv2d(1, 10, kernel_size=5)
        self.conv2 = nn.Conv2d(10, 20, kernel_size=5)
        self.conv2_drop = nn.Dropout2d()
        self.fc1 = nn.Linear(320, 50)
        self.fc2 = nn.Linear(50, 10)

    def forward(self, x):
        x = F.relu(F.max_pool2d(self.conv1(x), 2))
        x = F.relu(F.max_pool2d(self.conv2_drop(self.conv2(x)), 2))
        x = x.view(-1, x.shape[1]*x.shape[2]*x.shape[3])
        x = F.relu(self.fc1(x))
        x = F.dropout(x, training=self.training)
        x = self.fc2(x)
        return F.log_softmax(x, dim=1)


def get_test_dataset(fashion=False):
    data_dir = 'data/'
    apply_transform = transforms.Compose([
                        transforms.ToTensor(),
                        transforms.Normalize((0.5), (0.5)),
                    ])
    if fashion:
        test_dataset = datasets.FashionMNIST(data_dir,
                                      train=False,
                                      download=True,
                                      transform=apply_transform)
    else:
        test_dataset = datasets.MNIST(data_dir,
                                      train=False,
                                      download=True,
                                      transform=apply_transform)

    return test_dataset



def test_inference(model, test_dataset):
    model.eval()
    loss, total, correct = 0.0, 0.0, 0.0

    device = 'cpu'
    criterion = nn.NLLLoss().to(device)
    testloader = DataLoader(test_dataset, batch_size=128,
                            shuffle=False)

    for batch_idx, (images, labels) in enumerate(testloader):
        images, labels = images.to(device), labels.to(device)

        # Inference
        outputs = model(images)
        batch_loss = criterion(outputs, labels)
        loss += batch_loss.item()

        # Prediction
        _, pred_labels = torch.max(outputs, 1)
        pred_labels = pred_labels.view(-1)
        correct += torch.sum(torch.eq(pred_labels, labels)).item()
        total += len(labels)

    accuracy = correct / total
    return accuracy, loss



if __name__ == '__main__':

    test_dataset = get_test_dataset(fashion=False)
    epoch  = 13
    networks = glob.glob(f'../networks/*network{epoch}')
    test_accuracies, test_losses = [], []

    for network in networks:
        model = CNNMnist()
        model.load_state_dict(torch.load(network))
        test_acc, test_loss = test_inference(model, test_dataset)
        test_accuracies.append(test_acc)
        test_losses.append(test_loss)

    mean_test_acc = sum(test_accuracies) / len(test_accuracies)
    data = [[mean_test_acc]]
    df = pd.DataFrame(data=data, columns=['Test Accuracy'])
    df.to_csv('fbfl_test_accuracy.csv', index=False)











#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <mqueue.h>
#include <errno.h>
#include <math.h>

#define MSG_SIZE	4
#define NAME		"/m_queue"


void dosuAlgorithm(int returnRankArr[], int i, int inputNum) {
	int rank = inputNum / i;
	returnRankArr[rank]++;
}

void fileRead(char * filename, int readBytes, int Offset, char restore[]) {
	int fd;
	if ((fd = open(filename, O_RDONLY)) == -1) {
		printf("open에러 :%d", errno);
	}
	if (pread(fd, restore, readBytes, Offset) == -1) {
		printf("read에러 : %d", errno);
	}
	close(fd);
}

int main(int argc, char *argv[]) {

	int n = atoi(argv[1]) -1;          //프로세스, 쓰레드 갯수
	int i = atoi(argv[2]);          //간격크기
	char* file_name = argv[3];

	int rankNum = 1 + 9999 / i;     //도수분포 rank갯수
	int returnRank[rankNum];        //도수분포 배열

	for (int rank = 0; rank < rankNum; rank++) {
		returnRank[rank] = 0;
	}


	FILE *file = fopen(file_name, "r");
	if (file == NULL) {
		printf("File Open Error!\n");
		exit(0);
	}

	int amount;                     //숫자갯수
	int amountSize = 1;		//amout의 자릿수

	fscanf(file, "%d", &amount);    //amount는 숫자의 갯수
	fclose(file);
	int eachProcess;   //각 자식프로세스가 확인하는 정수갯수
	if(n ==0) eachProcess = 0;
	else eachProcess=amount / n;
	int remainder = amount- eachProcess*n;	//부모프로세스가 확인하는 정수갯수
	int size;
	if(eachProcess > remainder)
		size = eachProcess;       //한 프로세스 내에 정수가저장될 데이터배열
	else
		size = remainder;
	int dataArr[size];
	
	int amountSame = amount;
	while (amountSame > 10) {
		amountSame /= 10;
		amountSize++;
	}

	struct mq_attr attr;
	mqd_t mqdes;
	attr.mq_maxmsg = 10;
	attr.mq_msgsize = MSG_SIZE;

	mq_unlink(NAME) ;
	//----------------------------------------------------------------------
	//fork 부분

	pid_t pid[n];
	for (int j = 0; j < n; j++) {
		pid[j] = 99;
	}

	for (int j = 0; j < n; j++) {
		if ((pid[j] = fork()) == 0) {
			break;
		}
	}

	//----------------------------------------------------------------------
	//----------------------------------------------------------------------
	//자식프로세스부분
	//----------------------------------------------------------------------
	//----------------------------------------------------------------------
	for (int j = 0; j < n; j++) {//j는 몇번째 프로세스인

		if (pid[j] == 0) {
			int fd;
			char buffer[30];

			for (int p = 0; p < eachProcess; p++) {
				fileRead(file_name, 5, 1 + amountSize + j * eachProcess * 5 + p * 5, buffer);
				dataArr[p] = atoi(buffer);
				dosuAlgorithm(returnRank, i, dataArr[p]);
			}

			//----------------------------------------------------------------------
			//메세지큐 센드부분

			mqdes = mq_open(NAME, O_CREAT | O_WRONLY, 0666, &attr);
			if (mqdes < 0) {
				perror("mq_open()");
				exit(0);
			}
			for (int k = 0; k < rankNum; k++) {
				int value = returnRank[k];
				if (mq_send(mqdes, (char*)&value, MSG_SIZE, k) < 0) {
					perror("mq_send");
				}
			}
			mq_close(mqdes);
			exit(0);
		}
	}
	//----------------------------------------------------------------------
	//----------------------------------------------------------------------
	//부모프로세스부분
	//----------------------------------------------------------------------
	//----------------------------------------------------------------------
	mqdes = mq_open(NAME, O_CREAT | O_RDWR, 0666, &attr);

	if (mqdes < 0) {
		perror("부모open()에러");
		exit(0);
	}
	int receiveNum = 0;
	int value = 0;
	int rank;
	if(n != 0){
		while (1) {
			if (mq_receive(mqdes, (char*)&value, MSG_SIZE, &rank) != -1) {
				receiveNum++;
				returnRank[rank] += value;
			}
			else {
				perror("receive에러");
				printf("pid :%d\n", getpid());
				break;
			}
			if (receiveNum == n * rankNum) {
				break;
			}
		}
	}
        for (int k = 0; k < remainder; k++) {

                int fd;
                char buffer[30];
		fileRead(file_name, 5, 1 + amountSize + n * eachProcess * 5 + k * 5 , buffer);
                dataArr[k] = atoi(buffer);
                dosuAlgorithm(returnRank, i, dataArr[k]);
        }


	for (int k = 0; k < rankNum; k++) {
		printf("%d\n", returnRank[k]);
	}

}

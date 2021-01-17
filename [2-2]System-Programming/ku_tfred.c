#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/types.h>
#include <string.h>



typedef struct para {
	int eachThread;
	char* file_name;
	int i;
	int amountSize;
	int rankNum;
}PARA;

int num = 0;
int * returnRank[4];
pthread_mutex_t mymutex;
pthread_mutex_t mymutex2;
pthread_mutex_t mymutex3;

void dosuAlgorithm(int* returnRankArr[], int i, int inputNum) {
        int rank = inputNum / i;
        returnRankArr[rank]++;
}

int fileRead(char * filename, int readBytes, int Offset, char restore[]) {
	int fd;
	if ((fd = open(filename, O_RDONLY)) == -1) {
		printf("open에러 :%d", errno);
	}
	if (pread(fd, restore, readBytes, Offset) == -1) {
		printf("read에러 : %d", errno);
	}
	close(fd);
}

void *thread_function(void *data) { //file_name    몇번째 쓰레드인지 p

	pthread_mutex_lock(&mymutex);

	PARA * mypara = (PARA*)data;
	int j = num++;
	int i = mypara->i;
	int eachThread = mypara->eachThread;
	int amountSize = mypara->amountSize;
	int rankNum = mypara->rankNum;
	char * file_name = mypara->file_name;

	pthread_mutex_unlock(&mymutex);

	int fd;
	int size;
	int dataArr[eachThread];	// 이 쓰레드에서 읽어오는 eachThread만큼의 숫자를 저장하는 배열
	int pr;
	char number[30];

	for (int p = 0; p < eachThread; p++) {
		fileRead(file_name, 5, 1 + amountSize + j * eachThread * 5 + p * 5, number);
		dataArr[p] = atoi(number);
		
		pthread_mutex_lock(&mymutex2);
		dosuAlgorithm(returnRank, i, dataArr[p]);
				
		pthread_mutex_unlock(&mymutex2);
	}
}


int main(int argc, char *argv[]) {

	int thr_id;
	int amount;                     //총 숫자의 갯수
	int n = atoi(argv[1])-1 ;          //프로세스, 쓰레드 갯수
	int i = atoi(argv[2]);          //간격크기

	int rankNum = 1 + 9999 / i;

	*returnRank = malloc(sizeof(int) * rankNum);
	int amountSize = 1;
	for (int j = 0; j < rankNum; j++) {
		returnRank[j] = 0;
	}
	pthread_t p_thread[n];

	char * file_name = argv[3];

	FILE *file = fopen(file_name, "r");
	if (file == NULL) {
		printf("File Open Error!\n");
		exit(0);
	}
	fscanf(file, "%d", &amount);    //amount는 숫자의 갯수
	int amountSame = amount;
	while (amountSame > 10) {
		amountSame /= 10;
		amountSize++;
	}

	int eachThread;
	if(n == 0) eachThread = 0;
	else eachThread = amount / n;

	for (int j = 0; j < n; j++) {
		PARA p1;
		p1.eachThread = eachThread;
		p1.amountSize = amountSize;
		p1.rankNum = rankNum;
		p1.i = i;
		p1.file_name = file_name;

		thr_id = pthread_create(&p_thread[j], NULL, thread_function, (void*)&p1);
		if (thr_id < 0) {
			perror("%쓰레드 생성에러");
		}
	}
        int remainder = amount - n * eachThread;
	int dataArr[remainder];
	char number[30];
	int fd;
	int pr;

	for (int p = 0; p < remainder; p++) {

		fileRead(file_name, 5, 1 + amountSize + n * eachThread * 5 + p * 5, number);
		dataArr[p] = atoi(number);
		
		pthread_mutex_lock(&mymutex3);
		
		dosuAlgorithm(returnRank, i, dataArr[p]);
		
		pthread_mutex_unlock(&mymutex3);
	}

	int status;

	for (int j = 0; j < n; j++) {
		pthread_join(p_thread[j], (void**)&status);
	}

	for (int j = 0; j < rankNum; j++) {
		int i = (int)returnRank[j] / 4;
		printf("%d\n", i);
	}

}

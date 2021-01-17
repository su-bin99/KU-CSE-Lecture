#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>
#include <signal.h>
#include <errno.h>
#define INTERVAL 1

typedef struct Node {
	struct Node* prev;
	struct Node* next;
	int myPid;
	double vruntime;
	int nice;
}Node;

typedef struct linkedList {
	struct Node* head;      //head노드에는 저장X
	struct Node* tail;	//tail노드에는 저장X
	int nodeNum;
	//vruntime이 작은게 앞쪽에 있도록 sorting
}linkedList;

//변수
Node* runningNode;
linkedList* mylinkedList;

struct itimerval timer;
struct sigaction action;

int timesliceCount = 0;
int timesliceNum;
int processNum[5];
double constantValue[5] = {0.64, 0.8, 1, 1.25, 1.56};
char str[1] = "A";

pid_t parentPid;

//함수
linkedList* newList();

Node* newNode(int Pid, double vruntime, double nice);
void insertNode(Node* node);
void deleteNode(Node* node);
void killNode(Node* node);

void timer_handler(int signum);


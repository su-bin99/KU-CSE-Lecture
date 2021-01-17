#include "ku_cfs.h"

int main(int argc, char *argv[]) {
	runningNode = (Node*)malloc(sizeof(Node));
	parentPid = getpid();

	for(int i = 0 ; i < 5 ; i++){
		processNum[i] = atoi(argv[i+1]);
	}
	timesliceNum = atoi(argv[6]);
	
	//1. Node(PSB)를 저장할 linkedLIst생성 
	mylinkedList = newList();
	
	//2. 프로세스들 fork하기
	for(int i = 0 ; i < 5 ; i++){
		pid_t myPid;
		for( int j = 0 ; j < processNum[i] ; j++){
			myPid = fork(); 

			if(myPid ==0){//자식프로세스 
                        	if(execl("./ku_app" ,"ku_app", str, (char*)0)<0) {
					printf("%d...\n", errno);
					perror("execl");
				}
                        	break;
               		 }
                	else if(myPid > 0) //부모프로세스
				insertNode(newNode(myPid, 0, i-2));	
                	else printf("fork() fail");
                	
			str[0] += 1;
		}
	}	
	sleep(5);


	//3. timer초기화 및 실행

	action.sa_handler = timer_handler; 
	sigfillset(&action.sa_mask);	
	sigaction(SIGALRM, &action, NULL);

	timer.it_value.tv_sec = INTERVAL;
	timer.it_value.tv_usec = 0;
	timer.it_interval.tv_sec= INTERVAL;
	timer.it_interval.tv_usec = 0;	
	if(setitimer(ITIMER_REAL, &timer, (struct itimerval*)NULL)==-1){
		perror("setitimer");
		exit(1);
	}

	while(1){
		if(timesliceCount> timesliceNum) break;	
		if(getitimer(ITIMER_REAL , &timer) == -1){
			perror("getitimer");
			exit(1);
		}	
	}
	
	while( mylinkedList->head->next != mylinkedList->tail){
		deleteNode(mylinkedList->head->next);
	}
	free(mylinkedList->head);
	free(mylinkedList->tail);
	
	return 0;
}

linkedList* newList(){	
	//linkedList구조체를 생성하고 초기화한뒤 
	//해당 linkedList구조체 포인터 변수를 리턴하는 함수
	linkedList* mylinkedList= (linkedList*)malloc(sizeof(linkedList));
	
	mylinkedList->head = newNode(0, 0, 0);
	mylinkedList->tail = newNode(0, 0, 0);
	
	mylinkedList->head->prev = NULL;
	mylinkedList->head->next = mylinkedList->tail;
	mylinkedList->tail->prev = mylinkedList->head;
	mylinkedList->tail->next = NULL;
	
	mylinkedList->nodeNum = 0;
	
	return mylinkedList;
}
		
Node* newNode( int Pid, double vruntime, double nice){
	//Node구조체를 생성하고 
	//인자로 받아오는 값들로 구조체 멤버들을 초기화해준뒤 
	//해당 Node구조체 포인터 변수를 리턴하는 함수
 
	Node* node = (Node*)malloc(sizeof(Node));
	node->prev = (Node*)malloc(sizeof(Node));
	node->next = (Node*)malloc(sizeof(Node));

	node->myPid = Pid;
	node->vruntime = vruntime;
	node->nice = nice;
	node->nice = nice;
	
	node->prev = NULL;
	node->next = NULL;

	return node;
}

void insertNode(Node* node){
	//인자로 받아오는 Node를
	//linkedList의 알맞은 위치에 insert하는 함수

	Node* temp = mylinkedList->head;
	while(temp->vruntime <= node ->vruntime){
		temp = temp->next ;
		if(temp == mylinkedList ->tail) break;
	}
	Node* temp2 = temp->prev;

	temp->prev = node;
	node->next = temp;
	node->prev = temp2;
	temp2->next = node;

}	

void deleteNode(Node* node){
	//인자로 받아오는 Node를 linkedList에서 delete하는 함수 
	
	node -> prev -> next = node->next;
	node -> next -> prev = node->prev;
	node -> prev = NULL;
	node -> next = NULL;
}

void killNode(Node * node){
	//인자로 받아오는 Node를 linkedList에서 delete한 뒤 
	//동적할당한 메모리를 free해주는 함수 

	deleteNode(node);
	free(node);
}


void timer_handler(int signum){	
	//SIGALRM이 울리면 실행할 핸들러
	//실행중인 프로세스(runningNode)를 멈추게하고
	//vruntime이 가장작은 프로세스 (linkedList의 맨앞의 노드)를 실행시키는 함수 
	
	timesliceCount ++;

	if(runningNode-> myPid == 0 ){
		runningNode = mylinkedList->head->next;
		deleteNode(runningNode);
		kill(runningNode->myPid, SIGCONT);
	}
	else{
	  	runningNode->vruntime += INTERVAL* constantValue[runningNode->nice+2];
	    	insertNode(runningNode);
		kill(runningNode->myPid, SIGSTOP);	        
	     	runningNode = mylinkedList->head->next;
              	deleteNode(runningNode);
               	kill(runningNode->myPid, SIGCONT);	       

	}
}


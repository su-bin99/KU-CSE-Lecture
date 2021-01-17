#include <stdio.h>
#include <stdlib.h>

//ku_mmu_init()의 반환값으로 여러분이 만든 PhysicalMemory의 시작주소
//ku_run_proc()을 통해 지금 실행할 process의 PDBR을 받아감
//ku_traverse()에서 이 PhysicalMemory와 PDBR을 통해 
//특정 PID를 가진 process의 특정 VirtualAddress에 접근하게 됩니다.

typedef struct ku_pte {
	char pte;
}ku_pte;

typedef struct ku_mmu_Node { //페이지의 정보를 가지는 노드  
	struct ku_mmu_Node *prev;
	struct ku_mmu_Node *next;
	char pid;
	int level;
	int index;
	int pfn;
	struct ku_pte *pte;  
}ku_mmu_Node;

typedef struct ku_mmu_LIST {
	struct ku_mmu_Node *head;
	struct ku_mmu_Node *tail;
}ku_mmu_List;

typedef struct ku_mmu_Page { //4바이트의 페이지 
	char level, index, pid, a;
}ku_mmu_Page;


typedef ku_mmu_Page* ku_mmu_Space;
ku_mmu_Space *ku_mmu_Memory, *ku_mmu_Swap; //페이지배열로 활용하는 메모리 공간 
ku_mmu_List *ku_mmu_list, *ku_mmu_swapList;
unsigned int ku_mmu_memorysize = 0, ku_mmu_swapsize = 0;//메모리배열 사이즈 

ku_mmu_Node * ku_mmu_generateNode(char pid, int level, int index, int pfn, ku_pte *pte) {
	ku_mmu_Node* newNode = (ku_mmu_Node*)malloc(sizeof(ku_mmu_Node));
	newNode->prev = (ku_mmu_Node*)malloc(sizeof(ku_mmu_Node));
	newNode->next = (ku_mmu_Node*)malloc(sizeof(ku_mmu_Node));

	newNode->pid = pid;
	newNode->level = level;
	newNode->pte = pte;
	newNode->index = index;
	newNode->pfn = pfn;

	newNode->prev = NULL;
	newNode->next = NULL;
	return newNode;
}

ku_mmu_List* ku_mmu_setList() {
	ku_mmu_List* list = (ku_mmu_List*)malloc(sizeof(ku_mmu_List));
	list->head = ku_mmu_generateNode(0, 0, 0, 0, NULL);
	list->tail = ku_mmu_generateNode(0, 0, 0, 0, NULL);

	list->head->prev = NULL;
	list->head->next = list->tail;
	list->tail->prev = list->head;
	list->tail->next = NULL;

	return list;
}

void ku_mmu_setPte(ku_pte *ku_pte, int pfn, int present) {
	//메모리에 있으면 maped상태 , present가 1
	//Swap에 있으면 unmaped상태, present가 0

	if (present == 1) {//메모리에 있으면 
		int pfn6bits = pfn << 2;
		ku_pte->pte = pfn6bits | present;
	}
	else {//swap에 있으면 
		int pfn7bits = pfn << 1;
		ku_pte->pte = pfn7bits | present;
	}
}

void * ku_mmu_init(unsigned int mem_size, unsigned int swap_size) {
	ku_mmu_list = ku_mmu_setList();
	ku_mmu_swapList = ku_mmu_setList();

	ku_mmu_Memory = (ku_mmu_Space*)malloc(sizeof(mem_size));
	ku_mmu_Swap = (ku_mmu_Space*)malloc(sizeof(swap_size));

	//ku_mmu_setPage(ku_mmu_Memory[0], 0, 0, 0); //(pid 0으로)페이지 디렉토리 0번 할당 
	ku_mmu_Memory[0] = (ku_mmu_Page*)malloc(4);
	ku_mmu_Memory[0]->index = 0;
	ku_mmu_Memory[0]->level = 0;
	ku_mmu_Memory[0]->pid = 0;
	
	ku_mmu_memorysize = mem_size / 4;
	ku_mmu_swapsize = swap_size / 4;

	return *ku_mmu_Memory;
}

void ku_mmu_deletefromList(ku_mmu_Node* node) {
	ku_mmu_Node* left = node->prev;
	ku_mmu_Node* right = node->next;
	left->next = right;
	right->prev = left;
	node->next = NULL;
	node->prev = NULL;
}

void ku_mmu_addNodetoList(ku_mmu_Node* node, ku_mmu_List *list) {
	ku_mmu_Node* temp = (ku_mmu_Node*)malloc(sizeof(ku_mmu_Node));
	temp = ku_mmu_list->tail->prev;
	temp->next = node;
	node->prev = temp;
	node->next = ku_mmu_list->tail;
	ku_mmu_list->tail->prev = node;
}


int ku_mmu_findNullspace2() {
	for (int i = 0; i < ku_mmu_swapsize; i++) {
		if (ku_mmu_Swap[i] == NULL) return i;
	}
}

int ku_mmu_bitOp(int level, char va) {
	unsigned myVa = va;
	char pdMask = 0xc0; // 1100 0000
	char pmdMask = 0x30; // 0011 0000
	char ptMask = 0x0c; // 0000 1100
	char pfMask = 0x03; // 0000 0011

	switch (level) {
	case 0: return (myVa & pdMask) >> 6;
	case 1: return (myVa & pmdMask) >> 4;
	case 2: return (myVa & ptMask) >> 2;
	case 3: return (myVa & pfMask);
	}
}

ku_mmu_Node * ku_mmu_checkPD(char pid, int level, int index, ku_mmu_List* list) {
	//리스트에 해당 pid에 대한 page가 있는지 확인
	ku_mmu_Node *temp;
	temp = list->head->next;

	while (temp != list->tail) {
		if ((temp->pid == pid) && (temp->level == level) && (temp->index == index)) return temp;
		temp = temp->next;
	}
	if (temp == list->tail) {
		return NULL;
	}
	else
		return temp;
}

int ku_mmu_swapOut(char va) {
	ku_mmu_Node* temp = ku_mmu_list->head->next;
	while (temp != ku_mmu_list->tail) {
		if (temp->level == 3)break;
		temp = temp->next;
	}
	ku_mmu_Page * pagetodelete = ku_mmu_Memory[temp->pfn];
	ku_mmu_Memory[temp->pfn] = NULL;
	ku_mmu_deletefromList(temp);
	int nullIndex = temp->pfn;

	ku_mmu_addNodetoList(temp, ku_mmu_swapList);
	temp->pfn = ku_mmu_findNullspace2();
	ku_mmu_setPte(temp->pte, 0, 0);

	int preindex = ku_mmu_bitOp(1, va);
	ku_mmu_Node* prenode = ku_mmu_checkPD(temp->pid, 2, preindex, ku_mmu_list);
	ku_mmu_setPte(prenode->pte, temp->pfn, 0);
	ku_mmu_Swap[temp->pfn] = pagetodelete;

	return nullIndex;
}

int ku_mmu_findNullspace(char va) {

	for (int i = 0; i < ku_mmu_memorysize; i++) {
		if (ku_mmu_Memory[i] == NULL) return i;
		//else  printf("memory[%d]의 pid : %d", i, ku_mmu_Memory[i]->pid);

	}
	return ku_mmu_swapOut(va);

}

ku_mmu_Node* ku_mmu_generatePage(int level, int index, char pid, char va) {
	//페이지 생성, 메모리에 연결
	ku_mmu_Page *temp = (ku_mmu_Page*)malloc(sizeof(ku_mmu_Page));
	//ku_mmu_setPage(temp, level, index, pid);
	temp->level = level;
	temp->index = index;
	temp->pid = pid;
	ku_pte *myPte;
	myPte = (ku_pte*)malloc(sizeof(ku_pte));
	int pfnofMypte = ku_mmu_findNullspace(va);

	ku_mmu_setPte(myPte, 0, 0);
	if (level == 1) {
		ku_mmu_Node* prenode = ku_mmu_checkPD(pid, 0, 0, ku_mmu_list);
		ku_mmu_setPte(prenode->pte, pfnofMypte, 1);
	}
	else if(level>= 2) {
		int preindex = ku_mmu_bitOp(level - 2, va);
		ku_mmu_Node* prenode = ku_mmu_checkPD(pid, level-1, preindex, ku_mmu_list);
		ku_mmu_setPte(prenode->pte, pfnofMypte, 1);
	}
	ku_mmu_Memory[pfnofMypte] = temp;

	//list에 추가 
	ku_mmu_Node *newNode = ku_mmu_generateNode(pid, level, index, pfnofMypte, myPte);
	ku_mmu_addNodetoList(newNode, ku_mmu_list);

	return newNode;
}

void ku_mmu_swapIn(ku_mmu_Node* node, char va) {
	ku_mmu_Page * page = ku_mmu_Swap[node->pfn];
	ku_mmu_Swap[node->pfn] = NULL;
	ku_mmu_deletefromList(node);

	ku_mmu_addNodetoList(node, ku_mmu_list);
	node->pfn = ku_mmu_findNullspace(va);
	ku_mmu_setPte(node->pte, 0, 1);
	int preindex = ku_mmu_bitOp(1, va);
	ku_mmu_Node* prenode = ku_mmu_checkPD(node->pid, 2, preindex, ku_mmu_list);
	ku_mmu_setPte(prenode->pte, node->pfn, 1);

	ku_mmu_Memory[node->pfn] = page;
}

int ku_mmu_checkPage(int pmdIndex, int ptIndex, int finIndex) {
	//pd까지는 당연히 있는 상태
	//마지막 page는 없는 상태 
	int num = 0;
	ku_mmu_Node *temp = ku_mmu_list->head->next;
	while (temp != ku_mmu_list->tail) {
		if (temp->level == 1 && temp->index == pmdIndex) num += 1;//pmd까지 있음
		if (temp->level == 2 && temp->index == ptIndex) num += 1;//pt까지 있음 

		temp = temp->next;
	}
	return num;
	//num이 0 이면 pd만 있는거 -> 다 만들어줘야됨
	//1이면 pmd까지 있는거 -> 다 만들어줘야됨
	//2이면 pt까지 있는거 -> 만들던가 swap in해줘야됨 
}

int ku_run_proc(char pid, struct ku_pte **ku_cr3) {
	ku_mmu_Node *node = ku_mmu_checkPD(pid, 0, 0, ku_mmu_list);//pid의 page directory 있는지 확인

	if (node == NULL) {//처음 생성된 프로세스 ( PD없음 )
		node = ku_mmu_generatePage(0, 0, pid, 0);
		//ku_cr3에 시작주소값 저장
		*ku_cr3 = node->pte;
		return 0;
	}
	else {//page directory가 있는 경우
		printf("%d : already have\n", pid);
		*ku_cr3 = node->pte;
		return 0;
	}
	return -1;

}

int ku_page_fault(char pid, char va) {
	//페이지 폴트 핸들러
	//새로운 페이지가 업데이트되거나 swap in 해줘야 할때 
	int pmdIndex = ku_mmu_bitOp(0, va);
	int ptIndex = ku_mmu_bitOp(1, va);
	int finIndex = ku_mmu_bitOp(2, va);
	int offset = ku_mmu_bitOp(3, va);
	switch (ku_mmu_checkPage(pmdIndex, ptIndex, finIndex)) {
	case 0: {
		//pmd, pt, fin페이지없음(페이지 생성 )
		ku_mmu_generatePage(1, pmdIndex, pid, va);
		ku_mmu_generatePage(2, ptIndex, pid, va);
		ku_mmu_generatePage(3, finIndex, pid, va);
		return 0;
	}
	case 1: {
		//pt, fin페이지 없음 (페이지 생성)
		ku_mmu_generatePage(2, ptIndex, pid, va);
		ku_mmu_generatePage(3, finIndex, pid, va);
		return 0;
	}
	case 2: {
		//fin페이지 없음 ( 생성 or swap in 해줘야됨 )
		ku_mmu_Node * node = ku_mmu_checkPD(pid, 3, finIndex, ku_mmu_swapList);
		if (node == NULL) {
			ku_mmu_generatePage(3, finIndex, pid, va);
		}
		else {
			ku_mmu_swapIn(node, va);
		}
		return 0;
	}
	}
}


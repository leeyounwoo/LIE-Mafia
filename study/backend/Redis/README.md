# Redis

## Redis 개요
Remote dictionary server (Redis) : 외부에 있는 dictionary(key-value 쌍 형태) 자료구조를 사용하는 서버라는 의미이다.


### What is Redis
- Remote dictionary server
- Database, Cache, Message broker
- In-memory Data Structure Store
    - 메모리상에 데이터를 저장하는 서버
- Supports rich data structure

<br>

### Redis Issue

쿠팡에서 모든 상품이 품절로 표시되는 문제가 발생했다. <br/>
-> 원인은 Redis DB때문인 것으로 드러났다. <br/>
-> Redis는 DB구나!<br/>

### Quiz
- 32bit CPU에서 Int의 최대값은? 2147483647(21억 4748만 4637)
    - Key-Value 쌍에서 Key값이 Integer 범위를 넘어가서 발생한 문제. (Key 값이 너무 많아져서)
    - 패치 내용중 int 를 long으로 바뀐 것을 확인할 수 있음.



<br/><br/>

## Cache 개념
- 나중의 요청에 대한 결과를 미리 저장했다가 빠르게 사용하는 것
- 어디에?  Main Memory 에!!
<br>

### Memory Hierarchy
- CPU Register
- CPU Cache
- Main Memory(DRAM)
- Storage(SSD,HDD)

<br>

- 계층 구조 위로 갈수록 빠르고 비싸고, 아래로 갈수록 느리고 크고 저렴한 저장소이다.

### Mac Example
- i7 CPU - 12MB Cache Memory (12MB Cache Memroy(SRAM)에 해당)
    - 매우 빠르다.
    - 매우 비싸다.
    - 용량이 작다.
- 16GB DRAM
    - 적당히 빠르다.
    - 적당히 비싸다.
    - 용량이 적당하다.
    - 휘발성이다. (컴퓨터 종료시 메모리가 저장되지 않는다.)
- 512GB SSD
    - 비교적 느리다.
    - 비교적 저렴하다.
    - 용량이 크다.
    - 비휘발성이다. (컴퓨터가 종료되더라도 영구적으로 저장한다.)
- 기본적으로 Database 는 컴퓨터가 꺼지더라도 저장이 되어야 하므로 HDD,SSD 영역에 저장해왔다.
- 기술이 발전하고 하드웨어가 커짐에 따라 Main Memory(DRAM)에 저장해서 좀 더 빠르고 쉽게 데이터베이스에 저장하면 어떨까? 의 개념에서 나온 것이 Redis이다.
- 더 자주 접근하고 덜 자주 빠귀는 데이터를 Dataase보다 더빠른 메모리에 저장한다는 In-memory Database(Cache) 가 나온 것이다.


<br/>

## Redis 자료구조
- Redis와 다른 In-memory와 비교되는 가장 큰 차이가 Collection 자료구조를 제공한다는 것이다.

### Collection 자료구조
- String 
    - Key-Value 쌍
    - Java의 Map Entry
    - Set`<Key><Value>`
    - Get`<Key>`
    - Del`<Key>`
- List
    - Key-List 쌍
    - Java의 LinkedList
    - Lpush & Rpush `<Key><Elements..>`
    - Lpop & RPop `<Key>`
    - Lrange`<key><Start><End>`
    - Lindex`<Key><Index>`
- Set
    - Key-Set 쌍
    - Java의 HashSet
    - Sadd`<key><Element>`
    - Smembers`<Key>`
    - Sismember`<Key><Element>`
    - Srem`<Key><Element>`
- Sorted Set
    - Key-Set 쌍
    - Java의 TreeSet
    - Zadd`<Key><Score><Element>`
    - Zrange`<Key><Start><End>`
    - Zrangebyscore`<Key><min><max>`
    - Zrem`<key><Element>`
- Hash
    - key-Hash 쌍
    - Java의 HashMap(혹은 Object)
    - Hset`<Key><Sub-key><Sub-value>`
    - Hset`<Key><Sub-key>`
    - Hgetall`<Key>`

### Java의 자료구조 vs Redis의 자료구조
- HashMap에 저장해도 Memory 데이터베이스인데 왜 Java 자료구조를 안 쓰는건데??
- Java의 서버가 여러대인 경우 Consistency의 문제 발생
    - 서버마다 다른 데이터를 가지고 있기 때문
    - Session 같은 것을 Java 객체로 저장한다면 다른 서버에서는 해당 세션이 없으므로 문제 발생
    - Multi-Threaded 환경에서 Race Condition 문제 발생 위험

### Race Condition
- Race Condition이란 여러 개의 Thread가 경합하는 것
- Context Switching에 따라 원하지 않는 결과가 발생

### Race Condition 해결
- Redis는 기본적으로 Single Threaded
- Redis 자료구조는 Atomic Critical Section(동시에 프로세스가 여러개 접근하면 안되는 영역)에 대한 동기화를 제공
- 서로 다른 Transaction Read/Write를 동기화 (원치 않는 결과를 막아준다.)

### 어디에 쓰나요?
- 여러 서버에서 같은 데이터를 공유할 때
- Single Server라면? Atomic 자료구조 & Cache 자료구조를 사용하기 위해 사용

<br/><br/>

## Redis 주의사항
- Single Thread 서버이므로 시간복잡도를 고려해서 명령어를 사용해야 한다.
- In-memory 특성상 메모리 파편화, 가상 메모리 등의 이해가 필요하다. (OS, Memory)

### Single Threaded

- Event Driven(비동기)
- IO-bound Process
- Context Switching이 효율이 적다.

<br>

- 왜 Redis가 Single Thread로 동작하는가? IO-bound Process
    - CPU 연산보다 IO 관련 시간을 많이 보내기 때문에 CPU를 Aotimization했을 때 시간 효율이 크지 않다는 이유가 있다.
    - 개발의 단순화, 사용의 단순화를 위해 사용하기도 한다. (Simple is best)
    - Redis는 네트워크로부터 요청을 받아서 명령어를 처리하는데 conmmand를 형성하고 처리하는 과정이 single thread이기 때문에 command가 오랜 시간이 걸릴 경우 나머지 요청이 더이상 받아지지 않고 서버가 다운되는 문제가 발생할 수 있다.
    - 따라서 Single Thread이므로 빨리빨리 처리해야 한다.
    - O(N)의 시간복잡도를 가지는 KEYS,Flush,GetAll 과 같은 명령어 사용에 주의해야한다.

### Memory 관리
- 메모리 파편화
- 가상메모리 Swap
- Replication-Fork
- 다른것도 많다.

### Memory 파편화 
- 메모리를 할당받고 해제하는 과정에서 부분 부분 비어있는 공간이 생기는데 새로 생긴 커다란 process를 할당하려면 해당 크기만큼 비어있는 공간에만 할당할 수 있다.
    - 실제 pysical memory에서 사용하지 못하는 부분 발생
    - 실제로 사용하는 것보다 더 많은 메모리를 사용하는 것처럼 컴퓨터가 인식하고 이과정에서 프로세스가 죽는 현상이 발생할 수 있다.
    - 따라서 Redis를 사용할 때 Memory를 여유있게 사용해야 한다.

### Virtual memory - Swap
- 실제로 프로세스를 메모리에 올릴때 전체가 아닌 일부만 올려서 사용하고 덜 사용되는 프로세스의 메모리는 disk에 저장했다가 필요할 때 메모리를 올려서 사용하는 방식을 선택하고 있다.
- 이 과정에서 레이턴시가 발생하게 되고 그 레이턴시가 길어진다면 싱글 스레드 환경에서 문제가 발생할 가능성이 있다.
- 따라서 Swap 의 배경지식과 선택의 여지가 필요하다.

### Replication - Fork
- Redis는 휘발성을 가진 Memory 상의 데이터 저장소이므로 유실의 문제를 안고 있다.
- 따라서 데이터 복사 기능을 제공한다.
- 데이터를 복사해서 Slave의 Redis Server 혹은 Disk에 전송해서 저장하는 방식을 선택한다.
- 이과정에서 복사가 일어날 때 Fork(프로세스를 메모리상에서 복제)해서 사용하는 방식을 쓰는데 이 과정에서 메모리가 가득 차있다면 복사본이 제대로 생성되지 않고 서버가 죽는 현상이 발생할 위험이 있다.
- 따라서 Fork를 사용할 때 주의하고 메모리를 여유롭게 사용해야한다.

<br>

## 추천 공부
- Redis를 저장소처럼 사용하는 Redis Persistent,RDB,AOF 
- Redis의 메모리는 제한되어 있기 때문에 주기적으로 Scale out, Back up을 해야함 -> Redis Cluster
- 부하 분산 -> Constant Hashing
- Data Grid -> Spring Gemfire , Hazlecast



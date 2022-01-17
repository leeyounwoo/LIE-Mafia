# Redis
- Redis 소개
- 왜 Collection이 중요한가
- Redis Collections
- Redis 운영
- Redis 데이터 분산
- Redis Failover

### 오늘 안배우는 것들
- Redis Persistence (RDB,AOF) > 메모리에 모든 내용을 백업하는 기능, disk에 저장한걸 실시간으로 사용할수없으므로 RDB,AOF 가 필요하다
- Redis Pub/Sub
- Redis Stream
- 확률적 자료구조 (Hyperloglog)
- Redis Module


## Redis 소개
- In-Memory Data Structure Store
- Open Source(BSD 3 License)
- Support Data Structure
    - Strings,set,sorted-set,hashes,list
    - Hyperloglog, bitmap, geospatial index
    - Stream
- Only 1 Committer


## Redis를 설명하기 전에 Cache 먼저!
- Cache는 나중에 요청을 결과를 미리 저장해두었다가 빠르게 서비스 해주는 것을 의미
- Factorial 연산의 메모이제이션 : DP의 핵심이 앞의 연산을 저장해두고 다음 번에 같은 연산을 해야할 경우 빠르게 연산 결과를 제공한다.
- 20880!를 계산해두고 어딘가에 저장해뒀다면 20881! 계산은 금방이다.
- 접근 속도의 차이가 있는 것이다.
- Redis는 Memory 영역에 저장되어 Disk보다 훨씬 빠른 속도로 Cache를 제공해준다.


### 어디서 많이 사용하나요?
- DB도 내부적으로 Cache가 있다. Memory 사이즈보다 데이터가 크면 Disk를 사용해야 한다. 여러가지를 계속 접근하다보면 기존의 Cache를 날리고 Disk에서 새로읽어야 하므로 Disk에 접근할때마다 속도가 느릴 수 있다.
- 2:8 파레토의 법칙으로 전체 요청의 80%는 20%의 사용자가 결정한다는 것으로 Caching을 적은 메모리로 효율적으로 사용할 수 있을 것이다!

### Cache 구조1 - Look aside Cache
1. Web Server는 데이터가 존재하는지 Cache 먼저 확인
2. Cache에 데이터가 있으면 Cache에서 가져온다.
3. Cache에 데이터가 없다면 DB에서 읽어온다.
4. DB에서 얻어온 데이터를 Cache에 다시 저장한다.
- Memory Cache를 이용하게 되면 훨씬 더 빠른 속도로 서비스가 가능하다.
- 일반적으로 가장 많이 사용하는 방법

### Cache 구조2 - Write Back
1. Web Server는 모든 데이터를 Cache에만 저장
2. Cache에 특정 시간동안의 데이터가 저장
3. Cache에 있는 데이터를 DB에 저장한다.
4. Cache에 있는 DB에 저장한 데이터를 삭제한다.
- 쓰기가 굉장히 빈번한 경우 Disk 에 저장하기 전에 Cache에 저장해두고 특정 시점마다 DB에 저장하도록 한다.
- Batch 작업에 의해 insert를 한번씩 500번 보내는 것보다 insert 500개를 붙여서 한번에 보내는 것이 훨씬 빠르다.
- RAID Controller에도 Cache가 있는데 이것 또한 Write Back 형태로 저장하고 있다.
- 단점은 처음에 Cache에 저장하기 때문에 memory 영역이므로 재부팅 등 장애가 생길때 데이터 손실의 위험이 있다.
- 극단적으로 heavy한 write가 있는 작업, 백업 같은 작업이나 log 기록 등 에서 많이 활용된다.


## 왜 Collection이 중요한가?
- 개발의 편의성
- 개발의 난이도
- 외부의 Collection을 잘 이용한 것으로, 여러가지 개발 시간을 단축시키고 문제를 줄여줄 수 있기 때문에 Collection이 중요하다.


### Memcached
- Redis는 Memcached와 많이 비교되는데, Memcached는 Collection을 제공하지 않는다.
- Redis는 Collection을 제공함으로써 개발의 편의성이 향상된다.

랭킹 서버를 직접 구현한다면? 
- 이분도 게임 서버를 구현할 때 서버를 여러대 두어 싱크를 맞추기위해 활용!
- 가장 간단한 방법
    - DB에 유저의 Score를 저장하고 Score로 order by로 정렬 후 읽어오기
    - 개수가 많아지면 속도에 문제가 발생할 수 있음.
        - 결국 디스크를 사용하므로
    - In-Memory 기준으로 랭킹 서버의 구현이 필요함.
    - Redis의 Sorted Set을 이용하면 랭킹을 구현할 수 있음.
        - Replication도 가능하다.
        - 다만 Redis 한계에 종속적이 된다.

친구 리스트를 관리해야 한다면?
- 친구 리스트를 Key-Value 형태로 저장해야 한다면
    - 유저1 에 A친구가 있는 상태이다. 
    - 이때 B를 추가하는 트랜잭션과 C를 추가하는 트랜잭션이 있을 때 두개의 독립적인 작업이 타이밍이 갖게 되었을 때 정확히 데이터가 저장되지 않을 수 있다. (context switching 이 일어날 수 있으므로)
    - 데이터베이스는 무결성을 보장하기 위해 Atomicity(원자성), Consistency(일관성), Isolation(독립성, 고립성), Durability(영속성, 지속성) 를 만족해야 한다.
    - Redis의 경우 자료구조가 Atomic 하기 때문에 해당 Race Condition을 피할 수 있다.


### Redis 어디에 써야하는가?
- Remote Data Store 
    - 여러대의 서버에서 같은 데이터 공유하고 싶을 때
- 한대에서만 필요하다면, 전역 변수를 쓰면 되지 않을까?
    - Redis 자체가 Atomic을 보장해준다.(싱글 스레드이므로)
- 많이 사용되는 곳
    - 인증 토큰 등을 저장(Strings 또는 hash)
    - Ranking 보드로 사용 (Sorted Set)
    - 유저 API Limit
    - 자료 (List)


### Tip
- Key에 토큰 형태로 사용하여 Preffix 를 붙여 쓰는 경우가 많은데 Preffix인지 suffix인지에 따라 분산이 바뀔수 있으므로 선택하여 사용하는 것이 좋다.


## Redis Document
- 현재 스터디 (추가 예정)
https://redis.io/documentation 
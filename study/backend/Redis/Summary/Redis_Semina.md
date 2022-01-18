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

### Redis Collections
- Strings
  - key-value 형태
  - Key 를 어떻게 지정할지 고민하는 것이 중요하다.
  - Key에 토큰 형태로 사용하여 Preffix 를 붙여 쓰는 경우가 많은데 Preffix인지 suffix인지에 따라 분산이 바뀔수 있으므로 선택하여 사용하는 것이 좋다.
  - 멀티 Key를 통해 한번에 삽입, 연산 가능
  - 간단한 SQL문을 대체할 수 있다 (insert -> Set 컬럼:key 값 / mset 등등)
  - EX>
    ```
    Insert into user(name,email) values('ohj','ohj@naver.com')
    
    Set name:ohj ohj
    Set email: ohj ohj@naver.com

    Mset name:ohj ohj email:ohj ohj@naver.com
    ```
- List 
  - 앞이나 뒤에 삽입 빠르고, 중간 삽입 느림 (Lpush,RPush가 지원된다.)
  - job Queue에 많이 활용된다.
- Set 
  - 중복된 데이터를 담지 않으려고 한다. 안의 내용을 찾기 쉬움
  - SMEMBERS를 사용할때 모든 Value를 돌려주므로 100만개의 데이터가 있을때 100만개를 가져와야 하므로 사용에 주의해야한다.
  - 특정 유저를 Follow 하는 목록을 저장할 떄 많이 사용 (친구 목록)
- Sorted Set 
  - Set은 순서가 없지만 Score를 줘서 순서(asc)를 보장할 수 있다.
  - 유저 랭킹보드를 만들 수 있다.
  - Sorted Set의 Score는 정수형이 아닌 Double형임을 명심해야한다. (js의 범위와 다르므로 특정 정수에 정확한 정보가 아닐 수 있어서 이슈가 발생할 수 있다. 이를 모두 포용하기 위해 long값으로 보내지말고 String으로 보내면 정확한 값을 보낼 수 있다. )
  - EX>
     ```
    select * from rank order by score limit 50,20;
    zrange rank 50 70

    select * from rank order by score desc limit 50,20;
    zrevrange rank 50 70
    
    select * from rank wher score>=70 and score<100;
    zrangebyscore rank 70 100
    
    select * from rank where score>70
    Zrangebyscore rank (70 +inf
    ```
- Hash
  - Key-value 안에 다시 key value set을 갖을 수 있게 해준다.
  - EX>
     ```
     insert into users(name,email) values('ohj','ohj@naver.com');
     hmset ohj name ohj email ohj@naver.com
     ```
- Collections 주의 사항
  - 하나의 컬렉션에 너무 많은 아이템을 담으면 좋지 않음
  - 간응하면 만개 이하의 수준으로 유지하는게 좋다.
  - Expire(아이템 지우기)는 Collection의 item 개별로 걸리지 않고 전체 Collectio만 다룰 수 있다.
  - 데이터 구조에 따라 속도의 차이가 있으므로 Collections 선택에 주의해야 한다.
  - 가장 많이 사용하는 것은 Strings,Sorted Set이다.
  - 자세한 내용은 Redis Tutorial 에서 확인할 수 있다.


## Redis 운영
- 메모리 관리를 잘하자
  - In-Memory Data Store이므로 메모리가 꽉차서 다운되는 경우가 발생할 수 있다.
  -  Physical Memory 이상을 사용하면 문제 발생
     -  Swap이 있다면 Swap 사용으로 해당 메모리 Page 접근시마다 늦어짐
     -  메모리 페이지를 disk에 필요하면 로드하고 내려놓고를 반복한다. 따라서 필요에 따라 disk에 접근하므로 느려진다.
     -  Swap이 없다면 O(N) 복잡도에 다운될수 있는 위험이있다.
  - Maxmemory를 설정하더라도 이보다 더 사용할 가능성이 큼.
    - http://jemalloc.net/ 참고 (메모리 할당, 해제를 의존해서 사용)
    - 메모리 파편화 위험
  - RSS 값을 모니터링 해야함.
  - 메모리 관리
    - Redis 메모리 관리에서 많은 업체가 현재 메모리를 사용해서 Swap을 사용하고 있음을 모르는 경우가 많다. 
    - 큰 메모리를 사용하는 instance 하나보다 적은 메모리를 사용하는 instance 여러개가 안전하다. (특히 write가 많이 일어나는 작업)
    - 메모리 파편화를 줄이도록 jemalloc에 힌트를 주는 기능이 들어갔지만 jemalloc 버전에 따라서 다르게 동작할 수 있다.
    - 다양한 사이즈를 가지는 데이터보다 유사한 크기의 데이터를 갖고있는게 유리하다.
    - 메모리가 부족할 때 좀 더 메모리가 많은 ㅈ아비로 Migration.
    - 메모리가 빡빡하면 Migration 중에 문제가 발생할수도 있다.
    - 있는 데이터를 줄인다. 
    - Collection 자료구조를 바꾸어 사용
    - Ziplist 활용(속도가 느려질 가능성이 있다.)
- O(N) 관련 명령어를 주의하자
  - Redis 는 Single Treaded
    - Redis는 동시에 1개만 처리할 수 있다.
    - Packet으로 하나의 Command가 완성되면 processCommand에서 실제로 실행된다.
    - 초당 10만개 정도 처리가 가능하다. 
    - 한번에 하나의 명령만 수행 가능하므로 긴 시간을 요하는 명령을 사용하면 안된다. (1초가 걸리는 명령을 사용할때 9만9천개의 명령이 대기 중이므로)
    - 대표적인 O(N) 명령어들 : KEYS, FLUSHALL, FLUSHDB, Delete Collections, Get All Collections
    - 실수 사례 : 확인을 위해 KEYS 명령 사용(scan으로 대체), Collections 모든 데이터 호출하는 경우(일부로 나눠서 하는 등의 방법으로 교체)
- Replication
  - A라는 서버에 데이터를 B가 가지고 있다.
  - Async Replication
    - Relication Lag가 발생할 수 있다.
    - master에는 있지만 replica(slave)에는 아직 없을 때가 있음. 부하에 따라 차이가 있다.
    - DBMS로 보면 row replication이 아닌 statement replication과 유사 (쿼리가 가능, 쿼리로 보내면 now명령을 통해 다른시간에 사용될 수 있다. )
    - Replication 설정 과정
      - secondary에 replcaof 또는 slaveof 명령 전달
      - Secondary는 Primary에 sync 명령 전달
      - Primary는 현재 메모리 상태를 저장하기 위해 Fork
      - Fork한 프로세서는 현재 메모리 정보를 disk에 dump
      - 해당 정보를 secondary에 전달
      - Fork 이후의 데이터를 secondary에 계속 전달
      - stream으로 변경해서 disk의 사용량을 줄일 수 있다.
    - Replication 주의 사항
      - fork가 발생하므로 메모리 부족 발생 위험
      - redis -cli --rdb 명령은 현재 상태의 메모리 스냅샷을 가져오므로 같은 문제를 빌생시킴
      - AWS나 클라우드의 Redis는 좀 다르게 구현되어서 좀더 해당 부분이 안정적. 단 느리다.
      - 많은 대수의 Redis 서버가 Replica를 두고 있다면 네트워크 이슈나 사람의 작업으로 동시에 replication이 재시도 되록하면 문제가 발생할 수 있다.
- 권장 설정 tip
  - Maxclient 설정 50000 
    - 이 값을 높여야 접속에 문제가 줄어든다
  - RDB/AOF 설정 off (Replica를 쓸때만 사용)
  - 특정 commands disable
    - Keys
    - AWS의 ElasticCache는 이미하고 있음
  - 전체 장애의 90%이상은 keys와 save 설정을 사용해서 발생
    - 적절한 ziplist 설정
  - Redis 데이터 분산
    - 데이터 특성에 따라서 선택할 수 있는 방법이 달라진다.
    - Persistent 해야하면 안 우아한 Redis!!

## 데이터 분산 방법
- Application 
  - Consistent Hashing
    - twemproxy를 사용하는 방법으로 쉽게 사용가능
  - Sharding
- Redis Cluster


### Consistent Hashing
- 데이터가 꽉차서 새 서버를 추가하거나 서버가 다운되면 데이터들이 리밸런싱이 일어나야 된다.
- 장애에 취약하다.
- Consitent Hashing을 사용하면 서버 한대가 추가되거나 제거되더라도 해당 서버의 데이터만큼만(1/서버만큼만) 리밸런싱되는 방식으로 사용된다.


### sharding
- 데이터를 어떻게 나눌것인가? == 데이터를 어떻게 찾을 것인가?
- 하나의 데이터를 모든 서버에서 찾아야 하면??
- 상황마다 샤딩 전략이 달라진다.
- 가장 쉬운 방법은 Range 이다.
  - 특정 Range를 정의하고 해당 Range에 속하면 거기에 저장
  - 데이터 불균형이 생길수있다. 
  - 데이터가 몰린다고 해도 데이터를 이동시킬 수 없다. 
  - 즉, 확장은 편하지만 데이터 불균형으로 인한 문제가 발생
- 두배씩 서버가 확장되면 modular 연산으로 쉽게 분산할 수 있다. 하지만 16대의 서버에서 2배 확장하면 32대.. 이런 문제를 고려해서 적절한 방법을 선택해서 사용해야 한다.
- index 서버를 따로 두어 해당 Key가 어디에 저장되어야 할지 관리하도록 하면 modular 문제를 해결할 수 있지만 index 서버가 다운되면 모든 서버에 문제가 전파된다.

## Redis Cluster
- Hash 기반으로 Slot 16384로 구분
  - Hash 알고리즘은 CRC16을 사용
  - Slot=src16(key) % 16384 (모듈러 16384를 넘어가는 경우는 없다는 느낌)
  - Key가 Key{hashkey} 패턴이면 실제 crc16에 hashkey가 사용된다.
  - 특정 redis 서버는 이 slot range를 가지고 있고, 데이터 migration은 이 slot 다위의 데이터를 다른 서버로 전달하게 된다. (migrateCommand 이용)
  - 라이브러리 의존적이다.
- 장점
  - 자체적인 Primary, Secondary Failover
  - Slot 단위의 데이터 관리
- 단점
  - 메모리 사용량이 더 많다.
  - Migration 자체는 관리자가 시점을 결정해야 하마.
  - Library 구현이 필요하다.


## Redis Failover
- Coordinator 기반 Failover
  - Zookeeper,etcd,consul 등의 Coordinator 사용
  - Health Checker가 판단하여 Primary로 승격시켜줄 수 있고, Coordinator에 current Redis를 업데이트 한다. Coodinater는 APi 서버에 current Redis가 변경됨을 알려준다.
  - Coordinator 기반으로 설정을 관리한다면 동일한 방식으로 관리가 가능
- VIP/DNS 기반 Failover
  - VIP는 virtual IP인데 VIP로 접속을 하다가 Hearlth Checker가 서버가 다운됨을 발견하면 Primary를  승격시키고 VIP를 다시 할당하면서 기존 연결을 모두 끊어준다(클라이언트의 재접속 유도)
  - DNS도 VIP와 마찬가지로 DNS를 할당해두고 하는 것이다.
  - 클라이언트에 추가적인 구현이 필요없다.
  - VIP 기반은 외부로 서비스를 제공해야 하는 서비스에 유리핟.
  - DNS 기반은 DNS Cache TTL을 관리해야 하므로 언어별 DNS 캐싱 정책을 잘알아야 한다.(아마존)
  - VIP기반은 무한 캐싱등의 문제가 발생하지 않아서 안정적이지만, DNS 기반으로 사용할 경우 DNS가 저렴하고 편리하다는 이점이 있다.
- Redis Cluster 사용

## Monitoring

### Monitoring Factor
- Redis Info를 통한 정보
  - RSS (pyshical memory를 얼마나 사용하고 있는가)
  - Used Memory (Redis가 사용하고 있는 memory)
  - Connection 수 
  - 초당 처리 요청 수 ( CPU 영향을 받는다. )
- System
  - CPU
  - Disk
  - Network rx/tx

### 결론
- 기본적으로 Redis는 매우 좋은 툴
- 그러나 메모리를 빡빡하게 쓸 경우, 관리하기 어려움
- Client-output-buffer-limit 설정 필요
- Redis를 Cache로 사용할 경우는 문제가 적게 발생
  - DB 부하의 정도에 따라 문제가 발생하지 않을수도 있음.
  - Consistent Hashing도 실제 부하를 아주 균등하게 나누지는 않음
  - Adaptive Consitent Hashing을 사용할 수 도 있다.
- Redis를 Persistent Store로 사용할 경우
  - 무조건 Prmary/Secondary 구조로 구성이 필요
  - 메모리를 절대로 빡빡하게 사용하면 안된다.
  - RDB/AOF 가 필요하다면 Secondary에서만 구동 (AOF가 더 안정적)

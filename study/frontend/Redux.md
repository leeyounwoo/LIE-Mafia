# Redux

#### redux를 쓰는 이유

1. 모든 컴포넌트가 props 없이도 state 꺼내 쓸 수 있어서 
2. state 버그 관리가 용이, state 수정하려면 수정방법을 reducer로 미리 정의해놓고 컴포넌트는 dispatch() 를 이용해서 state 수정해달라고 reducer에 부탁하는 형식으로 코드를 짜야한다. 그래야 갑자기 state가 이상해지는 버그가 생겨도 범인을 reducer에서 쉽게 찾을 수 있다.

```
yarn add redux react-redux
```

```react
// index.js

import {Provider} from 'react-redux';

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <Provider>
        <App/>
      </Provider>
    </BrowserRouter>
  </React.StrictMode>
);
```

**셋팅1. Provider import**

**셋팅2. 내가 state값 공유를 원하는 컴포넌트를 다 감싸준다.**

```
<Provider>
  <App/>
</Provider>
```

<App>컴포넌트와 그 안에있는 모든 HTML, 컴포넌트들은 전부 state를 직접! props 전송없이! 사용할 수 있다.

**셋팅3. redux에서 state를 하나 만드려면 createStore() 함수를 써야한다.**

```react
// index.js

import {Provider} from 'react-redux';
import {createStore} from 'redux';

let store = createStore(()=>{ return [{id : 0, name : '멋진신발', quan : 2}]  }) // 콜백함수에는 내가 원하는 state 초기값 return

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <Provider>
        <App/>
      </Provider>
    </BrowserRouter>
  </React.StrictMode>
);
```

**셋팅4. <Provider>에 만든 state를 props처럼 등록하면 끝**

```react
// index.js

import {Provider} from 'react-redux';
import {createStore} from 'redux';

let store = createStore(()=>{ return [{id : 0, name : '멋진신발', quan : 2}]  })

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <Provider store={store}>
        <App/>
      </Provider>
    </BrowserRouter>
  </React.StrictMode>
);
```

redux 설치 후엔 state들을 store 라고 부른다.

### store에 있는 state 데이터 꺼내쓰는 법

그냥 쓸 수 X store 안에 있는 데이터를 props의 형태로 등록해야 사용가능

```react
// Cart.js

import {connect} from 'react-redux';
function Cart(){
  return (
    <div>
      <Table responsive>
        <tr>
          <th>#</th>
          <th>상품명</th>
          <th>수량</th>
          <th>변경</th>
        </tr>
        <tr>
          <td>1</td>
          <td>Table cell</td>
          <td>Table cell</td>
          <td>Table cell</td>
        </tr>
      </Table>
    </div>
  )
}

function state를props화(state){
  return {
    state : state
  }
}

export default connect(state를props화)(Cart);
```

1. store 데이터 사용을 원하는 컴포넌트 .js 파일 밑에 function을 하나 만든다.
   * store 안에 있던 state를 props로 만들어주는 역할
   * return 안에 `{ 자유작명 : state }` 적어준다.
   * store 안에 있던 모든 state 데이터가 props로 등록된다.
   * Cart.js 에서 {자유작명}을 출력해보면 아까 저장해뒀던 redux내의 장바구니 state가 출력된다.
2. export default 하던 부분에 connect() 적는다. (connect 함수는 import 해와야 함)

이제 state를 props처럼 사용하면 됨



**셋팅**

1. index.js에 <Provider>를 import 해온 다음
2. state 값 공유를 원하는 컴포넌트를 감싼다.
3. createStore를 import 해온 다음 사용법에 의해 state를 만들어 let store라는 변수에 저장한다.
4. <Provider store={store}> 이렇게 store를 등록하면 **Provider로 감싼 컴포넌트는 전부 store안에 있던 값을 props없이 공유 가능하다.**



**store안에 있던 state 사용**

원하는 컴포넌트 파일로 간다.

1. 하단에 function state를props화() 만들고 state를 props로 등록한다.
2. 하단에 export default connect(state를props화)(Cart); 
   이제 아까 만들어둔 state가 props로 등록이 된 것.
   **props.state이름** 이렇게 저장된 state를 자유롭게 사용할 수 있다.



### state 데이터를 수정하고 싶을 때

1. reducer 함수를 만들고 그 곳에 데이터 수정하는 방법을 정의해놈
2. 원하는 곳에서 dispatch() 라는 함수를 써서 reducer에게 수정해달라고 요청

### 데이터 수정하는 reducer 만드는 법

reducer funciton 안에

1. state 초기값
2. state 데이터 수정방법 

들어가야 함

```react
// index.js

let 기본state = [{id : 0, name : '멋진신발', quan : 2}];

function reducer(state = 기본state, 액션){
  // 수정방법 정의
  액션.type === '명령어'
  return state
}
let store = createStore(reducer);
```

```react
// Cart.js

<button onClick={()=>{ props.dispatch({type: '명령어'}) }}> + </button>
```



**내가 이 state 데이터를 다른 컴포넌트에서 쓸 일이 없다면 간단하게 useState()로 Cart 컴포넌트 안에 간단하게 만들자**

반면 많은 컴포넌트들이 공유하는 값은 redux store안에 보관



### useSelector & useDispatch

```react
import { useSelector, useDispatch } from 'react-redux';

function Cart(props) {
  let state = useSelector((state) => state )
  let dispatch = useDispatch()
  
  (생략)
} 
```




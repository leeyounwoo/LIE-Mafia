# Router

```
yarn add react-router-dom@5
```

```react
// index.js

import { BrowserRouter } from 'react-router-dom';

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <App/>
    </BrowserRouter>
  </React.StrictMode>
  document.getElementById('root')
);
```

```react
// App.js

import { Link, Router, Switch } from 'react-router-dom';

function App(){
    return (
    	<div>
            //방법1
        	<Route exact path="/" component={Home}>
            	<div>메인</div>
            </Route>
            //방법1
            <Route path="/detail" component={Card}></Route>
            //방법2
            <Route path="/어쩌구"> <Card/> </Route>
        </div>
    )
}
```

```jsx
<Route path="주소규칙" component={보여 줄 컴포넌트} />
```

### Link : 누르면 다른 주소로 이동시키기

```jsx
<Link to="주소">내용</Link>
```

```react
// App.js

function App(){
    return (
    	<div>
        	<Navbar>
                //방법1
            	<Nav.Link><Link to="/"> Home </Link></Nav.Link>
                //방법2
                <Nav.Link as={Link} to="/detail">Detail</Nav.Link>
            </Navbar>
        </div>
    )
}
```

## 파라미터와 쿼리

```
파라미터: /profiles/seongbiny // 특정id 나 이름을 가지고 조회할 때
쿼리: /about?details=true // 어떤 키워드를 검색하거나 요청할 때 필요한 옵션을 전달할 때
```

### 파라미터

```jsx
// src/Profile.js

const profileData = {
    seongbiny: {
        name: '윤성빈',
        description: 'Frontend Engineer'
    },
    eunwoo: {
        name: '차은우',
        description: 'Backend Engineer'
    }
};

function Profile ({ match }){
    const { username } = match.params;
    const profile = profileData[username];
    if (!profile) {
        return <div>존재하지 않는 유저입니다.</div>;
    }
    return (
        <div>
        	{username}({profile.name})<br />
            {profile.description}
        </div>
    )
};
export default Profile;
```

파라미터를 받아올 땐 match 안에 들어있는 params 값을 참조한다. 

```jsx
// src/App.js

import { Route } from 'react-router-dom';
import Profile from './Profile';

function App(){
    return (
    	<div>
        	<Route path="/profiles/:username" component={Profile} />
    	</div>
   	);
};
export default App;
```

`/profiles/:username` 에서 username에 해당하는 값을 파라미터로 넣어줘서 Profile 컴포넌트에서 match props를 통하여 전달받을 수 있게 된다.

### Query

쿼리는 라우트 컴포넌트에게 props 전달되는 location 객체에 있는 search 값에서 읽어올 수 있다. location 객체는 현재 앱이 갖고있는 주소에 대한 정보를 지니고 있다.

```json
{
  key: 'ac3df4', // not with HashHistory!
  pathname: '/somewhere'
  search: '?some=search-string',
  hash: '#howdy',
  state: {
    [userDefined]: true
  }
}
```

여기서 search 값은 문자열 형태로 되어있다. 객체 형태로 변환하는건 직접 해주어야 한다.

```bash
$ yarn add qs
```

```jsx
// src/About.js

import qs from 'qs';

function About({ location }){
    const query = qs.parse(location.search, {
        ignoreQueryPrefix: true
    });
    const detail = query.detail === 'true';
    return (
    	<div>
        	라우터 실습
        </div>
    )
}
export default About;
```

### 다른 방법으로 페이지 이동기능 만들기

코드 실행 중간중간에 페이지를 이동시키고 싶은 경우

페이지 이동 함수 사용

useHistory() 라는 함수를 import 해온다.

```react
// Detail.js

import React from 'react';
import { useHistory } from 'react-router-dom';

function Detail(){
  let history = useHistory();
  return (
    <div className="container">
      <button onClick={()=>{ history.goBack() }}>뒤로가기</button> 
  </div>  
  )   
}
```

1. import
2. let history goBack() 함수 사용 => 이런 라이브러리 사용법은 찾아서 읽거나 검색해봐야 알 수 있음

`onClick={()=>{ history.goBack() }` 뒤로가기

`onClick={()=>{ history.push('/') }` 그 경로로 이동

### Switch 컴포넌트

**매치되는 <Route>들을 전부 보여주지 말고 한번에 하나씩만 보여주세요**

여러 Route들을 감싸서 그 중 규칙이 일치하는 라우트 단 하나만을 렌더링시켜준다. Switch를 사용하면 아무것도 일치하지 않았을때 보여줄 Not Found 페이지를 구현할 수 있다.

```react
// App.js

function App(){
  return (
    <div>
      <나머지HTML/>
      <Switch>
        <Route exact path="/">
          어쩌구
        </Route>
        <Route path="/detail">
          <Detail/>
        </Route>
        <Route path="/:id">
          <div>새로 만든 route입니다</div>
        </Route>
        // path를 따로 정의하지 않으면 모든 상황에 렌더링 됨
        <Route>이 페이지는 존재하지 않습니다.</Route>
      </Switch>
    </div>
  )
}
```

Route태그들을 Switch로 감싸준다 그럼 **맨 위의 Route 하나만 보여줌**


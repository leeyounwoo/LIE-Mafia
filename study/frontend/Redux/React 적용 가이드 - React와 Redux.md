# React 적용 가이드 - React와 Redux

## React 시작하기

React를 사용하려면 설정해야 하는 도구가 많다. 모든 도구를 직접 설정하는 방법도 있지만 React Starter Kit와 같은 도구를 사용해 간편하게 설정하는 방법도 있다. 이 글에서 Facebook이 만든 도구인 create-react-app을 사용하는 방법을 설명하겠다. create-react-app을 사용한 이유는, React를 개발하는 Facebook이 만든 도구이고, 다른 도구에 비래서 기능은 적지만 쉽고 간편하게 쓸 수 있는 도구이기 때문이다. 만약 서버 렌더링 등 다양한 기능을 사용하고 싶다면 React Starter Kit를 사용할 수도 있다.

### 폴더 구조

다음 그림은 글에서 제안하는 프로젝트의 폴더 구조다. React와 Redux를 함께 사용하는 프로젝트에서 사용할 수 있게 Redux-book의 폴더 구조를 기준으로 폴더를 작성했다. 이 폴더 구조를 참고해 자신의 프로젝트에 맞게 폴더 구조를 변경하면 된다.

> 이 책에서 설명하는 샘플 프로젝트와 코드는 GitHub에서 다운로드할 수 있다.
>
> - 샘플 프로젝트의 GitHub 위치: https://github.com/naver/react-sample-code

![image-20220124141853827](C:\Users\LeeYounWoo\AppData\Roaming\Typora\typora-user-images\image-20220124141853827.png)

React와 Redux를 사용하는 프로젝트의 폴더는 위와 같이 __action__ 폴더와 __component__ 폴더, __reducer__ 폴더, __store__ 폴더로 구성된다.

##### action 폴더

__action__ 폴더는 애플리케이션에서 사용하는 명령어(action type)와 API 통신과 같은 작업을 하는 액션 메서드(action creator)를 모아둔 폴더다. 서비스에 따라 모든 명령어와 액션 메서드를 한 곳에 모아 두거나 도메인별로 구분해 나눠 놓기도 한다.

![image-20220124143541587](C:\Users\LeeYounWoo\AppData\Roaming\Typora\typora-user-images\image-20220124143541587.png)

다음 코드는 할 일을 완료하기 위한 액션 명령어와 액션 메서드를 구현한 todo.js 파일의 예다.

```react
// action type(명령어)
export const COMPLETE_TODO = 'COMPLETE_TODO'


// action creators(액션 메서드)
export function complete({complete, id}) {  
    return { type: COMPLETE_TODO, complete, id};
}
```

액션 메서드에서는 리듀서(reducer)로 데이터 생성을 요청한다. 비즈니스 로직을 주로 액션 메서드에 개발하기 때문에 액션 메서드는 컴포넌트의 재활용을 높이고 코드를 관리하는 데 중요한 역할을 한다.

> 비동기 통신이 필요할 때는 redux-thunk 라이브러리나 react-saga 라이브러리를 사용한다. 비동기 통신을 처리하는 방법은 "비동기 처리"에서 설명하겠다.



#### component 폴더

__component__ 폴더는 React 컴포넌트로 구성된 폴더다. 컴포넌트는 보통 도메인별로 구분돼 있다.

![image-20220124144829836](C:\Users\LeeYounWoo\AppData\Roaming\Typora\typora-user-images\image-20220124144829836.png)



#### reducer 폴더

__reducer__ 폴더는 리듀서로 구성된 폴더다. 리듀서는 액션 메서드에서 변경한 상태를 받아 기존의 상태를 새로운 상태로 변경하는 일을 한다. __reducer__ 폴더는 __action__ 폴더와 같이 하나로 만들기도 하지만 도메인별로 구분해 만들기도 한다. 액션 파일과 리듀서 파일을 합셔서 사용하는 ducks 기법도 있다.

이 글에서 설명하는 프로젝트에서는 많이 사용하는 방법인 액션과 리듀서를 분리하고 리듀서를 여러 개의 파일로 분리하는 방법을 사용한다. 다음과 같이 리듀서를 각각의 파일로 분리하고 __index.js__파일에서는 분리한 리듀서를 합친다. 만약 파일의 개수가 많아진다면 ducks 기법을 사용하는 것을 고려할 수 있다.

![image-20220124145830811](C:\Users\LeeYounWoo\AppData\Roaming\Typora\typora-user-images\image-20220124145830811.png)

리듀서는 스토어(store)를 새로 변경하는데, 입력받는 state와 반환하는 state가 항상 같은 순수 함수로 구현돼 있다. 그렇기 때문에 Redux로 이전의 state를 추적해 시간 여행을 하는 도구를 만들 수 있다.



#### store 폴더

__store__ 폴더에는 index.js 파일 하나만 있으며, 주로 미들웨어를 설정하는 일을 한다. 예를 들어 비동기 통신을 사용하기 위해 redux-thunk 라이브러리를 설정하거나, state의 변경 내역을 관리하기 위해 react-router-redux 라이브러리를 추가하거나, 디버깅을 위해 react-devtool을 설정하는 일을 주로 한다.
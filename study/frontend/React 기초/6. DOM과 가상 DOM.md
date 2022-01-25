# React - 가상 DOM

## DOM 이란?

문서 객체 모델(DOM, Document Object Model)은 XML이나 HTML 문서에 접근하기 위한 일종의 인터페이스입니다. 문서 내의 모든 요소를 정의하고, 각각의 요소에 접근하는 방법을 제공합니다. 

![HTML DOM](http://www.tcpschool.com/lectures/img_js_htmldom.png)

(출처: http://www.tcpschool.com/javascript/js_dom_concept)

__DOM은 HTML과 스크립트언어(자바스크립트)를 서로 이어주는 역할을 한다.__

<br>

<br>

## 브라우저의 렌더링 과정

- 렌더링이란 HTML, CSS, 자바스크립트 등 개발자가 작성한 문서가 브라우저에 출력되는 과정을 말합니다.

1. 서버로부터 받은 HTML, CSS 다운로드

2. HTML, CSS 파일을 파싱하여 각각 DOM Tree와 CSSOM 만들기

   ![DevTools에서 DOM 생성 추적](https://developers.google.com/web/fundamentals/performance/critical-rendering-path/images/dom-tree.png?hl=ko)

​                                                                                     (DOM Tree)

![DevTools에서 CSSOM 생성 추적](https://developers.google.com/web/fundamentals/performance/critical-rendering-path/images/cssom-tree.png?hl=ko)

​                                                                         (CSSOM, CSS Object Model)

​		(출처: https://developers.google.com/web/fundamentals/performance/critical-rendering-path/constructing-the-object-model?hl=ko)

3. DOM Tree와 CSSOM Tree로 Render Tree 생성

   순수한 요소들의 구조와 텍스트만 존재하는 DOM Tree와는 달리 Render Tree에는 __스타일 정보__가 설정되어 있으며 __실제 화면에 표현되는 노드들로만 구성__됩니다.

   ![DOM 및 CSSOM은 결합되어 렌더링 트리를 생성합니다.](https://developers.google.com/web/fundamentals/performance/critical-rendering-path/images/render-tree-construction.png?hl=ko)

   (출처: https://developers.google.com/web/fundamentals/performance/critical-rendering-path/render-tree-construction?hl=ko)

4. Layout

   브라우저의 뷰포트(Viewport) 내에서 각 노드들의 정확한 위치와 크기를 계산합니다. Render Tree 노드들이 가지고 있는 스타일과 속성에 따라서 __브라우저 화면의 어느 위치에 어느 크기로 출력될지 계산하는 단계__라고 할 수 있습니다. 

5. Paint

   요소들을 실제 화면에 그리는 단계입니다.



<br>

<br>

## 가상 DOM

#### 가상 DOM이 나오게 된 이유

기존 방식은 화면의 변경사항을 DOM을 직접 조작해 브라우저에 반영하였다. 이렇게 하면 돔 트리가 수정될 때마다 실시간으로 렌더 트리가 생성되고, 불필요한 렌더링 작업이 반복적으로 일어나게 된다.



#### 가상 DOM 활용

화면에 변화가 있을 때마다 실시간으로 돔 트리를 수정하지 않고, 변경사항이 모두 반영된 가상 돔을 만들어 한 번만 DOM 수정을 한다. 결과적으로 브라우저는 한 번만 렌더링을 할 수 있게 된다.

==__실제 DOM에 접근하여 조작하는 대신 가상 DOM을 활용하여 불필요한 렌더링 횟수를 줄일 수 있다.__==



#### React에서 가상 DOM을 반영하는 절차

1. 데이터가 업데이트 되면, 전체 UI를 가상 DOM에 리렌더링
2. 이전 가상 DOM에 있던 내용과 현재의 내용을 비교(<span style="color:red">가상 DOM 끼리 비교</span>)

3. <span style="color:red">바뀐 부분만</span> 실제 DOM에 적용

   (컴포넌트가 업데이트 될 때, 레이아웃 계산이 한 번만 이뤄짐)

![img](https://blog.kakaocdn.net/dn/Sjw1C/btrhBMKFIaQ/zSJrx0mIcjjvQQaVcEH8mk/img.png)

(출처: https://dev-cini.tistory.com/11)

> 이해하기 쉽게 DOM과 가상 DOM의 작업과정 비교
>
> - DOM을 사용할 때
>   1. 10개의 노드를 하나씩 수정한다.
>   2. 10번의 렌더링이 발생한다.
> - 가상 DOM을 사용할 때
>   1. 10개의 노드를 하나씩 수정한다.
>   2. 가상돔 끼리 비교하며 변경 사항을 가상돔에 10번 리렌더링 한다.
>   3. 실제 돔에 렌더링한다.

#### 결론

작은 규모의 레이아웃(리플로우)이 여러번 발생하는 것보다 큰 규모의 레이아웃이 한 번 발생하는 것은 성능상의 큰 차이를 나타낸다. 

React는 위와 같은 얕은 비교와 일괄 DOM 업데이트 방식을 이용해 성능 향상을 이끈다.
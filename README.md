# 학습시작기간 2023.03.06

# 목차
- [스프링 MVC - 기본 기능](#스프링-MVC---기본기능)
- [스프링 MVC - 웹 페이지 만들기](#스프링-MVC---웹-페이지-만들기)


## 스프링 MVC - 기본기능

### 로깅
```java
@RestController
@Slf4j
public class LogTestController {

    @RequestMapping("/log-test")
    public String logTest(){
        String name = "Spring";

        log.info("info log={}", name);
        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.error("error log={}", name);
        log.warn("warn log={}", name);
        return "ok";
    }
}
```

#### 로깅 라이브러리
스프링 부트 라이브러리를 사용하면 스프링 부트 로깅 라이브러리( spring-boot-starter-logging )가 함께 포함된다.

#### 로그 선언
- private Logger log = LoggerFactory.getLogger(getClass());
- private static final Logger log = LoggerFactory.getLogger(Xxx.class)
- @Slf4j : 롬복 사용 가능

#### @RestController
- @Controller 는 반환 값이 String 이면 뷰 이름으로 인식된다. 그래서 뷰를 찾고 뷰가 랜더링 된다.
- @RestController 는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다. 따라서 실행 결과로 ok 메세지를 받을 수 있다. @ResponseBody 와 관련이 있는데, 뒤에서 더 자세히 설명한다.

#### 로그 레벨 설정 - application.properties
```java
#전체 로그 레벨 설정(기본 info)
logging.level.root=info

#hello.springmvc 패키지와 그 하위 로그 레벨 설정
logging.level.hello.springmvc=debug
```

#### 올바른 로그 사용법
- log.debug("data="+data)
  - 로그 출력 레벨을 info로 설정해도 해당 코드에 있는 "data="+data가 실제 실행이 되어 버린다. 결과적으로 문자 더하기 연산이 발생한다.
- log.debug("data={}", data)
  - 로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않는다. 따라서 앞과 같은 의미없는 연산이 발생하지 않는다.

#### 로그 사용시 장점
- 쓰레드 정보, 클래스 이름 같은 부가 정보를 함께 볼 수 있고, 출력 모양을 조정할 수 있다.
- 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그를 상황에 맞게 조절할 수 있다.
- 시스템 아웃 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있다. 특히 파일로 남길 때는 일별, 특정 용량에 따라 로그를 분할하는 것도 가능하다.
- 성능도 일반 System.out보다 좋다. (내부 버퍼링, 멀티 쓰레드 등등) 그래서 실무에서는 꼭 로그를사용해야 한다.


## 스프링 MVC - 웹 페이지 만들기


_참고 문서 및 링크_
- 스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술(김영한)

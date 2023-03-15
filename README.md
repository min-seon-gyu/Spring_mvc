# 학습시작기간 2023.03.06()

# 목차
- [스프링 MVC - 기본 기능](#스프링-MVC---기본기능)

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

### 요청 매핑 예시
```java
    @RequestMapping(value = "/hello-basic", method = RequestMethod.GET)
    public String helloBasic(){
        log.info("helloBasic");
        return "0";
    }

    /**
     * method 특정 HTTP 메서드 요청만 허용 * GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1(){
        log.info("mappingGetV1");
        return "0";
    }

    /**
     * 편리한 축약 애노테이션 (코드보기)
     * @GetMapping
     * @PostMapping
     * @PutMapping
     * @DeleteMapping
     * @PatchMapping
     */
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2(){
        log.info("mappingGetV2");
        return "0";
    }

    /**
     * PathVariable 사용
     * 변수명이 같으면 생략 가능
     * @PathVariable("userId") String userId -> @PathVariable userId
     */
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId")String data){
        log.info("mappingPath userId={}", data);
        return "0";
    }

    /**
     * PathVariable 사용 다중
     */
    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable String userId, @PathVariable Long
            orderId) {
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "ok";
    }

    /**
     * 파라미터로 추가 매핑
     * params="mode",
     * params="!mode"
     * params="mode=debug"
     * params="mode!=debug" (! = )
     * params = {"mode=debug","data=good"}
     */
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        log.info("mappingParam");
        return "ok";
    }

    /**
     * 특정 헤더로 추가 매핑
     * headers="mode",
     * headers="!mode"
     * headers="mode=debug"
     * headers="mode!=debug" (! = )
     */
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
    }

    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * consumes="application/json"
     * consumes="!application/json"
     * consumes="application/*"
     * consumes="*\/*"
     * MediaType.APPLICATION_JSON_VALUE
     */
    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }

    /**
     * Accept 헤더 기반 Media Type
     * produces = "text/html"
     * produces = "!text/html" * produces = "text/*"
     * produces = "*\/*"
     */
    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }
```


### HTTP 요청 - 기본, 헤더 조회
```java
    @RequestMapping("/headers")
    public String headers(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpMethod httpMethod,
                          Locale locale,
                          @RequestHeader MultiValueMap<String, String> headerMap,
                          @RequestHeader("host") String host,
                          @CookieValue(value = "myCookie", required = false) String cookie){

        log.info("request={}", request);
        log.info("response={}", response);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);
        return "ok";
    }
```


- HttpServletRequest
- HttpServletResponse
- HttpMethod : HTTP 메서드를 조회한다. org.springframework.http.HttpMethod
- Locale : Locale 정보를 조회한다.
- @RequestHeader MultiValueMap<String, String> headerMap
    - 모든 HTTP 헤더를 MultiValueMap 형식으로 조회한다.
- @RequestHeader("host") String host
    - 특정 HTTP 헤더를 조회한다.
    - 속성
        - 필수 값 여부: required
        - 기본 값 속성: defaultValue
- @CookieValue(value = "myCookie", required = false) String cookie
    - 특정 쿠키를 조회한다.
    - 속성
        - 필수 값 여부: required
        - 기본 값: defaultValue

#### MultiValueMap
- MAP과 유사한데, 하나의 키에 여러 값을 받을 수 있다.
- HTTP header, HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다. 

### HTTP 요청 파라미터 처리
```java
@Slf4j
@Controller
public class RequestParamController {

    /**
    * 반환 타입이 없으면서 이렇게 응답에 값을 직접 집어넣으면, view 조회X
    */
    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username = {}, age = {}", username, age);

        response.getWriter().write("ok");
    }

    /**
     * @RequestParam 사용
     * - 파라미터 이름으로 바인딩
     * @ResponseBody 추가
     * - View 조회를 무시하고, HTTP message body에 직접 해당 내용 입력
     */
    @ResponseBody
    @RequestMapping("/request-param-v2")
    public String requestParamV2(@RequestParam("username")String memberName, @RequestParam("age")int memberAge){
        log.info("username = {}, age = {}", memberName, memberAge);
        return "ok";
    }

    /**
     * @RequestParam 사용
     * HTTP 파라미터 이름이 변수 이름과 같으면 @RequestParam(name="xx") 생략 가능
     */
    @ResponseBody
    @RequestMapping("/request-param-v3")
    public String requestParamV3(@RequestParam String username, @RequestParam int age){
        log.info("username = {}, age = {}", username, age);
        return "ok";
    }

    /**
     * @RequestParam 사용
     * String, int 등의 단순 타입이면 @RequestParam 도 생략 가능
     * @RequestParam 애노테이션을 생략하면 스프링 MVC는 내부에서 required=false 를 적용한다.
     */
    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamV4(String username, int age){
        log.info("username = {}, age = {}", username, age);
        return "ok";
    }

    /**
    * @RequestParam.required
    * /request-param-required -> username이 없으므로 예외
     *
    * 주의!
    * /request-param-required?username= -> 빈문자로 통과
    *
    * 주의!
    * /request-param-required
    * int age -> null을 int에 입력하는 것은 불가능, 따라서 Integer 변경해야 함(또는 다음에 나오는
    defaultValue 사용)
     */
    @ResponseBody
    @RequestMapping("/request-param-v5")
    public String requestParamV5(@RequestParam String username, @RequestHeader(required = false) Integer age){
        log.info("username = {}, age = {}", username, age);
        return "ok";
    }

    /**
    * @RequestParam
    * - defaultValue 사용
    *
    * 참고: defaultValue는 빈 문자의 경우에도 적용
    * /request-param-default?username=
    */
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(defaultValue = "guest") String username,
            @RequestHeader(required = false, defaultValue = "-1") int age){
        log.info("username = {}, age = {}", username, age);
        return "ok";
    }

    /**
    * @RequestParam Map, MultiValueMap
    * Map(key=value)
    * MultiValueMap(key=[value1, value2, ...]) ex) (key=userIds, value=[id1, id2])
    */
    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamMap(@RequestParam Map<String, Objects> paramMap){
        log.info("username = {}, age = {}", paramMap.get("username"), paramMap.get("age"));
        return "ok";
    }

    /**
    * @ModelAttribute 사용
    * 참고: model.addAttribute(helloData) 코드도 함께 자동 적용됨
    */
    @RequestMapping("/model-attribute-v1")
    public String modelAttributeV1(@ModelAttribute HelloData helloData){
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }

    /** * @ModelAttribute 생략 가능
    * String, int 같은 단순 타입 = @RequestParam
    * argument resolver 로 지정해둔 타입 외 = @ModelAttribute
    */
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData){
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }
}
```


### HTTP 요청 메세지 - 단순 텍스트
```java
@Slf4j
@Controller
public class RequestBodyStringController {

    @PostMapping("/request-body-string-v1")
    public void requestBodyString(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody = {}", messageBody);
        response.getWriter().write("ok");
    }

    /**
    * InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회
    * OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력
    */
    @PostMapping("/request-body-string-v2")
    public void requestBodyStringV2(InputStream inputStream, Writer writer) throws IOException {
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody = {}", messageBody);
        writer.write("ok");
    }

    /**
    * HttpEntity: HTTP header, body 정보를 편리하게 조회
    * - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
    * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
    *
    * 응답에서도 HttpEntity 사용 가능
    * - 메시지 바디 정보 직접 반환(view 조회X)
    * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
    */
    @PostMapping("/request-body-string-v3")
    public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity) throws IOException {
        String messageBody = httpEntity.getBody();
        log.info("messageBody = {}", messageBody);

        return new HttpEntity<String>("ok");
    }

    /**
    * @RequestBody
    * - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
    * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
    *
    * @ResponseBody
    * - 메시지 바디 정보 직접 반환(view 조회X)
    * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
    */
    @ResponseBody
    @PostMapping("/request-body-string-v4")
    public String requestBodyStringV4(@RequestBody String messageBody) throws IOException {
        log.info("messageBody = {}", messageBody);

        return "ok";
    }
}
```

- HttpEntity: HTTP header, body 정보를 편리하게 조회
    - 메시지 바디 정보를 직접 조회
    - 요청 파라미터를 조회하는 기능과 관계 없음 @RequestParam X, @ModelAttribute X
- HttpEntity는 응답에도 사용 가능
    - 메시지 바디 정보 직접 반환
    - 헤더 정보 포함 가능
    - view 조회XHttpEntity 를 상속받은 다음 객체들도 같은 기능을 제공한다.
- RequestEntity
    - HttpMethod, url 정보가 추가, 요청에서 사용
- ResponseEntity
    - HTTP 상태 코드 설정 가능, 응답에서 사용
    
#### 요청 파라미터 vs HTTP 메시지 바디
- 요청 파라미터를 조회하는 기능: @RequestParam , @ModelAttribute
- HTTP 메시지 바디를 직접 조회하는 기능: @RequestBody

#### @ResponseBody
- @ResponseBody 를 사용하면 응답 결과를 HTTP 메시지 바디에 직접 담아서 전달할 수 있다.
- 물론 이 경우에도 view를 사용하지 않는다

### HTTP 요청 메세지 - JSON
```java
@Slf4j
@Controller
public class RequestBodyJsonController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody = {}", messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        response.getWriter().write("ok");
    }

    /**
    * @RequestBody
    * HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
    *
    * @ResponseBody
    * - 모든 메서드에 @ResponseBody 적용
    * - 메시지 바디 정보 직접 반환(view 조회X)
    * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
    */
    @ResponseBody
    @PostMapping("/request-body-json-v2")
    public String requestBodyJsonV2(@RequestBody String messageBody) throws IOException {
        log.info("messageBody = {}", messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }

    /**
    * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
    * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (contenttype:application/json)
    */
    @ResponseBody
    @PostMapping("/request-body-json-v3")
    public String requestBodyJsonV3(@RequestBody HelloData helloData) {
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }

    @ResponseBody
    @PostMapping("/request-body-json-v4")
    public String requestBodyJsonV4(HttpEntity<HelloData> data) {
        HelloData helloData = data.getBody();
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }

    /**
    * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
    * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (contenttype:application/json)
    * @ResponseBody 적용
    * - 메시지 바디 정보 직접 반환(view 조회X)
    * - HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter 적용(Accept: application/json)
    */
    @ResponseBody
    @PostMapping("/request-body-json-v5")
    public HelloData requestBodyJsonV5(@RequestBody HelloData helloData) {
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());
        return helloData;
    }
}
```

HttpEntity , @RequestBody 를 사용하면 HTTP 메시지 컨버터가 HTTP 메시지 바디의 내용을 우리가 원하는 문자나 객체 등으로 변환해준다.

#### @RequestBody 요청
- JSON 요청 HTTP 메시지 컨버터 객체

#### @ResponseBody 응답
- 객체 HTTP 메시지 컨버터 JSON 응답

### HTTP 응답 - 정적 리소스, 뷰 템플릿

- 정적 리소스
    - 예) 웹 브라우저에 정적인 HTML, css, js를 제공할 때는, 정적 리소스를 사용한다.
    - /static , /public , /resources , /META-INF/resources
- 뷰 템플릿 사용
    - 예) 웹 브라우저에 동적인 HTML을 제공할 때는 뷰 템플릿을 사용한다.
    - src/main/resources/templates
- HTTP 메시지 사용
    - HTTP API를 제공하는 경우에는 HTML이 아니라 데이터를 전달해야 하므로, HTTP 메시지 바디에 JSON 같은 형식으로 데이터를 실어 보낸다.
    
```java
@Controller
public class ResponseViewController {

    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewV1(){
        ModelAndView mav = new ModelAndView("response/hello").addObject("data","hello!");
        return mav;
    }

    @RequestMapping("/response-view-v2")
    public String responseViewV2(Model model){
        model.addAttribute("data",  "hello!");
        return "response/hello";
    }

    @RequestMapping("/response/hello")
    public void responseViewV3(Model model){
        model.addAttribute("data",  "hello!");
    }
}

```


#### HTTP 메시지
@ResponseBody , HttpEntity 를 사용하면, 뷰 템플릿을 사용하는 것이 아니라, HTTP 메시지 바디에 직접 응답 데이터를 출력할 수 있다.

### HTTP 응답 - HTTP API, 메시지 바디에 직접 입력

```java
@Slf4j
@Controller
public class ResponseBodyController {

    @GetMapping("/response-body-string-v1")
    public void responseBodyV1(HttpServletResponse response) throws IOException {
        response.getWriter().write("ok");
    }

    @GetMapping("/response-body-string-v2")
    public ResponseEntity<String> responseBodyV2(){
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/response-body-string-v3")
    public String responseBodyV3() throws IOException {
        return "ok";
    }

    @GetMapping("/response-body-json-v1")
    public ResponseEntity<HelloData> responseBodyJsonV1() throws IOException {
        HelloData helloData = new HelloData();
        helloData.setAge(10);
        helloData.setUsername("userA");

        return new ResponseEntity<>(helloData, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/response-body-json-v2")
    public HelloData responseBodyJsonV2() throws IOException {
        HelloData helloData = new HelloData();
        helloData.setAge(10);
        helloData.setUsername("userA");

        return helloData;
    }
}
```

#### responseBodyV1
서블릿을 직접 다룰 때 처럼 HttpServletResponse 객체를 통해서 HTTP 메시지 바디에 직접 ok 응답 메시지를 전달한다.


#### responseBodyV2
ResponseEntity 엔티티는 HttpEntity 를 상속 받았는데, HttpEntity는 HTTP 메시지의 헤더, 바디 정보를 가지고 있다. ResponseEntity 는 여기에 더해서 HTTP 응답 코드를 설정할 수 있다.
HttpStatus.CREATED 로 변경하면 201 응답이 나가는 것을 확인할 수 있다.


#### responseBodyV3
@ResponseBody 를 사용하면 view를 사용하지 않고, HTTP 메시지 컨버터를 통해서 HTTP 메시지를 직접 입력할 수 있다. ResponseEntity 도 동일한 방식으로 동작한다.


#### responseBodyJsonV1
ResponseEntity 를 반환한다. HTTP 메시지 컨버터를 통해서 JSON 형식으로 변환되어서 반환된다.


#### esponseBodyJsonV2
ResponseEntity 는 HTTP 응답 코드를 설정할 수 있는데, @ResponseBody 를 사용하면 이런 것을 정하기 까다롭다. ResponseStatus(HttpStatus.OK) 애노테이션을 사용하면 응답 코드도 설정할 수 있다.

### HTTP 메시지 컨버터

스프링 MVC는 다음의 경우에 HTTP 메시지 컨버터를 적용한다.
- HTTP 요청: @RequestBody , HttpEntity(RequestEntity)
- HTTP 응답: @ResponseBody , HttpEntity(ResponseEntity)

HTTP 메시지 컨버터는 HTTP 요청, HTTP 응답 둘 다 사용된다.
- canRead() , canWrite() : 메시지 컨버터가 해당 클래스, 미디어타입을 지원하는지 체크
- read() , write() : 메시지 컨버터를 통해서 메시지를 읽고 쓰는 기능

스프링 부트 기본 메시지 컨버터(일부 생략)
- 0 = ByteArrayHttpMessageConverter
- 1 = StringHttpMessageConverter 
- 2 = MappingJackson2HttpMessageConverter

- ByteArrayHttpMessageConverter : byte[] 데이터를 처리한다.
    - 클래스 타입: byte[] , 미디어타입: */* ,
    - 요청 예) @RequestBody byte[] data
    - 응답 예) @ResponseBody return byte[] 쓰기 미디어타입 application/octet-stream
- StringHttpMessageConverter : String 문자로 데이터를 처리한다.
    - 클래스 타입: String , 미디어타입: */*
    - 요청 예) @RequestBody String data
    - 응답 예) @ResponseBody return "ok" 쓰기 미디어타입 text/plain
- MappingJackson2HttpMessageConverter : application/json
    - 클래스 타입: 객체 또는 HashMap , 미디어타입 application/json 관련
    - 요청 예) @RequestBody HelloData data
    - 응답 예) @ResponseBody return helloData 쓰기 미디어타입 application/json 관련
    
#### StringHttpMessageConverter
```
content-type: application/json
@RequestMapping
void hello(@RequestBody String data) {}
```


#### MappingJackson2HttpMessageConverter
```
content-type: application/json
@RequestMappingvoid 
hello(@RequestBody HelloData data) {}
```

#### HTTP 요청 데이터 읽기
- HTTP 요청이 오고, 컨트롤러에서 @RequestBody , HttpEntity 파라미터를 사용한다.
- 메시지 컨버터가 메시지를 읽을 수 있는지 확인하기 위해 canRead() 를 호출한다.
    - 대상 클래스 타입을 지원하는가.
        - 예) @RequestBody 의 대상 클래스 ( byte[] , String , HelloData )
    - HTTP 요청의 Content-Type 미디어 타입을 지원하는가.
        - 예) text/plain , application/json , */*
- canRead() 조건을 만족하면 read() 를 호출해서 객체 생성하고, 반환한다.

#### HTTP 응답 데이터 생성
- 컨트롤러에서 @ResponseBody , HttpEntity 로 값이 반환된다. 
- 메시지 컨버터가 메시지를 쓸 수 있는지 확인하기 위해 canWrite() 를 호출한다.
    - 대상 클래스 타입을 지원하는가.
        - 예) return의 대상 클래스 ( byte[] , String , HelloData )
    - HTTP 요청의 Accept 미디어 타입을 지원하는가.(더 정확히는 @RequestMapping 의 produces )
        - 예) text/plain , application/json , **/**
- canWrite() 조건을 만족하면 write() 를 호출해서 HTTP 응답 메시지 바디에 데이터를 생성한다.

### 요청 매핑 헨들러 어뎁터 구조

#### RequestMappingHandlerAdapter 동작 방식
![](https://velog.velcdn.com/images/gcael/post/286ea5ad-d868-42a5-ac0d-78dfc0b03be8/image.PNG)

#### ArgumentResolver
생각해보면, 애노테이션 기반의 컨트롤러는 매우 다양한 파라미터를 사용할 수 있었다.HttpServletRequest , Model 은 물론이고, @RequestParam , @ModelAttribute 같은 애노테이션
그리고 @RequestBody , HttpEntity 같은 HTTP 메시지를 처리하는 부분까지 매우 큰 유연함을 보여주었다.


이렇게 파라미터를 유연하게 처리할 수 있는 이유가 바로 ArgumentResolver 덕분이다. 애노테이션 기반 컨트롤러를 처리하는 RequestMappingHandlerAdapter는 바로 이 ArgumentResolver 를 호출해서 컨트롤러(핸들러)가 필요로 하는 다양한 파라미터의 값(객체)을 생성한다. 그리고 이렇게 파리미터의 값이 모두 준비되면 컨트롤러를 호출하면서 값을 넘겨준다.

#### 동작 방식
ArgumentResolver 의 supportsParameter() 를 호출해서 해당 파라미터를 지원하는지 체크하고, 지원하면 resolveArgument() 를 호출해서 실제 객체를 생성한다. 그리고 이렇게 생성된 객체가 컨트롤러 호출시 넘어가는 것이다.

#### ReturnValueHandler
HandlerMethodReturnValueHandler 를 줄여서 ReturnValueHandler 라 부른다. ArgumentResolver 와 비슷한데, 이것은 응답 값을 변환하고 처리한다. 컨트롤러에서 String으로 뷰 이름을 반환해도, 동작하는 이유가 바로 ReturnValueHandler 덕분이다.

#### HTTP 메시지 컨버터 위치
![](https://velog.velcdn.com/images/gcael/post/1c18b317-a969-4fef-9770-11cdfb09a4bf/image.PNG)
- 요청의 경우 @RequestBody 를 처리하는 ArgumentResolver 가 있고, HttpEntity 를 처리하는 ArgumentResolver 가 있다. 이 ArgumentResolver 들이 HTTP 메시지 컨버터를 사용해서 필요한 객체를 생성하는 것이다.
- 응답의 경우 @ResponseBody 와 HttpEntity 를 처리하는 ReturnValueHandler 가 있다. 그리고 여기에서 HTTP 메시지 컨버터를 호출해서 응답 결과를 만든다.

_참고 문서 및 링크_
- 스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술(김영한)

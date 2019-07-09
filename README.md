[![Build Status](https://travis-ci.org/Tradingchain/peppa-proto.svg?branch=master)](https://travis-ci.org/Tradingchain/peppa-proto)

# peppa-proto

* proto文件放在src/main/proto目录下。

* mvn install编译打包成jar。

* Spring Boot工程配置AESEncryptionFilter：

   +增加如下filter配置：filter 服务端会做处理
   
   ```
   @Configuration
   public class FilterConfig {
   
     @Bean
     public Filter aesEncryptionFilter() {
       return new AESEncryptionFilter();
     }
   
     @Bean
     public FilterRegistrationBean aesEncryptFilterRegistration() {
       FilterRegistrationBean registration = new FilterRegistrationBean();
       registration.setFilter(new DelegatingFilterProxy("aesEncryptionFilter"));
       registration.addUrlPatterns("/*"); //
       registration.setName("aesEncryptionFilter");
       registration.setOrder(1);
       return registration;
     }
   }
     ```
     
* Client代码示例：
```
PersonProto.Person.Builder builder = PersonProto.Person.newBuilder();
        PersonProto.Person person = builder
                .setId(1)
                .setName("Scala")
                .setEmail("scala@exxample.com")
                .addPhone(PersonProto.Person.PhoneNumber.newBuilder()
                        .setNumber("123456")
                        .setType(PersonProto.Person.PhoneType.HOME).build())
                .addPhone(PersonProto.Person.PhoneNumber.newBuilder()
                        .setNumber("100000")
                        .setType(PersonProto.Person.PhoneType.WORK).build())
                .build();
        System.out.println("original: " + person);

        String encrypted = AESEncryptionUtil.encrypt("somekey","someiv", person.toByteArray());
        Request request = new Request.Builder()
                .url("http://localhost:8080/update")
                .post(RequestBody.create(MediaType.parse("application/x-protobuf"), encrypted))
                ..header("TRADING_CHAIN_PLATFORM","请咨询客户经理")
                .build();
        Response response = new OkHttpClient().newCall(request).execute();
        byte[] decrypted = AESEncryptionUtil.decrypt("somekey", response.body().bytes());

        System.out.println(new String(decrypted, "utf-8"));
  ```
  
  * Server代码示例：
  ```
  @PostMapping(value = "/update")
    public ResponseEntity<PersonProto.Person> getPersonInfo(RequestEntity<PersonProto.Person> entity) {
        PersonProto.Person person = entity.getBody();
        System.out.println("request: " + person);
        PersonProto.Person updated = PersonProto.Person.newBuilder().setId(person.getId()).setName("Mr. " + person.getName()).addAllPhone(person.getPhoneList()).build();
        System.out.println("response: " + updated);
        return ResponseEntity.ok(updated);
    }
  ```

### [接口文档](https://github.com/Tradingchain/api-service-new/wiki)
# peppa-proto

* proto文件放在src/main/proto目录下。

* mvn install编译打包成jar。

* Spring Boot工程配置AESEncryptionFilter：

   - Application上加@ServletComponentScan。
   - 增加如下filter配置：
   ```
   @Bean
   public FilterRegistrationBean aesEncryptFilterRegistration() { 
        FilterRegistrationBean registration = new FilterRegistrationBean(new AESEncryptionFilter("somekey"));
        registration.addUrlPatterns("/*"); //
        registration.setName("aesEncryptionFilter");
        registration.setOrder(1);
        return registration;
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

        String encrypted = AESEncryptionUtil.encrypt("somekey", person.toByteArray());
        Request request = new Request.Builder()
                .url("http://localhost:8080/update")
                .post(RequestBody.create(MediaType.parse("application/x-protobuf"), encrypted))
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

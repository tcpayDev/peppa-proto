![Publish both to Gihub Packages and Maven Central](https://github.com/tcpayDev/peppa-proto/workflows/Publish%20both%20to%20Gihub%20Packages%20and%20Maven%20Central/badge.svg)
# peppa-proto

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
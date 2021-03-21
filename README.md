# Android Snooper

![Build Status](https://github.com/jainsahab/AndroidSnooper/workflows/AndroidSnooper%20CI/badge.svg?branch=master&event=push)[![codecov](https://codecov.io/gh/jainsahab/AndroidSnooper/branch/master/graph/badge.svg)](https://codecov.io/gh/jainsahab/AndroidSnooper)[![Maven Central](https://img.shields.io/maven-central/v/com.github.jainsahab/Snooper.svg)](https://mvnrepository.com/artifact/com.github.jainsahab/Snooper)[![BCH compliance](https://bettercodehub.com/edge/badge/jainsahab/AndroidSnooper?branch=master)](https://bettercodehub.com/)[![HitCount](http://hits.dwyl.com/jainsahab/AndroidSnooper.svg)](http://hits.dwyl.com/jainsahab/AndroidSnooper)


## Introduction
Android Snooper is a library which helps in debugging issues while running the applications on android devices. One of the basic features provided includes HTTP inspection, which observes all the HTTP calls made by the app. When the device is shaked, history of all the HTTP requests is presented with their responses and allows search, copy and sharing of the request/response. This library is inspired by the `Network Request History` feature of [FLEX](https://github.com/Flipboard/FLEX) app for iOS.

![Snooper](assets/snooper_demo.gif)

## How to use?
Android Snooper works on the [interceptor pattern](https://en.wikipedia.org/wiki/Interceptor_pattern) which is supported by almost every HTTP client. All you need to do is initialize Android Snooper using `AndroidSnooper.init(this);` statement in your Application class and set an instance of `SnooperInterceptor` to the list of your network interceptors.
As of now Snooper supports the interceptors for the following libraries:

* [Spring Android Rest Template](http://projects.spring.io/spring-android/)
* [Square's OkHttp Client](https://github.com/square/okhttp)

Didn't get your HTTP client's name in the list? No worries, You can still write your own implementation by using `Android Snooper's` core module and let Android Snooper know about the request being made. Below is given a dummy implementation.
```java
    AndroidSnooper androidSnooper = AndroidSnooper.getInstance();
    HttpCall httpCall = new HttpCall.Builder()
      .withUrl(httpRequest.getUrl())
      .withPayload(httpRequest.getRequestPayload())
      .withMethod(httpRequest.getMethod())
      .withResponseBody(httpResponse.getResponseBody())
      .withStatusCode(httpResponse.getRawStatusCode())
      .withStatusText(httpResponse.getStatusCode())
      .withRequestHeaders(httpRequest.getHeaders())
      .withResponseHeaders(httpResponse.getHeaders())
      .build();
    androidSnooper.record(httpCall);
```
The above implementation ought to be part of your custom interceptor where you will have access to the required `Request` and `Response` object to jot down the required data for Android Snooper to work properly.

**Warning:** Android Snooper records each and every HTTP call intercepted by it. The sole purpose of Android snooper is to help in *debugging*, hence **only Debug or QA builds** are the perfect candidates for integrating the library. Sensitive information such as **Auth Tokens**, **Headers**, etc are not supposed to be exposed and hence production apps should not integrate the library.

## Integration
```groovy
    repositories {
        mavenCentral()
    }

    // when using Android Snooper's core module
    compile ('com.github.jainsahab:Snooper:1.5.5@aar'){
      transitive = true
    }

    // Android Snooper library for "Spring Android Rest Template"
    compile ('com.github.jainsahab:Snooper-Spring:1.5.5@aar'){
      transitive = true
    }
    
    // Android Snooper library for "Square's Okhttp"
    compile ('com.github.jainsahab:Snooper-Okhttp:1.5.5@aar'){
      transitive = true
    }
```
Snapshot versions are available in [Sonatype's snapshots repository](https://oss.sonatype.org/content/repositories/snapshots).

## Contributing
If you would like to contribute code to `AndroidSnooper` you can do so by forking the repository and create a Pull request. You can also create an Issue to report bugs or features that you want to see in `AndroidSnooper` library.

## Attributions
This library uses:
* Icons made by [Madebyoliver](http://www.flaticon.com/authors/madebyoliver), [Freepik](http://www.freepik.com) and [Gregor Cresnar](http://www.flaticon.com/authors/gregor-cresnar) from [www.flaticon.com](http://www.flaticon.com) is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>.


LICENSE
-------

```LICENSE
Copyright (C) 2017 Prateek Jain

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
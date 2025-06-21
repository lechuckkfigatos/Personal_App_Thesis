//package org.lechuck.personal_app.FeignClient;
//
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//
//@FeignClient(name = "genimiClient", url = "https://generativelanguage.googleapis.com/v1beta")
//public interface GeminiClientInterface {
//
//    @PostMapping(value = "/models/gemini-2.5-pro-experimental-0325:generateContent",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    String generateContent(
//            @RequestHeader("x-goog-api-key") String apiKey,
//            @RequestBody String reqBody);
//
//}

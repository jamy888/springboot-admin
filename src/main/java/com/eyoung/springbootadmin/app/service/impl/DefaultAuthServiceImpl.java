package com.eyoung.springbootadmin.app.service.impl;

import com.eyoung.springbootadmin.app.service.IAuthService;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Source;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public abstract  class DefaultAuthServiceImpl implements IAuthService {

    public static RestTemplate getRestTemplate() {// 手动添加
        SimpleClientHttpRequestFactory requestFactory=new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(120000);
        List<HttpMessageConverter<?>> messageConverters = new LinkedList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter<Source>());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate=new RestTemplate(messageConverters);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }
}

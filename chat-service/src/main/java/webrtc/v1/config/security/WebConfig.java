package webrtc.v1.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import webrtc.v1.channel.interceptor.ChannelInterceptor;
import webrtc.v1.chat.interceptor.ChatInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Autowired
    private ChannelInterceptor channelInterceptor;
    @Autowired
    private ChatInterceptor chatInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(channelInterceptor)
            .addPathPatterns("/api/v1/webrtc/chat/channel");
        registry.addInterceptor(chatInterceptor)
            .addPathPatterns("/chat/room");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}

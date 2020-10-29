package com.eyoung.springbootadmin.security.core.login;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/10/28 17:54
 */
@Component
public class LoginFactory implements ApplicationContextAware {

//    public static final String ACOUNT = "Account";
//    public static final String WECHAT_MP = "WechatMp";
//    public static final String QQ = "qq";
//    public static final String DEFAULT_PAY = ACOUNT;
//
//    private static Map<String, LoginWay> strategy = new HashMap<String, LoginWay>();
//
//    static {
//        strategy.put(ACOUNT, new AccountLogin());
//        strategy.put(WECHAT_MP, new WechatMpLogin());
//        strategy.put(QQ, new QQLogin());
//    }
//
//    public static LoginWay getWay(String payKey) {
//        if (!strategy.containsKey(payKey)) {
//            return strategy.get(DEFAULT_PAY);
//        }
//        return strategy.get(payKey);
//    }

    @Autowired
    private ApplicationContext applicationContext;
    //存放所有策略类Bean的map
    public static Map<String, Class<LoginWay>> loginStrategyBeanMap = new HashMap<>();

    public LoginWay getWay(String type) {
        Class<LoginWay> strategyClass = loginStrategyBeanMap.get(type);
        if (strategyClass == null) {
            throw new IllegalArgumentException("没有对应的登录方式");
        }
        //从容器中获取对应的策略Bean
        return applicationContext.getBean(strategyClass);
    }

    /**
     * 获取所有的登录策略LoginWay的Beanclass 加入loginStrategyBeanMap属性中
     * <p>
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //获取所有策略注解的Bean
        Map<String, Object> orderStrategyMap = applicationContext.getBeansWithAnnotation(ChooseWay.class);
        orderStrategyMap.forEach((k, v) -> {
            //获取策略实现类
            Class<LoginWay> orderStrategyClass = (Class<LoginWay>) v.getClass();
            //获取策略实现类的注解值。
            String type = orderStrategyClass.getAnnotation(ChooseWay.class).value();
            //将class加入HandlerOrderContext的map中,type作为key
            loginStrategyBeanMap.put(type, orderStrategyClass);
        });
    }
}

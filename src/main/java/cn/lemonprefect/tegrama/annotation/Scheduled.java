package cn.lemonprefect.tegrama.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled{
    String cron();
    String group() default "default";
}

# Redis PubSub trial with Spring Native

## Presentation

This is a POC demonstrating the `spring-native` v`0.9.1` ability to compile and drive a not so-simple application. 
The code includes a Publisher/Subscriber implementation on a Redis queue and is intended to run on both `HotSpot` JVM and `Substrate` VM a.k.a Bytecode and native.

Both compilation types are proposed, and startup tests are done.

## Require

- GraalVM with `native-image`
- Maven 
- `Docker` to build natively and execute some startup commands.

## Build and starts

### In Bytecode
  
- Start the Redis database :
```
 % docker container run --name redis-pubsub --rm -it -d -p 6379:6379 redis:6.2.1
```

- Build and Run the pub/sub app :
```
 % mvn package spring-boot:run -Dspring-boot.run.arguments=--spring.redis.host=localhost
```
> We have to change the default hostname of redis database with `spring.redis.host`

- Expected output :
```
OpenJDK 64-Bit Server VM warning: forcing TieredStopAtLevel to full optimization because JVMCI is enabled
2021-03-31 14:53:37.266  INFO 79704 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.4)

.../...

2021-04-05 10:12:52.318  INFO 1031 --- [           main] i.s.c.virusapp.VirusappApplication       : Started VirusappApplication in 2.015 seconds (JVM running for 2.362)
Let's start!
1. Published Human(id=1, name=John, isInfected=false, isVaccinated=false)
End!!
2. Received Human(id=1, name=John, isInfected=false, isVaccinated=false)
3. Transformed Human(id=1, name=John, isInfected=true, isVaccinated=false)
```

### In Native

- Generate the native configurations (optional) :
```
% java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image/ -jar ./target/virusapp-0.0.1-lettuce.jar
```

- Create the docker network :
```
 % docker network create pubsub-network
```

- Start the Redis database :
```
 % docker container run --name redis-pubsub --network pubsub-network --rm -it -d -p 6379:6379 redis:6.2.1
```

- Build the pub/sub app :
```
 % mvn clean spring-boot:build-image
```

- Starts the pub/sub app :
```
% docker run --rm -it --name virus-app --network pubsub-network virusapp:0.0.1-lettuce
```

- Expected output :
```
% docker run --rm -it --name virus-app --network pubsub-network virusapp:0.0.1-lettuce
2021-04-05 08:21:27.035  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.4)

2021-04-05 08:21:27.037  INFO 1 --- [           main] i.s.c.virusapp.VirusappApplication       : Starting VirusappApplication using Java 11.0.10 on 5a57c43bed4a with PID 1 (/workspace/io.scalastic.coronapubsub.virusapp.VirusappApplication started by cnb in /workspace)
2021-04-05 08:21:27.037  INFO 1 --- [           main] i.s.c.virusapp.VirusappApplication       : No active profile set, falling back to default profiles: default
2021-04-05 08:21:27.037 DEBUG 1 --- [           main] o.s.boot.SpringApplication               : Loading source class io.scalastic.coronapubsub.virusapp.VirusappApplication
2021-04-05 08:21:27.050  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
2021-04-05 08:21:27.050  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2021-04-05 08:21:27.051 DEBUG 1 --- [           main] o.s.b.a.AutoConfigurationPackages        : @EnableAutoConfiguration was declared on a class in the package 'io.scalastic.coronapubsub.virusapp'. Automatic @Repository and @Entity scanning is enabled.
2021-04-05 08:21:27.051  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 0 ms. Found 0 Redis repository interfaces.
2021-04-05 08:21:27.096 ERROR 1 --- [pool-2-thread-1] o.s.d.r.l.RedisMessageListenerContainer  : SubscriptionTask aborted with exception:

java.lang.NoSuchMethodError: io.lettuce.core.pubsub.api.sync.RedisPubSubCommands.dispatch(io.lettuce.core.protocol.ProtocolKeyword, io.lettuce.core.output.CommandOutput, io.lettuce.core.protocol.CommandArgs)
	at com.sun.proxy.$Proxy232.<clinit>(Unknown Source) ~[na:na]
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.invokeClassInitializer(ClassInitializationInfo.java:375) ~[na:na]
	at com.oracle.svm.core.classinitialization.ClassInitializationInfo.initialize(ClassInitializationInfo.java:295) ~[na:na]
	at java.lang.reflect.Constructor.newInstance(Constructor.java:490) ~[na:na]
	at java.lang.reflect.Proxy.newProxyInstance(Proxy.java:1022) ~[na:na]
	at java.lang.reflect.Proxy.newProxyInstance(Proxy.java:1008) ~[na:na]
	at io.lettuce.core.RedisChannelHandler.syncHandler(RedisChannelHandler.java:322) ~[io.scalastic.coronapubsub.virusapp.VirusappApplication:6.1.0.RELEASE]
	at io.lettuce.core.pubsub.StatefulRedisPubSubConnectionImpl.newRedisSyncCommandsImpl(StatefulRedisPubSubConnectionImpl.java:103) ~[na:na]
	at io.lettuce.core.pubsub.StatefulRedisPubSubConnectionImpl.newRedisSyncCommandsImpl(StatefulRedisPubSubConnectionImpl.java:45) ~[na:na]
	at io.lettuce.core.StatefulRedisConnectionImpl.<init>(StatefulRedisConnectionImpl.java:81) ~[na:na]
	at io.lettuce.core.pubsub.StatefulRedisPubSubConnectionImpl.<init>(StatefulRedisPubSubConnectionImpl.java:61) ~[na:na]
	at io.lettuce.core.RedisClient.newStatefulRedisPubSubConnection(RedisClient.java:641) ~[io.scalastic.coronapubsub.virusapp.VirusappApplication:6.1.0.RELEASE]
	at io.lettuce.core.RedisClient.connectPubSubAsync(RedisClient.java:417) ~[io.scalastic.coronapubsub.virusapp.VirusappApplication:6.1.0.RELEASE]
	at io.lettuce.core.RedisClient.connectPubSub(RedisClient.java:363) ~[io.scalastic.coronapubsub.virusapp.VirusappApplication:6.1.0.RELEASE]
	at org.springframework.data.redis.connection.lettuce.StandaloneConnectionProvider.getConnection(StandaloneConnectionProvider.java:109) ~[na:na]
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$ExceptionTranslatingConnectionProvider.getConnection(LettuceConnectionFactory.java:1459) ~[na:na]
	at org.springframework.data.redis.connection.lettuce.LettuceConnection.switchToPubSub(LettuceConnection.java:907) ~[na:na]
	at org.springframework.data.redis.connection.lettuce.LettuceConnection.initSubscription(LettuceConnection.java:911) ~[na:na]
	at org.springframework.data.redis.connection.lettuce.LettuceConnection.subscribe(LettuceConnection.java:847) ~[na:na]
	at org.springframework.data.redis.listener.RedisMessageListenerContainer$SubscriptionTask.eventuallyPerformSubscription(RedisMessageListenerContainer.java:796) ~[na:na]
	at org.springframework.data.redis.listener.RedisMessageListenerContainer$SubscriptionTask.run(RedisMessageListenerContainer.java:752) ~[na:na]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128) ~[na:na]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628) ~[na:na]
	at java.lang.Thread.run(Thread.java:834) ~[na:na]
	at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:519) ~[na:na]
	at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:192) ~[na:na]

2021-04-05 08:21:32.075 DEBUG 1 --- [           main] ConditionEvaluationReportLoggingListener : 


============================
CONDITIONS EVALUATION REPORT
============================


Positive matches:
-----------------

   AopAutoConfiguration matched:
      - @ConditionalOnProperty (spring.aop.auto=true) matched (OnPropertyCondition)

   JacksonAutoConfiguration matched:
      - @ConditionalOnClass found required class 'com.fasterxml.jackson.databind.ObjectMapper' (OnClassCondition)

   LettuceConnectionConfiguration matched:
      - @ConditionalOnClass found required class 'io.lettuce.core.RedisClient' (OnClassCondition)
      - @ConditionalOnProperty (spring.redis.client-type=lettuce) matched (OnPropertyCondition)

   LettuceConnectionConfiguration#lettuceClientResources matched:
      - @ConditionalOnMissingBean (types: io.lettuce.core.resource.ClientResources; SearchStrategy: all) did not find any beans (OnBeanCondition)

   LifecycleAutoConfiguration#defaultLifecycleProcessor matched:
      - @ConditionalOnMissingBean (names: lifecycleProcessor; SearchStrategy: current) did not find any beans (OnBeanCondition)

   PersistenceExceptionTranslationAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor' (OnClassCondition)

   PersistenceExceptionTranslationAutoConfiguration#persistenceExceptionTranslationPostProcessor matched:
      - @ConditionalOnProperty (spring.dao.exceptiontranslation.enabled) matched (OnPropertyCondition)
      - @ConditionalOnMissingBean (types: org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor; SearchStrategy: all) did not find any beans (OnBeanCondition)

   PropertyPlaceholderAutoConfiguration#propertySourcesPlaceholderConfigurer matched:
      - @ConditionalOnMissingBean (types: org.springframework.context.support.PropertySourcesPlaceholderConfigurer; SearchStrategy: current) did not find any beans (OnBeanCondition)

   RedisAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.data.redis.core.RedisOperations' (OnClassCondition)

   RedisAutoConfiguration#stringRedisTemplate matched:
      - @ConditionalOnSingleCandidate (types: org.springframework.data.redis.connection.RedisConnectionFactory; SearchStrategy: all) found a primary bean from beans 'lettuceConnectionFactory'; @ConditionalOnMissingBean (types: org.springframework.data.redis.core.StringRedisTemplate; SearchStrategy: all) did not find any beans (OnBeanCondition)

   RedisReactiveAutoConfiguration matched:
      - @ConditionalOnClass found required classes 'org.springframework.data.redis.connection.ReactiveRedisConnectionFactory', 'org.springframework.data.redis.core.ReactiveRedisTemplate', 'reactor.core.publisher.Flux' (OnClassCondition)

   RedisReactiveAutoConfiguration#reactiveRedisTemplate matched:
      - @ConditionalOnBean (types: org.springframework.data.redis.connection.ReactiveRedisConnectionFactory; SearchStrategy: all) found bean 'lettuceConnectionFactory'; @ConditionalOnMissingBean (names: reactiveRedisTemplate; SearchStrategy: all) did not find any beans (OnBeanCondition)

   RedisReactiveAutoConfiguration#reactiveStringRedisTemplate matched:
      - @ConditionalOnBean (types: org.springframework.data.redis.connection.ReactiveRedisConnectionFactory; SearchStrategy: all) found bean 'lettuceConnectionFactory'; @ConditionalOnMissingBean (names: reactiveStringRedisTemplate; SearchStrategy: all) did not find any beans (OnBeanCondition)

   RedisRepositoriesAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.data.redis.repository.configuration.EnableRedisRepositories' (OnClassCondition)
      - @ConditionalOnProperty (spring.data.redis.repositories.enabled=true) matched (OnPropertyCondition)
      - @ConditionalOnBean (types: org.springframework.data.redis.connection.RedisConnectionFactory; SearchStrategy: all) found bean 'lettuceConnectionFactory'; @ConditionalOnMissingBean (types: org.springframework.data.redis.repository.support.RedisRepositoryFactoryBean; SearchStrategy: all) did not find any beans (OnBeanCondition)

   TaskExecutionAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor' (OnClassCondition)

   TaskExecutionAutoConfiguration#applicationTaskExecutor matched:
      - @ConditionalOnMissingBean (types: java.util.concurrent.Executor; SearchStrategy: all) did not find any beans (OnBeanCondition)

   TaskExecutionAutoConfiguration#taskExecutorBuilder matched:
      - @ConditionalOnMissingBean (types: org.springframework.boot.task.TaskExecutorBuilder; SearchStrategy: all) did not find any beans (OnBeanCondition)

   TaskSchedulingAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler' (OnClassCondition)

   TaskSchedulingAutoConfiguration#taskSchedulerBuilder matched:
      - @ConditionalOnMissingBean (types: org.springframework.boot.task.TaskSchedulerBuilder; SearchStrategy: all) did not find any beans (OnBeanCondition)

   TransactionAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.transaction.PlatformTransactionManager' (OnClassCondition)

   TransactionAutoConfiguration#platformTransactionManagerCustomizers matched:
      - @ConditionalOnMissingBean (types: org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers; SearchStrategy: all) did not find any beans (OnBeanCondition)


Negative matches:
-----------------

   AopAutoConfiguration.ClassProxyingConfiguration:
      Did not match:
         - @ConditionalOnProperty (spring.aop.proxy-target-class=true) found different value in property 'proxy-target-class' (OnPropertyCondition)
      Matched:
         - @ConditionalOnMissingClass did not find unwanted class 'org.aspectj.weaver.Advice' (OnClassCondition)

   CacheAutoConfiguration:
      Did not match:
         - @ConditionalOnBean (types: org.springframework.cache.interceptor.CacheAspectSupport; SearchStrategy: all) did not find any beans of type org.springframework.cache.interceptor.CacheAspectSupport (OnBeanCondition)
      Matched:
         - @ConditionalOnClass found required class 'org.springframework.cache.CacheManager' (OnClassCondition)

   CacheAutoConfiguration.CacheManagerEntityManagerFactoryDependsOnPostProcessor:
      Did not match:
         - @ConditionalOnClass did not find required class 'org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean' (OnClassCondition)
         - Ancestor org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration did not match (ConditionEvaluationReport.AncestorsMatchedCondition)

   JedisConnectionConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required classes 'org.apache.commons.pool2.impl.GenericObjectPool', 'org.springframework.data.redis.connection.jedis.JedisConnection', 'redis.clients.jedis.Jedis' (OnClassCondition)

   LettuceConnectionConfiguration#redisConnectionFactory:
      Did not match:
         - @ConditionalOnMissingBean (types: org.springframework.data.redis.connection.RedisConnectionFactory; SearchStrategy: all) found beans of type 'org.springframework.data.redis.connection.RedisConnectionFactory' lettuceConnectionFactory (OnBeanCondition)

   MailSenderValidatorAutoConfiguration:
      Did not match:
         - @ConditionalOnProperty (spring.mail.test-connection) did not find property 'test-connection' (OnPropertyCondition)

   MessageSourceAutoConfiguration:
      Did not match:
         - ResourceBundle did not find bundle with basename messages (MessageSourceAutoConfiguration.ResourceBundleCondition)

   ProjectInfoAutoConfiguration#buildProperties:
      Did not match:
         - @ConditionalOnResource did not find resource '${spring.info.build.location:classpath:META-INF/build-info.properties}' (OnResourceCondition)

   ProjectInfoAutoConfiguration#gitProperties:
      Did not match:
         - GitResource did not find git info at classpath:git.properties (ProjectInfoAutoConfiguration.GitResourceAvailableCondition)

   RedisAutoConfiguration#redisTemplate:
      Did not match:
         - @ConditionalOnMissingBean (names: redisTemplate; SearchStrategy: all) found beans named redisTemplate (OnBeanCondition)

   TaskSchedulingAutoConfiguration#taskScheduler:
      Did not match:
         - @ConditionalOnBean (names: org.springframework.context.annotation.internalScheduledAnnotationProcessor; SearchStrategy: all) did not find any beans named org.springframework.context.annotation.internalScheduledAnnotationProcessor (OnBeanCondition)

   TransactionAutoConfiguration#transactionalOperator:
      Did not match:
         - @ConditionalOnSingleCandidate (types: org.springframework.transaction.ReactiveTransactionManager; SearchStrategy: all) did not find any beans (OnBeanCondition)

   TransactionAutoConfiguration.EnableTransactionManagementConfiguration:
      Did not match:
         - @ConditionalOnBean (types: org.springframework.transaction.TransactionManager; SearchStrategy: all) did not find any beans of type org.springframework.transaction.TransactionManager (OnBeanCondition)

   TransactionAutoConfiguration.EnableTransactionManagementConfiguration.CglibAutoProxyConfiguration:
      Did not match:
         - @ConditionalOnProperty (spring.aop.proxy-target-class=true) found different value in property 'proxy-target-class' (OnPropertyCondition)
         - Ancestor org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$EnableTransactionManagementConfiguration did not match (ConditionEvaluationReport.AncestorsMatchedCondition)

   TransactionAutoConfiguration.EnableTransactionManagementConfiguration.JdkDynamicAutoProxyConfiguration:
      Did not match:
         - Ancestor org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$EnableTransactionManagementConfiguration did not match (ConditionEvaluationReport.AncestorsMatchedCondition)
      Matched:
         - @ConditionalOnProperty (spring.aop.proxy-target-class=false) matched (OnPropertyCondition)

   TransactionAutoConfiguration.TransactionTemplateConfiguration:
      Did not match:
         - @ConditionalOnSingleCandidate (types: org.springframework.transaction.PlatformTransactionManager; SearchStrategy: all) did not find any beans (OnBeanCondition)


Exclusions:
-----------

    None


Unconditional classes:
----------------------

    org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration

    org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration

    org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration

    org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration

    org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration



2021-04-05 08:21:32.076  INFO 1 --- [           main] i.s.c.virusapp.VirusappApplication       : Started VirusappApplication in 5.053 seconds (JVM running for 5.056)
Let's start!
1. Published Human(id=1, name=John, isInfected=false, isVaccinated=false)
End!!
```

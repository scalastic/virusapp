# Redis PubSub trial with Spring Native

## Presentation

This a POC demonstrating the `spring-native` v`0.9.1` ability to compile and drive a not so-simple application. 
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

2021-03-31 14:53:37.335  INFO 79704 --- [           main] i.s.c.virusapp.VirusappApplication       : Starting VirusappApplication using Java 11.0.10 on Computer-Gianni.local with PID 79704 (/Users/jeanjerome/Developments/Projects/hotspot-native-conquest/virusapp/target/classes started by jeanjerome in /Users/jeanjerome/Developments/Projects/hotspot-native-conquest/virusapp)
2021-03-31 14:53:37.336  INFO 79704 --- [           main] i.s.c.virusapp.VirusappApplication       : No active profile set, falling back to default profiles: default
2021-03-31 14:53:37.879  INFO 79704 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
2021-03-31 14:53:37.881  INFO 79704 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2021-03-31 14:53:37.899  INFO 79704 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 3 ms. Found 0 Redis repository interfaces.
2021-03-31 14:53:38.772  INFO 79704 --- [           main] i.s.c.virusapp.VirusappApplication       : Started VirusappApplication in 1.949 seconds (JVM running for 2.444)
Let's start!
Subscribed {"id":1,"name":"John","isInfected":false,"isVaccinated":false}
Transformed Human(id=1, name=John, isInfected=true, isVaccinated=false)
Published Human(id=1, name=John, isInfected=false, isVaccinated=false)
End!!
```

### In Native

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
% docker run --rm -it --name virus-app --network pubsub-network virusapp:0.0.1-SNAPSHOT
```

- Expected output :
```
2021-03-31 17:58:48.959  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.4)

2021-03-31 17:58:48.961  INFO 1 --- [           main] i.s.c.virusapp.VirusappApplication       : Starting VirusappApplication using Java 11.0.10 on 031d51401670 with PID 1 (/workspace/io.scalastic.coronapubsub.virusapp.VirusappApplication started by cnb in /workspace)
2021-03-31 17:58:48.962  INFO 1 --- [           main] i.s.c.virusapp.VirusappApplication       : No active profile set, falling back to default profiles: default
2021-03-31 17:58:48.977  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
2021-03-31 17:58:48.977  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2021-03-31 17:58:48.977  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 0 ms. Found 0 Redis repository interfaces.
2021-03-31 17:58:49.003  INFO 1 --- [           main] i.s.c.virusapp.VirusappApplication       : Started VirusappApplication in 0.053 seconds (JVM running for 0.056)
Let's start!
Published Human(id=1, name=John, isInfected=false, isVaccinated=false)
End!!
Subscribed {"id":1,"name":"John","isInfected":false,"isVaccinated":false}
Transformed Human(id=1, name=John, isInfected=true, isVaccinated=false)
```

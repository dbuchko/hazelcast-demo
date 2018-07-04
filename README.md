## Demo app for testing Hazelcast in embedded mode forming a cluster

### Local test
To know what to expect in other environments, run and test this locally first.
Run this app twice on you localhost:
```bash
mvn spring-boot:run
mvn spring-boot:run -Dserver.port=8081
```

Then get Hazelcast cluster info from the `info` endpoint:
```bash
curl localhost:8080/info | jq
```
You should see 2 entries in the `hazelcastCluster` array since you've started the app twice

> `| jq` in the above command is optional, but very helpful to read JSON

Now test writing to one node and reading to another:
```bash
curl localhost:8080/write?value=321
curl localhost:8081/read
```
Assert that you see the same value that you've written to the one node, coming from from the other.

### Test in PCF (or other target env)
Enable container-to-container communication and test:
```bash
mvn package
cf push hazelcast1 -p ./target/hazelcast-demo-0.0.1-SNAPSHOT.jar
cf push hazelcast2 -p ./target/hazelcast-demo-0.0.1-SNAPSHOT.jar
```

Do the same testing on `hazelcast1` and `hazelcast2` that we did with `localhost:8080` and `localhost:8081` locally.

## Happy Hazelcasting!
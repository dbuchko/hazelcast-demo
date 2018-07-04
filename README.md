## Demo app for testing Hazelcast in embedded mode forming a cluster

### Local test
To know what to expect in other environments, run and test this locally first.
Run this app twice on you localhost:
```
mvn spring-boot:run
mvn spring-boot:run -Dserver.port=8081
```

Then get Hazelcast cluster info from the `info` endpoint:
```
curl localhost:8080/info | jq
```
You should see 2 entries in the `hazelcastCluster` array since you've started the app twice

> `| jq` in the above command is optional, but very helpful to read JSON

Now test writing to one node and reading to another:
```
curl localhost:8080/write?value=321
curl localhost:8081/read
```
Assert that you see the same value that you've written to the one node, coming from from the other.

### Test in PCF (or other target env)
cf push -name node1
cf push -name node2

Do the same testing on `node1` and `node2` that we did with `localhost:8080` and `localhost:8081` locally.

## Happy Hazelcasting!
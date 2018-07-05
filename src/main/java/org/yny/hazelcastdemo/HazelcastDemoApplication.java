package org.yny.hazelcastdemo;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HazelcastDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HazelcastDemoApplication.class, args);
    }

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();


        config.getNetworkConfig().getInterfaces().setEnabled(true).addInterface("10.*.*.*");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig()
                .setEnabled(true)
                .addMember("hazelcast1.apps.internal")
                .addMember("hazelcast2.apps.internal");


        config.addMapConfig(
            new MapConfig().setName("hazelcastDemoCache")
                    .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                    .setEvictionPolicy(EvictionPolicy.LRU)
                    .setTimeToLiveSeconds(20)
        );
        return config;
    }

    @Bean
    public InfoContributor hazelcastInfo(HazelcastInstance hazelcastInstance) {
        return new InfoContributor() {
            @Override
            public void contribute(Info.Builder builder) {
                builder.withDetail("hazelcastCluster", hazelcastInstance.getCluster().getMembers());
            }
        };
    }

    @GetMapping(value = "write", produces = "text/plain")
    public String write(@RequestParam String value) {
        hazelcastInstance.getMap("hazelcastDemoCache").put("key", value);
        return "OK, wrote [" + value + "] to cache";
    }

    @GetMapping(value = "read", produces = "text/plain")
    public String read() {
        Object value = hazelcastInstance.getMap("hazelcastDemoCache").get("key");
        return value == null ? "<no-value-in-cache>" : value.toString();
    }

}

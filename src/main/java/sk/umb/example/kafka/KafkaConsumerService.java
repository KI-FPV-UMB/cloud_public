package sk.umb.example.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaConsumerService implements MessageConsumerService {

    private KafkaConsumer<String, String> consumer;
    private final List<MessageListener> listeners = new ArrayList<>();
    private ExecutorService executorService;
    private volatile boolean running = true;

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList("my-topic"));

        executorService = Executors.newFixedThreadPool(3);

        startPoller();
    }

    private void startPoller() {
        executorService.submit(() -> {
            try {
                while (running) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        for (MessageListener listener : listeners) {
                            executorService.submit(() -> listener.onMessage(record.value()));
                        }
                    }
                }
            } catch (WakeupException e) {
            } catch (Exception e) {
                System.err.println("Poller thread encountered an error: " + e.getMessage());
                running = false;
            }
        });
    }

    @Override
    public void registerListener(MessageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            running = false;
            consumer.wakeup();

            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }

            if (consumer != null) {
                consumer.close();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
package dev.datainmotion.bertqa;

import dev.datainmotion.bertqa.model.RawText;
import dev.datainmotion.bertqa.service.SentimentService;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.common.schema.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.github.javafaker.Faker;
import java.util.UUID;

import static org.apache.pulsar.client.api.SubscriptionType.Shared;

/**
 * example spring boot app to receive text from pulsar and run sentiment
 */

@EnableScheduling
@SpringBootApplication
public class BertQAApp {

	private static final Logger log = LoggerFactory.getLogger(BertQAApp.class);

	@Autowired
    private SentimentService sentimentService;

	@Autowired
	private PulsarTemplate<RawText> pulsarTemplate;

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(BertQAApp.class, args);
	}


	/**
	 *
	 * @return
	 */
	public String buildText() {
		Faker faker = new Faker();
		return faker.company().catchPhrase();
	}

	/**
	 *
	 * @return
	 */
	public String buildName() {
		Faker faker = new Faker();
		return faker.internet().domainName();
	}


	/**
	 *
	 */
	@Scheduled(initialDelay = 30, fixedRate = 30000)
	public void statusUpdate() {

		// Testing
		try {
			this.pulsarTemplate.setSchema(Schema.JSON(RawText.class));
			UUID uuidKey = UUID.randomUUID();
			RawText rawText = new RawText();
			rawText.setMessageText(buildText());
			rawText.setName(buildName());
			MessageId msgid = pulsarTemplate.newMessage(rawText)
					.withMessageCustomizer((mb) -> mb.key(uuidKey.toString()))
					.send();
			log.debug("MSGID Sent: {}", msgid.toString());
		}
		catch (Throwable e) {
			log.error("Pulsar Error", e);
		}
		// Testing

		this.log.info("Status Update. {}", Runtime.getRuntime().freeMemory());
	}


	@PulsarListener(subscriptionName = "sentiment-spring-reader", subscriptionType = Shared, schemaType = SchemaType.JSON,
			topics = "persistent://public/default/rawtext2")
	void analyzeText(RawText message) {

		this.log.info("RAW Text Message received: {}", message.toString());

		String result = sentimentService.getSentiment(message.getMessageText());

		this.log.info("Sentiment: {}", result);
	}

}

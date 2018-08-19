package server.core.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.TopicListing;

public final class KafkaAdmin {

	private AdminClient admin;
	private static KafkaAdmin instance;

	private KafkaAdmin() {
		init();
	}
	
	public static KafkaAdmin getInstance() {
		if (instance == null) {
			instance = new KafkaAdmin();
		}
		return instance;
	}

	private void init() {
		Properties adminp = new Properties();
		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		
		adminp.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
				propManager.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
		admin = AdminClient.create(adminp);
	}

	public boolean existsTopic(String topicName) {
		Collection<String> topicNames = new ArrayList<>();
		topicNames.add(topicName);

		return existsTopic(topicNames);
	}

	public boolean existsTopic(String topicName1, String topicName2) {
		Collection<String> topicNames = new ArrayList<>();
		topicNames.add(topicName1);
		topicNames.add(topicName2);

		return existsTopic(topicNames);
	}

	public boolean existsTopic(Collection<String> topicNames) {
		Collection<TopicListing> allListings = getExistingTopics();
		Collection<TopicListing> listingsToCheck = new ArrayList<TopicListing>();

		for (String topicName : topicNames) {
			listingsToCheck.add(new TopicListing(topicName, false));
		}
		if (!containsAllTopicListings(allListings, listingsToCheck)) {
			System.out.println("The chosen Input-Topics does not exits in Kafka");
			return false;
		} else {
			return true;
		}
	}
	
	private boolean containsAllTopicListings(Collection<TopicListing> allListings, Collection<TopicListing> listingsToCheck) {
		int num = 0;
		for (TopicListing a : allListings) {
			for (TopicListing b: listingsToCheck) {
				if (a.name().equals(b.name())) {
					num++;
				}
			}
		}
		if (num == listingsToCheck.size()) return true;
		return false;
	}
	
	private Collection<TopicListing> getExistingTopics() {
		Collection<TopicListing> topicListings = new ArrayList<>();
		try {
			topicListings = admin.listTopics().listings().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return topicListings;
	}

	public boolean deleteTopic(String topic) {
		Collection<TopicListing> topicListings = getExistingTopics();
		TopicListing tl = new TopicListing(topic, false);
		if (!topicListings.contains(tl))
			return true;

		Collection<String> topicsToRemove = new ArrayList<String>();
		topicsToRemove.add(topic);
		DeleteTopicsResult result = admin.deleteTopics(topicsToRemove);

		return result.all().isDone();
	}

}
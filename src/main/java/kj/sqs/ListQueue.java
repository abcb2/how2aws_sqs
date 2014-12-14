package kj.sqs;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ListQueuesResult;

public class ListQueue {
	/*
	 * mvn -q exec:java -Dexec.mainClass=kj.sqs.ListQueue -Dexec.args="-h"
	 *   queueを表示
	 */
	public static void main(String[] args){
		Options opts = new Options();
		opts.addOption("h", false, "ヘルプを表示します");
//		opts.addOption("config", true, "configのパス");
		CommandLineParser parser = new BasicParser();
		CommandLine commandLine;
		try {
			commandLine = parser.parse(opts, args);
		} catch(Exception e) {
			System.err.println("args are wrong..");
			return;
		}
		
		Properties config = new Properties();
		try {
			config.load(ListQueue.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			System.err.println("config load fail");
			e.printStackTrace();
		}
//		if(commandLine.hasOption("config")){
//			name = commandLine.getOptionValue("config");
//		}
		if(commandLine.hasOption("h")){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(ListQueue.class.getClass().getName(), opts);
			return;
		}
		
		String endpoint = config.getProperty("endpoint");
		AmazonSQSClient client = new AmazonSQSClient(new DefaultAWSCredentialsProviderChain());
		client.setEndpoint(endpoint);
		ListQueuesResult result = client.listQueues();
		List<String> list = result.getQueueUrls();
		for(String queueName : list){
			System.out.println(queueName);
		}
	}
}
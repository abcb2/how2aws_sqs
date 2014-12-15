package kj.sqs;

import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;

public class CreateQueue {
	@SuppressWarnings("static-access")
	public static void main(String args[]){
		Options opts = new Options();
		Option name = OptionBuilder.withDescription("queueの名前を指定します").
							withArgName("name").hasArg().isRequired().create("name");
		Option help = OptionBuilder.withDescription("ヘルプを表示します").
							withArgName("h").create("h");
		opts.addOption(name);
		opts.addOption(help);
		
		CommandLineParser parser = new BasicParser();
		CommandLine commandLine;
		try {
			commandLine = parser.parse(opts, args);
		} catch(Exception e){
			help(opts);
			return;
		}

		Properties config = new Properties();
		String queueName = "";
		try {
			config.load(CreateQueue.class.getResourceAsStream("/config.properties"));
		} catch(Exception e){
			System.err.println("config load fail");
			help(opts);
			return;
		}
		if(commandLine.hasOption("h")){
			help(opts);
			return;
		}
		if(commandLine.hasOption("name")){
			queueName = commandLine.getOptionValue("name");
		}
		String endpoint = config.getProperty("endpoint");
		AmazonSQSClient client = new AmazonSQSClient(new DefaultAWSCredentialsProviderChain());
		client.setEndpoint(endpoint);
		
		CreateQueueRequest request = new CreateQueueRequest().withQueueName(queueName);
		String url = client.createQueue(request).getQueueUrl();
		System.out.println("created: " + url);
	}
	
	public static void help(Options opts){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(CreateQueue.class.getClass().getName(), opts);		
	}

}

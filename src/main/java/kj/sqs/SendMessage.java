package kj.sqs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class SendMessage {
	@SuppressWarnings("static-access")
	public static void main(String[] args){
		Options opts = new Options();
		Option body = OptionBuilder.withDescription("メッセージボディ")
				.withArgName("body").hasArg().isRequired().create("body");
		Option help = OptionBuilder.withDescription("ヘルプを表示")
				.withArgName("h").create("h");
		opts.addOption(body);
		opts.addOption(help);

		CommandLineParser parser = new BasicParser();
		CommandLine commandLine;
		try {
			commandLine = parser.parse(opts, args);
		} catch(Exception e){
			help(opts);
			return;
		}
		if(commandLine.hasOption("h")){
			help(opts);
			return;
		}
		String bodyMessage = "";
		if(commandLine.hasOption("body")){
			bodyMessage = commandLine.getOptionValue("body");
		}
		
		Properties config = new Properties();
		try {
			config.load(SendMessage.class.getResourceAsStream("/config.properties"));
		} catch(Exception e){
			System.err.println("config load fail..");
			help(opts);
			return;
		}
		String endpoint = config.getProperty("endpoint");
		String queueName = config.getProperty("queueName");
		AmazonSQSClient client = new AmazonSQSClient(new DefaultAWSCredentialsProviderChain());
		client.setEndpoint(endpoint);
		
		SendMessageRequest request = new SendMessageRequest(queueName, bodyMessage);
		SendMessageResult result = client.sendMessage(request);
		String res_attr = result.getMD5OfMessageAttributes();
		String res_body = result.getMD5OfMessageBody();
		String res_mess = result.getMessageId();
		System.out.println("attr: " + res_attr + " body: " + res_body + " mess: " + res_mess);
	}

	public static void help(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(CreateQueue.class.getClass().getName(), opts);	
	}
}

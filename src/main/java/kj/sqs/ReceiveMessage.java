package kj.sqs;

import java.util.List;
import java.util.Map.Entry;
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
import com.amazonaws.services.sqs.model.DeleteMessageBatchResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class ReceiveMessage {

	@SuppressWarnings("static-access")
	public static void main(String[] args) { 
		Options opts = new Options();
		Option help = OptionBuilder.withDescription("ヘルプを表示")
							.withArgName("help").create("help");
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
		Properties config = new Properties();
		try {
			config.load(ReceiveMessage.class.getResourceAsStream("/config.properties"));
		} catch(Exception e){
			System.err.println("config load fail..");
			help(opts);
			return;
		}
		String endpoint = config.getProperty("endpoint");
		String queueName = config.getProperty("queueName");
		AmazonSQSClient client = new AmazonSQSClient(new DefaultAWSCredentialsProviderChain());
		client.setEndpoint(endpoint);
		
		ReceiveMessageRequest request = new ReceiveMessageRequest(queueName);
		List<Message> messages = client.receiveMessage(request).getMessages();
		for (Message message : messages) {
		    System.out.println("  Message");
		    System.out.println("    MessageId:     " + message.getMessageId());
		    System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
		    System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
		    System.out.println("    Body:          " + message.getBody());
		    for (Entry<String, String> entry : message.getAttributes().entrySet()) {
		        System.out.println("  Attribute");
		        System.out.println("    Name:  " + entry.getKey());
		        System.out.println("    Value: " + entry.getValue());
		    }
		    // 処理完了後、queueから削除する
		    String messageReceiptHandle = message.getReceiptHandle();
		    DeleteMessageRequest deleteRequest = 
		    		new DeleteMessageRequest().withQueueUrl(queueName)
		    		.withReceiptHandle(messageReceiptHandle);
		    // deleteMessageの返り値はvoid。べきとう性はアプリの側で確保して
		    // おく必要がある。つまり、削除が失敗して再度messageが取得できたとしても
		    // 同じ結果になるようにしたり、問題がおこらないようにするのはアプリ側の責務
		    client.deleteMessage(deleteRequest);
		}
	}
	
	public static void help(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(CreateQueue.class.getClass().getName(), opts);	
	}

}

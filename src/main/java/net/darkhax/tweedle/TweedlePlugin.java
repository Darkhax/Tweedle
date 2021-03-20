package net.darkhax.tweedle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweedlePlugin implements Plugin<Project> {
    
    @Override
    public void apply (Project project) {
        
        project.getLogger().debug("Successfully applied the Tweedle Plugin. Make sure you've set up some tasks.");
    }
    
    /**
     * Creates a new twitter client. A client can be used by a task to send tweets.
     * 
     * @param apiKey The API key for the twitter client.
     * @param apiKeySecret The API key secret for the twitter client.
     * @param accessToken The access token for the twitter client.
     * @param accessTokenSecret The access token secret for the twitter client.
     * @return The twitter client that was created.
     */
    public static Twitter createClient (String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
        
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        
        builder.setOAuthConsumerKey(notNull(apiKey, "API token is required!"));
        builder.setOAuthConsumerSecret(notNull(apiSecret, "API token secret is required!"));
        builder.setOAuthAccessToken(notNull(accessToken, "Access token is required! Token="));
        builder.setOAuthAccessTokenSecret(notNull(accessTokenSecret, "Access token secret is required! Secret="));
        
        return new TwitterFactory(builder.build()).getInstance();
    }
    
    /**
     * Creates a new tweet object. Does not send it.
     * 
     * @param status The message contained by the tweet.
     * @return A tweet object that can be modified or sent by a plugin task.
     */
    public static StatusUpdate createTweet (String status) {
        
        return new StatusUpdate(status);
    }
    
    /**
     * Checks if a string value is null/empty. Used to validate token input before sending it
     * to twitter. Take care not to log the input here.
     * 
     * @param input The input to validate.
     * @param msg The exception message posted when the input is invalid.
     * @return The input value.
     */
    private static String notNull (String input, String msg) {
        
        if (input != null && !input.isEmpty()) {
            
            return input;
        }
        
        throw new IllegalArgumentException(msg);
    }
}
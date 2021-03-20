package net.darkhax.tweedle;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;

public class TaskPostTweet extends DefaultTask {
    
    /**
     * An internal logger used to output debug information.
     */
    private final Logger log = Logging.getLogger("Tweedle");
    
    /**
     * A list of twitter clients to use when sending a tweet. Clients can be added using
     * {@link #addClient(String, String, String, String)}.
     */
    private final List<Twitter> clients = new ArrayList<>();
    
    /**
     * An internal flag used by {@link #sendTweet(StatusUpdate)} to determine if the plugin has
     * been configured yet.
     */
    private boolean hasApplied = false;
    
    /**
     * When set to true tweets sent from this task will fail quietly if they fail to send. This
     * is true by default.
     */
    public boolean sendQuietly = true;
    
    @TaskAction
    public void apply () {
        
        this.hasApplied = true;
    }
    
    /**
     * Sends a tweet using all added Twitter clients.
     * 
     * @param tweet The tweet to send.
     */
    public void sendTweet (String tweet) {
        
        this.sendTweet(TweedlePlugin.createTweet(tweet));
    }
    
    /**
     * Sends a tweet using all added Twitter clients.
     * 
     * @param tweet The tweet to send.
     */
    public void sendTweet (StatusUpdate tweet) {
        
        try {
            
            if (!this.hasApplied) {
                
                this.log.error("Attempted to send tweet before the task has been configured. This should only be done during doLast.");
                throw new IllegalStateException("Attempted to tweet before the task was configured and applied.");
            }
            
            else {
                
                if (this.clients.isEmpty()) {
                    
                    this.log.error("Attempted to send tweet but no clients have been added to the task. This is mandatory!");
                    throw new IllegalStateException("At least one client is required to send a tweet!");
                }
                
                else {
                    
                    for (final Twitter client : this.clients) {
                        
                        try {
                            
                            client.updateStatus(tweet);
                        }
                        
                        catch (final Exception e) {
                            
                            this.log.error("Failed to send tweet.", e);
                            throw new IllegalStateException(e);
                        }
                    }
                }
            }
        }
        
        catch (final Exception e) {
            
            this.log.error("Failed to send tweet.", e);
            
            if (!this.sendQuietly) {
                
                throw e;
            }
        }
    }
    
    /**
     * Adds a new twitter client.
     * 
     * @param apiKey The API key for the twitter client.
     * @param apiKeySecret The API key secret for the twitter client.
     * @param accessToken The access token for the twitter client.
     * @param accessTokenSecret The access token secret for the twitter client.
     */
    public void addClient (String apiKey, String apiKeySecret, String accessToken, String accessTokenSecret) {
        
        this.addClient(TweedlePlugin.createClient(apiKey, apiKeySecret, accessToken, accessTokenSecret));
    }
    
    /**
     * Adds a new twitter client.
     * 
     * @param client The new twitter client.
     */
    public void addClient (Twitter client) {
        
        try {
            
            this.log.debug("Added new client for " + client.getScreenName());
            this.clients.add(client);
        }
        
        catch (final Exception e) {
            
            this.log.error("Attempted to add an invalid client!", e);
            throw new IllegalStateException(e);
        }
    }
}
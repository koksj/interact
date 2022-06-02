# Thorntail with 3 JCA connectors

This server deploys three custom JCA connectors REST, MQ and SOAP. 
 This server will receive subscriber data from a Rabbit MQ server and convert the data to a SOAP request to send to IBM interact. Interact will respond with the appropriate campaign based on the subscriber detail. An SMS message is sent to the subscriber with the applicable details if Interact return a campaign for the subscriber.
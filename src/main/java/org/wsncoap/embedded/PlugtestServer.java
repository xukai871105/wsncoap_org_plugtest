
package org.wsncoap.embedded;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.network.interceptors.OriginTracer;

import org.wsncoap.embedded.resources.*;


/**
 * @author xukai
 */
public class PlugtestServer extends CoapServer {

    // exit codes for runtime errors
    public static final int ERR_INIT_FAILED = 1;
    
    // allows port configuration in Californium.properties
    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

    public static void main(String[] args) {
        
        // create server
        try {
            PlugtestServer server = new PlugtestServer();
            server.addEndpoints();
            server.start();
            // add special interceptor for message traces
            for (Endpoint ep:server.getEndpoints()) {
                ep.addInterceptor(new MessageTracer());
                // Eclipse IoT metrics
                ep.addInterceptor(new OriginTracer());
            }
            System.out.println(PlugtestServer.class.getSimpleName()+" listening on port " + COAP_PORT);
        } catch (Exception e) {
            System.err.printf("Failed to create "+PlugtestServer.class.getSimpleName()+": %s\n", e.getMessage());
            System.err.println("Exiting");
            System.exit(ERR_INIT_FAILED);
        }
    }
    
    /**
     * Add individual endpoints listening on default CoAP port on all IPv4 addresses of all network interfaces.
     */
    private void addEndpoints() {
        NetworkConfig config = NetworkConfig.getStandard();
        for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
            // only binds to IPv4 addresses and localhost
            if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
                InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
                // addEndpoint(new CoapEndpoint(bindToAddress));
                CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
                builder.setInetSocketAddress(bindToAddress);
                builder.setNetworkConfig(config);
                this.addEndpoint(builder.build());
            }
        }
    }
    
    public PlugtestServer() throws SocketException {
        
        NetworkConfig.getStandard() // used for plugtest
            .setInt(NetworkConfig.Keys.MAX_MESSAGE_SIZE, 64) 
            .setInt(NetworkConfig.Keys.PREFERRED_BLOCK_SIZE, 64)
            .setInt(NetworkConfig.Keys.NOTIFICATION_CHECK_INTERVAL_COUNT, 4)
            .setInt(NetworkConfig.Keys.NOTIFICATION_CHECK_INTERVAL_TIME, 30000);
        
        // add resources to the server
        add(new DefaultTest());
        add(new LongPath());
        add(new Query());
        add(new Separate());
        add(new Large());
        add(new LargeUpdate());
        add(new LargeCreate());
        add(new LargePost());
        add(new LargeSeparate());
        add(new Observe());
        add(new ObserveNon());
        add(new ObserveReset());
        add(new ObserveLarge());
        add(new ObservePumping());
        add(new ObservePumping(Type.NON));
        add(new LocationQuery());
        add(new MultiFormat());
        add(new Link1());
        add(new Link2());
        add(new Link3());
        add(new Path());
        add(new Validate());
        add(new Create());
        add(new Shutdown());
    }    
}

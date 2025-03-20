package com.jeremy.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Main application class for the Chess Game.
 * This is the entry point for the Spring Boot application.
 * 
 * @author Jeremy Kiley
 * @author ChatGPT
 */
@SpringBootApplication
public class ChessGameApplication {

	@Autowired
	private Environment environment;

	/**
	 * Main method that starts the Spring Boot application.
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ChessGameApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void printApplicationUrl() {
		try {
			String localIp = findLocalIpAddress();
			String port = environment.getProperty("server.port", "8080");
			
			// Print with ANSI escape codes for clickable link
			System.out.println("\nChess application is running!");
			System.out.printf("Local URL: \033]8;;http://localhost:%s\033\\http://localhost:%s\033]8;;\033\\\n", port, port);
			if (localIp != null) {
				System.out.printf("Network URL: \033]8;;http://%s:%s\033\\http://%s:%s\033]8;;\033\\\n", localIp, port, localIp, port);
			}
			System.out.println("Press Ctrl+C to exit gracefully");
			System.out.println(); // Empty line for better readability
		} catch (Exception e) {
			// Fallback to simple message if there's any error
			System.out.println("\nChess application is running on port " + 
				environment.getProperty("server.port", "8080"));
		}
	}

	private String findLocalIpAddress() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// Skip loopback, inactive, and virtual interfaces
				if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) {
					continue;
				}
				
				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					// Skip IPv6 addresses
					if (addr.getHostAddress().contains(":")) {
						continue;
					}
					// Skip loopback and link-local addresses
					if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
						return addr.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			// Silently fail and return null
		}
		return null;
	}
}

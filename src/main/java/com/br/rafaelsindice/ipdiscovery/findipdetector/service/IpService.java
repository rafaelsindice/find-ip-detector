package com.br.rafaelsindice.ipdiscovery.findipdetector.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Enumeration;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.br.rafaelsindice.ipdescovery.findipdetector.dto.IpDescoveryDto;

import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.JsonNode;

@Service
public class IpService {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(IpService.class);
	private final RestTemplate restTemplate;

	public IpService() {
		this.restTemplate = new RestTemplate();
	}

	public IpDescoveryDto getIpdiscovery(String ipAdress, String userAgent) {
		IpDescoveryDto ipDiscovery = new IpDescoveryDto();
		ipDiscovery.setIpAdress(ipAdress);
		ipDiscovery.setUserAgent(userAgent);
		ipDiscovery.setTimestamp(LocalDateTime.now());

		// pega geolocalização opcional

		String geoInfo = getGeoLocation(ipAdress);
		ipDiscovery.setLocation(geoInfo);

		// buscar hostname opcional

		String hostname = getHostname(ipAdress);
		ipDiscovery.setHostname(hostname);
		return ipDiscovery;
	}

	private String getGeoLocation(String ip) {
		try {
			String url = "http://ip-api.com/json/" + ip;
			JsonNode response = restTemplate.getForObject(url, JsonNode.class);

			if (response != null && response.get("status").asText().equals("success")) {
				String city = response.get("city").asText();
				String region = response.get("regionName").asText();
				String country = response.get("country").asText();
				return String.format("%s, %s, %s", city, region, country);

			}
		} catch (Exception e) {
			return "Localizacão não disponivel " + e;
		}
		return "Localização não disponivel";
	}

	public String getHostname(String ip) {
		try {
			InetAddress inetAddr = InetAddress.getByName(ip);
			String hostName = inetAddr.getCanonicalHostName();
			return hostName.equals(ip) ? "Não disponivel" : hostName;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Não disponível";
		}

	}

	public String getClientIP(HttpServletRequest request) {
		String ipAdress = request.getHeader("X-Forwarded-For");
		if(ipAdress == null || ipAdress.isEmpty() || "unknown".equalsIgnoreCase(ipAdress)) {
			ipAdress = request.getHeader("Proxy-Client-IP");
		}
		if(ipAdress == null || ipAdress.isEmpty() || "unknown".equalsIgnoreCase(ipAdress)) {
			ipAdress = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ipAdress == null || ipAdress.isEmpty() || "unknown".equalsIgnoreCase(ipAdress)) {
			ipAdress = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if(ipAdress == null || ipAdress.isEmpty() || "unknown".equalsIgnoreCase(ipAdress)) {
			ipAdress = request.getRemoteAddr();
		}
		if(ipAdress == null || ipAdress.equals("127.0.0.1") || ipAdress.equals("0:0:0:0:0:0:0:1")){ 
			ipAdress = getPublicIp();
		}
		if(ipAdress == null || ipAdress.isEmpty()) {
			ipAdress = getPublicIpSimple();
		}
		
		if(ipAdress != null && ipAdress.contains(",")) {
			ipAdress = ipAdress.split(",")[0].trim();
		}
		return ipAdress;
	}
	
	
	   public String getPublicIp() {
	        try {
	            // Usando ipify.org - serviço gratuito e sem limite de uso [citation:7]
	            String url = "https://api.ipify.org?format=json";
	            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
	            
	            if (response != null && response.has("ip")) {
	                return response.get("ip").asText();
	            }
	        } catch (Exception e) {
	            logger.error("Erro ao obter IP externo", e);
	        }
	        return null;
	    }
	    
	    // Método alternativo com fallback para texto puro
	    public String getPublicIpSimple() {
	        try {
	            String url = "https://api.ipify.org";
	            return restTemplate.getForObject(url, String.class);
	        } catch (Exception e) {
	            logger.error("Erro ao obter IP externo", e);
	            return null;
	        }
	    }

}

package com.br.rafaelsindice.ipdiscovery.findipdetector.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.rafaelsindice.ipdescovery.findipdetector.dto.IpDescoveryDto;
import com.br.rafaelsindice.ipdiscovery.findipdetector.service.IpService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ip")
public class IpController {
	@Autowired
	private IpService ipService;

	@GetMapping("/meu-ip")
	public ResponseEntity<IpDescoveryDto> getMyIp(HttpServletRequest request) {
		String clientIP = ipService.getClientIP(request);
		String userAgent = request.getHeader("User-Agent");

		IpDescoveryDto ipDiscovery = ipService.getIpdiscovery(clientIP, userAgent);
		return ResponseEntity.ok(ipDiscovery);
	}
	@GetMapping("/consulta/{ip}")
	public ResponseEntity<IpDescoveryDto> consultIp(@PathVariable String ip,
													HttpServletRequest request){
		String userAgent = request.getHeader("User-Agent");
		IpDescoveryDto ipDiscovery = ipService.getIpdiscovery(ip, userAgent);
		return ResponseEntity.ok(ipDiscovery);
	}
	

	@GetMapping("/headers")
	public ResponseEntity<Object> getRequestHeaders(HttpServletRequest request){
		Map<String, String> headers = new HashMap<>();
		
		Enumeration<String> headersNames = request.getHeaderNames();
		while(headersNames.hasMoreElements()) {
			String headerName = headersNames.nextElement();
			headers.put(headerName,request.getHeader(headerName));
		}
		return ResponseEntity.ok(headers);//
	}
}

package com.br.rafaelsindice.ipdescovery.findipdetector.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpDescoveryDto {
	private String ipAdress;
	private String userAgent;
	private LocalDateTime timestamp;
	private String location;
	private String hostname;
}

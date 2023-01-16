package com.cts.dlt.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import com.cts.dlt.dao.JwtExpired;


@FeignClient(name="auth-util" , url="http://localhost:7000/api/v1")
public interface FeignUtil {
	
	@GetMapping("/token/expired")
	JwtExpired validToken(@RequestHeader(name="auth" , required=false) String header);

}

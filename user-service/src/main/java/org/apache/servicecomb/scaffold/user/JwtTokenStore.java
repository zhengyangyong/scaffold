/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicecomb.scaffold.user;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;

import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenStore implements TokenStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenStore.class);

  private final String secretKey;

  private final int secondsToExpire;

  public JwtTokenStore() {
    this.secretKey = "someSecretKeyForAuthentication";
    this.secondsToExpire = 60 * 60 * 24;
  }

  public JwtTokenStore(String secretKey, int secondsToExpire) {
    this.secretKey = secretKey;
    this.secondsToExpire = secondsToExpire;
  }

  @Override
  public String generate(String userName) {
    return Jwts.builder().setSubject(userName)
        .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(secondsToExpire).toInstant()))
        .signWith(HS512, secretKey).compact();
  }

  @Override
  public boolean validate(String token) {
    try {
      return StringUtils
          .isNotEmpty(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
    } catch (JwtException | IllegalArgumentException e) {
      LOGGER.info("validateToken token : " + token + " failed", e);
    }
    return false;
  }
}
package org.apache.servicecomb.scaffold.log;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.scaffold.log.api.LogDTO;
import org.apache.servicecomb.scaffold.log.api.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestSchema(schemaId = "log")
@RequestMapping(path = "/")
public class LogServiceImpl implements LogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

  private static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

  @Override
  @PostMapping(path = "record")
  public boolean record(@RequestBody LogDTO log) {
    try {
      //简化处理，只是打印一下日志
      LOGGER.info(OBJ_MAPPER.writeValueAsString(log));
    } catch (JsonProcessingException ignored) {
    }
    return true;
  }
}
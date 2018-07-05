package org.apache.servicecomb.scaffold.payment;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.scaffold.payment.api.PaymentDTO;
import org.apache.servicecomb.scaffold.payment.api.PaymentService;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestSchema(schemaId = "payment")
@RequestMapping(path = "/")
public class PaymentServiceImpl implements PaymentService {
  //信用额度
  private static final double CREDIT_LIMIT = 1000000;

  private final PaymentRepository paymentRepository;

  private final DepositRepository depositRepository;

  @Autowired
  public PaymentServiceImpl(PaymentRepository paymentRepository, DepositRepository depositRepository) {
    this.paymentRepository = paymentRepository;
    this.depositRepository = depositRepository;
  }

  @Override
  @PostMapping(path = "deposit")
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ResponseEntity<Boolean> deposit(@RequestBody PaymentDTO payment) {
    if (validatePayment(payment)) {
      if (checkBalance(payment)) {
        if (recordPayment(payment, PaymentType.DEPOSIT)) {
          if (recordDeposit(payment)) {
            if (cutWithBank(payment)) {
              return new ResponseEntity<>(true, HttpStatus.OK);
            }
            throw new InvocationException(BAD_REQUEST, "cut with bank failed");
          }
          throw new InvocationException(BAD_REQUEST, "record deposit failed");
        }
        throw new InvocationException(BAD_REQUEST, "record payment failed");
      }
      throw new InvocationException(BAD_REQUEST, "check balance failed");
    }
    throw new InvocationException(BAD_REQUEST, "incorrect payment request");
  }

  private boolean validatePayment(PaymentDTO payment) {
    if (StringUtils.isNotEmpty(payment.getUserName()) && payment.getAmount() > 0 && StringUtils
        .isNotEmpty(payment.getTransactionId())
        && StringUtils.isNotEmpty(payment.getBankName()) && StringUtils.isNotEmpty(payment.getCardNumber())) {
      //TransactionId需要不重复，未被使用过
      PaymentEntity pay = paymentRepository.findByTransactionId(payment.getTransactionId());
      return pay == null;
    }
    return false;
  }

  //检查用户的余额，这里我们假设每一个用户都有一百万授信
  private boolean checkBalance(PaymentDTO payment) {
    //我们先要查一下系统里面已经用了多少
    List<PaymentEntity> pays = paymentRepository.findByUserName(payment.getUserName());
    double used = 0;
    for (PaymentEntity pay : pays) {
      used += pay.getAmount();
    }
    //预估一下账户余额够不够
    return payment.getAmount() <= (CREDIT_LIMIT - used);
  }

  //本地记账保留扣款凭据
  private boolean recordPayment(PaymentDTO payment, String paymentType) {
    paymentRepository
        .save(new PaymentEntity(payment.getTransactionId(), payment.getUserName(), payment.getBankName(),
            payment.getCardNumber(), payment.getAmount(), new Date(), paymentType));
    return true;
  }

  //登记缴纳的定金
  private boolean recordDeposit(PaymentDTO payment) {
    DepositEntity deposit = depositRepository.findByUserName(payment.getUserName());
    if (deposit == null) {
      deposit = new DepositEntity(payment.getUserName(), payment.getAmount());
    } else {
      deposit.setAmount(deposit.getAmount() + payment.getAmount());
    }
    depositRepository.save(deposit);
    return true;
  }

  //走银行接口请求银行划账，Demo不对接直接返回为true
  private boolean cutWithBank(PaymentDTO payment) {
    return true;
  }
}
